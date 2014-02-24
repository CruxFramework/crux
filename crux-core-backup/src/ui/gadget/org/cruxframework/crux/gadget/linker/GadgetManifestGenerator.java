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
package org.cruxframework.crux.gadget.linker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.rebind.screen.Widget;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.HTMLUtils;
import org.cruxframework.crux.core.utils.XMLUtils;
import org.cruxframework.crux.gadget.client.features.UserPreferences.DataType;
import org.cruxframework.crux.gadget.client.features.UserPreferences.Preference;
import org.cruxframework.crux.gadget.client.features.UserPreferences.PreferenceAttributes;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.ContainerFeature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.Feature;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.NeedsFeatures;
import org.cruxframework.crux.gadget.client.meta.GadgetFeature.WantsFeatures;
import org.cruxframework.crux.gadget.client.meta.GadgetInfo;
import org.cruxframework.crux.gadget.client.meta.GadgetInfo.ModulePrefs;
import org.cruxframework.crux.gadget.config.GadgetsConfigurationFactory;
import org.cruxframework.crux.gadget.rebind.gwt.GadgetUtils;
import org.cruxframework.crux.gadget.rebind.gwt.PreferenceGenerator;
import org.cruxframework.crux.gadget.rebind.scanner.GadgetScreenResolver;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Generate the manifest file for the Gadget ({@code .gadget.xml} file).
 * <p>
 * The manifest file is generated based on the project descriptor interface 
 * (that must extends the {@link GadgetInfo} interface).
 * <p> 
 * 
 * @author Thiago da Rosa de Bustamante
 * @see GadgetInfo
 */
public class GadgetManifestGenerator
{
	private final TreeLogger logger;
	private Class<?> moduleMetaClass;
	private String moduleName;

	private static Document manifestTemplate = null;
	
