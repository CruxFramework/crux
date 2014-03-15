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
package org.cruxframework.crux.core.server.rest.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.i18n.LocaleResolver;
import org.cruxframework.crux.core.i18n.LocaleResolverInitializer;
import org.cruxframework.crux.core.server.rest.core.HttpHeaders;
import org.cruxframework.crux.core.server.rest.core.RequestPreprocessors;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.core.dispatch.RestDispatcher;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryInitializer;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.HttpResponse;
import org.cruxframework.crux.core.server.rest.spi.HttpUtil;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.shared.rest.annotation.HttpMethod;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestServlet extends HttpServlet 
{
	private static final Log logger = LogFactory.getLog(RestServlet.class);
	private static final long serialVersionUID = -4338760751718522206L;

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Preflight request received");
		}
		processCorsPreflightRequest(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequestForWriteOperation(req, resp, HttpMethod.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequestForWriteOperation(req, resp, HttpMethod.PUT);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequestForWriteOperation(req, resp, HttpMethod.DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequest(req, resp, HttpMethod.GET);
	}

	protected void processRequestForWriteOperation(HttpServletRequest req, HttpServletResponse res, String method) throws IOException
	{
		String xsrfHeader = req.getHeader(HttpHeaderNames.XSRF_PROTECTION_HEADER);
		if (xsrfHeader == null || xsrfHeader.length() == 0)
		{
			HttpUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "XSRF Protection validation failed for this request.");
		}
		else
		{
			processRequest(req, res, method);
		}
	}
	
	
	protected void processRequest(HttpServletRequest req, HttpServletResponse res, String method) throws IOException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Request received. Method ["+method+"]");
		}
		if (!RestServiceFactoryInitializer.isFactoryInitialized())
		{
			RestServiceFactoryInitializer.initialize(getServletContext());
		}
		HttpHeaders headers = null;
		UriInfo uriInfo = null;
		try
		{
			headers = HttpUtil.extractHttpHeaders(req);
			uriInfo = HttpUtil.extractUriInfo(req);
		}
		catch (Exception e)
		{
			HttpUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request.");
			logger.warn("Failed to parse request.", e);
			return;
		}
		
		HttpRequest request = new HttpRequest(req, headers, uriInfo, method);
		HttpResponse response = new HttpResponse(res);

		boolean localeInitializedByServlet = false;
		try
		{
			localeInitializedByServlet = initUserLocaleResolver(request);
			MethodReturn methodReturn = RestDispatcher.dispatch(request, response, false);
			if (!response.isCommitted())
			{
				HttpUtil.writeResponse(request, response, methodReturn);
			}
		}
		catch (RestFailure e) 
		{
			response.sendException(e.getResponseCode(), e.getResponseMessage());
			logger.error(e.getMessage(), e);
		}
		catch (Exception e) 
		{
			response.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error processing request.");
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (localeInitializedByServlet)
			{
				LocaleResolverInitializer.clearLocaleResolverThreadData();
			}
		}
	}
	
	/**
	 * 
	 */
	protected boolean initUserLocaleResolver(HttpRequest req)
	{
		if (LocaleResolverInitializer.getLocaleResolver() == null)
		{
			LocaleResolverInitializer.createLocaleResolverThreadData();
			LocaleResolver resolver = LocaleResolverInitializer.getLocaleResolver();
			resolver.initializeUserLocale(req);
			return true;
		}
		return false;
	}

	protected void processCorsPreflightRequest(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
		if (!RestServiceFactoryInitializer.isFactoryInitialized())
		{
			RestServiceFactoryInitializer.initialize(getServletContext());
		}
		HttpHeaders headers = null;
		UriInfo uriInfo = null;
		try
		{
			headers = HttpUtil.extractHttpHeaders(req);
			uriInfo = HttpUtil.extractUriInfo(req);
		}
		catch (Exception e)
		{
			HttpUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request.");
			logger.warn("Failed to parse request.", e);
			return;
		}
		
		HttpResponse response = new HttpResponse(res);
		String httpActualMethod = headers.getHeaderString(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
		if (httpActualMethod != null && httpActualMethod.length() > 0)
		{
			HttpRequest request = new HttpRequest(req, headers, uriInfo, httpActualMethod);
			try
			{
				RestDispatcher.dispatch(request, response, true);
			}
			catch (Exception e) 
			{
				response.sendException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error processing request.");
				logger.error(e.getMessage(), e);
			}
		}
		response.sendEmptyResponse();
    }
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
	    super.init(config);
	    String processors = config.getInitParameter("preprocessors");
	    if (processors != null)
	    {
	    	String[] processorNames = processors.split(",");
	    	for (String proc : processorNames)
            {
	            try
                {
					RequestPreprocessors.registerPreprocessor(proc.trim());
                }
                catch (Exception e)
                {
	                logger.error(e.getMessage(), e);
                }
            }
	    }
	}
}
