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
package br.com.sysmap.crux.core.rebind.screen.config;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetConfig 
{
	private static Map<String, Class<? extends WidgetFactory<?>>> config = null;
	private static Map<String, Set<String>> registeredLibraries = null;
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Log logger = LogFactory.getLog(WidgetConfig.class);
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	public static void initialize()
	{
		if (config != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (config != null)
			{
				return;
			}
			
			initializeWidgetConfig();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void initializeWidgetConfig()
	{
		config = new HashMap<String, Class<? extends WidgetFactory<?>>>(100);
		registeredLibraries = new HashMap<String, Set<String>>();
		Set<String> factoriesNames =  ClassScanner.searchClassesByAnnotation(br.com.sysmap.crux.core.client.declarative.DeclarativeFactory.class);
		if (factoriesNames != null)
		{
			for (String name : factoriesNames) 
			{
				try 
				{
					Class<? extends WidgetFactory<?>> factoryClass = (Class<? extends WidgetFactory<?>>)Class.forName(name);
					br.com.sysmap.crux.core.client.declarative.DeclarativeFactory annot = 
						factoryClass.getAnnotation(br.com.sysmap.crux.core.client.declarative.DeclarativeFactory.class);
					if (!registeredLibraries.containsKey(annot.library()))
					{
						registeredLibraries.put(annot.library(), new HashSet<String>());
					}
					registeredLibraries.get(annot.library()).add(annot.id());
					String widgetType = annot.library() + "_" + annot.id();
					config.put(widgetType, factoryClass); 
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.widgetConfigInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
		if (logger.isInfoEnabled())
		{
			logger.info(messages.widgetCongigWidgetsRegistered());
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static Class<? extends WidgetFactory<?>> getClientClass(String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		return config.get(id);
	}

	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static Class<? extends WidgetFactory<?>> getClientClass(String library, String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		return config.get(library+"_"+id);
	}

	/**
	 * 
	 * @return
	 */
	public static Set<String> getRegisteredLibraries()
	{
		if (registeredLibraries == null)
		{
			initializeWidgetConfig();
		}
		
		return registeredLibraries.keySet();
	}

	/**
	 * 
	 * @param library
	 * @return
	 */
	public static Set<String> getRegisteredLibraryFactories(String library)
	{
		if (registeredLibraries == null)
		{
			initializeWidgetConfig();
		}
		
		return registeredLibraries.get(library);
	}
}
