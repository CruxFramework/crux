/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.server.offline;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.Environment;

/**
 * Class description: Dummy filter for development using DevMode. 
 * 
 * @author alexandre.costa
 *
 */
public class DevModeAppcacheFilter extends AppcacheFilter
{
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (Environment.isProduction())
		{
			super.doFilter(request, response, chain);
		}
		else
		{
			sendDebugManifest(req, resp, chain, request, response);
		}
	}

	private void sendDebugManifest(ServletRequest req, ServletResponse resp, FilterChain chain, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/cache-manifest");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.println("CACHE MANIFEST\n");
		writer.println("# Build Time [" + getStartTime() + "]\n");
		writer.println("\nCACHE:\n");
		writer.println("clear.cache.gif\n");
		writer.println("\nNETWORK:\n");
		writer.println("*\n");
		writer.close();
	}
}
