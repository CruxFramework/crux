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
package br.com.sysmap.crux.core.rebind.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.client.ui.Widget;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.controller.WidgetController;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * Maps all controllers in a module.
 * @author Thiago Bustamante
 *
 */
public class ClientControllers 
{
	private static final Log logger = LogFactory.getLog(ClientControllers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> controllers;
	private static List<String> globalControllers;
	private static Map<String, Set<String>> widgetControllers;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (controllers != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (controllers != null)
			{
				return;
			}
			
			initializeControllers();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeControllers()
	{
		controllers = new HashMap<String, String>();
		globalControllers = new ArrayList<String>();
		widgetControllers = new HashMap<String, Set<String>>();
		
		Set<String> controllerNames =  ClassScanner.searchClassesByAnnotation(Controller.class);
		if (controllerNames != null)
		{
			for (String controller : controllerNames) 
			{
				try 
				{
					Class<?> controllerClass = Class.forName(controller);
					Controller annot = controllerClass.getAnnotation(Controller.class);
					if (controllers.containsKey(annot.value()))
					{
						throw new CruxGeneratorException(messages.controllersDuplicatedController(annot.value()));
					}
					
					controllers.put(annot.value(), controllerClass.getCanonicalName());
					if (controllerClass.getAnnotation(Global.class) != null)
					{
						globalControllers.add(annot.value());
					}
					initWidgetControllers(controllerClass, annot);
					Fragments.registerFragment(annot.fragment(), controllerClass);
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.controllersInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}

	/**
	 * @param controllerClass
	 * @param annot
	 */
	private static void initWidgetControllers(Class<?> controllerClass, Controller annot)
    {
	    WidgetController widgetControllerAnnot = controllerClass.getAnnotation(WidgetController.class);
	    if (widgetControllerAnnot != null)
	    {
	    	
	    	Class<? extends Widget>[] widgets = widgetControllerAnnot.value();
	    	for (Class<? extends Widget> widgetClass : widgets)
	        {
	    		String widgetType = WidgetConfig.getWidgetType(widgetClass);
	    		if (!StringUtils.isEmpty(widgetType))
	    		{
	    			Set<String> controllers = widgetControllers.get(widgetType);
	    			if (controllers == null)
	    			{
	    				controllers = new HashSet<String>();
	    				widgetControllers.put(widgetType, controllers);
	    			}
	    			controllers.add(annot.value());
	    		}
	        }
	    }
    }
	
	/**
	 * @param name
	 * @return
	 */
	public static String getController(String name)
	{
		if (controllers == null)
		{
			initialize();
		}
		return controllers.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateControllers()
	{
		if (controllers == null)
		{
			initialize();
		}
		return controllers.keySet().iterator();
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateGlobalControllers()
	{
		if (globalControllers == null)
		{
			initialize();
		}
		return globalControllers.iterator();
	}
	
	/**
	 * @param widgetClass
	 * @return
	 */
	public static Iterator<String> iterateWidgetControllers(String widgetType)
	{
		if (widgetControllers == null)
		{
			initialize();
		}
		Set<String> controllers = widgetControllers.get(widgetType);
		if (controllers == null)
		{
			return null;
		}
		return controllers.iterator();
	}
}
