/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.utils.FileUtils;


/**
 * A Bridge class for allow Generators to know the name of the module 
 * that starts the generation process. Crux Generators need this information
 * to obtain better performance for method handlers in client side of 
 * applications. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxScreenBridge 
{
	private static CruxScreenBridge instance = new CruxScreenBridge();
	private static final Log logger = LogFactory.getLog(CruxScreenBridge.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	private String scanAllowedPackages = null;
	private File scanAllowedPackagesFile;
	private String scanIgnoredPackages = null;
	private File scanIgnoredPackagesFile;
	private String screenRequested = null;
	
	//How GWT generators and the application server run in different JVMs, 
	//the only way to obtain these informations is using a bridge.
	private File screenRequestedFile;
	private boolean singleVM = false;
	private String webinfClasses = null;
	private File webinfClassesFile;
	private String webinfLib = null;

	private File webinfLibFile;
	
	/**
	 * 
	 */
	private CruxScreenBridge() 
	{
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static CruxScreenBridge getInstance()
	{
		return instance;
	}
	
	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getLastPageRequested() 
	{
		try 
		{
			if (singleVM)
			{
				return screenRequested;
			}
			else
			{
				checkScreenRequestedFile();
				BufferedReader reader = new BufferedReader(new FileReader(screenRequestedFile));
				return reader.readLine();
			}
		} 
		catch (Exception e) 
		{
			logger.error(messages.screenBridgeErrorReadingScreenId(e.getLocalizedMessage()), e);
			return null;
		}
	}
	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getScanAllowedPackages() 
	{
		try 
		{
			if (singleVM)
			{
				return scanAllowedPackages;
			}
			else
			{
				checkScanAllowedPackagesFile();
				BufferedReader reader = new BufferedReader(new FileReader(scanAllowedPackagesFile));
				return reader.readLine();
			}
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingAllowedPackages(e.getLocalizedMessage()), e);
			return null;
		}
	}

	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getScanIgnoredPackages() 
	{
		try 
		{
			if (singleVM)
			{
				return scanIgnoredPackages;
			}
			else
			{
				checkScanIgnoredPackagesFile();
				BufferedReader reader = new BufferedReader(new FileReader(scanIgnoredPackagesFile));
				return reader.readLine();
			}
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingIgnoredPackages(e.getLocalizedMessage()), e);
			return null;
		}
	}
	
	/**
	 * Return the web-inf/classes URL .
	 * @return
	 */
	public String getWebinfClasses() 
	{
		try 
		{
			if (singleVM)
			{
				return webinfClasses;
			}
			else
			{
				checkWebinfClassesFile();
				BufferedReader reader = new BufferedReader(new FileReader(webinfClassesFile));
				return reader.readLine();
			}
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingWebinfClasses(e.getLocalizedMessage()), e);
			return null;
		}
	}

	/**
	 * Return the web-inf/lib URL.
	 * @return
	 */
	public String getWebinfLib() 
	{
		try 
		{
			if (singleVM)
			{
				return webinfLib;
			}
			else
			{
				checkWebinfLibFile();
				BufferedReader reader = new BufferedReader(new FileReader(webinfLibFile));
				return reader.readLine();
			}
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingWebinfLib(e.getLocalizedMessage()), e);
			return null;
		}
	}

	/**
	 * @return
	 */
	public boolean isSingleVM()
	{
		return singleVM;
	}
	
	/** 
	 * Inform the name of the last page the client requested. This is used
	 * only in hosted mode of GWT, when we will have only the developer
	 * working on a page.
	 */
	public void registerLastPageRequested(String lastPage)
	{
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
			logger.error(messages.screenBridgeErrorRegisteringScreen(e.getLocalizedMessage()), e);
		}
	}

	/**
	 * @param allowedPackages
	 */
	public void registerScanAllowedPackages(String allowedPackages)
	{
		PrintWriter writer;
		try 
		{
			if (singleVM)
			{
				scanAllowedPackages = allowedPackages;
			}
			else
			{
				checkScanAllowedPackagesFile();
				writer = new PrintWriter(scanAllowedPackagesFile);
				writer.println(allowedPackages);
				writer.close();
			}
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringAllowedPackages(e.getLocalizedMessage()), e);
		}
	}

	/**
	 * @param ignoredPackages
	 */
	public void registerScanIgnoredPackages(String ignoredPackages)
	{
		PrintWriter writer;
		try 
		{
			if (singleVM)
			{
				scanIgnoredPackages = ignoredPackages;
			}
			else
			{
				checkScanIgnoredPackagesFile();
				writer = new PrintWriter(scanIgnoredPackagesFile);
				writer.println(ignoredPackages);
				writer.close();
			}
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringIgnoredPackages(e.getLocalizedMessage()), e);
		}
	}

	/**
	 * @param webinfClasses
	 */
	public void registerWebinfClasses(String webinfClasses)
	{
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
			logger.error(messages.screenBridgeErrorRegisteringWebinfClasses(e.getLocalizedMessage()), e);
		}
	}
	
	
	/**
	 * @param webinfLib
	 */
	public void registerWebinfLib(String webinfLib)
	{
		PrintWriter writer;
		try 
		{
			if(singleVM)
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
			logger.error(messages.screenBridgeErrorRegisteringWebinfLib(e.getLocalizedMessage()), e);
		}
	}

	/**
	 * @param singleVM
	 */
	public void setSingleVM(boolean singleVM)
	{
		this.singleVM = singleVM;
	}

	private void checkScanAllowedPackagesFile()
    {
	    if (scanAllowedPackagesFile == null)
	    {
	    	initializeScanAllowedPackagesFile();
	    }
    }

	private void checkScanIgnoredPackagesFile()
    {
	    if (scanIgnoredPackagesFile == null)
	    {
	    	initializeScanIgnoredPackagesFile();
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
	 */
	private synchronized void initializeScanAllowedPackagesFile()
    {
		if (scanAllowedPackagesFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			scanAllowedPackagesFile = new File(tmpDir+"scanAllowedPackagesFile");
		}
    }

	/**
	 * 
	 */
	private synchronized void initializeScanIgnoredPackagesFile()
    {
		if (scanIgnoredPackagesFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			scanIgnoredPackagesFile = new File(tmpDir+"scanIgnoredPackagesFile");
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
			screenRequestedFile = new File(tmpDir+"screenRequestedBridgeFile");
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
			webinfClassesFile = new File(tmpDir+"webinfClassesFile");
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
			webinfLibFile = new File(tmpDir+"webinfLibFile");
		}
    }	
}
