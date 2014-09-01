/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tool for stream manipulation
 * @author Gesse S. F. Dafe
 */
public class StreamUtils
{
	private static final Log log = LogFactory.getLog(StreamUtils.class);
	
	/**
	 * Reads an input stream and writes it to a byte array
	 * @param responseBodyAsStream
	 * @return
	 * @throws IOException 
	 */
	public static byte[] read(InputStream in) throws IOException
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int read = 0;
		byte[] buff = new byte[1024];
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		in.close();
		out.flush();
		out.close();
				
		return out.toByteArray();
    }
	
	/**
	 * Reads an input stream and writes it to a string, using the desired charset
	 * @param responseBodyAsStream
	 * @return
	 * @throws IOException 
	 */
	public static String read(InputStream in, String charset) throws IOException
    {
		return new String(read(in), charset);
    }
	
	/**
	 * Reads an input stream and writes it to a string, using UTF-8 charset
	 * @param responseBodyAsStream
	 * @return
	 * @throws IOException 
	 */
	public static String readAsUTF8(InputStream in) throws IOException
    {
		return new String(read(in), "UTF-8");
    }
	
	/**
	 * Writes the input stream to the output stream. Closes both if desired.
	 * @param responseBodyAsStream
	 * @return
	 * @throws IOException 
	 */
	public static void write(InputStream in, OutputStream out, boolean closeBoth) throws IOException
    {
		byte[] buff = new byte[1024];
		int read = 0;
		
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		
		out.flush();
	
		if(closeBoth)
		{
			safeCloseStreams(in, out);
		}
    }

	/**
	 * Writes the input stream to the output stream. Closes both if desired.
	 * @param responseBodyAsStream
	 * @return
	 * @throws IOException 
	 */
	public static void write(String in, OutputStream out, boolean closeBoth) throws IOException
    {
		ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));
		write(is, out, closeBoth);
    }
	
	/**
	 * Close streams without pain
	 * @param out
	 * @param in
	 */
	public static void safeCloseStreams(InputStream in, OutputStream out)
	{
		if(in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e) 
			{
				log.error(e.getMessage(), e);
			}
		}
		
		if(out != null)
		{
			try
			{
				out.close();
			}
			catch (Exception e) 
			{
				log.error(e.getMessage(), e);
			}
		}
	}
}