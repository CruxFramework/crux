package org.cruxframework.crux.core.server.rest.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectWriter;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.JsonUtil;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpResponse
{
	private static final Lock lock = new ReentrantLock();

	private HttpServletResponse response;
	private int status = 200;
	private HttpServletResponseHeaders outputHeaders;
	private static ObjectWriter exceptionDataWriter;

	public HttpResponse(HttpServletResponse response)
	{
		this.response = response;
		outputHeaders = new HttpServletResponseHeaders(response);
		initializeExceptionWriter();
	}

	public int getStatus()
	{
		return status;
	}

	void setStatus(int status)
	{
		this.status = status;
		this.response.setStatus(status);
	}

	public void sendEmptyResponse()
	{
		response.setContentLength(0);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	/**
	 * Flushes the response as a file.
	 * @param mimeType the file mimeType. 
	 * @param fileName the file name.
	 * @param fileContent the file content. 
	 * @throws IOException
	 */
	public void flushFileContent(String mimeType, String fileName, String fileContent) throws IOException
	{
		generateFileContent(mimeType, "UTF-8", fileName, fileContent);
		this.response.flushBuffer();
	}
	
	/**
	 * Flushes the response as a file.
	 * @param mimeType the file mimeType. 
	 * @param charset the file charset. 
	 * @param fileName the file name.
	 * @param fileContent the file content. 
	 * @throws IOException
	 */
	public void flushFileContent(String mimeType, String charset, String fileName, String fileContent) throws IOException
	{
		generateFileContent(mimeType, charset, fileName, fileContent);
		this.response.flushBuffer();
	}

	private void generateFileContent(String mimeType, String charset, String fileName, String fileContent) throws IOException 
	{
		this.response.setContentType(mimeType + "; " + "charset=" + charset);
		this.response.setHeader(HttpHeaderNames.CONTENT_DISPOSITION, "inline; filename=" + fileName);
		
		PrintWriter writer = this.response.getWriter();
		writer.println(fileContent);
		writer.close();
	}
	
	public HttpServletResponseHeaders getOutputHeaders()
	{
		return outputHeaders;
	}

	public OutputStream getOutputStream() throws IOException
	{
		return response.getOutputStream();
	}

	public void sendError(int status, String message) throws IOException
	{
		response.setStatus(status);
		if (message!= null)
		{
			byte[] responseBytes = message.getBytes("UTF-8");
			response.setContentLength(responseBytes.length);
			outputHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, new MediaType("text", "plain", "UTF-8"));
			response.getOutputStream().write(responseBytes);
			response.flushBuffer();
		}
	}
	
	public void sendException(int status, String message) throws IOException
	{
		sendError(status, serializeException(message));
	}

	public static String serializeException(String message) throws IOException
	{
		ExceptionData exceptionData = new ExceptionData(message);
		return exceptionDataWriter.writeValueAsString(exceptionData);
	}

	public boolean isCommitted()
	{
		return response.isCommitted();
	}

	public void reset()
	{
		response.reset();
		outputHeaders = new HttpServletResponseHeaders(response);
	}

	public void flushBuffer() throws IOException
	{
		response.flushBuffer();
	}

	void setContentLength(int length)
    {
		response.setContentLength(length);
    }
	
	static class ExceptionData
	{
		private String message;

		public ExceptionData(String message)
		{
			this.message = message;
		}

		public String getMessage()
        {
        	return message;
        }
	}

	private void initializeExceptionWriter()
    {
	    if (exceptionDataWriter == null)
		{
			lock.lock();
			try
			{
				if (exceptionDataWriter == null)
				{
					exceptionDataWriter = JsonUtil.createWriter(ExceptionData.class);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
    }
}