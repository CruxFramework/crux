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
package org.cruxframework.crux.core.server.rest.spi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.cruxframework.crux.core.server.rest.core.Cookie;
import org.cruxframework.crux.core.server.rest.core.EntityTag;
import org.cruxframework.crux.core.server.rest.core.Headers;
import org.cruxframework.crux.core.server.rest.core.HttpHeaders;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMap;
import org.cruxframework.crux.core.server.rest.core.UriBuilder;
import org.cruxframework.crux.core.server.rest.core.dispatch.CacheInfo;
import org.cruxframework.crux.core.server.rest.core.dispatch.ConditionalResponse;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.server.rest.util.MediaTypeHelper;
import org.cruxframework.crux.core.server.rest.util.PathHelper;
import org.cruxframework.crux.core.server.rest.util.header.MediaTypeHeaderParser;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpUtil
{
	public static UriInfo extractUriInfo(HttpServletRequest request)
	{
		String servletPrefix = request.getServletPath();
		String contextPath = request.getContextPath();
		if (servletPrefix != null && servletPrefix.length() > 0 && !servletPrefix.equals("/"))
		{
			if (!contextPath.endsWith("/") && !servletPrefix.startsWith("/"))
			{
				contextPath += "/";
			}
			contextPath += servletPrefix;
		}
		URI absolutePath = null;
		try
		{
			URL absolute = new URL(request.getRequestURL().toString());

			UriBuilder builder = new UriBuilder();
			builder.scheme(absolute.getProtocol());
			builder.host(absolute.getHost());
			builder.port(absolute.getPort());
			builder.path(absolute.getPath());
			builder.replaceQuery(null);
			absolutePath = builder.build();
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}

		String path = PathHelper.getEncodedPathInfo(absolutePath.getRawPath(), contextPath);
		URI relativeURI = UriBuilder.fromUri(path).replaceQuery(request.getQueryString()).build();

		URI baseURI = absolutePath;
		if (!path.trim().equals(""))
		{
			String tmpContextPath = contextPath;
			if (!tmpContextPath.endsWith("/"))
			{
				tmpContextPath += "/";
			}
			baseURI = UriBuilder.fromUri(absolutePath).replacePath(tmpContextPath).build();
		}
		UriInfo uriInfo = new UriInfo(baseURI, relativeURI);
		return uriInfo;
	}

	public static HttpHeaders extractHttpHeaders(HttpServletRequest request)
	{
		HttpHeaders headers = new HttpHeaders();

		MultivaluedMap<String, String> requestHeaders = extractRequestHeaders(request);
		headers.setRequestHeaders(requestHeaders);
		List<MediaType> acceptableMediaTypes = extractAccepts(requestHeaders);
		List<String> acceptableLanguages = extractLanguages(requestHeaders);
		headers.setAcceptableMediaTypes(acceptableMediaTypes);
		headers.setAcceptableLanguages(acceptableLanguages);
		headers.setLanguage(requestHeaders.getFirst(HttpHeaderNames.CONTENT_LANGUAGE));

		String contentType = request.getContentType();
		if (contentType != null)
		{
			headers.setMediaType(MediaType.valueOf(contentType));
		}

		Map<String, Cookie> cookies = extractCookies(request);
		headers.setCookies(cookies);
		return headers;

	}

	public static String wGet(String targetURL, String urlParameters, String method, String locale)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			if(method != null && method.equals("GET") && !StringUtils.isEmpty(urlParameters))
			{
				targetURL += "?" + urlParameters;
			}
			
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", locale);  
			connection.setUseCaches(false);
			connection.setDoInput(true);
			
			//Send request
			if(method != null && method.equals("POST"))
			{
				DataOutputStream wr = new DataOutputStream (
						connection.getOutputStream ());
				connection.setDoOutput(true);
				wr.writeBytes (urlParameters);
				wr.flush ();
				wr.close ();	
			}

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		} finally 
		{
			if(connection != null) 
			{
				connection.disconnect(); 
			}
		}
	}

	static Map<String, Cookie> extractCookies(HttpServletRequest request)
	{
		Map<String, Cookie> cookies = new HashMap<String, Cookie>();
		if (request.getCookies() != null)
		{
			for (javax.servlet.http.Cookie cookie : request.getCookies())
			{
				cookies.put(cookie.getName(), new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion()));
			}
		}
		return cookies;
	}

	public static List<MediaType> extractAccepts(MultivaluedMap<String, String> requestHeaders)
	{
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		List<String> accepts = requestHeaders.get(HttpHeaderNames.ACCEPT);
		if (accepts == null)
		{
			return acceptableMediaTypes;
		}

		for (String accept : accepts)
		{
			acceptableMediaTypes.addAll(MediaTypeHelper.parseHeader(accept));
		}
		return acceptableMediaTypes;
	}

	public static List<String> extractLanguages(MultivaluedMap<String, String> requestHeaders)
	{
		List<String> acceptable = new ArrayList<String>();
		List<String> accepts = requestHeaders.get(HttpHeaderNames.ACCEPT_LANGUAGE);
		if (accepts == null)
		{
			return acceptable;
		}

		for (String accept : accepts)
		{
			String[] splits = accept.split(",");
			for (String split : splits)
			{
				acceptable.add(split.trim());
			}
		}
		return acceptable;
	}

	@SuppressWarnings("unchecked")
	public static MultivaluedMap<String, String> extractRequestHeaders(HttpServletRequest request)
	{
		Headers<String> requestHeaders = new Headers<String>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();
			Enumeration<String> headerValues = request.getHeaders(headerName);
			while (headerValues.hasMoreElements())
			{
				String headerValue = headerValues.nextElement();
				// System.out.println("ADDING HEADER: " + headerName +
				// " value: " + headerValue);
				requestHeaders.add(headerName, headerValue);
			}
		}
		return requestHeaders;
	}

	public static boolean acceptsGzipEncoding(HttpRequest request)
	{
		assert (request != null);

		String acceptEncoding = request.getHttpHeaders().getHeaderString(HttpHeaderNames.ACCEPT_ENCODING);
		if (null == acceptEncoding)
		{
			return false;
		}

		return (acceptEncoding.indexOf("gzip") != -1);
	}

	private static final int UNCOMPRESSED_BYTE_SIZE_LIMIT = 256;

	private static boolean exceedsUncompressedContentLengthLimit(String content)
	{
		return (content != null) && ((content.length() * 2) > UNCOMPRESSED_BYTE_SIZE_LIMIT);
	}

	public static boolean shouldGzipResponseContent(HttpRequest request, String responseContent)
	{
		return acceptsGzipEncoding(request) && exceedsUncompressedContentLengthLimit(responseContent);
	}

	public static void writeResponse(HttpRequest request, HttpResponse response, MethodReturn methodReturn) throws IOException
	{
		HttpServletResponseHeaders outputHeaders = response.getOutputHeaders();
		if (methodReturn == null)
		{
			response.sendEmptyResponse();
		}
		else if (methodReturn.getCheckedExceptionData() != null)
		{
			response.sendError(HttpResponseCodes.SC_FORBIDDEN, methodReturn.getCheckedExceptionData());
		}
		else if (!methodReturn.hasReturnType())
		{
			response.setContentLength(0);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
		else if (methodReturn.getConditionalResponse() != null)
		{
			writeConditionalResponse(response, methodReturn, outputHeaders);
		}
		else
		{
			CacheInfo cacheInfo = methodReturn.getCacheInfo();
			if (cacheInfo != null)
			{
				writeCacheHeaders(response, cacheInfo, methodReturn.getEtag(), methodReturn.getDateModified(), methodReturn.isEtagGenerationEnabled());
			}

			String responseContent = methodReturn.getReturn();
			byte[] responseBytes = getResponseBytes(request, response, responseContent);
			response.setContentLength(responseBytes.length);
			response.setStatus(HttpServletResponse.SC_OK);
			outputHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, new MediaType("application", "json", "UTF-8"));
			response.getOutputStream().write(responseBytes);
		}
	}

	private static void writeConditionalResponse(HttpResponse response, MethodReturn methodReturn, HttpServletResponseHeaders outputHeaders)
	{
		ConditionalResponse conditionalResponse = methodReturn.getConditionalResponse();
		response.setStatus(conditionalResponse.getStatus());

		EntityTag etag = conditionalResponse.getEtag();
		long dateModified = conditionalResponse.getLastModified();
		CacheInfo cacheInfo = methodReturn.getCacheInfo();

		if (cacheInfo != null)
		{
			writeCacheHeaders(response, cacheInfo, etag, dateModified, methodReturn.isEtagGenerationEnabled());
		}
		else
		{
			//Confirmar se devo mandar etag e last modified em 412 ou 304 para escritas
			if (etag != null)
			{
				outputHeaders.putSingle(HttpHeaderNames.ETAG, etag);
			}
			if (dateModified > 0)
			{
				outputHeaders.addDateHeader(HttpHeaderNames.LAST_MODIFIED, dateModified);
			}
		}
	}

	private static void writeCacheHeaders(HttpResponse response, CacheInfo cacheInfo, EntityTag etag, long dateModified, boolean forceEtagGeneration)
	{
		org.cruxframework.crux.core.server.rest.core.CacheControl cacheControl = new org.cruxframework.crux.core.server.rest.core.CacheControl();
		HttpServletResponseHeaders outputHeaders = response.getOutputHeaders();
		if (!cacheInfo.isCacheEnabled())
		{
			cacheControl.setNoStore(true);
			outputHeaders.addDateHeader(HttpHeaderNames.EXPIRES, 0);
			if (forceEtagGeneration && etag != null)
			{
				outputHeaders.putSingle(HttpHeaderNames.ETAG, etag);
			}
		}
		else
		{
			outputHeaders.add(HttpHeaderNames.VARY, HttpHeaderNames.ACCEPT_LANGUAGE);
			long expires = cacheInfo.defineExpires();
			outputHeaders.addDateHeader(HttpHeaderNames.EXPIRES, expires);
			if (etag != null)
			{
				outputHeaders.putSingle(HttpHeaderNames.ETAG, etag);
			}
			if (dateModified > 0)
			{
				outputHeaders.addDateHeader(HttpHeaderNames.LAST_MODIFIED, dateModified);
			}
			switch (cacheInfo.getCacheControl())
			{
			case PUBLIC:
				cacheControl.setPublic(true);
				cacheControl.setMaxAge(cacheInfo.getCacheTime());
				break;
			case PRIVATE:
				cacheControl.setPrivate(true);
				cacheControl.setMaxAge(cacheInfo.getCacheTime());
				break;
			case NO_CACHE:
				cacheControl.setNoCache(true);
				break;
			}
			cacheControl.setNoTransform(cacheInfo.isNoTransform());
			cacheControl.setMustRevalidate(cacheInfo.isMustRevalidate());
			cacheControl.setProxyRevalidate(cacheInfo.isProxyRevalidate());
		}
		outputHeaders.putSingle(HttpHeaderNames.CACHE_CONTROL, cacheControl);
	}

	private static byte[] getResponseBytes(HttpRequest request, HttpResponse response, String responseContent) throws UnsupportedEncodingException, IOException
	{
		boolean gzipResponse = shouldGzipResponseContent(request, responseContent);
		byte[] responseBytes = (responseContent!=null?responseContent.getBytes("UTF-8"):new byte[0]);
		if (gzipResponse)
		{
			ByteArrayOutputStream output = null;
			GZIPOutputStream gzipOutputStream = null;
			try
			{
				output = new ByteArrayOutputStream(responseBytes.length);
				gzipOutputStream = new GZIPOutputStream(output);
				gzipOutputStream.write(responseBytes);
				gzipOutputStream.finish();
				gzipOutputStream.flush();
				response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_ENCODING, "gzip");
				responseBytes = output.toByteArray();
			}
			catch (IOException e)
			{
				throw new InternalServerErrorException("Unable to compress response", "Error processing requested service", e);
			}
			finally
			{
				if (null != gzipOutputStream)
				{
					gzipOutputStream.close();
				}
				if (null != output)
				{
					output.close();
				}
			}
		}
		return responseBytes;
	}

	public static void sendError(HttpServletResponse response, int status, String message) throws IOException
	{
		response.setStatus(status);
		if (message!= null)
		{
			byte[] responseBytes = HttpResponse.serializeException(message).getBytes("UTF-8");
			response.setContentLength(responseBytes.length);
			response.setHeader(HttpHeaderNames.CONTENT_TYPE, MediaTypeHeaderParser.toString(new MediaType("text", "plain", "UTF-8")));
			response.getOutputStream().write(responseBytes);
		}
	}
}