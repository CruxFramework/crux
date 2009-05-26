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
package br.com.sysmap.crux.core.server.dispatch;

import java.net.URL;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;

/**
 * Class for retrieve the controller class, based on the remote interface it implements
 * @author Thiago Bustamante
 */
public class Controllers 
{
	private static final Log logger = LogFactory.getLog(Controllers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static URL[] urls;
	
	/**
	 * Initialise the Controllers factory
	 * @param urls
	 */
	public static void initialize(URL[] urls)
	{
		Controllers.urls = urls;
	}
	
	/**
	 * Return the controller that implements the interface informed.
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getController(String interfaceName)
	{
		if (Controllers.urls == null)
		{
			initialize(ScannerURLS.getURLsForSearch());
		}
		try 
		{
			Set<String> controllerNames =  ClassScanner.getInstance(urls).searchClassesByInterface(Class.forName(interfaceName));
			if (controllerNames != null)
			{
				for (String controller : controllerNames) 
				{
					Class<?> controllerClass = Class.forName(controller);
					return controllerClass;
				}
			}
		} 
		catch (ClassNotFoundException e) 
		{
			logger.error(messages.controllersInitializeError(e.getLocalizedMessage()),e);
		}
		return null;
	}
}
