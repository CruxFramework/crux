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
package org.cruxframework.crux.core.declarativeui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.CruxXmlPreProcessor;
import org.cruxframework.crux.core.declarativeui.XPathUtils;
import org.cruxframework.crux.core.rebind.crossdevice.Devices;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplatesPreProcessor implements CruxXmlPreProcessor
{
	private XPathExpression findTemplatesExpression;
	private XPathExpression findScreensExpression;
	private XPathExpression findViewsExpression;
	private XPathExpression findBodyExpression;
	private XPathExpression findCrossBrowserExpression;
	private XPathExpression templateAttributesExpression;
	
	private TemplateParser templateParser;
	
	/**
	 * 
	 */
	public TemplatesPreProcessor()
	{
		this.templateParser = new TemplateParser();
		XPath findPath = XPathUtils.getCruxPagesXPath();
		XPath htmlPath = XPathUtils.getHtmlXPath();
		try
		{
			findTemplatesExpression = findPath.compile(".//*[contains(namespace-uri(), 'http://www.cruxframework.org/templates/')]");
			findScreensExpression = findPath.compile("//c:screen");
			findViewsExpression = findPath.compile("//v:view");
			findBodyExpression = htmlPath.compile("//h:body");
			findCrossBrowserExpression = findPath.compile("//c:crossDevice");
			templateAttributesExpression = findPath.compile("//@*[contains(., 'X{')] | //text()[contains(., 'X{')]");

		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error initializing templates pre-processor.", e);
		}
	}
	
	/**
	 * 
	 * @param doc
	 * @return
	 */
	public Document preprocess(Document doc, String device)
	{
		if (doc == null)
		{
			return null;
		}
		Set<String> controllers = new HashSet<String>();
		Set<String> resources = new HashSet<String>();
		Set<String> dataSources = new HashSet<String>();
		Set<String> formatters = new HashSet<String>();
		
		Document result = preprocess(doc, device, controllers, resources, dataSources, formatters);
		updateScreenProperties(doc, controllers, resources, dataSources, formatters);
		return result;
	}
	
	/**
	 * 
	 * @param doc
	 * @param controllers
	 * @param resources
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 */
	private void updateScreenProperties(Document doc, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters)
	{
		try
		{
			NodeList nodes = (NodeList)findScreensExpression.evaluate(doc, XPathConstants.NODESET);
			Element screen = null;
			if (nodes.getLength() > 0)
			{
				screen = (Element)nodes.item(0);
			}
			else
			{
				nodes = (NodeList)findViewsExpression.evaluate(doc, XPathConstants.NODESET);
				if (nodes.getLength() > 0)
				{
					screen = (Element)nodes.item(0);
				}
				else
				{
					screen = doc.createElementNS("http://www.cruxframework.org/crux", "c:screen");
					NodeList bodyNodes = (NodeList)findBodyExpression.evaluate(doc, XPathConstants.NODESET);
					if (bodyNodes.getLength() > 0)
					{
						Element body = (Element)bodyNodes.item(0);
						body.appendChild(screen);
					}
				}
			}
			extractScreenPropertiesFromElement(screen, controllers, resources, dataSources, formatters);
			updateScreenProperty(screen, controllers, "useController");
			updateScreenProperty(screen, resources, "useResource");
			updateScreenProperty(screen, dataSources, "useDataSource");
			updateScreenProperty(screen, formatters, "useFormatter");
				
			
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
	}

	/**
	 * 
	 * @param screen
	 * @param properties
	 * @param property
	 */
	private void updateScreenProperty(Element screen, Set<String> properties, String property)
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (String propValue : properties)
		{
			if (!first)
			{
				str.append(",");
			}
			str.append(propValue);
			first = false;
		}
		if (str.length() > 0)
		{
			screen.setAttribute(property, str.toString());
		}
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private Document preprocess(Document doc, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters)
	{
		Element documentElement = doc.getDocumentElement();
		preprocessCrossBrowserTags(documentElement, device, controllers, dataSources, formatters);
		preprocess(documentElement, device, controllers, resources, dataSources, formatters, false);
		return doc;
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private void preprocess(Element root, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters, boolean allowInnerSections)
	{
		try
		{
			NodeList nodes = (NodeList)findTemplatesExpression.evaluate(root, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Element element = (Element)nodes.item(i);
				if (isAttached(element) && (allowInnerSections || !isAnInnerSection(element)))
				{
					preprocessTemplate(element, device, controllers, resources, dataSources, formatters);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
	}

	/**
	 * 
	 * @param documentElement
	 * @param device
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 */
	private void preprocessCrossBrowserTags(Element documentElement, String device, Set<String> controllers, Set<String> dataSources, Set<String> formatters)
    {
		try
		{
			NodeList childNodes = (NodeList)findCrossBrowserExpression.evaluate(documentElement, XPathConstants.NODESET);
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
				preprocessCrossBrowserTag(element, device, controllers, dataSources, formatters);
			}
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
    }
	
	/**
	 * 
	 * @param element
	 * @return
	 */
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

	/**
	 * 
	 * @param doc
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param element
	 */
	private void preprocessTemplate(Element element, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, 
			                Set<String> formatters)
	{
		Document doc = element.getOwnerDocument();
		String library = element.getNamespaceURI();
		library = library.substring(library.lastIndexOf('/')+1);
		Document template = Templates.getTemplate(library, element.getLocalName(), true);
		if (template == null)
		{
			throw new TemplateException("Template not found. Library: ["+library+"]. Template: ["+element.getLocalName()+"].");
		}
		template = preprocess(template, device, controllers, resources, dataSources, formatters);

		updateTemplateAttributes(element, template);
		updateTemplateChildren(element, device, template, controllers, resources, dataSources, formatters);

		Element templateElement = (Element) doc.importNode(template.getDocumentElement(), true);
		extractScreenPropertiesFromElement(templateElement, controllers, resources, dataSources, formatters);										

		replaceByChildren(element, templateElement);
	}

	/**
	 * 
	 * @param element
	 * @param device
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 */
	private void preprocessCrossBrowserTag(Element element, String device, Set<String> controllers, Set<String> dataSources, 
            Set<String> formatters)
	{
		List<Element> replacements = getCrossBrowserReplacements(element, device);
		if (replacements.size() == 0)
		{
			throw new TemplateException("No condition associated with device ["+device+"]");
		}
		NodeList nodes = element.getChildNodes(); 
		Node parentNode = element.getParentNode();
		
		for (int i=0; i< nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element child = (Element)node;
				
				if (!child.getLocalName().equals("conditions") || !child.getNamespaceURI().equals("http://www.cruxframework.org/crux"))
 				{
					updateCrossBrowserAttributes(child, replacements);
					element.removeChild(child);
					parentNode.insertBefore(child, element);
				}
			}
		}
		parentNode.removeChild(element);
	}

	/**
	 * 
	 * @param crossBrowserElement
	 * @param device
	 */
	private List<Element> getCrossBrowserReplacements(Element crossBrowserElement, String device)
    {
	    List<Element> result = new ArrayList<Element>();
		NodeList nodes = crossBrowserElement.getElementsByTagNameNS("http://www.cruxframework.org/crux", "conditions");
		
		Device[] supportedDevices = Devices.getDevicesForDevice(device);
		for (Device supportedDevice : supportedDevices)
        {
			for (int i=0; i< nodes.getLength(); i++)
			{
				Element child = (Element)nodes.item(i);
				NodeList conditions = child.getElementsByTagNameNS("http://www.cruxframework.org/crux", "condition");
				for (int j=0; j< conditions.getLength(); j++)
				{
					Element condition = (Element)conditions.item(j);
					String userAgentValue = condition.getAttribute("when");
					Device templateDevice = Device.valueOf(userAgentValue);
					if (templateDevice.equals(supportedDevice))
					{
						NodeList replacements = condition.getElementsByTagNameNS("http://www.cruxframework.org/crux", "parameter");
						for (int k=0; k< replacements.getLength(); k++)
						{
							Element replacement = (Element)replacements.item(k);
							result.add(replacement);
						}
						return result;
					}
				}
			}
        }
		
		return result;
    }
	
	/**
	 * 
	 * @param child
	 * @param replacements
	 */
	private void updateCrossBrowserAttributes(Element child, List<Element> replacements)
    {
		try
		{
			NodeList nodes = (NodeList)templateAttributesExpression.evaluate(child, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node attribute = nodes.item(i);
				String attrValue = attribute.getNodeValue();
				if (attrValue.contains("X{"))
				{
					applyCrossBrowserReplacement(attribute, replacements);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
    }
	
	/**
	 * 
	 * @param attribute
	 * @param replacements
	 */
	private void applyCrossBrowserReplacement(Node attribute, List<Element> replacements)
    {
		String attrValue = attribute.getNodeValue();
		Map<String, String> replace = new HashMap<String, String>();
		int indexStarParam = attrValue.indexOf("X{");
		while (indexStarParam >= 0)
		{
			attrValue = attrValue.substring(indexStarParam+2);
			int indexEndParam = attrValue.indexOf("}");
			if (indexEndParam >= 0)
			{
				String param = attrValue.substring(0, indexEndParam);
				String paramValue = getCrossBrowserReplacement(param, replacements);
				replace.put(param, paramValue);
				attrValue = attrValue.substring(indexEndParam+1);
				indexStarParam = attrValue.indexOf("X{");
			}
			else
			{
				break;
			}
		}
		
		Set<String> parameters = replace.keySet();
		if (parameters.size() > 0)
		{
			attrValue = attribute.getNodeValue();
			for (String key : parameters)
			{
				attrValue = attrValue.replace("X{"+key+"}", replace.get(key));
			}
			
			attribute.setNodeValue(attrValue);
		}
    }

	/**
	 * 
	 * @param name
	 * @param replacements
	 */
	private String getCrossBrowserReplacement(String name, List<Element> replacements)
    {
	    for (Element replacement : replacements)
        {
	        if (replacement.getLocalName().equals("parameter"))
	        {
	        	String nameAttribute = replacement.getAttribute("name");
	        	if (!StringUtils.isEmpty(nameAttribute) && nameAttribute.equals(name))
	        	{
	        		return replacement.getAttribute("value");
	        	}
	        }
        }
		throw new TemplateException("CrossDevice parameter ["+name+"] not found.");
    }

	/**
	 * 
	 * @param template
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 */
	private void extractScreenPropertiesFromElement(Element template, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters)
	{
		extractScreenPropertyFromTemplate(controllers, template.getAttribute("useController"));
		extractScreenPropertyFromTemplate(resources, template.getAttribute("useResource"));
		extractScreenPropertyFromTemplate(dataSources, template.getAttribute("useDataSource"));
		extractScreenPropertyFromTemplate(formatters, template.getAttribute("useFormatter"));
	}

	/**
	 * 
	 * @param properties
	 * @param property
	 */
	private void extractScreenPropertyFromTemplate(Set<String> properties, String property)
	{
		if (property != null)
		{
			String[] strs = RegexpPatterns.REGEXP_COMMA.split(property);
			for (String str : strs)
			{
				if (!StringUtils.isEmpty(str) && !str.contains("#{"))
				{
					properties.add(str);
				}
			}
		}
	}

	/**
	 * 
	 * @param replacementElement
	 * @param templateElement
	 */
	private void replaceByChildren(Node replacementElement, Node templateElement)
	{
		Node parentNode = replacementElement.getParentNode();
		Node refNode = replacementElement;
		
		List<Node> children = getChildren(templateElement);

		for (int i=children.size()-1; i>=0; i--)
		{
			Node node = children.get(i);
			refNode = parentNode.insertBefore(node, refNode);
		}
		parentNode.removeChild(replacementElement);
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	private List<Node> getChildren(Node element)
	{
		List<Node> children = new ArrayList<Node>();
		Node child = element.getFirstChild();
		while (child != null)
		{
			children.add(child);
			child = child.getNextSibling();
		}
		return children;
	}

	/**
	 * 
	 * @param elementFromElement
	 * @return
	 */
	private boolean isAnInnerSection(Element element)
	{
		String namespace = element.getNamespaceURI();
		if (namespace != null)
		{
			Element documentElement = element.getOwnerDocument().getDocumentElement();
			element = (Element) element.getParentNode();
			while (element != null && !element.equals(documentElement))
			{
				if (namespace.equals(element.getNamespaceURI()))
				{
					return true;
				}
				element = (Element) element.getParentNode();
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @param template
	 */
	private void updateTemplateAttributes(Element element, Document template)
	{
		Set<Node> parameters = templateParser.getParametersNodes(template);
		for (Node attributeNode : parameters)
		{
			Set<String> attributes = new HashSet<String>();
			String nodeValue = attributeNode.getNodeValue();
			templateParser.extractParameterNames(nodeValue, attributes);
			for (String attribute: attributes)
			{	
				String value = element.getAttribute(attribute);
				nodeValue = nodeValue.replace("#{"+attribute+"}", value);
			}
			attributeNode.setNodeValue(nodeValue);
		}
	}

	/**
	 * 
	 * @param element
	 * @param device
	 * @param template
	 * @param controllers
	 * @param resources
	 * @param dataSources
	 * @param formatters
	 */
	private void updateTemplateChildren(Element element, String device, Document template, Set<String> controllers, Set<String> resources, 
										Set<String> dataSources, Set<String> formatters)
	{
		Map<String, Node> sections = templateParser.getSectionElements(template);
		List<Node> children = getChildren(element);
		for (int i=children.size()-1; i>=0; i--)
		{
			Node section = children.get(i);
			if (section.getNodeType() == Node.ELEMENT_NODE)
			{
				String sectionName = section.getLocalName();
				Node templateNode = sections.get(sectionName);
				
				preprocess((Element)section, device, controllers, resources, dataSources, formatters, true);
				
				section = template.importNode(section, true);
				replaceByChildren(templateNode, section);
			}
		}
	}
}