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
package org.cruxframework.crux.tools.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.template.TemplateParser;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetLibraries;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AllChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.SequenceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.TextChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyTag;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.core.utils.ViewUtils;
import org.cruxframework.crux.scanner.ClasspathUrlFinder;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.tools.scanner.template.Templates;
import org.w3c.dom.Document;

import com.google.gwt.resources.client.ResourcePrototype;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DefaultSchemaGenerator implements CruxSchemaGenerator
{
	private static final Log logger = LogFactory.getLog(DefaultSchemaGenerator.class);

	protected File destDir;
	protected Map<String, Class<?>> enumTypes;

	protected Map<String, File> namespacesForCatalog;
	protected File projectBaseDir;
	protected SchemaMessages schemaMessages;
	protected Stack<Class<? extends WidgetChildProcessor<?>>> subTagTypes;

	protected TemplateParser templateParser;

	private String XHTML_XSD = "xhtml.xsd";
	
	/**
	 * 
	 * @param destDir
	 */
	public DefaultSchemaGenerator(File projectBaseDir, File destDir, File webDir)
	{
		this.projectBaseDir = projectBaseDir;
		this.destDir = destDir;
		this.destDir.mkdirs();
		this.enumTypes = new HashMap<String, Class<?>>();
		this.namespacesForCatalog = new HashMap<String, File>();
		this.subTagTypes = new Stack<Class<? extends WidgetChildProcessor<?>>>();
		this.templateParser = new TemplateParser();
		this.schemaMessages = MessagesFactory.getMessages(SchemaMessages.class);

		if (Boolean.valueOf(ConfigurationFactory.getConfigurations().useHTML5XSD()))
		{
			XHTML_XSD = "xhtml5.xsd";
		}
		
		initializeSchemaGenerator(projectBaseDir, webDir);
	}

	/**
	 * 
	 * @param destRelativeDir
	 */
	public DefaultSchemaGenerator(String projectBaseDir, String destDir, String webDir)
	{
		this(new File(projectBaseDir), new File(destDir), new File(webDir));
	}

	/**
	 * @param out
	 */
	public void generateCatalog() throws SchemaGeneratorException
	{
		try
		{
			File catalog = new File(destDir, "crux-catalog.xml");
			catalog.createNewFile();
			PrintStream stream = null;
			try
			{
				stream = new PrintStream(catalog);

				stream.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
				stream.println("<!DOCTYPE catalog PUBLIC \"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN\" \"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd\">");

				stream.println("<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">");		
				for (Entry<String, File> entry : namespacesForCatalog.entrySet())
				{
					stream.println("\t<uri name=\"" + entry.getKey() + "\" uri=\"platform:/resource/" + projectBaseDir.getName() + "/" + getRelativeName(entry.getValue()) + "\"/>");
				}
				stream.println("</catalog>");
			}
			finally
			{
				if (stream != null)
				{
					stream.close();
				}
			}
		}
		catch (Exception e) 
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}	

	@Override
	public void generateDocumentation() throws SchemaGeneratorException
	{
		try
		{
			TransformerFactory factory = TransformerFactory.newInstance();
			StreamSource xslStream = new StreamSource(getClass().getResourceAsStream("/META-INF/xs3p.xsl"));
			Transformer transformer = factory.newTransformer(xslStream);
			

			for (File file: destDir.listFiles())
			{
				String fileName = file.getName();
				logger.info("Generating Documentation for library ["+fileName+"].");
				if (fileName.endsWith("xsd"))
				{
					transformer.setParameter("title", schemaMessages.documentationTitle(fileName.substring(0, fileName.length() - 4).toUpperCase()));
					
					if (fileName.endsWith("core.xsd") || fileName.endsWith("module.xsd") || 
						fileName.endsWith("offline.xsd")  || fileName.endsWith("template.xsd")  || 
						fileName.endsWith("view.xsd")  || fileName.endsWith("xdevice.xsd")  || 
						fileName.endsWith(XHTML_XSD))
					{
						transformer.setParameter("globalDeclarationsTitle", schemaMessages.globalDeclarationsTitle());
					}
					else
					{
						transformer.setParameter("globalDeclarationsTitle", schemaMessages.globalDeclarationsWidgetsTitle());
					}
					transformer.setParameter("printLegend", false);
					transformer.setParameter("printGlossary", false);
					StreamSource in = new StreamSource(new FileInputStream(file));
					StreamResult out = new StreamResult(new File(destDir, file.getName()+".html"));
					transformer.transform(in, out);
				}
			}
			logger.info("All documentation generated.");
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException("Error generation HTML documentation for XSD files", e);
		}
	}

	/**
	 * @see org.cruxframework.crux.tools.schema.CruxSchemaGenerator#generateSchemas()
	 */
	public void generateSchemas() throws SchemaGeneratorException
	{
		try
		{
			Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
			Set<String> templateLibraries = Templates.getRegisteredLibraries();
			while (libraries.hasNext())
			{
				String library = libraries.next();
				logger.info("Generating xsd file for library ["+library+"]");
				generateSchemaForLibrary(library, templateLibraries);
			}

			logger.info("Generating template.xsd file.");
			generateTemplateSchema(templateLibraries);

			for (String library : templateLibraries)
			{
				logger.info("Generating XSD file for library ["+library+"]");
				generateSchemaForTemplateLibrary(library);
			}

			logger.info("Generating core.xsd file");
			generateCoreSchema(templateLibraries);
			generateOfflineSchema();
			generateXDeviceSchema(templateLibraries);
			generateViewSchema(templateLibraries);

			copyXHTMLSchema();

			logger.info("XSD Files Generated.");
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	protected void copyXHTMLSchema() 
	{
		try
		{
			File xhtmlFile = new File(destDir, XHTML_XSD);
			if (xhtmlFile.exists())
			{
				xhtmlFile.delete();
			}
			xhtmlFile.createNewFile();
			FileOutputStream out = new FileOutputStream(xhtmlFile);

			String targetNS = "http://www.w3.org/1999/xhtml";
			registerNamespaceForCatalog(targetNS, xhtmlFile);

			StreamUtils.write(getClass().getResourceAsStream("/META-INF/" + XHTML_XSD), out, true);
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param widgetFactory
	 * @return
	 */
	protected boolean factorySupportsInnerText(Class<? extends WidgetCreator<?>> widgetFactory)
	{
		try
		{
			return hasTextChild(widgetFactory);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * 
	 * @param out
	 * @param attributes
	 * @param children
	 * @throws NoSuchMethodException
	 */
	protected void generateAllChild(PrintStream out, TagConstraints attributes, TagChildren children, String library) throws NoSuchMethodException
	{
		out.print("<xs:all ");
		if (attributes!= null)
		{
			out.print("minOccurs=\""+attributes.minOccurs()+"\" ");
			out.print("maxOccurs=\""+attributes.maxOccurs()+"\" ");
		}
		out.println(">");
		for (TagChild child : children.value())
		{
			generateChild(out, child, true, library);
		}
		out.println("</xs:all>");
	}

	/**
	 * 
	 * @param out
	 * @param library
	 * @param added
	 * @param processorClass
	 */
	protected void generateAttributes(PrintStream out, String library, Set<String> added, Class<?> processorClass)
	{
		TagAttributesDeclaration attrsDecl = processorClass.getAnnotation(TagAttributesDeclaration.class);
		if (attrsDecl != null)
		{
			for (TagAttributeDeclaration attr : attrsDecl.value())
			{
				if (!added.contains(attr.value()))
				{
					out.print("<xs:attribute name=\""+attr.value()+"\" type=\""+getSchemaType(attr.type(), library, attr.supportsDataBinding())+"\" ");
					if (attr.required())
					{
						out.print("use=\"required\" ");
					}
					else
					{
						String defaultValue = attr.defaultValue();
						if (defaultValue.length() > 0)
							out.print("default=\""+defaultValue+"\" ");
					}
					out.println(">");
					
					String attrDescription = attr.description();
					if (attrDescription != null && attrDescription.length() > 0)
					{
						out.println("<xs:annotation>");
						out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(attrDescription)+"</xs:documentation>");
						out.println("</xs:annotation>");
					}
					out.println("</xs:attribute>");
					
					added.add(attr.value());
				}
			}
		}
		TagAttributes attrs = processorClass.getAnnotation(TagAttributes.class);
		if (attrs != null)
		{
			for (TagAttribute attr : attrs.value())
			{
				if (!added.contains(attr.value()) && !attr.xsdIgnore())
				{
					out.print("<xs:attribute name=\""+attr.value()+"\" type=\""+getSchemaType(attr.type(), library, attr.supportsDataBinding())+"\" ");
					if (attr.required())
					{
						out.print("use=\"required\" ");
					}
					else
					{
						String defaultValue = attr.defaultValue();
						if (defaultValue.length() > 0)
							out.print("default=\""+defaultValue+"\" ");
					}

					out.println(">");
					String attrDescription = attr.description();
					if (attrDescription != null && attrDescription.length() > 0)
					{
						out.println("<xs:annotation>");
						out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(attrDescription)+"</xs:documentation>");
						out.println("</xs:annotation>");
					}
					out.println("</xs:attribute>");
					added.add(attr.value());
				}
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 */
	protected void generateAttributesForFactory(PrintStream out, Class<?> widgetFactory, String library, Set<String> added)
	{
		generateAttributes(out, library, added, widgetFactory);

		Class<?> superclass = widgetFactory.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			generateAttributesForFactory(out, superclass, library, added);
		}
		Class<?>[] interfaces = widgetFactory.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			generateAttributesForFactory(out, interfaceClass, library, added);
		}
	}

	/**
	 * 
	 * @param out
	 * @param processorClass
	 * @param library
	 * @param added
	 */
	protected void generateAttributesForProcessor(PrintStream out, Class<?> processorClass, String library, Set<String> added)
	{
		try
		{
			TagConstraints attributes = ViewUtils.getChildTagConstraintsAnnotation(processorClass);
			if (attributes != null && attributes.applyDeviceFilters())
			{
				out.println("<xs:attribute name=\"input\" type=\"c:Input\" />");
				out.println("<xs:attribute name=\"size\" type=\"c:Size\" />");
			}

			generateAttributes(out, library, added, processorClass);

			Class<?> superclass = processorClass.getSuperclass();
			if (superclass!= null && !superclass.equals(Object.class))
			{
				generateAttributesForProcessor(out, superclass, library, added);
			}
		}
		catch (Exception e)
		{
			logger.error("Error creating XSD File: Error generating attributes for Processor.", e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param template
	 */
	protected void generateAttributesForTemplate(PrintStream out, Document template)
	{
		Set<String> attributesForTemplate = templateParser.getParameters(template);
		for (String attrValue : attributesForTemplate)
		{
			out.println("<xs:attribute name=\""+attrValue+"\" type=\"xs:string\" use=\"required\" />");
		}
	}

	/**
	 * 
	 * @param out
	 * @param tagChild
	 * @param parentIsAnAgregator
	 * @param library
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	protected void generateChild(PrintStream out, TagChild tagChild, boolean parentIsAnAgregator, String library) throws SecurityException, NoSuchMethodException
	{
		Class<? extends WidgetChildProcessor<?>> processorClass = tagChild.value();
		TagConstraints attributes = ViewUtils.getChildTagConstraintsAnnotation(processorClass);
		TagChildren children = processorClass.getAnnotation(TagChildren.class);

		if (ChoiceChildProcessor.class.isAssignableFrom(processorClass))
		{
			generateChoiceChild(out, attributes, children, library);	
		}
		else if (SequenceChildProcessor.class.isAssignableFrom(processorClass))
		{
			generateSequenceChild(out, attributes, children, library);	
		}
		else if (AllChildProcessor.class.isAssignableFrom(processorClass))
		{
			generateAllChild(out, attributes, children, library);	
		}
		else
		{
			if (attributes != null)
			{
				generateGenericChildWithAttributes(out, library, processorClass, attributes);
			}	
			else if (AnyWidgetChildProcessor.class.isAssignableFrom(processorClass))
			{
				out.println("<xs:group ref=\"c:widgets\" >");
				out.println("<xs:annotation>");
				out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.anyWidgetsDescription())+"</xs:documentation>");
				out.println("</xs:annotation>");
				out.println("</xs:group>");
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param library
	 * @param processorClass
	 * @throws NoSuchMethodException
	 */
	protected void generateChildren(PrintStream out, String library, Class<?> processorClass) throws NoSuchMethodException
	{
		TagChildren annot = processorClass.getAnnotation(TagChildren.class);
		if (annot != null)
		{
			if (annot.value().length > 1)
			{
				out.println("<xs:sequence>");
				for (TagChild tagChild : annot.value())
				{
					generateChild(out, tagChild, true, library);
				}
				out.println("</xs:sequence>");
			}
			else if (annot.value().length == 1)
			{
				TagChild tagChild = annot.value()[0];
				if (isChildAnAgregator(tagChild))
				{
					generateChild(out, tagChild, true, library);
				}
				else
				{
					out.println("<xs:sequence>");
					generateChild(out, tagChild, true, library);
					out.println("</xs:sequence>");
				}
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 */
	protected void generateChildrenForFactory(PrintStream out, Class<? extends WidgetCreator<?>> widgetFactory, String library)
	{
		try
		{
			generateChildren(out, library, widgetFactory);
		}
		catch (Exception e)
		{
			logger.error("Error creating XSD File: Error generating children for Processor ["
		          + widgetFactory.getCanonicalName() + "].", e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 * @param library
	 */
	protected void generateChildrenForProcessor(PrintStream out, Class<? extends WidgetChildProcessor<?>> processorClass, String library)
	{
		try
		{
			generateChildren(out, library, processorClass);
		}
		catch (Exception e)
		{
			logger.error("Error creating XSD File: ProcessChildren method not found.", e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param template
	 */
	protected void generateChildrenForTemplate(PrintStream out, Document template)
	{
		Set<String> childrenForTemplate = templateParser.getSections(template);

		if (childrenForTemplate.size() > 0)
		{
			out.println("<xs:all>");
			for (String section : childrenForTemplate)
			{
				out.println("<xs:element type=\"xs:anyType\" name=\""+section+"\" />");
			}
			out.println("</xs:all>");
		}
	}

	/**
	 * 
	 * @param out
	 * @param attributes
	 * @param children
	 * @throws NoSuchMethodException
	 */
	protected void generateChoiceChild(PrintStream out, TagConstraints attributes, TagChildren children, String library) throws NoSuchMethodException
	{
		out.print("<xs:choice ");
		if (attributes!= null)
		{
			out.print("minOccurs=\""+attributes.minOccurs()+"\" ");
			out.print("maxOccurs=\""+attributes.maxOccurs()+"\" ");
		}
		out.println(">");
		for (TagChild child : children.value())
		{
			generateChild(out, child, true, library);
		}
		out.println("</xs:choice>");
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateCoreCrossDeviceElement(PrintStream out)
	{
		out.println("<xs:element name=\"crossDevice\" type=\"CrossDevice\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.crossDeviceDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"CrossDevice\">");
		out.println("<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">");
		out.println("<xs:group ref=\"widgetsCrossDev\" />");
		out.println("<xs:element name=\"conditions\" type=\"CrossDeviceConditions\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.crossDeviceConditionsDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("</xs:choice>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"CrossDeviceConditions\">");
		out.println("<xs:sequence minOccurs=\"1\" maxOccurs=\"unbounded\">");
		out.println("<xs:element name=\"condition\" type=\"CrossDeviceCondition\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.crossDeviceConditionDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("</xs:sequence>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"CrossDeviceCondition\">");
		out.println("<xs:choice minOccurs=\"1\" maxOccurs=\"unbounded\">");
		out.println("<xs:element name=\"parameter\" type=\"CrossDeviceParameterCondition\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.crossDeviceConditionParameterDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"when\" type=\"DeviceType\" use=\"required\"/>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"CrossDeviceParameterCondition\">");
		out.println("<xs:attribute name=\"name\" type=\"xs:string\" use=\"required\"/>");
		out.println("<xs:attribute name=\"value\" type=\"xs:string\" use=\"required\"/>");
		out.println("</xs:complexType>");


		out.println("<xs:simpleType name=\"DeviceType\">");
		out.println("<xs:restriction base=\"xs:string\">");
		Device[] values = Device.values();
		for (Device device : values)
		{
			out.println("<xs:enumeration value=\""+device.toString()+"\" />");
		}
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");
	}

	/**
	 * 
	 * @param out
	 * @param templateLibraries 
	 */
	protected void generateCoreCrossDevWidgetsType(PrintStream out, Set<String> templateLibraries)
	{
		generateCoreWidgetsType(out, templateLibraries, "widgetsCrossDev", false, true);
	}

	/**
	 * 
	 * @param templateLibraries 
	 */
	protected void generateCoreSchema(Set<String> templateLibraries)
	{
		try
		{
			File coreFile = new File(destDir, "core.xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();

			String targetNS = "http://www.cruxframework.org/crux";
			registerNamespaceForCatalog(targetNS, coreFile);

			PrintStream out = new PrintStream(coreFile);
			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/crux\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
			while (libraries.hasNext())
			{
				String lib = libraries.next();
				out.println("xmlns:_"+lib+"=\"http://www.cruxframework.org/crux/"+lib+"\" ");
			}
			for (String lib : templateLibraries)
			{
				out.println("xmlns:_"+lib+"=\"http://www.cruxframework.org/templates/"+lib+"\" ");
			}
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateCoreSchemaBody(templateLibraries, out);
			
			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	protected void generateCoreSchemaBody(Set<String> templateLibraries, PrintStream out)
    {
	    generateCoreSchemasImport(templateLibraries, out);
	    generateCoreSplashScreenElement(out);
	    generateCoreScreenElement(out);
	    generateCoreDataProviderElements(out);
	    generateCoreCrossDeviceElement(out);
	    generateCoreWidgetsType(out, templateLibraries);	
	    generateCoreCrossDevWidgetsType(out, templateLibraries);
	    generateCoreTypesSupportingBinding(out);
	    generateCoreIfDeviceElement(out);
    }

	protected void generateCoreIfDeviceElement(PrintStream out)
    {
		out.println("<xs:element name=\"ifDevice\" type=\"IfDevice\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+"A rendering condition that allows developers to switch the implementation according to the device."+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"IfDevice\">");
		out.println("<xs:sequence>");
		out.println("<xs:element name=\"excludes\" type=\"IfDeviceExclusions\" minOccurs=\"0\" maxOccurs=\"1\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+"Exclude any devices that will no make apart of the rule declared above."+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:group ref=\"widgets\" minOccurs=\"0\" maxOccurs=\"unbounded\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>Accepts any valid widget</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:group>");
		out.println("</xs:sequence>");
		out.println("<xs:attribute name=\"input\" type=\"Input\" />");
		out.println("<xs:attribute name=\"size\" type=\"Size\" />");
		out.println("</xs:complexType>");
		
		out.println("<xs:complexType name=\"IfDeviceExclusions\">");
		out.println("<xs:sequence minOccurs=\"1\" maxOccurs=\"unbounded\">");
		out.println("<xs:element name=\"exclude\" type=\"IfDeviceExclusion\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>A exclusion to indicate that this device will not accept the above property.</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("</xs:sequence>");
		out.println("</xs:complexType>");
		
		out.println("<xs:complexType name=\"IfDeviceExclusion\">");
		out.println("<xs:attribute name=\"input\" type=\"Input\" />");
		out.println("<xs:attribute name=\"size\" type=\"Size\" />");
		out.println("</xs:complexType>");

		out.println("<xs:simpleType name=\"Input\">");
		out.println("<xs:restriction base=\"xs:string\">");
		Input[] inputs = Input.values();
		for (Input input : inputs)
		{
			out.println("<xs:enumeration value=\""+input.toString()+"\" />");
		}
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");

		out.println("<xs:simpleType name=\"Size\">");
		out.println("<xs:restriction base=\"xs:string\">");
		Size[] sizes = Size.values();
		for (Size size : sizes)
		{
			out.println("<xs:enumeration value=\""+size.toString()+"\" />");
		}
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");
    }

	/**
	 * 
	 * @param templateLibraries 
	 * @param out
	 */
	protected void generateCoreSchemasImport(Set<String> templateLibraries, PrintStream out)
	{
		Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
		while (libraries.hasNext())
		{
			String lib = libraries.next();
			out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.cruxframework.org/crux/"+lib+"\"/>");
		}
		for (String lib : templateLibraries)
		{
			out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.cruxframework.org/templates/"+lib+"\"/>");
		}
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateCoreScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"screen\" type=\"Screen\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.screenDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:group name=\"ScreenContent\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("</xs:group>");

		out.println("<xs:complexType name=\"Screen\" mixed=\"true\">");
		out.println("<xs:group ref=\"ScreenContent\" />");
		generateElementAttributesForAllViewElements(out);
		out.println("<xs:attribute name=\"smallViewport\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"largeViewport\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"disableRefresh\" type=\"xs:boolean\" default=\"false\" />");
		out.println("</xs:complexType>");
	}	

	/**
	 * 
	 * @param out
	 */
	protected void generateCoreDataProviderElements(PrintStream out)
	{
		out.println("<xs:element name=\"dataProvider\" type=\"DataProvider\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.dataProviderDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"DataProvider\">");
		generateCoreDataProviderCommonAttributes(out);
		out.println("<xs:attribute name=\"onLoadData\" type=\"xs:string\" use=\"required\" />");
		out.println("</xs:complexType>");

		out.println("<xs:element name=\"lazyDataProvider\" type=\"LazyDataProvider\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.lazyDataProviderDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"LazyDataProvider\">");
		generateCoreDataProviderCommonAttributes(out);
		out.println("<xs:attribute name=\"onMeasureData\" type=\"xs:string\" use=\"required\" />");
		out.println("<xs:attribute name=\"onFetchData\" type=\"xs:string\" use=\"required\" />");
		out.println("</xs:complexType>");

		out.println("<xs:element name=\"streamingDataProvider\" type=\"StreamingDataProvider\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.streamingDataProviderDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"StreamingDataProvider\">");
		out.println("<xs:attribute name=\"onFetchData\" type=\"xs:string\" use=\"required\" />");
		generateCoreDataProviderCommonAttributes(out);
		out.println("</xs:complexType>");
}

	private void generateCoreDataProviderCommonAttributes(PrintStream out)
    {
		out.println("<xs:attribute name=\"id\" type=\"xs:string\" use=\"required\" />");
		out.println("<xs:attribute name=\"dataObject\" type=\"xs:string\" use=\"required\" />");
	    out.println("<xs:attribute name=\"pageSize\" type=\"xs:integer\" />");
		out.println("<xs:attribute name=\"autoLoadData\" type=\"xs:boolean\" default=\"false\" />");
    }	

	/**
	 * 
	 * @param out
	 */
	protected void generateCoreSplashScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"splashScreen\" type=\"SplashScreen\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.splashScreenDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"SplashScreen\">");
		out.println("<xs:sequence minOccurs=\"0\" maxOccurs=\"1\">");
		out.println("<xs:element type=\"xs:string\" >");
		out.println("</xs:sequence>");
		out.println("<xs:attribute name=\"style\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"transactionDelay\" type=\"xs:integer\"/>");
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateCoreTypesSupportingBinding(PrintStream out)
    {
		out.println("<xs:simpleType name=\"_bindableInt\">");
		out.println("<xs:restriction base=\"xs:string\">");
		out.println("<xs:pattern value=\""+StringEscapeUtils.escapeXml("-?[0-9]+|@\\{.+\\}")+"\"/>");
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");
    
		out.println("<xs:simpleType name=\"_bindableBoolean\">");
		out.println("<xs:restriction base=\"xs:string\">");
		out.println("<xs:pattern value=\""+StringEscapeUtils.escapeXml("false|true|@\\{.+\\}")+"\"/>");
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");
		
		out.println("<xs:simpleType name=\"_bindableFloat\">");
		out.println("<xs:restriction base=\"xs:string\">");
		out.println("<xs:pattern value=\""+StringEscapeUtils.escapeXml("-?[0-9]+(\\.[0-9]+)?|@\\{.+\\}")+"\"/>");
		out.println("</xs:restriction>");
		out.println("</xs:simpleType>");
    }

	/**
	 * 
	 * @param out
	 * @param templateLibraries 
	 */
	protected void generateCoreWidgetsType(PrintStream out, Set<String> templateLibraries)
	{
		generateCoreWidgetsType(out, templateLibraries, "widgets", true, true);
	}

	/**
	 * 
	 * @param out
	 * @param templateLibraries
	 * @param groupName
	 * @param supportCrossDevice
	 */
	protected void generateCoreWidgetsType(PrintStream out, Set<String> templateLibraries, String groupName, boolean supportCrossDevice, boolean supportTemplates)
	{
		out.println("<xs:group name=\""+groupName+"\">");
		out.println("<xs:choice>");

		Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
		while (libraries.hasNext())
		{
			String lib = libraries.next();
			Iterator<String> factories = WidgetLibraries.getInstance().iterateRegisteredLibraryWidgetCreators(lib);
			if (factories != null)
			{
				while (factories.hasNext())
				{
					String factoryID = factories.next();
					out.println("<xs:element ref=\"_"+lib+":"+factoryID+"\" />");
				}
			}
		}

		if (supportTemplates)
		{
			for (String lib : templateLibraries)
			{
				Set<String> templates = Templates.getRegisteredLibraryWidgetTemplates(lib);
				if (templates != null)
				{
					for (String templateID : templates)
					{
						out.println("<xs:element ref=\"_"+lib+":"+templateID+"\" />");
					}
				}
			}
		}

		if (supportCrossDevice)
		{
			out.println("<xs:element ref=\"crossDevice\" />");
			out.println("<xs:element ref=\"ifDevice\" />");
		}
		out.println("</xs:choice>");
		out.println("</xs:group>");
	}

	/**
	 * 
	 * @param out
	 * @param annot
	 */
	protected void generateDocumentationForTypeFactory(PrintStream out, DeclarativeFactory annot)
    {
	    String elementDescription = annot.description();
		String demoURL = annot.infoURL();
		String illustration = annot.illustration();
		if ((elementDescription != null && elementDescription.length() > 0) ||
			(demoURL != null && demoURL.length() > 0) || 
			(illustration != null && illustration.length() > 0))
		{
			out.println("<xs:annotation>");
			if (elementDescription != null && elementDescription.length() > 0)
			{
				out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(elementDescription)+"</xs:documentation>");
			}
			if (demoURL != null && demoURL.length() > 0)
			{
				out.println("<xs:appinfo source=\""+StringEscapeUtils.escapeXml(demoURL)+"\">"+
						StringEscapeUtils.escapeXml(schemaMessages.moreInfoDescription())+"</xs:appinfo>");
			}
			if (illustration != null && illustration.length() > 0)
			{
				out.println("<xs:appinfo source=\""+StringEscapeUtils.escapeXml(illustration)+"\">"+
						StringEscapeUtils.escapeXml(schemaMessages.illustrationDescription())+"</xs:appinfo>");
			}
			out.println("</xs:annotation>");
		}
    }

	/**
	 * 
	 * @param out
	 */
	protected void generateElementAttributesForAllViewElements(PrintStream out)
	{
		out.println("<xs:attribute name=\"title\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"fragment\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useResource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useView\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClosing\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClose\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onResized\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onLoad\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onActivate\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onHistoryChanged\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"width\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"height\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"dataObject\" type=\"xs:string\"/>");
	}

	/**
	 * 
	 * @param out
	 * @param added
	 * @param processorClass
	 */
	protected void generateEvents(PrintStream out, Set<String> added, Class<?> processorClass)
	{
		TagEvents evts = processorClass.getAnnotation(TagEvents.class);
		if (evts != null)
		{
			for (TagEvent evt : evts.value())
			{
				Class<? extends EvtProcessor> evtBinder = evt.value();
				try
				{
					String eventName = evtBinder.getConstructor(WidgetCreator.class).newInstance((WidgetCreator<?>)null).getEventName();
					if (!added.contains(eventName))
					{
						out.print("<xs:attribute name=\""+eventName+"\" ");
						if (evt.required())
						{
							out.print("use=\"required\" ");
						}
						out.println(" >");
						
						String attrDescription = evt.description();
						if (attrDescription != null && attrDescription.length() > 0)
						{
							out.println("<xs:annotation>");
							out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(attrDescription)+"</xs:documentation>");
							out.println("</xs:annotation>");
						}
						out.println("</xs:attribute>");
						added.add(eventName);
					}
				}
				catch (Exception e)
				{
					logger.error("Error creating XSD File: Error generating events for Processor.", e);
				}
			}
		}
		TagEventsDeclaration evtsDecl = processorClass.getAnnotation(TagEventsDeclaration.class);
		if (evtsDecl != null)
		{
			for (TagEventDeclaration evt : evtsDecl.value())
			{
				out.println("<xs:attribute name=\""+evt.value()+"\" ");
				if (evt.required())
				{
					out.print("use=\"required\" ");
				}
				out.println(" >");
				String attrDescription = evt.description();
				if (attrDescription != null && attrDescription.length() > 0)
				{
					out.println("<xs:annotation>");
					out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(attrDescription)+"</xs:documentation>");
					out.println("</xs:annotation>");
				}
				out.println("</xs:attribute>");
			}
		}
	}
	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 */
	protected void generateEventsForFactory(PrintStream out, Class<?> widgetFactory, Set<String> added)
	{
		generateEvents(out, added, widgetFactory);
		Class<?> superclass = widgetFactory.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			generateEventsForFactory(out, superclass, added);
		}
		Class<?>[] interfaces = widgetFactory.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			generateEventsForFactory(out, interfaceClass, added);
		}
	}

	/**
	 * 
	 * @param out
	 * @param processorClass
	 */
	protected void generateEventsForProcessor(PrintStream out, Class<?> processorClass, Set<String> added)
	{
		try
		{
			generateEvents(out, added, processorClass);
			Class<?> superclass = processorClass.getSuperclass();
			if (superclass!= null && !superclass.equals(Object.class))
			{
				generateEventsForProcessor(out, superclass, added);
			}
		}
		catch (Exception e)
		{
			logger.error("Error creating XSD File: Error generating events for Processor.", e);
		}
	}
	
	/**
	 * 
	 * @param out
	 * @param library
	 * @param processorClass
	 * @param attributes
	 */
	protected void generateGenericChildWithAttributes(PrintStream out, String library, Class<? extends WidgetChildProcessor<?>> processorClass, TagConstraints attributes)
	{
		Class<?> type = attributes.type();
		String tagName = attributes.tagName();
		String tagDescription = attributes.description();
		
		if (AnyWidgetChildProcessor.class.isAssignableFrom(processorClass))
		{
			out.println("<xs:group ref=\"c:widgets\"  minOccurs=\""+attributes.minOccurs()+
					"\" maxOccurs=\""+attributes.maxOccurs()+"\">");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.anyWidgetsDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:group>");
		}
		else if (type.equals(Void.class))
		{
			if (tagName.length() == 0)
			{
				logger.error("Error creating XSD File: Tag Name expected in processor class: ["+processorClass.getName()+"].");
			}
			else
			{
				out.println("<xs:element name=\""+tagName+"\" minOccurs=\""+attributes.minOccurs()+
						"\" maxOccurs=\""+attributes.maxOccurs()+
						"\" type=\""+getSchemaType(processorClass, library, false)+"\">");
				if (tagDescription != null && tagDescription.length() > 0)
				{
					out.println("<xs:annotation>");
					out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(tagDescription)+"</xs:documentation>");
					out.println("</xs:annotation>");
				}
				out.println("</xs:element>");
			}
		}
		else if (AnyWidget.class.isAssignableFrom(type))
		{
			out.println("<xs:group ref=\"c:widgets\" minOccurs=\"" + attributes.minOccurs() +
					"\" maxOccurs=\"" + attributes.maxOccurs() + "\">");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.anyWidgetsDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:group>");
		}
		else if (AnyTag.class.isAssignableFrom(type) && tagName.length() == 0)
		{
			out.println("<xs:any minOccurs=\""+attributes.minOccurs()+ "\" maxOccurs=\""+attributes.maxOccurs()+ "\" />");			
		}
		else if (HTMLTag.class.isAssignableFrom(type) && tagName.length() == 0)
		{
			out.println("<xs:any minOccurs=\""+attributes.minOccurs()+ "\" maxOccurs=\""+attributes.maxOccurs()+ "\" namespace=\"http://www.w3.org/1999/xhtml\"/>");			
		}
		else
		{
			if ((tagName.length() == 0) && (WidgetCreator.class.isAssignableFrom(type)))
			{
				DeclarativeFactory annot = type.getAnnotation(DeclarativeFactory.class);
				if (annot != null)
				{
					tagName = annot.id();
				}
			}
			out.println("<xs:element name=\""+tagName+"\" minOccurs=\""+attributes.minOccurs()+
					"\" maxOccurs=\""+attributes.maxOccurs()+
					"\" type=\""+getSchemaType(type, library, false)+"\">");
			if (tagDescription != null && tagDescription.length() > 0)
			{
				out.println("<xs:annotation>");
				out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(tagDescription)+"</xs:documentation>");
				out.println("</xs:annotation>");
			}
			out.println("</xs:element>");
		}
	}

	/**
	 * 
	 */
	protected void generateOfflineSchema()
	{
		try
		{
			File coreFile = new File(destDir, "offline.xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();

			String targetNS = "http://www.cruxframework.org/offline";
			registerNamespaceForCatalog(targetNS, coreFile);

			PrintStream out = new PrintStream(coreFile);
			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/offline\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateOfflineScreenElement(out);

			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateOfflineScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"includes\" type=\"Includes\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.offlineIncludesDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		
		out.println("<xs:element name=\"excludes\" type=\"Excludes\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.offlineExcludesDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:element name=\"include\" type=\"Include\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.offlineIncludesDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		
		out.println("<xs:element name=\"exclude\" type=\"Exclude\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.offlineExcludesDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");

		out.println("<xs:complexType name=\"Includes\" mixed=\"true\">");
		out.println("<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">");
		out.println("<xs:element ref=\"include\" />");
		out.println("</xs:choice>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"Excludes\" mixed=\"true\">");
		out.println("<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">");
		out.println("<xs:element ref=\"exclude\" />");
		out.println("</xs:choice>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"Include\" mixed=\"true\">");
		out.println("<xs:attribute name=\"path\" type=\"xs:string\" use=\"required\"/>");
		out.println("</xs:complexType>");

		out.println("<xs:complexType name=\"Exclude\" mixed=\"true\">");
		out.println("<xs:attribute name=\"path\" type=\"xs:string\" use=\"required\"/>");
		out.println("</xs:complexType>");

		out.println("<xs:element name=\"offlineScreen\" type=\"OfflineScreen\">");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.offlineScreenDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"OfflineScreen\" mixed=\"true\">");
		out.println("<xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">");
		out.println("<xs:element ref=\"includes\" />");
		out.println("<xs:element ref=\"excludes\" />");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"moduleName\" type=\"xs:string\" use=\"required\"/>");
		out.println("<xs:attribute name=\"screenId\" type=\"xs:string\" use=\"required\"/>");
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param library
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	protected void generateSchemaForLibrary(String library, Set<String> templateLibraries)
	{
		try
		{
			File coreFile = new File(destDir, library+".xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();
			PrintStream out = new PrintStream(coreFile);

			String targetNS = "http://www.cruxframework.org/crux/" + library;
			registerNamespaceForCatalog(targetNS, coreFile);

			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/crux/"+library+"\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("xmlns:c=\"http://www.cruxframework.org/crux\" ");
			Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
			while (libraries.hasNext())
			{
				String lib = libraries.next();
				if (!lib.equals(library))
				{
					out.println("xmlns:_"+lib+"=\"http://www.cruxframework.org/crux/"+lib+"\" ");
				}
			}
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateSchemaImportsForLibrary(library, templateLibraries, out);

			Iterator<String> factories = WidgetLibraries.getInstance().iterateRegisteredLibraryWidgetCreators(library);
			while (factories.hasNext())
			{
				String id = factories.next();
				Class<? extends WidgetCreator<?>> widgetFactory = (Class<? extends WidgetCreator<?>>) 
						Class.forName(WidgetLibraries.getInstance().getFactoryClass(library, id));
				generateTypeForFactory(out, widgetFactory, library);
			}

			generateTypesForSubTags(out, library);
			generateTypesForEnumerations(out);

			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param library
	 */
	protected void generateSchemaForTemplateLibrary(String library)
	{
		try
		{
			File coreFile = new File(destDir, library+".xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();
			PrintStream out = new PrintStream(coreFile);

			String targetNS = "http://www.cruxframework.org/templates/" + library;
			registerNamespaceForCatalog(targetNS, coreFile);

			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/templates/"+library+"\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("xmlns:c=\"http://www.cruxframework.org/crux\" ");
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS+ "\" >");

			Set<String> templates = Templates.getRegisteredLibraryTemplates(library);
			for (String id : templates)
			{
				Document template = Templates.getTemplate(library, id);
				generateTypeForTemplate(out, template, id);
			}
			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param library
	 * @param templateLibraries
	 * @param out
	 */
	protected void generateSchemaImportsForLibrary(String library, Set<String> templateLibraries, PrintStream out)
	{
		out.println("<xs:import schemaLocation=\"core.xsd\" namespace=\"http://www.cruxframework.org/crux\"/>");
		Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
		while (libraries.hasNext())
		{
			String lib = libraries.next();
			if (!lib.equals(library))
			{
				out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.cruxframework.org/crux/"+lib+"\"/>");
			}
		}
		for (String lib : templateLibraries)
		{
			if (!lib.equals(library))
			{
				out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.cruxframework.org/templates/"+lib+"\"/>");
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param attributes
	 * @param children
	 */
	protected void generateSequenceChild(PrintStream out, TagConstraints attributes, TagChildren children, String library) 
	{
		try
		{
			out.print("<xs:sequence ");
			if (attributes!= null)
			{
				out.print("minOccurs=\""+attributes.minOccurs()+"\" ");
				out.print("maxOccurs=\""+attributes.maxOccurs()+"\" ");
			}
			out.println(">");
			for (TagChild child : children.value())
			{
				generateChild(out, child, true, library);
			}
			out.println("</xs:sequence>");
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateTemplateElement(PrintStream out)
	{
		out.println("<xs:element name=\"template\" type=\"Template\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.templateDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"Template\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"library\" type=\"xs:string\" use=\"required\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useResource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param templateLibraries
	 */
	protected void generateTemplateSchema(Set<String> templateLibraries)
	{
		try
		{
			File coreFile = new File(destDir, "template.xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();

			String targetNS = "http://www.cruxframework.org/templates";
			registerNamespaceForCatalog(targetNS, coreFile);

			PrintStream out = new PrintStream(coreFile);
			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/templates\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateCoreSchemasImport(templateLibraries, out);

			generateTemplateElement(out);
			generateTemplateSectionElement(out);	

			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateTemplateSectionElement(PrintStream out)
	{
		out.println("<xs:element name=\"section\" type=\"Section\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.templateSectionDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"Section\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"name\" type=\"xs:string\" use=\"required\"/>");
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 */
	protected void generateTypeForFactory(PrintStream out, Class<? extends WidgetCreator<?>> widgetFactory, String library)
	{
		DeclarativeFactory annot = widgetFactory.getAnnotation(DeclarativeFactory.class);
		String elementName = annot.id();

		out.println("<xs:element name=\""+elementName+"\" type=\"T"+elementName+"\">");

		generateDocumentationForTypeFactory(out, annot);
		
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"T"+elementName+"\">");
		boolean hasTextChild = factorySupportsInnerText(widgetFactory);
		if (hasTextChild)
		{
			out.println("<xs:simpleContent>");
			out.println("<xs:extension base=\"xs:string\">");
		}
		else
		{
			generateChildrenForFactory(out, widgetFactory, library);
		}
		generateAttributesForFactory(out, widgetFactory, library, new HashSet<String>());
		generateEventsForFactory(out, widgetFactory, new HashSet<String>());
		if (hasTextChild)
		{
			out.println("</xs:extension>");
			out.println("</xs:simpleContent>");
		}
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param out
	 * @param template
	 */
	protected void generateTypeForTemplate(PrintStream out, Document template, String templateName)
	{
		out.println("<xs:element name=\""+templateName+"\" type=\"T"+templateName+"\" />");
		out.println("<xs:complexType name=\"T"+templateName+"\">");

		generateChildrenForTemplate(out, template);
		generateAttributesForTemplate(out, template);

		out.println("</xs:complexType>");
	}		

	/**
	 * 
	 * @param out
	 */
	@SuppressWarnings("unchecked")
	protected void generateTypesForEnumerations(PrintStream out)
	{
		for (String enumType : enumTypes.keySet())
		{
			out.println("<xs:simpleType name=\""+enumType+"\">");
			out.println("<xs:restriction base=\"xs:string\">");

			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) enumTypes.get(enumType);
			Enum<?>[] enumConstants=enumClass.getEnumConstants();
			for (Enum<?> enumConstant : enumConstants)
			{
				out.println("<xs:enumeration value=\""+enumConstant.toString()+"\" />");
			}

			out.println("</xs:restriction>");
			out.println("</xs:simpleType>");

		}
		enumTypes.clear();
	}

	/**
	 * 
	 * @param out
	 * @param library
	 */
	protected void generateTypesForSubTags(PrintStream out, String library)
	{
		Set<String> added = new HashSet<String>();
		while (subTagTypes.size() > 0)
		{
			Class<? extends WidgetChildProcessor<?>> type = subTagTypes.pop();
			String elementName = type.getCanonicalName().replace('.', '_');
			if (!added.contains(elementName))
			{
				out.println("<xs:complexType name=\""+elementName+"\">");

				boolean hasTextChild = processorSupportsInnerText(type);
				if (hasTextChild)
				{
					out.println("<xs:simpleContent>");
					out.println("<xs:extension base=\"xs:string\">");
				}
				else
				{
					generateChildrenForProcessor(out, type, library);
				}
				generateAttributesForProcessor(out, type, library, new HashSet<String>());
				generateEventsForProcessor(out, type, new HashSet<String>());
				if (hasTextChild)
				{
					out.println("</xs:extension>");
					out.println("</xs:simpleContent>");
				}				
				out.println("</xs:complexType>");
				added.add(elementName);
			}
		}
	}

	/**
	 * @param templateLibraries
	 */
	protected void generateViewSchema(Set<String> templateLibraries)
	{
		try
		{
			File coreFile = new File(destDir, "view.xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();

			String targetNS = "http://www.cruxframework.org/view";
			registerNamespaceForCatalog(targetNS, coreFile);

			PrintStream out = new PrintStream(coreFile);
			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/view\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("xmlns:c=\"http://www.cruxframework.org/crux\" ");
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateViewSchemasImport(out);

			out.println("<xs:element name=\"view\" type=\"View\" >");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.viewDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:element>");
			out.println("<xs:complexType name=\"View\">");
			out.println("<xs:choice maxOccurs=\"unbounded\">");
			out.println("<xs:element ref=\"c:dataProvider\" />");
			out.println("<xs:element ref=\"c:lazyDataProvider\" />");
			out.println("<xs:element ref=\"c:streamingDataProvider\" />");
			out.println("<xs:group ref=\"c:widgets\" >");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.anyWidgetsDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:group>");
			out.println("<xs:any namespace=\"http://www.w3.org/1999/xhtml\"/>");
			out.println("</xs:choice>");
			generateElementAttributesForAllViewElements(out);
			out.println("<xs:attribute name=\"onUnload\" type=\"xs:string\"/>");
			out.println("<xs:attribute name=\"onDeactivate\" type=\"xs:string\"/>");
			out.println("</xs:complexType>");

			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param out
	 */
	protected void generateViewSchemasImport(PrintStream out)
	{
		out.println("<xs:import schemaLocation=\"core.xsd\" namespace=\"http://www.cruxframework.org/crux\"/>");
		Iterator<String> libraries = WidgetLibraries.getInstance().iterateRegisteredLibraries();
		while (libraries.hasNext())
		{
			String lib = libraries.next();
			out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.cruxframework.org/crux/"+lib+"\"/>");
		}
	}

	/**
	 * 
	 * @param templateLibraries
	 */
	protected void generateXDeviceSchema(Set<String> templateLibraries)
	{
		try
		{
			File coreFile = new File(destDir, "xdevice.xsd");
			if (coreFile.exists())
			{
				coreFile.delete();
			}
			coreFile.createNewFile();

			String targetNS = "http://www.cruxframework.org/xdevice";
			registerNamespaceForCatalog(targetNS, coreFile);

			PrintStream out = new PrintStream(coreFile);
			out.println("<xs:schema ");
			out.println("xmlns=\"http://www.cruxframework.org/xdevice\" ");
			out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
			out.println("xmlns:c=\"http://www.cruxframework.org/crux\" ");
			out.println("attributeFormDefault=\"unqualified\" ");
			out.println("elementFormDefault=\"qualified\" ");
			out.println("targetNamespace=\"" + targetNS + "\" >");

			generateViewSchemasImport(out);

			out.println("<xs:element name=\"xdevice\" type=\"XDevice\" >");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.xdeviceDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:element>");
			out.println("<xs:complexType name=\"XDevice\">");
			out.println("<xs:choice maxOccurs=\"unbounded\">");
			out.println("<xs:group ref=\"c:widgetsCrossDev\" >");
			out.println("<xs:annotation>");
			out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.anyWidgetsDescription())+"</xs:documentation>");
			out.println("</xs:annotation>");
			out.println("</xs:group>");
			out.println("<xs:any namespace=\"http://www.w3.org/1999/xhtml\"/>");
			out.println("</xs:choice>");
			out.println("<xs:attribute name=\"useController\" type=\"xs:string\" use=\"required\"/>");
			out.println("<xs:attribute name=\"useResource\" type=\"xs:string\"/>");
			out.println("<xs:attribute name=\"width\" type=\"xs:string\"/>");
			out.println("<xs:attribute name=\"height\" type=\"xs:string\"/>");
			out.println("</xs:complexType>");

			out.println("</xs:schema>");
			out.close();
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}	

	/**
	 * @param value
	 * @return
	 */
	protected String getRelativeName(File value)
	{
		String absolutePath = value.toURI().getPath();
		String projectPath = projectBaseDir.toURI().getPath();
		return absolutePath.substring(projectPath.length());
	}

	/**
	 * 
	 * @param type
	 * @param library
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getSchemaType(Class<?> type, String library, boolean supportsBinding)
	{
		if (String.class.isAssignableFrom(type))
		{
			return "xs:string";
		}
		else if (Boolean.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableBoolean":"xs:boolean";
		}
		else if (Integer.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableInt":"xs:int";
		}
		else if (Short.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableInt":"xs:short";
		}
		else if (Long.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableInt":"xs:long";
		}
		else if (Double.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableFloat":"xs:double";
		}
		else if (Float.class.isAssignableFrom(type))
		{
			return supportsBinding?"c:_bindableFloat":"xs:float";
		}
		else if (Character.class.isAssignableFrom(type))
		{
			return "xs:string";
		}
		else if (type.isEnum())
		{
			String typeName = type.getCanonicalName().replace('.', '_');
			this.enumTypes.put(typeName, type);
			return typeName;
		}
		else if (WidgetCreator.class.isAssignableFrom(type))
		{
			DeclarativeFactory annot = type.getAnnotation(DeclarativeFactory.class);
			if (annot != null)
			{
				if (annot.library().equals(library))
				{
					return "T"+annot.id();
				}
				else
				{
					return "_"+annot.library()+":T"+annot.id();
				}
			}
		}
		else if (WidgetChildProcessor.class.isAssignableFrom(type))
		{
			String typeName = type.getCanonicalName().replace('.', '_');
			this.subTagTypes.add((Class<? extends WidgetChildProcessor<?>>) type);
			return typeName;
		}
		else if (AnyTag.class.isAssignableFrom(type))
		{
			return "xs:anyType";
		}
		else if (HTMLTag.class.isAssignableFrom(type))
		{
			return "xs:anyType";
		}
		else if (ResourcePrototype.class.isAssignableFrom(type))
		{
			return "xs:string";
		}
		return null;
	}

	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	protected boolean hasTextChild(Class<?> processorClass)
	{
		TagChildren tagChildren = processorClass.getAnnotation(TagChildren.class);
		return (tagChildren != null && tagChildren.value().length == 1 && TextChildProcessor.class.isAssignableFrom(tagChildren.value()[0].value()));
	}

	/**
	 * @param projectBaseDir
	 * @param webDir
	 */
	protected void initializeSchemaGenerator(File projectBaseDir, File webDir)
	{
		try
		{
			URL[] urls = ClasspathUrlFinder.findClassPaths();
			Scanners.setSearchURLs(urls);
			
			if (webDir == null)
			{
				webDir = new File(projectBaseDir, "war/");
			}
		}
		catch (Exception e)
		{
			throw new SchemaGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param tagChild
	 * @return
	 */
	protected boolean isChildAnAgregator(TagChild tagChild)
	{
		Class<? extends WidgetChildProcessor<?>> processorClass = tagChild.value();
		return processorClass != null && (ChoiceChildProcessor.class.isAssignableFrom(processorClass) || 
				SequenceChildProcessor.class.isAssignableFrom(processorClass) ||
				AllChildProcessor.class.isAssignableFrom(processorClass));
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	protected boolean processorSupportsInnerText(Class<? extends WidgetChildProcessor<?>> processorClass)
	{
		try
		{
			return hasTextChild(processorClass);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * @param targetNS
	 * @param coreFile
	 */
	protected void registerNamespaceForCatalog(String targetNS, File file)
	{
		this.namespacesForCatalog.put(targetNS, file);
	}
}