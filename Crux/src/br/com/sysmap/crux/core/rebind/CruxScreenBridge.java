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
 * @author Thiago
 *
 */
public class CruxScreenBridge 
{
	private static final Log logger = LogFactory.getLog(CruxScreenBridge.class);
	private static CruxScreenBridge instance = new CruxScreenBridge();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	//How GWT generators and the application server run in different JVMs, 
	//the only way to obtain these informations is using a bridge.
	private File screenRequestedBridgeFile;
	private File scanAllowedPackagesFile;
	private File scanIgnoredPackagesFile;
	private File webinfClassesFile;
	private File webinfLibFile;
	
	private CruxScreenBridge() 
	{
		String tmpDir = FileUtils.getTempDir();
		
		screenRequestedBridgeFile = new File(tmpDir+"screenRequestedBridgeFile");
		scanAllowedPackagesFile = new File(tmpDir+"scanAllowedPackagesFile");
		scanIgnoredPackagesFile = new File(tmpDir+"scanIgnoredPackagesFile");
		webinfClassesFile = new File(tmpDir+"webinfClassesFile");
		webinfLibFile = new File(tmpDir+"webinfLibFile");
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
	 * Inform the name of the last page the client requested. This is used
	 * only in hosted mode of GWT, when we will have only the developer
	 * working on a page.
	 */
	public void registerLastPageRequested(String lastPage)
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(screenRequestedBridgeFile);
			writer.println(lastPage);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringScreen(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Return the last page requested by client.
	 * @return
	 */
	public String getLastPageRequested() 
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(screenRequestedBridgeFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.error(messages.screenBridgeErrorReadingScreenId(e.getLocalizedMessage()), e);
			return null;
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
			writer = new PrintWriter(scanIgnoredPackagesFile);
			writer.println(ignoredPackages);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringIgnoredPackages(e.getLocalizedMessage()), e);
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
			BufferedReader reader = new BufferedReader(new FileReader(scanIgnoredPackagesFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingIgnoredPackages(e.getLocalizedMessage()), e);
			return null;
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
			writer = new PrintWriter(scanAllowedPackagesFile);
			writer.println(allowedPackages);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringAllowedPackages(e.getLocalizedMessage()), e);
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
			BufferedReader reader = new BufferedReader(new FileReader(scanAllowedPackagesFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingAllowedPackages(e.getLocalizedMessage()), e);
			return null;
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
			writer = new PrintWriter(webinfClassesFile);
			writer.println(webinfClasses);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringWebinfClasses(e.getLocalizedMessage()), e);
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
			BufferedReader reader = new BufferedReader(new FileReader(webinfClassesFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingWebinfClasses(e.getLocalizedMessage()), e);
			return null;
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
			writer = new PrintWriter(webinfLibFile);
			writer.println(webinfLib);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringWebinfLib(e.getLocalizedMessage()), e);
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
			BufferedReader reader = new BufferedReader(new FileReader(webinfLibFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.debug(messages.screenBridgeErrorReadingWebinfLib(e.getLocalizedMessage()), e);
			return null;
		}
	}	
}
