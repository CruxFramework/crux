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
package org.cruxframework.crux.core.declarativeui;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.HTMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * Create the html page to be sent to browser. This page is created based on the 
 * equivalent .crux.xml page.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
class HTMLBuilder
{
	private static final String CRUX_CORE_NAMESPACE= "http://www.cruxframework.org/crux";
	private static DocumentBuilder documentBuilder;
	private static XPathExpression findCruxPagesBodyExpression;
	private static XPathExpression findHTMLHeadExpression;
	private static Set<String> htmlPanelContainers;
	private static final Log log = LogFactory.getLog(CruxToHtmlTransformer.class);
	private static Map<String, String> referenceWidgetsList;
	private static final String WIDGETS_NAMESPACE_PREFIX= "http://www.cruxframework.org/crux/";
	private static Set<String> widgetsSubTags;
	private static Set<String> hasInnerHTMLWidgetTags;
	private static Set<String> orphanWidgets;
	private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
	
	static 
	{
		try
        {
	        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	        builderFactory.setNamespaceAware(false);
	        builderFactory.setIgnoringComments(true);
	        
	        documentBuilder = builderFactory.newDocumentBuilder();
	        generateReferenceWidgetsList();
	        generateSpecialWidgetsList();

	        XPath htmlPath = XPathUtils.getHtmlXPath();
			findCruxPagesBodyExpression = htmlPath.compile("//h:body");
			findHTMLHeadExpression = htmlPath.compile("//h:head");
        }
        catch (Exception e)
        {
        	log.error("Error creating htmlBuilder.", e);
        }
	}
	private String cruxTagName;
	private final boolean escapeXML;
	private final boolean indentOutput;
	private final boolean generateWidgetsMetadata;

	private int jsIndentationLvl;
	private String screenId;

	/**
	 * Constructor.
	 * 
	 * @param escapeXML If true will escape all inner text nodes to ensure that the generated HTML will be parsed correctly by a XML parser.
	 * @param generateWidgetsMetadata If true generates a JSON metadata that represents the widgets structure on .crux.xml page. 
	 * @param indentOutput True makes the generated HTML be indented.
	 * @throws HTMLBuilderException
	 */
	public HTMLBuilder(boolean escapeXML, boolean generateWidgetsMetadata, boolean indentOutput) throws HTMLBuilderException
    {
		this.escapeXML = escapeXML;
		this.generateWidgetsMetadata = generateWidgetsMetadata;
		this.indentOutput = indentOutput;
    }
	
	/**
	 * That method maps all widgets that needs special handling from Crux.
	 * Panels that can contain HTML mixed with widgets as contents (like {@link HTMLPanel}) and widgets 
	 * that must not be attached to DOM must be handled	differentially. 
	 *    
	 * @return
	 * @throws HTMLBuilderException 
	 */
	private static void generateSpecialWidgetsList() throws HTMLBuilderException
	{
		htmlPanelContainers = new HashSet<String>();
		orphanWidgets = new HashSet<String>();
		Set<String> registeredLibraries = WidgetConfig.getRegisteredLibraries();
		for (String library : registeredLibraries)
		{
			Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(library);
			for (String widget : factories)
			{
				try
				{
					Class<?> clientClass = Class.forName(WidgetConfig.getClientClass(library, widget));
					DeclarativeFactory factory = clientClass.getAnnotation(DeclarativeFactory.class);
					if (factory.htmlContainer())
					{
						htmlPanelContainers.add(library+"_"+widget);				
					}
					if (!factory.attachToDOM())
					{
						orphanWidgets.add(library+"_"+widget);				
					}
				}
				catch (Exception e)
				{
					throw new HTMLBuilderException("Error creating XSD File: Error generating widgets reference list.", e);
				}
			}
		}
	}

