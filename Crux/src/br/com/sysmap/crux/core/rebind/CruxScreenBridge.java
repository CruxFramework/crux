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


	private File bridgeFile;
	
	private CruxScreenBridge() 
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\"))
		{
			tmpDir += File.separator;
		}
		
		bridgeFile = new File(tmpDir+"bridgeFile");
		bridgeFile.deleteOnExit();
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
			writer = new PrintWriter(bridgeFile);
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
			BufferedReader reader = new BufferedReader(new FileReader(bridgeFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.error(messages.screenBridgeErrorReadingScreenId(e.getLocalizedMessage()), e);
			return null;
		}
	}
	
}
