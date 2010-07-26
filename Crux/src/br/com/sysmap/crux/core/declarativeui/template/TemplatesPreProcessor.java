/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.declarativeui.template;

import java.util.ArrayList;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.declarativeui.CruxToHtmlTransformer;
import br.com.sysmap.crux.core.declarativeui.CruxXmlPreProcessor;
import br.com.sysmap.crux.core.declarativeui.DeclarativeUIMessages;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplatesPreProcessor implements CruxXmlPreProcessor
{
	private XPathExpression findTemplatesExpression;
	private XPathExpression findScreensExpression;
	private static DeclarativeUIMessages messages = (DeclarativeUIMessages)MessagesFactory.getMessages(DeclarativeUIMessages.class);
	private static final Log log = LogFactory.getLog(CruxToHtmlTransformer.class);
	
	private TemplateParser templateParser;
	
	/**
	 * 
	 */
	public TemplatesPreProcessor()
	{
		this.templateParser = new TemplateParser();
		XPathFactory factory = XPathFactory.newInstance();
		XPath findPath = factory.newXPath();
		findPath.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				return "http://www.sysmap.com.br/crux";
			}

			public String getPrefix(String namespaceURI)
			{
				return "c";
			}

			public Iterator<?> getPrefixes(String namespaceURI)
			{
				List<String> prefixes = new ArrayList<String>();
				prefixes.add("c");

				return prefixes.iterator();
			}
		});
		try
		{
			findTemplatesExpression = findPath.compile(".//*[contains(namespace-uri(), 'http://www.sysmap.com.br/templates/')]");
			findScreensExpression = findPath.compile("//c:screen");
		}
		catch (XPathExpressionException e)
		{
			log.error(messages.templatesPreProcessorInitializingError());
			throw new TemplateException(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param doc
	 * @return
	 */
	public Document preprocess(Document doc)
	{
		if (doc == null)
		{
			return null;
		}
		Set<String> controllers = new HashSet<String>();
		Set<String> dataSources = new HashSet<String>();
		Set<String> formatters = new HashSet<String>();
		Set<String> serializables = new HashSet<String>();
		
		Document result = preprocess(doc, controllers, dataSources, formatters, serializables);
		updateScreenProperties(doc, controllers, dataSources, formatters, serializables);
		return result;
	}
	
	/**
	 * 
	 * @param doc
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 */
	private void updateScreenProperties(Document doc, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
	{
		try
		{
			NodeList nodes = (NodeList)findScreensExpression.evaluate(doc, XPathConstants.NODESET);
			if (nodes.getLength() > 0)
			{
				Element screen = (Element)nodes.item(0);
				extractScreenPropertiesFromElement(screen, controllers, dataSources, formatters, serializables);
				updateScreenProperty(screen, controllers, "useController");
				updateScreenProperty(screen, dataSources, "useDataSource");
				updateScreenProperty(screen, formatters, "useFormatter");
				updateScreenProperty(screen, serializables, "useSerializable");
			}
		}
		catch (XPathExpressionException e)
		{
			log.error(messages.templatesPreProcessorError());
			throw new TemplateException(e.getLocalizedMessage(), e);
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
	private Document preprocess(Document doc, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
	{
		preprocess(doc.getDocumentElement(), controllers, dataSources, formatters, serializables, false);
		return doc;
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	private void preprocess(Element root, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables, boolean allowInnerSections)
	{
		try
		{
			NodeList nodes = (NodeList)findTemplatesExpression.evaluate(root, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Element element = (Element)nodes.item(i);
				if (isAttached(element) && (allowInnerSections || !isAnInnerSection(element)))
				{
					preprocessTemplate(element, controllers, dataSources, formatters, serializables);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			log.error(messages.templatesPreProcessorError());
			throw new TemplateException(e.getLocalizedMessage(), e);
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
	 * @param serializables
	 * @param element
	 */
	private void preprocessTemplate(Element element, Set<String> controllers, Set<String> dataSources, 
			                Set<String> formatters, Set<String> serializables)
	{
		Document doc = element.getOwnerDocument();
		String library = element.getNamespaceURI();
		library = library.substring(library.lastIndexOf('/')+1);
		Document template = Templates.getTemplate(library, element.getLocalName(), true);
		if (template == null)
		{
			throw new TemplateException(messages.templatesPreProcessorTemplateNotFound(library, element.getLocalName()));
		}
		template = preprocess(template, controllers, dataSources, formatters, serializables);

		updateTemplateAttributes(element, template);
		updateTemplateChildren(element, template, controllers, dataSources, formatters, serializables);

		Element templateElement = (Element) doc.importNode(template.getDocumentElement(), true);
		extractScreenPropertiesFromElement(templateElement, controllers, dataSources, formatters, serializables);										

		replaceByChildren(element, templateElement);
	}

	/**
	 * 
	 * @param template
	 * @param controllers
	 * @param dataSources
	 * @param formatters
	 * @param serializables
	 */
	private void extractScreenPropertiesFromElement(Element template, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
	{
		extractScreenPropertyFromTemplate(controllers, template.getAttribute("useController"));
		extractScreenPropertyFromTemplate(dataSources, template.getAttribute("useDataSource"));
		extractScreenPropertyFromTemplate(formatters, template.getAttribute("useFormatter"));
		extractScreenPropertyFromTemplate(serializables, template.getAttribute("useSerializable"));
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
	 * @param template
	 */
	private void updateTemplateChildren(Element element, Document template, Set<String> controllers, Set<String> dataSources, Set<String> formatters, Set<String> serializables)
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
				
				preprocess((Element)section, controllers, dataSources, formatters, serializables, true);
				
				section = template.importNode(section, true);
				replaceByChildren(templateNode, section);
			}
		}
	}
}