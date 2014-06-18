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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplateParser
{
	private XPathExpression templateAttributesExpression;
	private XPathExpression templateChildrenNameExpression;
	private XPathExpression templateChildrenExpression;
	
	/**
	 * 
	 */
	public TemplateParser()
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath findTemplates = factory.newXPath();
		findTemplates.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				return "http://www.cruxframework.org/templates";
			}

			public String getPrefix(String namespaceURI)
			{
				return "t";
			}

			public Iterator<?> getPrefixes(String namespaceURI)
			{
				List<String> prefixes = new ArrayList<String>();
				prefixes.add("t");

				return prefixes.iterator();
			}
		});
		try
		{
			templateAttributesExpression = findTemplates.compile("//@*[contains(., '#{')] | //text()[contains(., '#{')]");
			templateChildrenNameExpression = findTemplates.compile("//t:section/@name");
			templateChildrenExpression = findTemplates.compile("//t:section");
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error initializing template parser.", e);
		}
	}
	
	/**
	 * 
	 * @param template
	 */
	public Set<String> getParameters(Document template)
	{
		Set<Node> parametersNodes = getParametersNodes(template);
		Set<String> attributes = new HashSet<String>();		
		for (Node node : parametersNodes)
		{
			String attrValue = node.getNodeValue();
			if (attrValue.contains("#{"))
			{
				extractParameterNames(attrValue, attributes);
			}
		}
		return attributes;
	}
	
	/**
	 * 
	 * @param template
	 */
	public Set<Node> getParametersNodes(Document template)
	{
		try
		{
			NodeList nodes = (NodeList)templateAttributesExpression.evaluate(template, XPathConstants.NODESET);
			Set<Node> attributes = new HashSet<Node>();
			
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node attribute = nodes.item(i);
				String attrValue = attribute.getNodeValue();
				if (attrValue.contains("#{"))
				{
					attributes.add(attribute);
				}
			}
			return attributes;
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error generating attributes for template ["+template.getLocalName()+"].", e);
		}
	}

	/**
	 * 
	 * @param template
	 * @return
	 */
	public Set<String> getSections(Document template)
	{
		try
		{
			NodeList sections = (NodeList) templateChildrenNameExpression.evaluate(template.getDocumentElement(), XPathConstants.NODESET); 
			Set<String> children = new HashSet<String>();
			if (sections != null && sections.getLength() > 0)
			{
				for (int i=0; i< sections.getLength(); i++)
				{
					Node section = sections.item(i);
					String sectionName = section.getNodeValue();
					children.add(sectionName);
				}
			}
			return children;
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error generating children for template ["+template.getLocalName()+"]", e);
		}
	}
	
	/**
	 * 
	 * @param template
	 * @return
	 */
	public Map<String, Node> getSectionElements(Document template)
	{
		try
		{
			NodeList sections = (NodeList) templateChildrenExpression.evaluate(template.getDocumentElement(), XPathConstants.NODESET); 
			Map<String, Node> children = new HashMap<String, Node>();
			if (sections != null && sections.getLength() > 0)
			{
				for (int i=0; i< sections.getLength(); i++)
				{
					Node section = sections.item(i);
					String sectionName = ((Element)section).getAttribute("name");
					children.put(sectionName, section);
				}
			}
			return children;
		}
		catch (XPathExpressionException e)
		{
			throw new TemplateException("Error generating children for template ["+template.getLocalName()+"]", e);
		}
	}

	/**
	 * 
	 * @param attrValue
	 * @param attributes
	 */
	public void extractParameterNames(String attrValue, Set<String> attributes)
	{
		int indexStarParam = attrValue.indexOf("#{");
		while (indexStarParam >= 0)
		{
			attrValue = attrValue.substring(indexStarParam+2);
			int indexEndParam = attrValue.indexOf("}");
			if (indexEndParam >= 0)
			{
				String param = attrValue.substring(0, indexEndParam);
				attributes.add(param);
				attrValue = attrValue.substring(indexEndParam+1);
				indexStarParam = attrValue.indexOf("#{");
			}
			else
			{
				break;
			}
		}
	}
}
