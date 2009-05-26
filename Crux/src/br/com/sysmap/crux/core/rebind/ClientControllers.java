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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.event.annotation.Controller;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;

/**
 * Maps all client Handlers
 * @author Thiago Bustamante
 *
 */
public class ClientControllers 
{
	private static final Log logger = LogFactory.getLog(ClientControllers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Class<?>> clientHandlers;
	
	public static void initialize(URL[] urls)
	{
		if (clientHandlers != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (clientHandlers != null)
			{
				return;
			}
			
			initializeClientHandlers(urls);
		}
		finally
		{
			lock.unlock();
		}
	}

	protected static void initializeClientHandlers(URL[] urls)
	{
		clientHandlers = new HashMap<String, Class<?>>();
		Set<String> controllerNames =  ClassScanner.getInstance(urls).searchClassesByAnnotation(Controller.class);
		if (controllerNames != null)
		{
			for (String controller : controllerNames) 
			{
				try 
				{
					Class<?> controllerClass = Class.forName(controller);
					Controller annot = controllerClass.getAnnotation(Controller.class);
					clientHandlers.put(annot.value(), controllerClass);
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.clientHandlersHandlerInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}
	
	public static Class<?> getClientHandler(String name)
	{
		if (clientHandlers == null)
		{
			initialize(ScannerURLS.getURLsForSearch());
		}
		return clientHandlers.get(name);
	}
}
