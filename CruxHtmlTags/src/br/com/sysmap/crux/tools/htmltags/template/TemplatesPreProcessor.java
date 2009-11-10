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
package br.com.sysmap.crux.tools.htmltags.template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.tools.htmltags.CruxToHtmlTransformer;
import br.com.sysmap.crux.tools.htmltags.CruxXmlPreProcessor;
import br.com.sysmap.crux.tools.htmltags.HTMLTagsMessages;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class TemplatesPreProcessor implements CruxXmlPreProcessor
{
	private XPathExpression findTemplatesExpression;
	private static HTMLTagsMessages messages = (HTMLTagsMessages)MessagesFactory.getMessages(HTMLTagsMessages.class);
	private static final Log log = LogFactory.getLog(CruxToHtmlTransformer.class);
	
	private TemplateParser templateParser;
	
	/**
	 * 
	 */
	public TemplatesPreProcessor()
	{
		this.templateParser = new TemplateParser();
		XPathFactory factory = XPathFactory.newInstance();
		XPath findTemplatespath = factory.newXPath();
		try
		{
			findTemplatesExpression = findTemplatespath.compile("//*[contains(namespace-uri(), 'http://www.sysmap.com.br/templates/')]");
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
		try
		{
			NodeList nodes = (NodeList)findTemplatesExpression.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Element element = (Element)nodes.item(i);
				if (!isAnInnerSection(element))
				{
					String library = element.getNamespaceURI();
					library = library.substring(library.lastIndexOf('/')+1);
					Document template = preprocess(Templates.getTemplate(library, element.getLocalName(), true));

					updateTemplateAttributes(element, template);
					updateTemplateChildren(element, template);

					Element templateElement = template.getDocumentElement();
					templateElement = (Element) doc.importNode(templateElement, true);
					
					replaceByChildren(element, templateElement);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			log.error(messages.templatesPreProcessorError());
			throw new TemplateException(e.getLocalizedMessage(), e);
		}
		
		return doc;
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

		for (Node node : children)
		{
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
			while (!element.equals(documentElement))
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
		Set<Node> parameters = templateParser.getAttributeWithParameters(template);
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
	private void updateTemplateChildren(Element element, Document template)
	{
		Map<String, Node> sections = templateParser.getSectionElements(template);
		List<Node> children = getChildren(element);
		for (Node section : children)
		{
			if (section.getNodeType() == Node.ELEMENT_NODE)
			{
				String sectionName = section.getLocalName();
				Node templateNode = sections.get(sectionName);
				section = template.importNode(section, true);
				replaceByChildren(templateNode, section);
			}
		}
	}
}