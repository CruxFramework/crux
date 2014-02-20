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
package org.cruxframework.crux.core.server.offline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.http.GZIPResponseWrapper;
import org.cruxframework.crux.core.server.rest.core.CacheControl;
import org.cruxframework.crux.core.server.rest.util.DateUtil;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.server.rest.util.header.CacheControlHeaderParser;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.core.utils.StreamUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AppcacheFilter implements Filter
{
	private Map<String, Long> lastModifiedDates = Collections.synchronizedMap(new HashMap<String, Long>());
	private FilterConfig filterConfig;
	private long startTime;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (Environment.isProduction())
		{
			String ifModifiedHeader = request.getHeader(HttpHeaderNames.IF_MODIFIED_SINCE);
			long dateModified = getDateModified(request);
			if ((ifModifiedHeader == null) || isFileModified(ifModifiedHeader, dateModified))
			{
				sendRequestedFile(chain, request, response, dateModified);
			}
			else
			{
				response.setStatus(HttpResponseCodes.SC_NOT_MODIFIED);
			}
		}
		else
		{
			sendDebugManifest(req, resp, chain, request, response);
		}
	}

	private void sendDebugManifest(ServletRequest req, ServletResponse resp, FilterChain chain, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
	    response.setContentType("text/cache-manifest");
	    response.setCharacterEncoding("UTF-8");
	    PrintWriter writer = response.getWriter();
	    writer.println("CACHE MANIFEST\n");
	    writer.println("# Build Time [" + startTime + "]\n");
	    writer.println("\nCACHE:\n");
	    writer.println("clear.cache.gif\n");
	    writer.println("\nNETWORK:\n");
	    writer.println("*\n");
	    writer.close();
    }

	private boolean isFileModified(String ifModifiedHeader, long dateModified)
	{
		boolean result = true;
		try
		{
			if (dateModified > 0)
			{
				Date date = DateUtil.parseDate(ifModifiedHeader);

				if (date.getTime() >= dateModified)
				{
					result = false;
				}
			}
		}
		catch (Exception e) 
		{
			result = true;
		}
		return result;
	}

	private long getDateModified(HttpServletRequest request) throws IOException
	{
		Long result;
		try
		{
			String file = request.getRequestURI();
			result = lastModifiedDates.get(file);
			if (result == null)
			{
				InputStream stream = filterConfig.getServletContext().getResourceAsStream(file);
				if (stream != null)
				{
					String content = StreamUtils.readAsUTF8(stream);
					int indexStart = content.indexOf("# Build Time [");
					int indexEnd = content.indexOf("]", indexStart);
					if (indexStart > 0 && indexEnd > 0)
					{
						String dateStr = content.substring(indexStart+14, indexEnd);
						result = Long.parseLong(dateStr);
						lastModifiedDates.put(file, result);
					}
					else
					{
						result = 0l;
					}
				}
				else
				{
					result = 0l;
				}
			}
		}
		catch (Exception e) 
		{
			result = 0l;
		}

		return result;
	}

	private void sendRequestedFile(FilterChain chain, HttpServletRequest request, HttpServletResponse response, long dateModified) 
	throws IOException, ServletException
	{
		String ae = request.getHeader(HttpHeaderNames.ACCEPT_ENCODING);
		if (ae != null && ae.indexOf("gzip") != -1) 
		{        
			response = new GZIPResponseWrapper(response);
		}
		response = new ResponseWrapper(response, request.getContextPath());

		response.setContentType("text/cache-manifest");
		response.setCharacterEncoding("UTF-8");
		CacheControl cache = new CacheControl();
		cache.setNoCache(true);
		response.addHeader(HttpHeaderNames.CACHE_CONTROL, CacheControlHeaderParser.toString(cache));
		response.addDateHeader(HttpHeaderNames.EXPIRES, System.currentTimeMillis());
		if (dateModified > 0)
		{
			response.addDateHeader(HttpHeaderNames.LAST_MODIFIED, dateModified);
		}
		chain.doFilter(request, response);
		((ResponseWrapper)response).finishResponse();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;
		this.startTime = new Date().getTime();
	}

	@Override
	public void destroy()
	{
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class ResponseWrapper extends HttpServletResponseWrapper
	{
		private PrintWriter fWriter; 
		private ModifiedOutputStream fOutputStream;
		private boolean fWriterReturned;
		private boolean fOutputStreamReturned;
		private final String context;

		public ResponseWrapper(HttpServletResponse response, String context) throws IOException
        {
	        super(response);
			this.context = context;
	        fOutputStream = new ModifiedOutputStream(response.getOutputStream());
	        fWriter = new PrintWriter(new OutputStreamWriter(fOutputStream, "UTF-8"));
        }
		
		public final ServletOutputStream getOutputStream() 
		{
			if ( fWriterReturned ) 
			{
				throw new IllegalStateException();
			}
			fOutputStreamReturned = true;
			return fOutputStream;
		}
		
		public final PrintWriter getWriter() 
		{
			if ( fOutputStreamReturned ) 
			{
				throw new IllegalStateException();
			}
			fWriterReturned = true;
			return fWriter;
		}

		@Override
		public void flushBuffer() throws IOException
		{
		    fOutputStream.flush();
		}
		
		public void finishResponse() 
		{
			try 
			{
				if (fWriter != null) 
				{
					fWriter.close();
				} 
				else 
				{
					if (fOutputStream != null) 
					{
						fOutputStream.close();
					}
				}
			} 
			catch (IOException e) 
			{
				//
			}
		}
		
		private byte[] modifyResponse(String input)
		{
			int indexContext = input.indexOf("/{context}");
			if (indexContext >= 0)
			{
				input = RegexpPatterns.REGEXP_CONTEXT.matcher(input).replaceAll(context);
//				input = input.replace("/{context}", this.context);
			}
			
			return input.getBytes(Charset.forName("UTF-8"));
		}

		private class ModifiedOutputStream extends ServletOutputStream 
		{
			private ServletOutputStream fServletOutputStream;
			private ByteArrayOutputStream fBuffer;
			private boolean fIsClosed = false;

			public ModifiedOutputStream(ServletOutputStream aOutputStream) 
			{
				fServletOutputStream = aOutputStream;
				fBuffer = new ByteArrayOutputStream();
			}
			
			public void write(int aByte) throws IOException
			{
				fBuffer.write(aByte) ;
				if (aByte == '\n')
				{
					processStream();
					fBuffer.reset();
				}
			}
			
			public void close() throws IOException 
			{
				if ( !fIsClosed )
				{
					processStream();
					fServletOutputStream.close();
					fIsClosed = true;
				}
			}
			
			public void flush() throws IOException 
			{
				if ( fBuffer.size() != 0 )
				{
					if ( !fIsClosed ) 
					{
						processStream();
						fBuffer.reset();
					}
				}
			}
			
			public void processStream() throws IOException 
			{
				fServletOutputStream.write(modifyResponse(fBuffer.toString()));
			}
		}
	}
}