	private static DocumentBuilder getDocumentBuilder()
	{
		DocumentBuilder documentBuilder = null;
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new CruxGeneratorException("Could not create manifest document builder.", e);
		}
		return documentBuilder;
	}
	
	/**
	 * 
	 * @param library
	 * @param id
	 * @return
	 */
	private static Document getManifestTemplate()
	{
		try
        {
	        if (manifestTemplate == null)
	        {
	        	DocumentBuilder documentBuilder = getDocumentBuilder();
	        	String gadgetTemplateFile = GadgetsConfigurationFactory.getConfigurations().gadgetTemplateFile();
	        	InputStream is = null;
	        	if (gadgetTemplateFile.startsWith("/META-INF"))
	        	{
	        		is = GadgetManifestGenerator.class.getResourceAsStream(gadgetTemplateFile);
	        	}
	        	else
	        	{
	        		is = new FileInputStream(gadgetTemplateFile);
	        	}
	        	
	        	manifestTemplate = documentBuilder.parse(is);
	        }
	        
	        return (Document) manifestTemplate.cloneNode(true);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Could not create manifest document.", e);
        }
	}
	
	/**
	 * @param logger
	 * @param moduleName
	 */
	public GadgetManifestGenerator(TreeLogger logger, String moduleName)
    {
		this.logger = logger;
		this.moduleName = moduleName;
		Set<String> descriptorClasses = ClassScanner.searchClassesByInterface(GadgetInfo.class);
		if (descriptorClasses == null || descriptorClasses.size() != 1)
		{
			logger.log(TreeLogger.ERROR, "Error generating gadget descriptor. You must declare a interface (only one) that implements the interface GadgetInfo.");
			throw new CruxGeneratorException();
		}

		try
        {
	        moduleMetaClass = Class.forName(descriptorClasses.iterator().next());
        }
        catch (ClassNotFoundException e)
        {
			logger.log(TreeLogger.ERROR, "Gadget descriptor not found.");
			throw new CruxGeneratorException();
        }
		if (!moduleMetaClass.isInterface())
		{
			logger.log(TreeLogger.ERROR, "Gadget Descriptor must be an interface.");
			throw new CruxGeneratorException();
		}
    }

	/**
	 * @param printWriter
	 * @throws UnableToCompleteException 
	 */
	public void generateGadgetManifest(PrintWriter out) throws UnableToCompleteException
    {
	    logger.log(TreeLogger.DEBUG, "Building gadget manifest", null);

		Document d;
		LSSerializer serializer;
		LSOutput output;

		try
		{
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementation impl = registry.getDOMImplementation("Core 3.0");
//			d = impl.createDocument(null, "Module", null);
			d = getManifestTemplate();
			DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
			output = implLS.createLSOutput();
			output.setCharacterStream(out);
			serializer = implLS.createLSSerializer();
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Could not create manifest document.", e);
			throw new UnableToCompleteException();
		}

		Element module = d.getDocumentElement();
		Element modulePrefs = getModulePrefsElement(d, module);    
	    
	    generateModulePreferences(d, modulePrefs);
		generateUserPreferences(d, module);
		HashSet<String> added = new HashSet<String>();
		generateRequiredFeaturesList(d, modulePrefs, moduleMetaClass, added);
		generateOptionalFeaturesList(d, modulePrefs, moduleMetaClass, added);
		generateContentSections(d, module);
		
	    serializer.write(d, output);
    }
	
	/**
	 * @throws UnableToCompleteException
	 */
	public String generateGadgetManifestFile() throws UnableToCompleteException
    {
		StringWriter writer = new StringWriter();
		generateGadgetManifest(new PrintWriter(writer));
		return writer.toString();
	}
	
	/**
	 * @return
	 */
	public String getManifestName()
	{
		String manifestName = moduleMetaClass.getCanonicalName();
		if (!GadgetUtils.useLongManifestName(logger, moduleMetaClass))
		{
			int lastIndex = manifestName.lastIndexOf('.');
			if (lastIndex != -1)
			{
				manifestName = manifestName.substring(lastIndex + 1);
			}
		}
		return manifestName + ".gadget.xml";
	}
	
	/**
	 * @return
	 */
	public Class<?> getModuleMetaClass()
    {
    	return moduleMetaClass;
    }
	
	/**
	 * 
	 * @param d
	 * @param module
	 * @return
	 */
	private Element getModulePrefsElement(Document d, Element module)
	{
		NodeList children = module.getChildNodes();
		Element firstElement = null;
		for (int i=0; i< children.getLength(); i++)
		{
			Node item = children.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE)
			{
				firstElement = (Element) item;
				break;
			}
			
		}
		if (firstElement == null)
		{
			return (Element) module.appendChild(d.createElement("ModulePrefs"));	    
		}
		
		if (firstElement.getLocalName().equals("ModulePrefs"))
		{
			return firstElement;
		}
		
		Element modulePrefsElement = d.createElement("ModulePrefs");
		
		return (Element) module.insertBefore(modulePrefsElement, firstElement);
	}

	/**
	 * @param d
	 * @param userPref
	 * @param preferenceType
	 * @param m
	 * @throws UnableToCompleteException
	 */
	private void configurePreferenceElement(Document d, Element userPref, Method m) throws UnableToCompleteException
	{
		logger.log(TreeLogger.DEBUG, "Generating userpref element for method " + m.toString(), null);

		Class<?> prefType = m.getReturnType();

		if (!Preference.class.isAssignableFrom(prefType))
		{
			logger.log(TreeLogger.ERROR, m.getReturnType().getCanonicalName() + " is not assignable to " + Preference.class.getCanonicalName(), null);
			throw new UnableToCompleteException();
		}

		DataType dataType = prefType.getAnnotation(DataType.class);

		if (dataType == null)
		{
			logger.log(TreeLogger.ERROR, prefType + " must define a DataType annotation", null);
			throw new UnableToCompleteException();
		}

		userPref.setAttribute("name", m.getName());
		userPref.setAttribute("datatype", dataType.value());

		PreferenceAttributes attributes = m.getAnnotation(PreferenceAttributes.class);
		if (attributes != null)
		{
			GadgetUtils.writeAnnotationToElement(logger, attributes, userPref);

			switch (attributes.options())
			{
			case HIDDEN:
				userPref.setAttribute("datatype", "hidden");
				break;
			case NORMAL:
				break;
			case REQUIRED:
				userPref.setAttribute("required", "true");
				break;
			default:
				logger.log(TreeLogger.ERROR, "Unknown Option " + attributes.options().name(), null);
				throw new UnableToCompleteException();
			}
		}

		// Allow type-specific modifications to the userpref Element to be made
		PreferenceGenerator prefGenerator = GadgetUtils.getPreferenceGenerator(logger, prefType);
		prefGenerator.configurePreferenceElement(logger, d, userPref, prefType, m);
	}

	/**
	 * @param d
	 * @param module
	 * @throws UnableToCompleteException 
	 */
	private void generateContentSections(Document d, Element module) throws UnableToCompleteException
    {
    	try
        {
    		GadgetScreenResolver screenResourceResolver = (GadgetScreenResolver) ScreenResourceResolverInitializer.getScreenResourceResolver();
			Set<String> screenIDs = screenResourceResolver.getAllScreenIDs(moduleName);
	        for (String screenId : screenIDs)
            {
				InputStream stream = screenResourceResolver.getScreenResource(screenId);
				Document screenElement = XMLUtils.createNSUnawareDocument(stream);
				Screen screen = ScreenFactory.getInstance().getScreen(screenId, null);
				
				List<Widget> gadgetViewWidgets = getGadgetViewWidget(screen);
				for (Widget gadgetViewWidget : gadgetViewWidgets)
		        {
					module.appendChild(getContentElement(d, screenElement, gadgetViewWidget));
		        }
            }
        }
    	catch (ClassCastException e)
    	{
			logger.log(TreeLogger.ERROR, "Gadget projects must use GadgetScreenResolver or a subClass of it.", e);
			throw new UnableToCompleteException();
    	}
        catch (Exception e)
        {
			logger.log(TreeLogger.ERROR, "Could not retrieve screen ids.", e);
			throw new UnableToCompleteException();
        }
    }

	/**
	 * Add required features to the manifest
	 * {@code <require feature="someFeature" />}
	 * @param d
	 * @param modulePrefs
	 */
	private void generateRequiredFeaturesList(Document d, Element modulePrefs, Class<?> moduleMetaClass, Set<String> added)
    {
		NeedsFeatures needsFeature = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		
		if (needsFeature != null)
		{
			Feature[] features = needsFeature.value();
			if (features != null)
			{
				for (Feature feature : features)
                {
	                ContainerFeature containerFeature = feature.value();
	                if (!added.contains(containerFeature.getFeatureName()))
	                {
	                	Element require = (Element) modulePrefs.appendChild(d.createElement("Require"));
	                	require.setAttribute("feature", containerFeature.getFeatureName());

	                	added.add(containerFeature.getFeatureName());
	                }
                }
			}
		}
		
		Class<?>[] interfaces = moduleMetaClass.getInterfaces();
		if (interfaces != null)
		{
			for (Class<?> interfaceType : interfaces)
            {
				generateRequiredFeaturesList(d, modulePrefs, interfaceType, added);
            }
		}
    }

	/**
	 * Add required features to the manifest
	 * {@code <require feature="someFeature" />}
	 * @param d
	 * @param modulePrefs
	 */
	private void generateOptionalFeaturesList(Document d, Element modulePrefs, Class<?> moduleMetaClass, Set<String> added)
    {
		WantsFeatures needsFeature = moduleMetaClass.getAnnotation(WantsFeatures.class);
		
		if (needsFeature != null)
		{
			Feature[] features = needsFeature.value();
			if (features != null)
			{
				for (Feature feature : features)
                {
	                ContainerFeature containerFeature = feature.value();
	                if (!added.contains(containerFeature.getFeatureName()))
	                {
	                	Element require = (Element) modulePrefs.appendChild(d.createElement("Optional"));
	                	require.setAttribute("feature", containerFeature.getFeatureName());

	                	added.add(containerFeature.getFeatureName());
	                }
                }
			}
		}
		
		Class<?>[] interfaces = moduleMetaClass.getInterfaces();
		if (interfaces != null)
		{
			for (Class<?> interfaceType : interfaces)
            {
				generateOptionalFeaturesList(d, modulePrefs, interfaceType, added);
            }
		}
    }

	/**
	 * @param d
	 * @param modulePrefs
	 * @throws UnableToCompleteException 
	 */
	private void generateModulePreferences(Document d, Element modulePrefs) throws UnableToCompleteException
    {
		ModulePrefs prefs = moduleMetaClass.getAnnotation(ModulePrefs.class);
		if (prefs != null) 
		{
			GadgetUtils.writeAnnotationToElement(logger, prefs, modulePrefs, "requirements", "locales", "icon");
			GadgetUtils.writeLocalesToElement(logger, d, modulePrefs, prefs.locales());

			if (!StringUtils.isEmpty(prefs.icon()))
			{
				Element require = (Element) modulePrefs.appendChild(d.createElement("Icon"));
				require.setTextContent(prefs.icon());
			}
		}
    }

	/**
	 * @param d
	 * @param module
	 * @throws UnableToCompleteException 
	 */
	private void generateUserPreferences(Document d, Element module) throws UnableToCompleteException
    {

		Class<?> prefsType = GadgetUtils.getUserPrefsType(logger, moduleMetaClass);
		for (Method m : getOverridableMethods(prefsType))
		{
			Element userPref = (Element) module.appendChild(d.createElement("UserPref"));
			configurePreferenceElement(d, userPref, m);
		}
    }

	/**
	 * @param out
	 * @param element
	 * @throws IOException
	 */
	private void getBodyContent(StringWriter out, Element element) throws IOException
    {
	    NodeList bodyChildren = element.getChildNodes();
	    
	    for(int j=0; j<bodyChildren.getLength(); j++)
	    {
	    	Node child = bodyChildren.item(j);
	    	if (child.getNodeType() != Node.ELEMENT_NODE || !isModuleScriptTag((Element) child))
	    	{
	    		HTMLUtils.write(child, out);
	    	}
	    }
    }
	
	/**
	 * @param d
	 * @param screenDocument
	 * @param screen 
	 * @return
	 * @throws IOException
	 */
	private Element getContentElement(Document d, Document screenDocument, Widget gadgetViewWidget) throws IOException
	{
		Element html = screenDocument.getDocumentElement();
		StringWriter out = new StringWriter();
		
		DocumentType doctype = screenDocument.getDoctype();
		if (doctype != null)
		{
			out.write("<!DOCTYPE " + doctype.getName() + ">\n");
		}
		
		NodeList children = html.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) child;
				String nodeName = element.getNodeName();
				if (nodeName.equalsIgnoreCase("body"))
				{
					getBodyContent(out, element);
				}
				else if (nodeName.equalsIgnoreCase("head"))
				{
					getHeadContent(out, element);
				}
			}
		}
		
		Element content = d.createElement("Content");
		content.setAttribute("type", "html");

		String viewName = gadgetViewWidget.getMetadata().optString("view");
		if (!StringUtils.isEmpty(viewName) && !viewName.equals("noViews")) 
		{
			content.setAttribute("view", viewName);
		}

	    content.appendChild(d.createCDATASection(out.toString() + "__BOOTSTRAP__"));
		return content;
	}

	/**
	 * @param screen
	 * @return
	 */
	public static List<Widget> getGadgetViewWidget(Screen screen)
    {
		List<Widget> result = new ArrayList<Widget>();
		
	    Iterator<Widget> widgets = screen.getRootView().iterateWidgets();
		while (widgets.hasNext())
		{
			Widget widget = widgets.next();
			if (widget.getType().equals("gadget_gadgetView"))
			{
				result.add(widget);
			}
		}
		return result;
    }
	
	/**
	 * @param out
	 * @param element
	 * @throws IOException
	 */
	private void getHeadContent(StringWriter out, Element element) throws IOException
    {
	    NodeList headChildren = element.getChildNodes();
	    for (int j = 0; j < headChildren.getLength(); j++)
	    {
	    	
	    	Node child = headChildren.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				Element item = (Element) child;

				String nodeName = item.getNodeName();
				if (!isModuleScriptTag(item) && (nodeName.equalsIgnoreCase("link") || nodeName.equalsIgnoreCase("style") || nodeName.equalsIgnoreCase("script") ))
				{
					HTMLUtils.write(item, out);
				}
			}
	    }
    }
	
	/**
	 * @param prefsType
	 * @return
	 */
	private Method[] getOverridableMethods(Class<?> prefsType)
	{
		List<Method> allMethods = new ArrayList<Method>();
		Method[] methods = prefsType.getMethods();
		
		for (Method method : methods)
        {
			int mod = method.getModifiers();
	        if (!Modifier.isFinal(mod) && Modifier.isPublic(mod))
	        {
	        	allMethods.add(method);
	        }
        }
		
		Class<?>[] interfaces = prefsType.getInterfaces();
		for (Class<?> superIntf : interfaces)
        {
	        Method[] superMethods = getOverridableMethods(superIntf);
			for (Method method : superMethods)
	        {
				int mod = method.getModifiers();
		        if (!Modifier.isFinal(mod) && Modifier.isPublic(mod))
		        {
		        	allMethods.add(method);
		        }
	        }
        }
		return allMethods.toArray(new Method[allMethods.size()]);
	}

	/**
	 * @param elem
	 * @return
	 */
	private boolean isModuleScriptTag(Element elem)
	{
		if (elem.getNodeName().equalsIgnoreCase("script"))
		{
			String src = elem.getAttribute("src");
			if (src != null && src.endsWith(".nocache.js"))
			{
				return true;
			}
		}
		return false;
		
	}
}
