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
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrossDevices 
{
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(CrossDevices.class);
	private static Map<String, Set<Device>> registeredDevices = null;
	private static Map<String, Document> templates = null;
	

	/**
	 * 
	 * @return
	 */
	public static Set<String> getDeviceAdaptiveWidgets()
	{
		if (registeredDevices == null)
		{
			initialize();
		}
		
		return registeredDevices.keySet();
	}
	
	/**
	 * 
	 * @param deviceAdaptive
	 * @return
	 */
	public static Set<Device> getDeviceAdaptiveDevices(String deviceAdaptive)
	{
		if (registeredDevices == null)
		{
			initialize();
		}
		
		return registeredDevices.get(deviceAdaptive);
	}

	/**
	 * 
	 * @param deviceAdaptive
	 * @param device
	 * @return
	 */
	public static Document getDeviceAdaptiveTemplate(String deviceAdaptive, Device device)
	{
		return getDeviceAdaptiveTemplate(deviceAdaptive, device, false);
	}

	/**
	 * 
	 * @param deviceAdaptive
	 * @param device
	 * @return
	 */
	public static Document getDeviceAdaptiveTemplate(String deviceAdaptive, Device device, boolean clone)
	{
		if (templates == null)
		{
			initialize();
		}
		Document document = templates.get(deviceAdaptive+"_"+device.toString());
		if (document != null)
		{
			document = (Document) document.cloneNode(true);
		}
		return document;
	}
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (templates != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (templates != null)
			{
				return;
			}
			
			initializeTemplates();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	public static void restart()
	{
		templates = null;
		initialize();
	}
	
	/**
	 * 
	 */
	protected static void initializeTemplates()
	{
		templates = new HashMap<String, Document>();
		registeredDevices = new HashMap<String, Set<Device>>();
		logger.info("Searching for cross device files.");
		CrossDevicesScanner.getInstance().scanArchives();
	}

	/**
	 * @param parentElement
	 * @return
	 */
	static List<Element> extractChildrenElements(Element parentElement)
	{
		List<Element> result = new ArrayList<Element>();
		NodeList childNodes = parentElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node node = childNodes.item(i);
			switch (node.getNodeType())
			{
				case Node.COMMENT_NODE:
					//ignore node
				break;
				case Node.TEXT_NODE:
					Text textNode = (Text) node;
					if (textNode.getWholeText().trim().length() > 0)
					{
						return null;
					}
				break;
				case Node.ELEMENT_NODE:
					result.add((Element) node);
				break;
				default:
					return null;
			}
		}
		
		return result;
	}

	/**
	 * @param deviceAdaptive
	 * @param device
	 * @param template
	 */
	static void registerTemplate(String deviceAdaptive, Device device, Document template)
	{
		if (templates.containsKey(deviceAdaptive+"_"+device.toString()))
		{
			throw new CrossDevicesException("Duplicated cross device file found. Library: ["+deviceAdaptive+"]. Template: ["+device.toString()+"].");
		}
		
		if (!registeredDevices.containsKey(deviceAdaptive))
		{
			registeredDevices.put(deviceAdaptive, new HashSet<Device>());
		}
		registeredDevices.get(deviceAdaptive).add(device);
		
		templates.put(deviceAdaptive+"_"+device.toString(), template);
	}
}
