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
package org.cruxframework.crux.core.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.utils.FileUtils;

/**
 * A Bridge class for allow Generators to know the name of the module that
 * starts the generation process. Crux Generators need this information to
 * obtain better performance for method handlers in client side of applications.
 * <p>
 * When GWT generators and the application server run in different JVMs, the
 * only way to obtain these informations is using a bridge.
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxBridge
{
	private static CruxBridge instance = new CruxBridge();
	private static final Log logger = LogFactory.getLog(CruxBridge.class);

	private String screenRequested = null;
	private String outputCharset = null;

	private File screenRequestedFile;
	private File outputCharsetFile;
	private boolean singleVM = false;
	private String webinfClasses = null;
	private File webinfClassesFile;
	private String webinfLib = null;

	private File webinfLibFile;

	/**
	 * 
	 */
	private CruxBridge()
	{
	}
	
	public static void removeOldCompilationFiles()
	{
		File tempDirFile = FileUtils.getTempDirFile();
		if(!FileUtils.recursiveDelete(tempDirFile))
		{
			handleRemoveFolderError(tempDirFile);
		}
	}

	private static void handleRemoveFolderError(File tempDirFile) 
	{
		handleCriticalError("Temp files inside crux compilation folder [" +
				tempDirFile.getPath() +
				"] could not be deleted. Check if your SO is not blocking this operation.", new IOException());
	}

	public static void removeOldGWTCodeServerCompilationFiles()
	{
		File tempDirFile = FileUtils.getTempDirFile();
		if(!FileUtils.recursiveDelete(tempDirFile, "gwt-codeserver"))
		{
			handleRemoveFolderError(tempDirFile);
		}
	}
	
	/**
	 * Singleton method
	 * 
	 * @return
	 */
	public static CruxBridge getInstance()
	{
		return instance;
	}
	
	private static void handleCriticalError(String message, Exception e)
	{
		logger.error("CruxBridge critical error: " + message, e);
		//kills JVM
		System.exit(1);
	}

	/**
	 * Return the last page requested by client.
	 * 
	 * @return
	 */
	public String getLastPageRequested()
	{
		BufferedReader reader = null;
		try
		{
			if (singleVM)
			{
				return screenRequested;
			}
			else
			{
				checkScreenRequestedFile();
				try
				{
					reader = new BufferedReader(new FileReader(screenRequestedFile));
				} catch (FileNotFoundException e)
				{
					handleCriticalError("Error getting lastPageRequestedFile.", e);
				}
				return reader.readLine();
			}
		}
		catch (Exception e)
		{
			handleCriticalError("Error getting lastPageRequestedFile.", e);
			return null;
		}
		finally
		{
			closeReader(reader);
		}
	}

	/**
	 * Return the last page requested by client.
	 * 
	 * @return
	 */
	public String getOutputCharset()
	{
		BufferedReader reader = null;
		try
		{
			if (singleVM)
			{
				return outputCharset;
			}
			else
			{
				checkOutputCharsetFile();
				try
				{
					reader = new BufferedReader(new FileReader(outputCharsetFile));	
				} catch (FileNotFoundException e)
				{
					handleCriticalError("Error getting outputCharset.", e);
				}

				return reader.readLine();
			}
		}
		catch (Exception e)
		{
			handleCriticalError("Error getting outputCharset.", e);
			return null;
		}
		finally
		{
			closeReader(reader);
		}
	}

	/**
	 * Return the web-inf/classes URL .
	 * 
	 * @return
	 */
	public String getWebinfClasses()
	{
		BufferedReader reader = null;
		try
		{
			if (singleVM)
			{
				return webinfClasses;
			}
			else
			{
				checkWebinfClassesFile();
				try
				{
					reader = new BufferedReader(new FileReader(webinfClassesFile));
				} catch (FileNotFoundException e)
				{
					handleCriticalError("Error getting webinfClasses.", e);
				}
				return reader.readLine();
			}
		}
		catch (Exception e)
		{
			handleCriticalError("Error getting webinfClasses.", e);
			return null;
		}
		finally
		{
			closeReader(reader);
		}
	}

	/**
	 * Return the web-inf/lib URL.
	 * 
	 * @return
	 */
	public String getWebinfLib()
	{
		BufferedReader reader = null;
		try
		{
			if (singleVM)
			{
				return webinfLib;
			}
			else
			{
				checkWebinfLibFile();
				try
				{
					reader = new BufferedReader(new FileReader(webinfLibFile));
				} catch (FileNotFoundException e)
				{
					handleCriticalError("Error getting webinfLib.", e);
				}
				return reader.readLine();
			}
		}
		catch (Exception e)
		{
			handleCriticalError("Error getting webinfLib.", e);
			return null;
		}
		finally
		{
			closeReader(reader);
		}
	}

	/**
	 * @return
	 */
	public boolean isSingleVM()
	{
		return singleVM;
	}

	public void registerPageOutputCharset(String charset)
	{
		if(StringUtils.isEmpty(charset))
		{
			handleCriticalError("Error registering charset.", null);
		}
		
		PrintWriter writer;
		try
		{
			if (singleVM)
			{
				outputCharset = charset;
			}
			else
			{
				checkOutputCharsetFile();
				writer = new PrintWriter(outputCharsetFile);
				writer.println(charset);
				writer.close();
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error registering outputCharset.", e);
		}
	}

	/**
	 * Inform the name of the last page the client requested. This is used only
	 * in hosted mode of GWT, when we will have only the developer working on a
	 * page.
	 */
	public void registerLastPageRequested(String lastPage)
	{
		if(StringUtils.isEmpty(lastPage))
		{
			handleCriticalError("Error registering lastPage.", null);
		}
		
		PrintWriter writer;
		try
		{
			if (singleVM)
			{
				screenRequested = lastPage;
			}
			else
			{
				checkScreenRequestedFile();
				writer = new PrintWriter(screenRequestedFile);
				writer.println(lastPage);
				writer.close();
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error registering screen.", e);
		}
	}

	/**
	 * @param webinfClasses
	 */
	public void registerWebinfClasses(String webinfClasses)
	{
		if(StringUtils.isEmpty(webinfClasses))
		{
			handleCriticalError("Error registering webInfClasses.", null);
		}
		
		PrintWriter writer;
		try
		{
			if (singleVM)
			{
				this.webinfClasses = webinfClasses;
			}
			else
			{
				checkWebinfClassesFile();
				writer = new PrintWriter(webinfClassesFile);
				writer.println(webinfClasses);
				writer.close();
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error registering webinfClasses.", e);
		}
	}

	/**
	 * @param webinfLib
	 */
	public void registerWebinfLib(String webinfLib)
	{
		if(StringUtils.isEmpty(webinfLib))
		{
			handleCriticalError("Error registering webinfLib.", null);
		}
		
		PrintWriter writer;
		try
		{
			if (singleVM)
			{
				this.webinfLib = webinfLib;
			}
			else
			{
				checkWebinfLibFile();
				writer = new PrintWriter(webinfLibFile);
				writer.println(webinfLib);
				writer.close();
			}
		}
		catch (FileNotFoundException e)
		{
			logger.error("Error registering webinfLib.", e);
		}
	}

	/**
	 * @param singleVM
	 */
	public void setSingleVM(boolean singleVM)
	{
		this.singleVM = singleVM;
	}

	/**
	 * 
	 */
	private void checkOutputCharsetFile()
	{
		if (outputCharsetFile == null)
		{
			initializeOutputCharsetFile();
		}
	}

	/**
	 * 
	 */
	private void checkScreenRequestedFile()
	{
		if (screenRequestedFile == null)
		{
			initializeScreenRequestedFile();
		}
	}

	/**
	 * 
	 */
	private void checkWebinfClassesFile()
	{
		if (webinfClassesFile == null)
		{
			initializeWebinfClassesFile();
		}
	}

	/**
	 * 
	 */
	private void checkWebinfLibFile()
	{
		if (webinfLibFile == null)
		{
			initializeWebinfLibFile();
		}
	}

	/**
	 * 
	 * @param reader
	 */
	private void closeReader(BufferedReader reader)
	{
		try
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		catch (IOException e)
		{
			logger.error(e);
		}
	}

	/**
	 * 
	 */
	private synchronized void initializeOutputCharsetFile()
	{
		if (outputCharsetFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			outputCharsetFile = new File(tmpDir + "outputCharsetBridgeFile");
		}
	}

	/**
	 * 
	 */
	private synchronized void initializeScreenRequestedFile()
	{
		if (screenRequestedFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			screenRequestedFile = new File(tmpDir + "screenRequestedBridgeFile");
		}
	}

	/**
	 * 
	 */
	private synchronized void initializeWebinfClassesFile()
	{
		if (webinfClassesFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			webinfClassesFile = new File(tmpDir + "webinfClassesFile");
		}
	}

	/**
	 * 
	 */
	private synchronized void initializeWebinfLibFile()
	{
		if (webinfLibFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			webinfLibFile = new File(tmpDir + "webinfLibFile");
		}
	}

}
