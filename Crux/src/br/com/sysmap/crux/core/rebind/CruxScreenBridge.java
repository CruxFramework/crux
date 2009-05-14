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

	//Woww :) Thanks to GWT different JVMs for generators, 
	//the only way to obtain these informations is using a bridge in file system
	private File screenRequestedBridgeFile;
	private File screenResolverBridgeFile;
	private File webBaseDirBridgeFile;
	
	private CruxScreenBridge() 
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\"))
		{
			tmpDir += File.separator;
		}
		
		screenRequestedBridgeFile = new File(tmpDir+"screenRequestedBridgeFile");
		screenRequestedBridgeFile.deleteOnExit();
		screenResolverBridgeFile = new File(tmpDir+"screenResolverBridgeFile");
		screenResolverBridgeFile.deleteOnExit();
		webBaseDirBridgeFile = new File(tmpDir+"webBaseDirBridgeFile");
		webBaseDirBridgeFile.deleteOnExit();
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
	 * Inform the name of resource resolver to use.
	 */
	public void registerScreenResourceResolver(String resourceResolver)
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(screenResolverBridgeFile);
			writer.println(resourceResolver);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringScreenResolver(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Return the screen resource resolver.
	 * @return
	 */
	public String getScreenResourceResolver() 
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(screenResolverBridgeFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.error(messages.screenBridgeErrorReadingScreenResolver(e.getLocalizedMessage()), e);
			return null;
		}
	}
	
	/** 
	 * Inform the web base dir.
	 */
	public void registerWebBaseDir(String webBaseDir)
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(webBaseDirBridgeFile);
			writer.println(webBaseDir);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.screenBridgeErrorRegisteringWebBaseDir(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Return the web base dir.
	 * @return
	 */
	public String getWebBaseDir() 
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(webBaseDirBridgeFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.error(messages.screenBridgeErrorReadingwebBaseDir(e.getLocalizedMessage()), e);
			return null;
		}
	}
}
