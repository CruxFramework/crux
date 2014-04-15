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
 
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.hotdeploy.HotDeploymentScanner;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.utils.URLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Templates 
{
	private static boolean hotDeploymentScannerStarted = false;
	private static final Log logger = LogFactory.getLog(Templates.class);
	private static Map<String, Set<String>> registeredLibraries = new HashMap<String, Set<String>>();
	private static boolean starting = false;
	private static Map<String, Document> templates = new HashMap<String, Document>();
	private static Map<String, Set<String>> widgetTemplates = new HashMap<String, Set<String>>();
	private static Map<String, URL> foundTemplates = new HashMap<String, URL>();

	private static boolean initialized = false;

	/**
	 * 
	 * @return
	 */
	public static Set<String> getRegisteredLibraries()
	{
		if (!initialized)
		{
			initialize();
		}
		
		return registeredLibraries.keySet();
	}
	
	/**
	 * 
	 * @param library
	 * @return
	 */
	public static Set<String> getRegisteredLibraryWidgetTemplates(String library)
	{
		if (!initialized)
		{
			initialize();
		}
		
		return widgetTemplates.get(library);
	}
	
	/**
	 * 
	 * @param library
	 * @return
	 */
	public static Set<String> getRegisteredLibraryTemplates(String library)
	{
		if (!initialized)
		{
			initialize();
		}
		
		return registeredLibraries.get(library);
	}

	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static Document getTemplate(String library, String id)
	{
		return getTemplate(library, id, false);
	}

	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	public static Document getTemplate(String library, String id, boolean clone)
	{
		if (!initialized)
		{
			initialize();
		}
		Document document = templates.get(library+"_"+id);
		if (document != null)
		{
			document = (Document) document.cloneNode(true);
		}
		return document;
	}
	
	/**
	 * 
	 */
	public static synchronized void initialize()
	{
		if (initialized)
		{
			return;
		}
		starting = true;
		templates.clear();
		widgetTemplates.clear();
		registeredLibraries.clear();
		foundTemplates.clear();
		logger.info("Searching for template files.");
		TemplatesScanner.getInstance().scanArchives();
		initializeWidgetTemplates();
		setInitialized();
	}

	/**
	 * 
	 */
	public static void restart()
	{
		initialized = false;
		initialize();
	}

	/**
	 * 
	 */
	public static void reset()
	{
		initialized = false;
		templates.clear();
		widgetTemplates.clear();
		registeredLibraries.clear();
		foundTemplates.clear();
	}
	
	static void setInitialized()
    {
		if (!hotDeploymentScannerStarted && Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableHotDeploymentForWebDirs()) && !Environment.isProduction())
		{
			hotDeploymentScannerStarted = true;
			HotDeploymentScanner.scanWebDirs();
		}
		
		starting = false;
	    initialized = true;
    }

	/**
	 * @param parentElement
	 * @return
	 */
	static List<Element> extractChildrenElements(Element parentElement)
	{
		List<Element> result = new ArrayList<Element>();
		NodeList childNodes = parentElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node node = childNodes.item(i);
			switch (node.getNodeType())
			{
				case Node.COMMENT_NODE:
					//ignore node
				break;
				case Node.TEXT_NODE:
					Text textNode = (Text) node;
					if (textNode.getWholeText().trim().length() > 0)
					{
						return null;
					}
				break;
				case Node.ELEMENT_NODE:
					result.add((Element) node);
				break;
				default:
					return null;
			}
		}
		
		return result;
	}

	/**
	 * @return
	 */
	public static boolean isStarting(){
		return starting;
	}
	
	/**
	 * @param template
	 * @return
	 */
	static boolean isWidgetTemplate(Document template)
	{
		boolean isWidget = false;
		if (template != null)
		{
			Element templateElement = template.getDocumentElement();

			List<Element> children = extractChildrenElements(templateElement);
			
			if (children != null && children.size() == 1)
			{
				Element element = children.get(0);
				String namespaceURI = element.getNamespaceURI();
				if (namespaceURI != null)
				{
					if (namespaceURI.startsWith("http://www.cruxframework.org/crux/"))
					{
						isWidget = true;
					}
					else if (namespaceURI.startsWith("http://www.cruxframework.org/templates/"))
					{
						String library = namespaceURI.substring("http://www.cruxframework.org/templates/".length());
						Document refTemplate = getTemplate(library, element.getLocalName());
						isWidget = isWidgetTemplate(refTemplate);
					}
				}
			}
		}
		
		return isWidget;
	}

	/**
	 * 
	 * @param templateId
	 * @param template
	 */
	static void registerTemplate(String templateId, Document template, URL templateURL)
	{
		Element templateElement = template.getDocumentElement();
		String library = templateElement.getAttribute("library");
		String key = library+"_"+templateId;
		if (templates.containsKey(key))
		{
			URL registeredURL = foundTemplates.get(key);
			if (!URLUtils.isIdenticResource(templateURL, registeredURL, templateId+".template.xml"))
			{
				throw new TemplateException("Duplicated template found. Library: ["+library+"]. Template: ["+templateId+"].");
			}
		}
		
		if (!registeredLibraries.containsKey(library))
		{
			registeredLibraries.put(library, new HashSet<String>());
		}
		registeredLibraries.get(library).add(templateId);
		
		templates.put(key, template);
		foundTemplates.put(key, templateURL);
	}
	
	/**
	 * 
	 */
	private static void initializeWidgetTemplates()
	{
		for (Entry<String, Document> entry : templates.entrySet())
		{
			if (isWidgetTemplate(entry.getValue()))
			{
				String templateKey = entry.getKey(); 
				String[] templateIdParts = templateKey.split("_");
				
				Set<String> templates = widgetTemplates.get(templateIdParts[0]);
				if (templates == null)
				{
					templates = new HashSet<String>();
					widgetTemplates.put(templateIdParts[0], templates);
				}
				templates.add(templateIdParts[1]);
			}
		}
	}
}
