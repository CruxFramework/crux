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
package org.cruxframework.crux.core.server.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Samuel Cardoso
 *
 */
public class LoggingErrorDAO 
{
	private static Logger logger = Logger.getLogger(LoggingErrorDAO.class.getName());
	private static File errorLogFile = null;
	
	public static boolean clear()
	{
		return getErrorLogFile().delete();
	}
	
	public static void overwrite(List<Throwable> throwables)
	{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try 
		{
			fout = new FileOutputStream(getErrorLogFile());
			logger.info("Saving exception at: " + getErrorLogFile().getAbsolutePath());
			oos = new ObjectOutputStream(fout);
			oos.writeObject(throwables);
		} catch(IOException e)
		{
			logger.log(Level.SEVERE, "Error to write log file.", e);
		} finally
		{
			try
			{
				fout.close();
				oos.close();
			} catch (Exception e)
			{
				logger.log(Level.SEVERE, "Error to close log file.", e); 
			}					
		}
	}
	
	public static void append(Throwable throwable)
	{
		List<Throwable> actualThrowables = read();
		
		if(actualThrowables == null)
		{
			actualThrowables = new ArrayList<Throwable>();
		}
		
		if(throwable == null)
		{
			return;
		}
		
		actualThrowables.add(throwable);
		
		overwrite(actualThrowables);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Throwable> read()
	{
		ObjectInputStream oos = null;
		FileInputStream fins = null;
		try
		{
			fins = new FileInputStream(getErrorLogFile());
			logger.info("Reading exception from: " + getErrorLogFile().getAbsolutePath());
			oos = new ObjectInputStream(fins);
			return (ArrayList<Throwable>) oos.readObject();
		} catch (FileNotFoundException e)
		{
			return null;
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, "Error to read log file.", e);
		} catch (ClassNotFoundException e) 
		{
			return null;
		} finally
		{
			try
			{
				fins.close();
				oos.close();
			} catch (Exception e)
			{
				logger.log(Level.SEVERE, "Error to close log file.", e);
			}
		}
		return null;
	}
	
	private static File getErrorLogFile()
	{
		if(errorLogFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			errorLogFile = new File(tmpDir+"deferredbindingexception");
		}
		
		return errorLogFile; 
	}
}