	/**
	 * Some widgets can define tags with custom names that has its type as other widget. It can be handled properly
	 * and those situations are mapped by this method. 
	 * 
	 * @return
	 * @throws HTMLBuilderException 
	 */
	private static void generateReferenceWidgetsList() throws HTMLBuilderException
	{
		referenceWidgetsList = new HashMap<String, String>();
		widgetsSubTags = new HashSet<String>();
		hasInnerHTMLWidgetTags = new HashSet<String>();
		Set<String> registeredLibraries = WidgetConfig.getRegisteredLibraries();
		for (String library : registeredLibraries)
		{
			Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(library);
			for (String widget : factories)
			{
				try
				{
					Class<?> clientClass = Class.forName(WidgetConfig.getClientClass(library, widget));
					generateReferenceWidgetsListFromTagChildren(clientClass.getAnnotation(TagChildren.class), 
																		library, widget, new HashSet<String>());
				}
				catch (Exception e)
				{
					throw new HTMLBuilderException("Error creating XSD File: Error generating widgets reference list.", e);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param widgetList 
	 * @param tagChildren
	 * @param parentLibrary
	 * @throws HTMLBuilderException 
	 */
	private static void generateReferenceWidgetsListFromTagChildren(TagChildren tagChildren, 
																    String parentLibrary, String parentWidget, Set<String> added) throws HTMLBuilderException
	{
		if (tagChildren != null)
		{
			String parentPath;
			for (TagChild child : tagChildren.value())
			{
				Class<? extends WidgetChildProcessor<?>> processorClass = child.value();
				if (!added.contains(processorClass.getCanonicalName()))
				{
					parentPath = parentWidget;
					added.add(processorClass.getCanonicalName());
					TagConstraints childAttributes = ClassUtils.getChildTagConstraintsAnnotation(processorClass);
					if (childAttributes!= null)
					{
						if (!StringUtils.isEmpty(childAttributes.tagName()))
						{
							parentPath = parentWidget+"_"+childAttributes.tagName();
							if (WidgetCreator.class.isAssignableFrom(childAttributes.type()))
							{
								DeclarativeFactory declarativeFactory = childAttributes.type().getAnnotation(DeclarativeFactory.class);
								if (declarativeFactory != null)
								{
									referenceWidgetsList.put(parentLibrary+"_"+parentPath,
											declarativeFactory.library()+"_"+declarativeFactory.id());
								}
							}
							else
							{
								widgetsSubTags.add(parentLibrary+"_"+parentPath);							
							}
						}
						if (HTMLTag.class.isAssignableFrom(childAttributes.type()))
						{
							hasInnerHTMLWidgetTags.add(parentLibrary+"_"+parentPath);
						}
					}
					
					try
					{
						generateReferenceWidgetsListFromTagChildren(processorClass.getAnnotation(TagChildren.class), 
																	parentLibrary, parentPath, added);
					}
					catch (Exception e)
					{
						throw new HTMLBuilderException("Error creating XSD File: Error generating widgets list.", e);
					}
				}
			}
		}				
	}
	
	/**
	 * Generates the HTML page form the given .crux.xml page.
	 *
	 * @param screenId The id of the screen associated with the .crux.xml page. 
	 * @param cruxPageDocument a XML Document representing the .crux.xml page.
	 * @param out Where the generated HTML will be written.
	 * @throws HTMLBuilderException
	 */
	public void build(String screenId, Document cruxPageDocument, Writer out) throws HTMLBuilderException
	{
		try
        {
			Document htmlDocument = createHTMLDocument(cruxPageDocument);
			translateDocument(screenId, cruxPageDocument, htmlDocument);
	        write(htmlDocument, out);
        }
        catch (IOException e)
        {
        	throw new HTMLBuilderException(e.getMessage(), e);
        }
	}

	/**
	 * 
	 */
	private void clearCurrentWidget()
	{
		cruxTagName = "";
	}

	/**
	 * @param cruxPageDocument
	 * @return
	 */
	private Document createHTMLDocument(Document cruxPageDocument)
    {
	    Document htmlDocument;
		DocumentType doctype = cruxPageDocument.getDoctype();
		if (doctype != null)
		{
			DocumentType newDoctype =  documentBuilder.getDOMImplementation().createDocumentType(doctype.getName(), doctype.getPublicId(), doctype.getSystemId());
			htmlDocument = documentBuilder.getDOMImplementation().createDocument(XHTML_NAMESPACE, "html", newDoctype);
		}
		else
		{
			htmlDocument = documentBuilder.newDocument();
			Element cruxPageElement = cruxPageDocument.getDocumentElement();
			Node htmlElement = htmlDocument.importNode(cruxPageElement, false);
			htmlDocument.appendChild(htmlElement);
		}
	    return htmlDocument;
    }

	/**
	 * @param cruxPageInnerTag
	 * @param cruxArrayMetaData
	 * @throws HTMLBuilderException 
	 */
	private void generateCruxInnerMetaData(Element cruxPageInnerTag, StringBuilder cruxArrayMetaData) throws HTMLBuilderException
    {
		if (indentOutput)
		{
			writeIndentationSpaces(cruxArrayMetaData);
		}
		cruxArrayMetaData.append("{");
		
		String currentWidgetTag = getCurrentWidgetTag() ;
		if (isWidget(currentWidgetTag))
		{
			cruxArrayMetaData.append("\"_type\":\""+currentWidgetTag+"\"");
		}
		else
		{
			cruxArrayMetaData.append("\"_childTag\":\""+cruxPageInnerTag.getLocalName()+"\"");
		}
		
		if (allowInnerHTML(currentWidgetTag))
		{
			String innerHTML = getHTMLFromNode(cruxPageInnerTag);
			if (innerHTML.length() > 0)
			{
				cruxArrayMetaData.append(",\"_html\":\""+innerHTML+"\"");
			}
		}
		else
		{
			String innerText = getTextFromNode(cruxPageInnerTag);
			if (innerText.length() > 0)
			{
				cruxArrayMetaData.append(",\"_text\":\""+innerText+"\"");
			}
		}
		generateCruxMetaDataAttributes(cruxPageInnerTag, cruxArrayMetaData);
		NodeList childNodes = cruxPageInnerTag.getChildNodes();
		if (childNodes != null && childNodes.getLength() > 0)
		{
			cruxArrayMetaData.append(",\"_children\":[");
			indent();
			generateCruxMetaData(cruxPageInnerTag, cruxArrayMetaData);
			outdent();
			cruxArrayMetaData.append("]");
		}

		cruxArrayMetaData.append("}");
    }
	
	/**
	 * @param tagName
	 * @return
	 */
	private boolean allowInnerHTML(String tagName)
	{
		return hasInnerHTMLWidgetTags.contains(tagName);	
	}
	
	
	/**
	 * @param cruxPageBodyElement
	 * @param cruxArrayMetaData
	 * @throws HTMLBuilderException 
	 */
	public void generateCruxMetaData(Node cruxPageBodyElement, StringBuilder cruxArrayMetaData) throws HTMLBuilderException
    {
		NodeList childNodes = cruxPageBodyElement.getChildNodes();
		if (childNodes != null)
		{
			boolean needsComma = false;
			for (int i=0; i<childNodes.getLength(); i++)
			{
				Node child = childNodes.item(i);
				String namespaceURI = child.getNamespaceURI();
				String nodeName = child.getLocalName(); 
					
				if (namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE) && nodeName.equals("screen"))
				{
					if (needsComma)
					{
						cruxArrayMetaData.append(",");
					}
					generateCruxScreenMetaData((Element)child, cruxArrayMetaData);
					needsComma = true;
				}
				else if (namespaceURI != null && namespaceURI.startsWith(WIDGETS_NAMESPACE_PREFIX))
				{
					if (needsComma)
					{
						cruxArrayMetaData.append(",");
					}
					String widgetType = getCurrentWidgetTag(); 
					updateCurrentWidgetTag((Element)child);
					generateCruxInnerMetaData((Element)child, cruxArrayMetaData);
					setCurrentWidgetTag(widgetType);
					needsComma = true;
				}
				else
				{
					StringBuilder childrenMetaData = new StringBuilder();
					generateCruxMetaData(child, childrenMetaData);
					if (childrenMetaData.length() > 0)
					{
						if (needsComma)
						{
							cruxArrayMetaData.append(",");
						}
						cruxArrayMetaData.append(childrenMetaData);
						needsComma = true;
					}		
				}
			}
		}
    }

	/**
	 * @param cruxPageMetaData
	 * @param cruxArrayMetaData
	 */
	private void generateCruxMetaDataAttributes(Element cruxPageMetaData, StringBuilder cruxArrayMetaData)
	{
		NamedNodeMap attributes = cruxPageMetaData.getAttributes();
		if (attributes != null)
		{
			for (int i=0; i<attributes.getLength(); i++)
			{
				Node attribute = attributes.item(i);
				String attrName = attribute.getLocalName();
				if (attrName == null)
				{
					attrName = attribute.getNodeName();
				}
				String attrValue = attribute.getNodeValue();
				
				if (!StringUtils.isEmpty(attrValue))
				{
					cruxArrayMetaData.append(",");
					cruxArrayMetaData.append("\""+attrName+"\":");
					cruxArrayMetaData.append("\""+HTMLUtils.escapeJavascriptString(attrValue, escapeXML)+"\"");
				}
			}
		}
	}

	/**
	 * @param screenId 
	 * @param cruxPageBodyElement
	 * @param htmlElement
	 * @param htmlDocument
	 * @throws HTMLBuilderException 
	 */
	private void generateCruxMetaDataElement(Element cruxPageBodyElement, Element htmlElement, Document htmlDocument) throws HTMLBuilderException
    {
		ScreenFactory factory = ScreenFactory.getInstance();
		String screenModule = null;
		try
		{
			screenModule = factory.getScreenModule(htmlDocument);
			
		}
		catch (Exception e)
		{
			throw new HTMLBuilderException(e.getMessage(), e);
		}
			
		if (screenModule == null)
		{
			throw new HTMLBuilderException("No module declared on screen ["+screenId+"].");
		}
		try
		{
			String screenId = factory.getRelativeScreenId(this.screenId, screenModule);

			Element cruxMetaData = htmlDocument.createElement("script");
			cruxMetaData.setAttribute("id", "__CruxMetaDataTag_");		
			htmlElement.appendChild(cruxMetaData);

			StringBuilder cruxArrayMetaData = new StringBuilder();
			cruxArrayMetaData.append("[");
			generateCruxMetaData(cruxPageBodyElement, cruxArrayMetaData);
			cruxArrayMetaData.append("]");

			Text textNode = htmlDocument.createTextNode("function __CruxMetaData_(){return {");
			cruxMetaData.appendChild(textNode);

			if (generateWidgetsMetadata)
			{
				textNode = htmlDocument.createTextNode("\"elements\":"+cruxArrayMetaData.toString()+",");
				cruxMetaData.appendChild(textNode);
			}

			textNode = htmlDocument.createTextNode("\"id\":\""+screenModule+"/"+HTMLUtils.escapeJavascriptString(screenId, escapeXML)+"\"");
			cruxMetaData.appendChild(textNode);

			textNode = htmlDocument.createTextNode(",\"lazyDeps\":"+ new LazyWidgets(escapeXML).generateScreenLazyDeps(cruxArrayMetaData.toString()));
			cruxMetaData.appendChild(textNode);

			textNode = htmlDocument.createTextNode("}}");
			cruxMetaData.appendChild(textNode);
		}
		catch (Exception e)
		{
			throw new HTMLBuilderException(e.getMessage(), e);
		}
    }
	
	/**
	 * @param cruxPageScreen
	 * @param cruxArrayMetaData
	 * @throws HTMLBuilderException 
	 */
	private void generateCruxScreenMetaData(Element cruxPageScreen, StringBuilder cruxArrayMetaData) throws HTMLBuilderException
    {
		if (indentOutput)
		{
			writeIndentationSpaces(cruxArrayMetaData);
		}
		cruxArrayMetaData.append("{");
		cruxArrayMetaData.append("\"_type\":\"screen\"");
		
		generateCruxMetaDataAttributes(cruxPageScreen, cruxArrayMetaData);
		
		cruxArrayMetaData.append("}");
		StringBuilder childrenMetaData = new StringBuilder();
		generateCruxMetaData(cruxPageScreen, childrenMetaData);
		
		if (childrenMetaData.length() > 0)
		{
			cruxArrayMetaData.append(",");
			cruxArrayMetaData.append(childrenMetaData);
		}		
    }
	
	/**
	 * @param cruxPageDocument
	 * @return
	 * @throws HTMLBuilderException
	 */
	private Element getCruxPageBodyElement(Document cruxPageDocument) throws HTMLBuilderException
	{
		try
        {
	        NodeList bodyNodes = (NodeList)findCruxPagesBodyExpression.evaluate(cruxPageDocument, XPathConstants.NODESET);
	        if (bodyNodes.getLength() > 0)
	        {
	        	return (Element)bodyNodes.item(0);
	        }
	        return null;
        }
        catch (XPathExpressionException e)
        {
        	throw new HTMLBuilderException(e.getMessage(), e);
        }
	}
	
	/**
	 * @return
	 */
	private String getCurrentWidgetTag()
	{
		return cruxTagName;
	}

	/**
	 * @param htmlDocument
	 * @return
	 * @throws HTMLBuilderException
	 */
	private Element getHtmlHeadElement(Document htmlDocument) throws HTMLBuilderException
	{
		try
        {
	        NodeList headNodes = (NodeList)findHTMLHeadExpression.evaluate(htmlDocument, XPathConstants.NODESET);
	        if (headNodes.getLength() > 0)
	        {
	        	return (Element)headNodes.item(0);
	        }
	        Element headElement = htmlDocument.createElement("head");
	        Element bodyElement = getCruxPageBodyElement(htmlDocument);
	        if (bodyElement != null)
	        {
	        	htmlDocument.getDocumentElement().insertBefore(headElement, bodyElement);
	        	return headElement;
	        }
	        return null;
        }
        catch (XPathExpressionException e)
        {
        	throw new HTMLBuilderException(e.getMessage(), e);
        }
	}

	/**
	 * @param node
	 * @return
	 */
	private String getLibraryName(Node node)
	{
		String namespaceURI = node.getNamespaceURI();
		
		if (namespaceURI != null && namespaceURI.startsWith(WIDGETS_NAMESPACE_PREFIX))
		{
			return namespaceURI.substring(WIDGETS_NAMESPACE_PREFIX.length());
		}
		return null;
	}

	/**
	 * @param node
	 * @return
	 */
	private String getReferencedWidget(String tagName)
    {
	    return referenceWidgetsList.get(tagName);
    }

	/**
	 * @param node
	 * @return
	 */
	private String getTextFromNode(Node node)
	{
		StringBuilder text = new StringBuilder(); 
		
		NodeList children = node.getChildNodes();
		if (children != null)
		{
			for (int i=0; i<children.getLength(); i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE)
				{
					text.append(child.getNodeValue());
				}
			}
		}
		
		return HTMLUtils.escapeJavascriptString(text.toString().trim(), escapeXML);
	}
	
	/**
	 * @param node
	 * @return
	 * @throws HTMLBuilderException 
	 */
	private String getHTMLFromNode(Element elem) throws HTMLBuilderException
	{
		try
		{
			StringWriter innerHTML = new StringWriter(); 
			NodeList children = elem.getChildNodes();
			
			if (children != null)
			{
				for (int i=0; i<children.getLength(); i++)
				{
					Node child = children.item(i);
					HTMLUtils.write(child, innerHTML);
				}
			}
	        return HTMLUtils.escapeJavascriptString(innerHTML.toString(), escapeXML);
		}
		catch (IOException e)
		{
			throw new HTMLBuilderException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 */
	private void indent()
	{
		jsIndentationLvl++;
	}
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isHTMLChild(Node node)
	{
		Node parentNode = node.getParentNode();
		String namespaceURI = parentNode.getNamespaceURI();
		if (namespaceURI == null)
		{
			log.warn("The screen ["+this.screenId+"] contains elements that is not bound to any namespace. It can cause errors while translating to an HTML page.");
		}
		if (namespaceURI != null && namespaceURI.equals(XHTML_NAMESPACE) || isHtmlContainerWidget(parentNode))
		{
			return true;
		}
		if (parentNode instanceof Element && namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE) && parentNode.getLocalName().equals("screen"))
		{
			return isHTMLChild(parentNode);
		}
		
		return false;
	}
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isHtmlContainerWidget(Node node)
	{
		if (node instanceof Element)
		{
			return isHtmlContainerWidget(node.getLocalName(), getLibraryName(node));
		}
		return false;
	}
	
	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isHtmlContainerWidget(String localName, String libraryName)
	{
	    return htmlPanelContainers.contains(libraryName+"_"+localName);
	}
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isOrphanWidget(Node node)
	{
		if (node instanceof Element)
		{
			return isOrphanWidget(node.getLocalName(), getLibraryName(node));
		}
		return false;
	}

	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isOrphanWidget(String localName, String libraryName)
	{
	    return orphanWidgets.contains(libraryName+"_"+localName);
	}
	
	
	/**
	 * @param tagName
	 * @return
	 */
	private boolean isReferencedWidget(String tagName)
    {
	    return referenceWidgetsList.containsKey(tagName);
    }

	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isWidget(String tagName)
    {
	    return (WidgetConfig.getClientClass(tagName) != null);
    }
	
	/**
	 * @param cruxTagName
	 * @return
	 */
	private boolean isWidgetSubTag(String cruxTagName)
    {
		if (cruxTagName.indexOf('_') == cruxTagName.lastIndexOf('_'))
		{
			return false;
		}
		return widgetsSubTags.contains(cruxTagName);
    }
	
	/**
	 * 
	 */
	private void outdent()
	{
		jsIndentationLvl--;
	}

	/**
	 * @param cruxTagName
	 */
	private void setCurrentWidgetTag(String cruxTagName)
	{
		this.cruxTagName = cruxTagName;
	}

	/**
	 * 
	 * @param screenId
	 */
	private void setCurrentScreenId(String screenId)
	{
		this.screenId = screenId;
	}
	
	/**
	 * 
	 */
	private void clearScreenId()
	{
		this.screenId = null;
	}
	
	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateCruxCoreElements(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
	    String nodeName = cruxPageElement.getLocalName();
	    if (nodeName.equals("splashScreen"))
	    {
	    	translateSplashScreen(cruxPageElement, htmlElement, htmlDocument);
	    }
	    else if (nodeName.equals("screen"))
	    {
	    	translateDocument(cruxPageElement, htmlElement, htmlDocument, true);
	    }
    }
	
	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateCruxInnerTags(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
		boolean htmlContainerWidget = isHtmlContainerWidget(cruxPageElement);
		String currentWidgetTag = getCurrentWidgetTag();
		boolean notOrphanWidget = !isOrphanWidget(cruxPageElement);
		if (htmlContainerWidget || ((isWidget(currentWidgetTag)) && notOrphanWidget && isHTMLChild(cruxPageElement)))
		{
			Element widgetHolder;
			boolean hasSiblings = hasSiblingElements(cruxPageElement);
			if (!isOrphanRequiresResizeWidget(currentWidgetTag, cruxPageElement) && (hasSiblings || isCruxWidgetParent(htmlElement) || isBody(htmlElement)))
			{
				widgetHolder = htmlDocument.createElement("div");
				htmlElement.appendChild(widgetHolder);
			}
			else
			{
				widgetHolder = htmlElement;
			}
			
			widgetHolder.setAttribute("id", "_crux_"+cruxPageElement.getAttribute("id"));
			if (htmlContainerWidget)
			{
				String style = widgetHolder.getAttribute("style");
				widgetHolder.setAttribute("style", "display:none;"+(style==null?"":style));
			}
			translateDocument(cruxPageElement, widgetHolder, htmlDocument, htmlContainerWidget);
		}
		else
		{
			translateDocument(cruxPageElement, htmlElement, htmlDocument, false);
		}
    }

	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isOrphanRequiresResizeWidget(String tagName, Element cruxPageElement)
    {
	    Node parentNode = cruxPageElement.getParentNode(); 
		return (WidgetConfig.isRequiresResizeWidget(tagName) && isBody(parentNode));
    }

	/**
	 * 
	 * @param node
	 * @return
	 */
	private boolean isBody(Node node)
    {
	    return node.getLocalName().equalsIgnoreCase("body") && node.getNamespaceURI().equals(XHTML_NAMESPACE);
    }

	/**
	 * 
	 * @param htmlElement
	 * @return
	 */
	private boolean isCruxWidgetParent(Element htmlElement)
    {
	    String id = htmlElement.getAttribute("id");
	    return (id != null && id.startsWith("_crux_"));
    }
	
	/**
	 * 
	 * @param cruxPageElement
	 * @return
	 */
	private boolean hasSiblingElements(Element cruxPageElement)
	{
		Node sibling = cruxPageElement.getPreviousSibling();
		
		while (sibling != null)
		{
			if (isValidSibling(sibling))
			{
				return true;
			}
			
			sibling = sibling.getPreviousSibling();
		}

		sibling = cruxPageElement.getNextSibling();
		while (sibling != null)
		{
			if (isValidSibling(sibling))
			{
				return true;
			}
			
			sibling = sibling.getNextSibling();
		}
		return false;
    }

	/**
	 * 
	 * @param sibling
	 * @return
	 */
	private boolean isValidSibling(Node sibling)
    {
	    short nodeType = sibling.getNodeType();
		if (nodeType == Node.ELEMENT_NODE)
	    {
	    	return true;
	    }
	    else if (nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE || nodeType == Node.ENTITY_NODE)
	    {
	    	return sibling.getNodeValue().replaceAll("\\s+", "").length() > 0;
	    }
	    return false;
    }
	
	/**
	 * @param screenId 
	 * @param cruxPageDocument
	 * @param htmlDocument
	 * @throws HTMLBuilderException 
	 */
	private void translateDocument(String screenId, Document cruxPageDocument, Document htmlDocument) throws HTMLBuilderException
    {
		setCurrentScreenId(screenId);

		Element cruxPageElement = cruxPageDocument.getDocumentElement();
		Node htmlElement = htmlDocument.getDocumentElement();
		clearCurrentWidget();
		translateDocument(cruxPageElement, htmlElement, htmlDocument, true);
		clearCurrentWidget();
		generateCruxMetaDataElement(getCruxPageBodyElement(cruxPageDocument), getHtmlHeadElement(htmlDocument), htmlDocument);
		
		clearScreenId();
    }

	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateDocument(Node cruxPageElement, Node htmlElement, Document htmlDocument, boolean copyHtmlNodes)
    {
		NodeList childNodes = cruxPageElement.getChildNodes();
		if (childNodes != null)
		{
			for (int i=0; i<childNodes.getLength(); i++)
			{
				Node child = childNodes.item(i);
				String namespaceURI = child.getNamespaceURI();
				
				if (namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE))
				{
				    translateCruxCoreElements((Element)child, (Element)htmlElement, htmlDocument);
				}
				else if (namespaceURI != null && namespaceURI.startsWith(WIDGETS_NAMESPACE_PREFIX))
				{
					String widgetType = getCurrentWidgetTag(); 
					updateCurrentWidgetTag((Element)child);
					translateCruxInnerTags((Element)child, (Element)htmlElement, htmlDocument);
					setCurrentWidgetTag(widgetType);
				}
				else
				{
					Node htmlChild;
					if (copyHtmlNodes)
					{
						htmlChild = htmlDocument.importNode(child, false);
						htmlElement.appendChild(htmlChild);
					}
					else
					{
						htmlChild = htmlElement;
					}
					translateDocument(child, htmlChild, htmlDocument, copyHtmlNodes);
				}
			}
		}
    }
	
	/**
	 * @param cruxPageNode
	 * @param htmlNode
	 * @param htmlDocument
	 */
	private void translateSplashScreen(Element cruxPageNode, Element htmlNode, Document htmlDocument)
	{
		Element splashScreen = htmlDocument.createElement("div");
		splashScreen.setAttribute("id", "cruxSplashScreen");
		
		String style = cruxPageNode.getAttribute("style");
		if (!StringUtils.isEmpty(style))
		{
			splashScreen.setAttribute("style", style);
		}
		String transactionDelay = cruxPageNode.getAttribute("transactionDelay");
		if (!StringUtils.isEmpty(transactionDelay))
		{
			splashScreen.setAttribute("transactionDelay", transactionDelay);
		}
		htmlNode.appendChild(splashScreen);
	}
	
	
	/**
	 * @param cruxPageElement
	 * @return
	 */
	private String updateCurrentWidgetTag(Element cruxPageElement)
    {
		String canditateWidgetType = getLibraryName(cruxPageElement)+"_"+cruxPageElement.getLocalName();
		if (StringUtils.isEmpty(cruxTagName))
		{
			cruxTagName = canditateWidgetType;
		}
		else
		{
			cruxTagName += "_"+cruxPageElement.getLocalName(); 
		}
		if (!isWidgetSubTag(cruxTagName) && (isWidget(canditateWidgetType)))
		{
			cruxTagName = canditateWidgetType;
		}
		
		if (isReferencedWidget(cruxTagName))
		{
			cruxTagName = getReferencedWidget(cruxTagName);
		}
		return cruxTagName;
    }

	/**
	 * @param out
	 * @throws IOException
	 */
	private void write(Document htmlDocument, Writer out) throws IOException
	{
		DocumentType doctype = htmlDocument.getDoctype();
		
		if (doctype != null)
		{
			out.write("<!DOCTYPE " + doctype.getName() + ">\n");
		}
	    HTMLUtils.write(htmlDocument.getDocumentElement(), out, indentOutput);
	}
	
	/**
	 * @param cruxArrayMetaData
	 */
	private void writeIndentationSpaces(StringBuilder cruxArrayMetaData)
    {
	    cruxArrayMetaData.append("\n");
	    for (int i=0; i< jsIndentationLvl; i++)
	    {
	    	cruxArrayMetaData.append("  ");
	    }
    }	
}
