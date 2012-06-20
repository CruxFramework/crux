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
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.declarativeui.CruxToHtmlTransformer;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrossDevicesTemplateParser
{
	private static CrossDevicesTemplateParser instance = new CrossDevicesTemplateParser();
	
	private XPathExpression templateControllerExpression;

	/**
	 * 
	 */
	private CrossDevicesTemplateParser()
    {		
		XPathFactory factory = XPathFactory.newInstance();
		XPath findTemplates = factory.newXPath();
		findTemplates.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				return "http://www.cruxframework.org/xdevice";
			}

			public String getPrefix(String namespaceURI)
			{
				return "x";
			}

			public Iterator<?> getPrefixes(String namespaceURI)
			{
				List<String> prefixes = new ArrayList<String>();
				prefixes.add("x");

				return prefixes.iterator();
			}
		});
		try
		{
			templateControllerExpression = findTemplates.compile("/x:xdevice/@controller");
		}
		catch (XPathExpressionException e)
		{
			throw new CrossDevicesException("Error initializing generator.", e);
		}
    }

	/**
	 * 
	 * @return
	 */
	public String getTemplateController(Document template, String deviceAdaptive, Device device)
    {
		try
		{
			NodeList nodes = (NodeList)templateControllerExpression.evaluate(template, XPathConstants.NODESET);
			if (nodes.getLength() > 0)
			{
				Node attribute = nodes.item(0);
				return attribute.getNodeValue();
			}
		}
		catch (XPathExpressionException e)
		{
			throw new CrossDevicesException("Error searching for controller associated with the deviceAdaptive widget ["+deviceAdaptive+"]. Device ["+device.toString()+"]", e);
		}
		throw new CrossDevicesException("Can not find the controller attribute for deviceAdaptive widget ["+deviceAdaptive+"]. Device ["+device.toString()+"]");
    }
	
	/**
	 * 
	 * @param template
	 * @param qualifiedSourceName
	 * @param device
	 * @return
	 */
	public JSONObject getTemplateMetadata(Document template, String deviceAdaptive, Device device)
    {
	    try
	    {
	    	String metaData = CruxToHtmlTransformer.extractWidgetsMetadata(template, true, true);
	    	return new JSONObject(metaData);
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
