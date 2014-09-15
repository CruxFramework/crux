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
package org.cruxframework.crux.tools.compile.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class BlockRemoveTask extends Task
{
	private File file;
	
	private String beginMarker;
	private String endMarker;
	
	@Override
	public void execute() throws BuildException
	{
		try
		{
			String content = read(file);
			int indexBegin = content.indexOf(beginMarker);
			int indexEnd = content.indexOf(endMarker);
			if (indexBegin >= 0 && indexEnd > indexBegin)
			{
				content = content.substring(0, indexBegin) + content.substring(indexEnd + endMarker.length());
				write(content, file);
			}
		}
		catch (Exception e)
		{
			log(e.getMessage());
			throw new BuildException(e.getMessage(), e);
		}
	}

	/**
	 * @param content 
	 * @param outputFile
	 * @throws IOException
	 */
	private void write(String content, File outputFile) throws IOException
	{
		FileOutputStream out = null;
		
		try
		{
			out = new FileOutputStream(outputFile);
			out.write(content.getBytes());
		}
		finally
		{
			if(out != null)
			{
				out.flush();
				out.close();
			}
		}
		
	}

	/**
	 * @param inputFile
	 * @return
	 * @throws IOException 
	 */
	private String read(File inputFile) throws IOException
	{
		FileInputStream in = null;
		
		try
		{
			ByteArrayOutputStream str = new ByteArrayOutputStream();
			
			in = new FileInputStream(inputFile);
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while((read = in.read(bytes)) > 0)
			{
				str.write(bytes, 0, read);
			}
			
			return str.toString();
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
		}
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * @param beginMarker the beginMarker to set
	 */
	public void setBeginMarker(String beginMarker)
	{
		this.beginMarker = beginMarker;
	}

	/**
	 * @param endMarker the endMarker to set
	 */
	public void setEndMarker(String endMarker)
	{
		this.endMarker = endMarker;
	}
}
