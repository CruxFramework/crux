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
	
	/**
	 * 
	 */
	public TemplatesPreProcessor()
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath findTemplatespath = factory.newXPath();
		try
		{
			findTemplatesExpression = findTemplatespath.compile("//*[contains(namespace-uri(), 'http://www.sysmap.com.br/crux/templates/')]");
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
				Node parentNode = element.getParentNode();
				String library = element.getAttribute("library");
				Document template = preprocess(Templates.getTemplate(library, element.getLocalName()));		
				parentNode.replaceChild(template.getDocumentElement(), element);
			}
		}
		catch (XPathExpressionException e)
		{
			log.error(messages.templatesPreProcessorError());
			throw new TemplateException(e.getLocalizedMessage(), e);
		}
		
		return doc;
	}
}