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
@Deprecated
@Legacy(TemplatesPreProcessor.class)
public class TemplatesPreProcessorLegacy
{
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
		Set<String> serializables = new HashSet<String>();
		
		Document result = preprocess(doc, device, controllers, resources, dataSources, formatters, serializables);
		updateScreenProperties(doc, controllers, resources, dataSources, formatters, serializables);
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
	private void updateScreenProperties(Document doc, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
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
			extractScreenPropertiesFromElement(screen, controllers, resources, dataSources, formatters, serializables);
			updateScreenProperty(screen, controllers, "useController");
			updateScreenProperty(screen, resources, "useResource");
			updateScreenProperty(screen, dataSources, "useDataSource");
			updateScreenProperty(screen, formatters, "useFormatter");
			updateScreenProperty(screen, serializables, "useSerializable");
				
			
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private Document preprocess(Document doc, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
	{
		Element documentElement = doc.getDocumentElement();
		preprocessCrossBrowserTags(documentElement, device, controllers, dataSources, formatters, serializables);
		preprocess(documentElement, device, controllers, resources, dataSources, formatters, serializables, false);
		return doc;
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private void preprocess(Element root, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters, Set<String> serializables, boolean allowInnerSections)
	{
		try
		{
			NodeList nodes = (NodeList)findTemplatesExpression.evaluate(root, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Element element = (Element)nodes.item(i);
				if (isAttached(element) && (allowInnerSections || !isAnInnerSection(element)))
				{
					preprocessTemplate(element, device, controllers, resources, dataSources, formatters, serializables);
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
	 * @param serializables
	 */
	private void preprocessCrossBrowserTags(Element documentElement, String device, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
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
				preprocessCrossBrowserTag(element, device, controllers, dataSources, formatters, serializables);
			}
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error pre-processing templates.", e);
		}
    }
	
	/**
	 * 
	 * @param doc
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 * @param element
	 */
	private void preprocessTemplate(Element element, String device, Set<String> controllers, Set<String> resources, Set<String> dataSources, 
			                Set<String> formatters, Set<String> serializables)
	{
		Document doc = element.getOwnerDocument();
		String library = element.getNamespaceURI();
		library = library.substring(library.lastIndexOf('/')+1);
		Document template = Templates.getTemplate(library, element.getLocalName(), true);
		if (template == null)
		{
			throw new TemplateException("Template not found. Library: ["+library+"]. Template: ["+element.getLocalName()+"].");
		}
		template = preprocess(template, device, controllers, resources, dataSources, formatters, serializables);

		updateTemplateAttributes(element, template);
		updateTemplateChildren(element, device, template, controllers, resources, dataSources, formatters, serializables);

		Element templateElement = (Element) doc.importNode(template.getDocumentElement(), true);
		extractScreenPropertiesFromElement(templateElement, controllers, resources, dataSources, formatters, serializables);										

		replaceByChildren(element, templateElement);
	}

	/**
	 * 
	 * @param element
	 * @param device
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 */
	private void preprocessCrossBrowserTag(Element element, String device, Set<String> controllers, Set<String> dataSources, 
            Set<String> formatters, Set<String> serializables)
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
	 * @param template
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 */
	private void extractScreenPropertiesFromElement(Element template, Set<String> controllers, Set<String> resources, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
	{
		extractScreenPropertyFromTemplate(controllers, template.getAttribute("useController"));
		extractScreenPropertyFromTemplate(resources, template.getAttribute("useResource"));
		extractScreenPropertyFromTemplate(dataSources, template.getAttribute("useDataSource"));
		extractScreenPropertyFromTemplate(formatters, template.getAttribute("useFormatter"));
		extractScreenPropertyFromTemplate(serializables, template.getAttribute("useSerializable"));
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
	 * @param serializables
	 */
	private void updateTemplateChildren(Element element, String device, Document template, Set<String> controllers, Set<String> resources, 
										Set<String> dataSources, Set<String> formatters, Set<String> serializables)
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
				
				preprocess((Element)section, device, controllers, resources, dataSources, formatters, serializables, true);
				
				section = template.importNode(section, true);
				replaceByChildren(templateNode, section);
			}
		}
	}
}