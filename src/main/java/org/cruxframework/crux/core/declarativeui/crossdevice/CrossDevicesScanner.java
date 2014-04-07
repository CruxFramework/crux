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
package org.cruxframework.crux.core.declarativeui.crossdevice;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.w3c.dom.Document;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrossDevicesScanner extends AbstractScanner
{
	private static final CrossDevicesScanner instance = new CrossDevicesScanner();
	private DocumentBuilder documentBuilder;
	
	/**
	 * 
	 */
	private CrossDevicesScanner() 
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new CrossDevicesException("Error creating XML Parser.", e);
		}
	}

	public static Templates getChildTagTemplatesAnnotation(Class<?> templateClass)
	{
		Templates attributes = templateClass.getAnnotation(Templates.class);
		if (attributes == null)
		{
			if(templateClass.getGenericInterfaces() != null)
			{
				for(Type type : templateClass.getGenericInterfaces())
				{
					Class<?> rawType = ClassUtils.getRawType(type);
					if (!rawType.equals(DeviceAdaptive.class))
					{
						attributes = getChildTagTemplatesAnnotation(rawType);
					}
				}
			}
		}

		return attributes;
	}

	/**
	 * 
	 * @param urls
	 */
	public void scanArchives()
	{
		Set<String> deviceAdaptiveNames =  ClassScanner.searchClassesByInterface(DeviceAdaptive.class);
		if (deviceAdaptiveNames != null)
		{
			for (String deviceAdaptive : deviceAdaptiveNames) 
			{
				try 
				{
					Class<?> deviceAdaptiveClass = Class.forName(deviceAdaptive);
					if (deviceAdaptiveClass.isInterface() && !deviceAdaptiveClass.equals(DeviceAdaptive.class))
					{
						Templates templates = getChildTagTemplatesAnnotation(deviceAdaptiveClass);
						if (templates == null)
						{
							throw new CrossDevicesException("DeviceAdaptive widget ["+deviceAdaptive+"] does not declare any templates. Use the annotation @Views to add templates to this widget.");
						}

						for (Template template : templates.value())
						{
							try
							{
								Package adaptiveDevicePackage = deviceAdaptiveClass.getPackage();
								String templateResource = "/"+(adaptiveDevicePackage!=null?adaptiveDevicePackage.getName().replaceAll("\\.", "/"):"")+"/"+template.name()+".xdevice.xml";
								InputStream stream = deviceAdaptiveClass.getResourceAsStream(templateResource);
								Document templateDocument = documentBuilder.parse(stream);
								CrossDevices.registerTemplate(deviceAdaptiveClass.getCanonicalName(), template.device(), templateDocument);
							}
							catch (Exception e)
							{
								throw new CrossDevicesException("Error parsing cross device file: ["+template.name()+"], for DeviceAdaptive interface ["+deviceAdaptiveClass.getCanonicalName()+"].", e);
							}
						}
					}
				} 
				catch (ClassNotFoundException e) 
				{
					throw new CrossDevicesException("DeviceAdaptive widget ["+deviceAdaptive+"] not found on classpath.",e);
				}
				catch (Exception e)
				{
					throw new CrossDevicesException("Error initializing CrossDevicesScanner.", e);
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public static CrossDevicesScanner getInstance()
	{
		return instance;
	}

}
