/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.declarativeui.conditional;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.CruxXmlPreProcessor;
import org.cruxframework.crux.core.declarativeui.XPathUtils;
import org.cruxframework.crux.core.declarativeui.template.TemplateException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Samuel Almeida Cardoso
 *
 */
public class IfDevicePreProcessor implements CruxXmlPreProcessor
{
	private static final String INPUT = "input";
	private static final String SIZE = "size";
	private XPathExpression findIfDefiveExpression;
	
	public IfDevicePreProcessor()
	{
		XPath findPath = XPathUtils.getCruxPagesXPath();
		try
		{
			findIfDefiveExpression = findPath.compile("//c:ifDevice");
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error initializing ifDevice pre-processor.", e);
		}
	}
	
	@Override
	public Document preprocess(Document doc, String device)
	{
		if (doc == null || device == null)
		{
			return doc;
		}
		
		Element documentElement = doc.getDocumentElement();
		preprocessIfDevice(documentElement, device);
		return doc;
	}

	private void preprocessIfDevice(Element documentElement, String device)
    {
		try
		{
			NodeList childNodes = (NodeList)findIfDefiveExpression.evaluate(documentElement, XPathConstants.NODESET);
			List<Element> elements = new ArrayList<Element>();
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				Element element = (Element)childNodes.item(i);
				if (isAttached(element))
				{
					elements.add(element);
				}
			}
			for (Element element: elements)
			{
				preprocessIfDefiveTag(element, device);
			}
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing ifDevice directive.", e);
		}
    }
	
	private boolean isAttached(Node element)
	{
		boolean attached = false;
		
		while (element.getParentNode() != null)
		{
			if (element.equals(element.getOwnerDocument().getDocumentElement()))
			{
				attached = true;
				break;
			}
			element = element.getParentNode();
		}
		
		return attached;
	}

	private void preprocessIfDefiveTag(Element element, String device)
	{
		NodeList nodes = element.getChildNodes(); 
		Node parentNode = element.getParentNode();
		
		if(!renderForThisDevice(element, device))
		{
			parentNode.removeChild(element);
			return;
		}
		
		for (int i=0; i< nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element)node;
				element.removeChild(child);
				parentNode.insertBefore(child, element);
			}
		}
		parentNode.removeChild(element);
	}

	private boolean renderForThisDevice(Element ifDeviceElement, String strDevice)
    {
	    Device device = Device.valueOf(strDevice);
	    Node excludesNode = getExcludesNode(ifDeviceElement);
	    boolean render = false;
	    
	    String strSize = ifDeviceElement.getAttribute(SIZE);
	    String strInput = ifDeviceElement.getAttribute(INPUT);
	    
		if((StringUtils.isEmpty(strSize) || device.getSize().toString().equals(strSize)))
		{
			render = true;
		} else
		{
			render = false; 
		}
		
		if(render && (StringUtils.isEmpty(strInput) || device.getInput().toString().equals(strInput)))
		{
			render = true;
		} else
		{
			render = false; 
		}

		if(render)
	    {
	    	render = renderBasedInExclusions(excludesNode, device.getSize().toString(), device.getInput().toString());
	    }
		
	    if(excludesNode != null)
	    {
	    	excludesNode.getParentNode().removeChild(excludesNode);
	    }
		
		return render;
    }

	private boolean renderBasedInExclusions(Node excludesNode, String compilationSize, String compilationInput) 
	{
		if(excludesNode == null)
		{
			return true;
		}
		
		NodeList childNodes = excludesNode.getChildNodes();
		
		for (int i=0; i< childNodes.getLength(); i++)
		{
			Node excludeNode = childNodes.item(i);
			if(excludeNode.getNodeType() == Node.ELEMENT_NODE)
			{
				String excludeSize = ((Element)excludeNode).getAttribute(SIZE);
				String excludeInput = ((Element)excludeNode).getAttribute(INPUT);
				 if(
					 (StringUtils.isEmpty(excludeSize) || excludeSize.equals(compilationSize))
					 &&
					 (StringUtils.isEmpty(excludeInput) || excludeInput.equals(compilationInput))
					)
				 {
					 return false;
				 }
			}
		}
		
		return true;
	}
	
	private Node getExcludesNode(Element ifDeviceElement) 
	{
		NodeList nodes = ifDeviceElement.getChildNodes(); 
	    for (int i=0; i< nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().contains("excludes"))
			{
				return node; 	
			}
		}
	    return null;
	}
}