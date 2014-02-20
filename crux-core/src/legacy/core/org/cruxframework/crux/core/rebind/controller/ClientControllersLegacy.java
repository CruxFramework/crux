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
package org.cruxframework.crux.core.rebind.controller;

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
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.WidgetController;
import org.cruxframework.crux.core.client.controller.crossdoc.RequiresCrossDocumentSupport;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.server.scan.ClassScanner;

import com.google.gwt.user.client.ui.IsWidget;


/**
 * Maps all controllersCanonicalNames in a module.
 * @author Thiago Bustamante
 *
 */
@Legacy(value=ClientControllers.class)
public class ClientControllersLegacy 
{
	/**
	 * 
	 */
	protected static void initializeControllers()
	{
		controllersCanonicalNames = new HashMap<String, Map<String, String>>();
		controllersNames = new HashMap<String, Map<String, String>>();
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
					boolean validController = true;
					if (controllerClass.getAnnotation(RequiresCrossDocumentSupport.class) != null)
					{
						String enableCrossDocumentSupport = ConfigurationFactory.getConfigurations().enableCrossDocumentSupport();
						if (enableCrossDocumentSupport == null || !enableCrossDocumentSupport.equals("true"))
						{
							validController = false;
						}
						
					}
					if (validController)
					{
						Controller annot = controllerClass.getAnnotation(Controller.class);
						Device[] devices = annot.supportedDevices();
						String resourceKey = annot.value();
						if (devices == null || devices.length ==0)
						{
							addResource(controllerClass, resourceKey, Device.all);
						}
						else
						{
							for (Device device : devices)
                            {
								addResource(controllerClass, resourceKey, device);
                            }
						}
						if (controllerClass.getAnnotation(Global.class) != null)
						{
							globalControllers.add(annot.value());
						}
						initWidgetControllers(controllerClass, annot);
					}
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error("Error initializing client controller.",e);
				}
			}
		}
	}
}
