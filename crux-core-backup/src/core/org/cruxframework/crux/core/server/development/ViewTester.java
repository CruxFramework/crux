/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.server.development;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.w3c.dom.Document;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewTester extends HttpServlet
{
    private static final long serialVersionUID = 5790516839959792981L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	if (Environment.isProduction())
    	{
    		resp.setStatus(HttpResponseCodes.SC_FORBIDDEN);
    		resp.getWriter().println("This servlet can only be used on development environment.");
    		return;
    	}
    	String moduleName = req.getParameter("moduleName");
    	if (StringUtils.isEmpty(moduleName))
    	{
    		resp.setStatus(HttpResponseCodes.SC_BAD_REQUEST);
    		resp.getWriter().println("You must inform the maduleName and the viewName parameters.");
    		return;
    	}
    	else
    	{
    		processRequest(resp, moduleName);
    	}
    }

	protected void processRequest(HttpServletResponse resp, String moduleName) throws IOException, UnsupportedEncodingException
    {
		ViewTesterScreen.registerTestViewPageRequested(moduleName);
	    InputStream screenResource = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/cruxViewTester.crux.xml");
	    String screenContent = StreamUtils.readAsUTF8(screenResource);
	    screenContent = screenContent.replace("{moduleName}", moduleName);
	    ByteArrayInputStream inputStream = new ByteArrayInputStream(screenContent.getBytes("UTF-8"));

	    Document screen = ViewProcessor.getView(inputStream, null);
	    ViewProcessor.generateHTML(ViewTesterScreen.getTestViewScreenSuffix(), screen, resp.getOutputStream());
    }
}
