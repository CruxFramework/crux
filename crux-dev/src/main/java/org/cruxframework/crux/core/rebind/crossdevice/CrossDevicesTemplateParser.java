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
package org.cruxframework.crux.core.rebind.crossdevice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.view.ViewProvider;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.w3c.dom.Document;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.dev.resource.Resource;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrossDevicesTemplateParser
{
	private ViewFactory viewFactory;
	private JClassType baseIntf;
	private GeneratorContext context;
	private Device device;
	private static DocumentBuilder documentBuilder;
	private static Lock builderLock = new ReentrantLock();
	
	public CrossDevicesTemplateParser(GeneratorContext context, JClassType baseIntf, Device device)
    {
		this.context = context;
		this.baseIntf = baseIntf;
		this.device = device;
		this.viewFactory = new ViewFactory(new ViewProvider.SimpleViewProvider());
		
		initializeDocumentBuilder();
    }

	private static void initializeDocumentBuilder()
    {
	    if (documentBuilder == null)
	    {
	    	builderLock.lock();
	    	try
	    	{
	    	    if (documentBuilder == null)
	    	    {
	    	    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    	    	documentBuilderFactory.setNamespaceAware(true);
	    	    	documentBuilder = documentBuilderFactory.newDocumentBuilder();
	    	    }
	    	}
	    	catch (ParserConfigurationException e)
	    	{
	    		throw new CrossDevicesException("Error creating XML Parser.", e);
	    	}
	    	finally
	    	{
		    	builderLock.unlock();
	    		
	    	}
	    }
    }

	/**
	 * 
	 * @return
	 */
	public String getTemplateController(View view, String deviceAdaptive, Device device)
    {
		Iterator<String> controllers = view.iterateControllers();
		String controllerName = null;
		if (controllers.hasNext())
		{
			controllerName = controllers.next();
			if (controllers.hasNext())
			{
				throw new CrossDevicesException("Cross device templates can not define more than one controller. Cross device widget["+deviceAdaptive+"]. Device ["+device.toString()+"]");
			}
		}
		if (StringUtils.isEmpty(controllerName))
		{
			throw new CrossDevicesException("Can not find the controller attribute for deviceAdaptive widget ["+deviceAdaptive+"]. Device ["+device.toString()+"]");
		}
		return controllerName;
    }
	
	/**
	 * 
	 * @param template
	 * @param qualifiedSourceName
	 * @param device
	 * @return
	 */
	public View getTemplateView(Document template, String deviceAdaptive, Device device)
    {
	    try
	    {
	    	return viewFactory.getView(deviceAdaptive, device.toString(), template, false);
	    }
	    catch (Exception e)
	    {
			throw new CrossDevicesException("Error retrieving metadata from template associated with the deviceAdaptive widget ["+deviceAdaptive+"]. Device ["+device.toString()+"]", e);
	    }
    }

	public Document getDeviceAdaptiveTemplate()
    {
		Templates templates = baseIntf.getAnnotation(Templates.class);
		Template template = getTemplateForDevice(templates);
		JPackage adaptiveDevicePackage = baseIntf.getPackage();
		
		String templateResource = "/"+(adaptiveDevicePackage!=null?adaptiveDevicePackage.getName().replaceAll("\\.", "/"):"")+"/"+template.name()+".xdevice.xml";
		Resource resource = context.getResourcesOracle().getResource(templateResource);
		if (resource != null)
		{
            try
            {
            	InputStream stream = resource.openContents();
            	try
            	{
            		return documentBuilder.parse(stream);
            	}
            	finally
            	{
            		if (stream != null)
            		{
            			try
            			{
            				stream.close();
            			}
            			catch (IOException e)
            			{
            				// do nothing
            			}
            		}
            	}
            }
            catch (Exception e)
            {
            	return null;
            }
		}
    	return null;
    }

	private Template getTemplateForDevice(Templates templates)
    {
		Template defaultTemplate = null;
		
		for (Template template : templates.value())
        {
	        if (template.device().equals(device))
	        {
	        	return template;
	        }
	        if (template.device().equals(Device.all))
	        {
	        	defaultTemplate = template;
	        }
        }
		
	    return defaultTemplate;
    }
}
