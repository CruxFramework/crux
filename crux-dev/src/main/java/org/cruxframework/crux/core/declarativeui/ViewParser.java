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
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.utils.HTMLUtils;
import org.cruxframework.crux.core.utils.ViewUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * Parses Crux view pages to extract metadata and generate the equivalent html for host pages.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewParser
{
	public static final String CRUX_VIEW_PREFIX = "_crux_view_prefix_";
	private static final String CRUX_CORE_SCREEN = "screen";
	private static final String CRUX_CORE_SPLASH_SCREEN = "splashScreen";
	private static final String CRUX_CORE_NAMESPACE= "http://www.cruxframework.org/crux";
	private static DocumentBuilder documentBuilder;
	private static XPathExpression findCruxPagesBodyExpression;
	private static XPathExpression findHTMLHeadExpression;
	private static XPathExpression findCruxSplashScreenExpression;
	private static Set<String> htmlPanelContainers;
	private static final Log log = LogFactory.getLog(ViewProcessor.class);
	private static Map<String, String> referenceWidgetsList;
	private static final String WIDGETS_NAMESPACE_PREFIX= "http://www.cruxframework.org/crux/";
	private static Set<String> widgetsSubTags;
	private static Set<String> hasInnerHTMLWidgetTags;
	private static Set<String> attachableWidgets;
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
			
			findCruxSplashScreenExpression = XPathUtils.getCruxPagesXPath().compile("//c:splashScreen");
        }
        catch (Exception e)
        {
        	log.error("Error creating viewParser.", e);
        }
	}
	
	private final boolean escapeXML;
	private final boolean indentOutput;
	private String cruxTagName;
	private int jsIndentationLvl;
	private final String viewId;
	private Document htmlDocument;
	private Document cruxPageDocument;
	private final boolean xhtmlInput;

	/**
	 * Constructor.
	 * 
	 * @param escapeXML If true will escape all inner text nodes to ensure that the generated outputs will be parsed correctly by a XML parser.
	 * @param indentOutput True makes the generated outputs be indented.
	 * @param xhtmlInput True if the given document represents a valid XHTML page. If false, parser will assume that the input is 
	 * composed by a root tag representing a the view.
	 * @throws ViewParserException
	 */
	public ViewParser(String viewId, boolean escapeXML, boolean indentOutput, boolean xhtmlInput) throws ViewParserException
    {
		this.viewId = viewId;
		this.escapeXML = escapeXML;
		this.indentOutput = indentOutput;
		this.xhtmlInput = xhtmlInput;
    }
	
	/**
	 * That method maps all widgets that needs special handling from Crux.
	 * Panels that can contain HTML mixed with widgets as contents (like {@link HTMLPanel}) and widgets 
	 * that must not be attached to DOM must be handled	differentially. 
	 *    
	 * @return
	 * @throws ViewParserException 
	 */
	private static void generateSpecialWidgetsList() throws ViewParserException
	{
		htmlPanelContainers = new HashSet<String>();
		attachableWidgets = new HashSet<String>();
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
					if (factory.attachToDOM())
					{
						attachableWidgets.add(library+"_"+widget);				
					}
				}
				catch (Exception e)
				{
					throw new ViewParserException("Error creating XSD File: Error generating widgets reference list.", e);
				}
			}
		}
	}

	/**
	 * Some widgets can define tags with custom names that has its type as other widget. It can be handled properly
	 * and those situations are mapped by this method. 
	 * 
	 * @return
	 * @throws ViewParserException 
	 */
	private static void generateReferenceWidgetsList() throws ViewParserException
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
					throw new ViewParserException("Error creating XSD File: Error generating widgets reference list.", e);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param widgetList 
	 * @param tagChildren
	 * @param parentLibrary
	 * @throws ViewParserException 
	 */
	private static void generateReferenceWidgetsListFromTagChildren(TagChildren tagChildren, 
																    String parentLibrary, String parentWidget, Set<String> added) throws ViewParserException
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
					TagConstraints childAttributes = ViewUtils.getChildTagConstraintsAnnotation(processorClass);
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
						throw new ViewParserException("Error creating XSD File: Error generating widgets list.", e);
					}
				}
			}
		}				
	}
	
	/**Extract the view metadata form the current document
	 * @param element
	 */
	public String extractCruxMetaData(Document view) throws ViewParserException
    {	
		try
		{
			this.htmlDocument = createHTMLDocument(view);

			Element htmlElement = view.getDocumentElement();
			StringBuilder elementsMetadata = new StringBuilder();
			elementsMetadata.append("[");
			indent();
			generateCruxMetadataForView(htmlElement, elementsMetadata);
			outdent();
			elementsMetadata.append("]");

			StringBuilder metadata = new StringBuilder();
			metadata.append("{");
			indent();
			metadata.append("\"elements\":"+elementsMetadata.toString());
			metadata.append(",\"lazyDeps\":"+new LazyWidgets(escapeXML).generateScreenLazyDeps(elementsMetadata.toString()));
		
			Element viewHtmlElement = htmlDocument.createElementNS(XHTML_NAMESPACE,"body");
			
			Element rootElement = (xhtmlInput?getPageBodyElement(view):view.getDocumentElement());
			translateDocument(rootElement, viewHtmlElement, true);
			generateCruxInnerHTMLMetadata(metadata, viewHtmlElement);
			outdent();
			metadata.append("}");

			return metadata.toString();
		}
		catch (Exception e) 
		{
			throw new ViewParserException("Error extracting Crux Metadata from view.", e);
		}
    }

	/**
	 * Generates the HTML page from the given .crux.xml page.
	 *
	 * @param viewId The id of the screen associated with the .crux.xml page. 
	 * @param cruxPageDocument a XML Document representing the .crux.xml page.
	 * @param out Where the generated HTML will be written.
	 * @throws ViewParserException
	 */
	public void generateHTMLHostPage(Document cruxPageDocument, Writer out) throws ViewParserException
	{
		try
        {
			if (!xhtmlInput)
			{
	        	throw new ViewParserException("Can not generate an HTML host page for a non XHTML document.");
			}
			this.cruxPageDocument = cruxPageDocument;
			this.htmlDocument = createHTMLDocument(cruxPageDocument);
			translateHTMLHostDocument();
	        write(htmlDocument, out);
        }
        catch (IOException e)
        {
        	throw new ViewParserException(e.getMessage(), e);
        }
	}

	/**
	 * @param cruxPageScreen
	 * @param cruxArrayMetaData
	 * @throws ViewParserException 
	 */
	private void generateCruxScreenMetaData(Element cruxPageScreen, StringBuilder cruxArrayMetaData) throws ViewParserException
    {
		generateCruxMetadataForView(cruxPageScreen, cruxArrayMetaData, xhtmlInput);		
    }

	/**
	 * 
	 * @param htmlElement
	 * @param elementsMetadata
	 * @throws ViewParserException
	 */
	private void generateCruxMetadataForView(Element htmlElement, StringBuilder elementsMetadata) throws ViewParserException
    {
		generateCruxMetadataForView(htmlElement, elementsMetadata, !xhtmlInput);		
    }

	/**
	 * 
	 * @param cruxPageScreen
	 * @param cruxArrayMetaData
	 * @param generateViewTag
	 * @throws ViewParserException
	 */
	private void generateCruxMetadataForView(Element cruxPageScreen, StringBuilder cruxArrayMetaData, boolean generateViewTag) throws ViewParserException
    {
	    writeIndentationSpaces(cruxArrayMetaData);
		if (generateViewTag)
		{
			cruxArrayMetaData.append("{");
			cruxArrayMetaData.append("\"_type\":\"screen\"");
			
			generateCruxMetaDataAttributes(cruxPageScreen, cruxArrayMetaData);
			
			cruxArrayMetaData.append("}");
		}
		StringBuilder childrenMetaData = new StringBuilder();
		generateCruxMetaData(cruxPageScreen, childrenMetaData);
		
		if (childrenMetaData.length() > 0)
		{
			if (generateViewTag)
			{
				cruxArrayMetaData.append(",");
			}
			cruxArrayMetaData.append(childrenMetaData);
		}
    }

	/**
	 * @param viewId 
	 * @throws ViewParserException 
	 */
	private void translateHTMLHostDocument() throws ViewParserException
    {
		Element htmlHeadElement = getPageHeadElement(htmlDocument);
		Element htmlBodyElement = getPageBodyElement(htmlDocument);
		Element cruxHeadElement = getPageHeadElement(cruxPageDocument);
		
		clearCurrentWidget();
		translateDocument(cruxHeadElement, htmlHeadElement, true);
		clearCurrentWidget();
		
		generateCruxMetaDataElement(htmlBodyElement);
		generateCruxModuleElement(htmlBodyElement);
		handleCruxSplashScreen();
    }
	
	/**
	 * 
	 * @param htmlBodyElement
	 * @throws ViewParserException
	 */
	private void generateCruxModuleElement(Element htmlBodyElement) throws ViewParserException
    {
		Element child = getScreenModule(cruxPageDocument);
		if (child == null)
		{
			throw new ViewParserException("No module declared on screen ["+viewId+"].");
		}
		Node htmlChild = htmlDocument.importNode(child, false);
		htmlBodyElement.appendChild(htmlChild);
    }

	/**
	 * 
	 * @param source
	 * @return
	 * @throws ViewParserException
	 */
	private Element getScreenModule(Document source) throws ViewParserException 
	{
		Element result = null;
		NodeList nodeList = source.getElementsByTagName("script");
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element item = (Element) nodeList.item(i);
			
			String src = item.getAttribute("src");
			
			if (src != null && src.endsWith(".nocache.js"))
			{
				if (result != null)
				{
					throw new ViewParserException("Multiple modules in the same html page is not allowed in CRUX.");
				}
				result = item;
			}
		}
		return result;
	}	
	
	/**
	 * 
	 * @throws ViewParserException
	 */
	private void handleCruxSplashScreen() throws ViewParserException
    {
		try
        {
	        NodeList splashScreenNodes = (NodeList)findCruxSplashScreenExpression.evaluate(cruxPageDocument, XPathConstants.NODESET);
	        if (splashScreenNodes.getLength() > 0)
	        {
	        	if (splashScreenNodes.getLength() > 1)
	        	{
	            	throw new ViewParserException("The view ["+this.viewId+"] declares more than one splashScreen. Only one is allowed.");
	        	}
	        	Element splashScreen = (Element)splashScreenNodes.item(0);
	        	translateSplashScreen(splashScreen, getPageBodyElement(htmlDocument));
	        }
        }
        catch (XPathExpressionException e)
        {
        	throw new ViewParserException("Error inspecting the view ["+this.viewId+"]. Error while searching for splashScreen elements.", e);
        }
    }

	/**
	 * @param cruxPageNode
	 * @param htmlNode
	 * @param htmlDocument
	 */
	private void translateSplashScreen(Element cruxPageNode, Element htmlNode)
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
	 * @param cruxPageDocument
	 * @return
	 */
	private Document createHTMLDocument(Document cruxPageDocument)
    {
	    Document htmlDocument;
		DocumentType doctype = cruxPageDocument.getDoctype();
		
		if (doctype != null || Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableGenerateHTMLDoctype()))
		{
			String name     = doctype != null ? doctype.getName() : "HTML";
			String publicId = doctype != null ? doctype.getPublicId() : null;
			String systemId = doctype != null ? doctype.getSystemId() : null;
			
			DocumentType newDoctype =  documentBuilder.getDOMImplementation().createDocumentType(name, publicId, systemId);
			htmlDocument = documentBuilder.getDOMImplementation().createDocument(XHTML_NAMESPACE, "html", newDoctype);
		}
		else
		{
			htmlDocument = documentBuilder.newDocument();
			Element cruxPageElement = cruxPageDocument.getDocumentElement();
			Node htmlElement = htmlDocument.importNode(cruxPageElement, false);
			htmlDocument.appendChild(htmlElement);
		}
		
		String manifest = cruxPageDocument.getDocumentElement().getAttribute("manifest");
		if (!StringUtils.isEmpty(manifest))
		{
			htmlDocument.getDocumentElement().setAttribute("manifest", manifest);
		}
	    return htmlDocument;
    }

	/**
	 * @param cruxPageInnerTag
	 * @param cruxArrayMetaData
	 * @throws ViewParserException 
	 */
	private void generateCruxInnerMetaData(Element cruxPageInnerTag, StringBuilder cruxArrayMetaData) throws ViewParserException
    {
		writeIndentationSpaces(cruxArrayMetaData);
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
		
		if (isHtmlContainerWidget(cruxPageInnerTag))
		{
			Element htmlElement = htmlDocument.createElementNS(XHTML_NAMESPACE,"body");
			translateDocument(cruxPageInnerTag, htmlElement, true);
			generateCruxInnerHTMLMetadata(cruxArrayMetaData, htmlElement);
		}
		else if (allowInnerHTML(currentWidgetTag))
		{
			generateCruxInnerHTMLMetadata(cruxArrayMetaData, cruxPageInnerTag);
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
	 * 
	 * @param cruxArrayMetaData
	 * @param htmlElement
	 * @throws ViewParserException
	 */
	private void generateCruxInnerHTMLMetadata(StringBuilder cruxArrayMetaData, Element htmlElement) throws ViewParserException
    {
	    String innerHTML = getHTMLFromNode(htmlElement);
    	cruxArrayMetaData.append(",\"_html\":\""+innerHTML+"\"");
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
	 * @throws ViewParserException 
	 */
	private void generateCruxMetaData(Node cruxPageBodyElement, StringBuilder cruxArrayMetaData) throws ViewParserException
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
					
				if (namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE) && !nodeName.equals(CRUX_CORE_SPLASH_SCREEN))
				{
					if (needsComma)
					{
						cruxArrayMetaData.append(",");
					}
					if (nodeName.equals(CRUX_CORE_SCREEN))
					{
						generateCruxScreenMetaData((Element)child, cruxArrayMetaData);
					}
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
				
				String namespaceURI = attribute.getNamespaceURI();
				if (!StringUtils.isEmpty(attrValue) && (namespaceURI == null || !namespaceURI.endsWith("/xmlns/")))
				{
					cruxArrayMetaData.append(",");
					cruxArrayMetaData.append("\""+attrName+"\":");
					cruxArrayMetaData.append("\""+HTMLUtils.escapeJavascriptString(attrValue, escapeXML)+"\"");
				}
			}
		}
	}

	/**
	 * @param htmlHeadElement
	 * @throws ViewParserException 
	 */
	private void generateCruxMetaDataElement(Element htmlHeadElement) throws ViewParserException
    {
		ScreenFactory factory = ScreenFactory.getInstance();
		String screenModule = null;
		try
		{
			screenModule = factory.getScreenModule(cruxPageDocument);
			
		}
		catch (Exception e)
		{
			throw new ViewParserException(e.getMessage(), e);
		}
			
		if (screenModule == null)
		{
			throw new ViewParserException("No module declared on view ["+viewId+"].");
		}
		try
		{
			String screenId = factory.getRelativeScreenId(this.viewId, screenModule);

			Element cruxMetaData = htmlDocument.createElement("script");
			cruxMetaData.setAttribute("id", "__CruxMetaDataTag_");		
			htmlHeadElement.appendChild(cruxMetaData);
			Text textNode = htmlDocument.createTextNode("var __CruxScreen_ = \""+screenModule+"/"+HTMLUtils.escapeJavascriptString(screenId, escapeXML)+"\"");
			cruxMetaData.appendChild(textNode);
		}
		catch (Exception e)
		{
			throw new ViewParserException(e.getMessage(), e);
		}
    }
	
	/**
	 * @param cruxPageDocument
	 * @return
	 * @throws ViewParserException
	 */
	private Element getPageBodyElement(Document cruxPageDocument) throws ViewParserException
	{
		try
        {
	        NodeList bodyNodes = (NodeList)findCruxPagesBodyExpression.evaluate(cruxPageDocument, XPathConstants.NODESET);
	        if (bodyNodes.getLength() > 0)
	        {
	        	return (Element)bodyNodes.item(0);
	        }
	        Element bodyElement = cruxPageDocument.createElementNS(XHTML_NAMESPACE, "body");
	        cruxPageDocument.getDocumentElement().appendChild(bodyElement);
	        return bodyElement;
        }
        catch (XPathExpressionException e)
        {
        	throw new ViewParserException(e.getMessage(), e);
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
	 * @throws ViewParserException
	 */
	private Element getPageHeadElement(Document htmlDocument) throws ViewParserException
	{
		try
        {
	        NodeList headNodes = (NodeList)findHTMLHeadExpression.evaluate(htmlDocument, XPathConstants.NODESET);
	        if (headNodes.getLength() > 0)
	        {
	        	return (Element)headNodes.item(0);
	        }
	        Element headElement = htmlDocument.createElementNS(XHTML_NAMESPACE,"head");
	        Element bodyElement = getPageBodyElement(htmlDocument);
	        if (bodyElement != null)
	        {
	        	htmlDocument.getDocumentElement().insertBefore(headElement, bodyElement);
	        	return headElement;
	        }
	        htmlDocument.getDocumentElement().appendChild(headElement);
	        return headElement;
        }
        catch (XPathExpressionException e)
        {
        	throw new ViewParserException(e.getMessage(), e);
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
	 * @throws ViewParserException 
	 */
	private String getHTMLFromNode(Element elem) throws ViewParserException
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
					if (!isCruxModuleImportTag(child))
					{
						HTMLUtils.write(child, innerHTML);
					}
				}
			}
	        return HTMLUtils.escapeJavascriptString(innerHTML.toString(), false);
		}
		catch (IOException e)
		{
			throw new ViewParserException(e.getMessage(), e);
		}
	} 

	/**
	 * 
	 * @param node
	 * @return
	 */
	private boolean isCruxModuleImportTag(Node node)
	{
		if (node instanceof Element)
		{
			Element elem = (Element)node;
			String tagName = elem.getTagName();
			String namespaceURI = elem.getNamespaceURI();
			String src = elem.getAttribute("src");
			return (namespaceURI == null || namespaceURI.equals(XHTML_NAMESPACE)) && tagName.equalsIgnoreCase("script") && (src != null && src.endsWith(".nocache.js"));
		}
		return false;
	}
	
	/**
	 * 
	 */
	private void indent()
	{
		jsIndentationLvl++;
	}
	
	/**
	 * Check if the target node is child from a rootDocument element or from a native XHTML element.
	 * 
	 * @param node
	 * @return
	 */
	private boolean isHTMLChild(Node node)
	{
		Node parentNode = node.getParentNode();
		String namespaceURI = parentNode.getNamespaceURI();
		if (namespaceURI == null)
		{
			log.warn("The view ["+this.viewId+"] contains elements that is not bound to any namespace. It can cause errors while translating to an HTML page.");
		}
		if (node.getOwnerDocument().getDocumentElement().equals(parentNode))
		{
			return true;
		}
		if (namespaceURI != null && namespaceURI.equals(XHTML_NAMESPACE) || isHtmlContainerWidget(parentNode))
		{
			return true;
		}
		if (parentNode instanceof Element && namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE) && parentNode.getLocalName().equals(CRUX_CORE_SCREEN))
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
	private boolean isAttachableWidget(Node node)
	{
		if (node instanceof Element)
		{
			return isAttachableWidget(node.getLocalName(), getLibraryName(node));
		}
		return false;
	}

	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isAttachableWidget(String localName, String libraryName)
	{
	    return attachableWidgets.contains(libraryName+"_"+localName);
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
	 */
	private void clearCurrentWidget()
	{
		cruxTagName = "";
	}
	
	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 */
	private void translateCruxCoreElements(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
	    String nodeName = cruxPageElement.getLocalName();
	    if (nodeName.equals(CRUX_CORE_SCREEN))
	    {
	    	translateDocument(cruxPageElement, htmlElement, true);
	    }
    	// else IGNORE
    }
	
	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateCruxInnerTags(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
		String currentWidgetTag = getCurrentWidgetTag();
		boolean attachableWidget = isAttachableWidget(cruxPageElement);
		if ((isWidget(currentWidgetTag)) && attachableWidget && isHTMLChild(cruxPageElement))
		{
			Element widgetHolder = htmlDocument.createElement("div");
			htmlElement.appendChild(widgetHolder);
			widgetHolder.setAttribute("id", ViewFactoryUtils.ENCLOSING_PANEL_PREFIX + CRUX_VIEW_PREFIX+cruxPageElement.getAttribute("id"));
		}
    }

	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 */
	private void translateDocument(Node cruxPageElement, Node htmlElement, boolean copyHtmlNodes)
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
					translateDocument(child, htmlChild, copyHtmlNodes);
				}
			}
		}
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
		if (indentOutput)
		{
			cruxArrayMetaData.append("\n");
			for (int i=0; i< jsIndentationLvl; i++)
			{
				cruxArrayMetaData.append("  ");
			}
	    }
    }	
}
