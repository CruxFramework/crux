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

import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.w3c.dom.Document;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrossDevicesTemplateParser
{
	private static CrossDevicesTemplateParser instance = new CrossDevicesTemplateParser();
	
	/**
	 * 
	 */
	private CrossDevicesTemplateParser()
    {		
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
	    	return ViewFactory.getInstance().getView(deviceAdaptive+"_"+device, template, false);
	    }
	    catch (Exception e)
	    {
			throw new CrossDevicesException("Error retrieving metadata from template associated with the deviceAdaptive widget ["+deviceAdaptive+"]. Device ["+device.toString()+"]", e);
	    }
    }

	/**
	 * 
	 * @return
	 */
	public static CrossDevicesTemplateParser getInstance() 
	{
		return instance;
	}
}
