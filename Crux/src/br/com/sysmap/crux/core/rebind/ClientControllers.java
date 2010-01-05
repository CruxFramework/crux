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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

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
	private static List<String> globalClientHandlers;
	
	public static void initialize()
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
			
			initializeClientHandlers();
		}
		finally
		{
			lock.unlock();
		}
	}

	protected static void initializeClientHandlers()
	{
		clientHandlers = new HashMap<String, Class<?>>();
		globalClientHandlers = new ArrayList<String>();
		Set<String> controllerNames =  ClassScanner.searchClassesByAnnotation(Controller.class);
		if (controllerNames != null)
		{
			for (String controller : controllerNames) 
			{
				try 
				{
					Class<?> controllerClass = Class.forName(controller);
					Controller annot = controllerClass.getAnnotation(Controller.class);
					if (clientHandlers.containsKey(annot.value()))
					{
						throw new CruxGeneratorException(messages.clientHandlersDuplicatedController(annot.value()));
					}
					
					clientHandlers.put(annot.value(), controllerClass);
					if (controllerClass.getAnnotation(Global.class) != null)
					{
						globalClientHandlers.add(annot.value());
					}
					Fragments.registerFragment(annot.fragment(), controllerClass);
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
			initialize();
		}
		return clientHandlers.get(name);
	}
	
	public static Iterator<String> iterateGlobalClientHandler()
	{
		if (globalClientHandlers == null)
		{
			initialize();
		}
		return globalClientHandlers.iterator();
	}
	
}
