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
package org.cruxframework.crux.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.utils.FileUtils;



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

	//Woww :) How GWT generators and the application server run in different JVMs, 
	//the only way to obtain these informations is using a bridge in file system.
	private File currentModuleBridgeFile;
	
	private CruxModuleBridge() 
	{
		String tmpDir = FileUtils.getTempDir();
		
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
			logger.error("Error registering current module.", e);
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
			logger.info("No module registered under the bridge module. Assuming the development module");
			return null;
		}
	}
}
