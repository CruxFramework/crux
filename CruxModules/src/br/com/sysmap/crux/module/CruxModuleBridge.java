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
package br.com.sysmap.crux.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.utils.FileSystemUtils;


/**
 * A Bridge class for allow Generators to know the name of the module 
 * that starts the generation process. Crux Generators need this information
 * to obtain better performance for method handlers in client side of 
 * applications. 
 * 
 * @author Thiago
 *
 */
public class CruxModuleBridge 
{
	private static final Log logger = LogFactory.getLog(CruxModuleBridge.class);
	private static CruxModuleBridge instance = new CruxModuleBridge();
	private static CruxModuleMessages messages = MessagesFactory.getMessages(CruxModuleMessages.class);

	//Woww :) How GWT generators and the application server run in different JVMs, 
	//the only way to obtain these informations is using a bridge in file system.
	private File currentModuleBridgeFile;
	
	private CruxModuleBridge() 
	{
		String tmpDir = FileSystemUtils.getTempDir();
		
		currentModuleBridgeFile = new File(tmpDir+"currentModuleBridgeFile");
	}

	/**
	 * Singleton method
	 * @return
	 */
	public static CruxModuleBridge getInstance()
	{
		return instance;
	}
	
	/** 
	 */
	public void registerCurrentModule(String moduleName)
	{
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(currentModuleBridgeFile);
			writer.println(moduleName);
			writer.close();
		} 
		catch (FileNotFoundException e) 
		{
			logger.error(messages.moduleBridgeErrorRegisteringModule(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * @return
	 */
	public String getCurrentModule() 
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(currentModuleBridgeFile));
			return reader.readLine();
		} 
		catch (Exception e) 
		{
			logger.info(messages.moduleBridgeErrorReadingModule());
			return null;
		}
	}
}
