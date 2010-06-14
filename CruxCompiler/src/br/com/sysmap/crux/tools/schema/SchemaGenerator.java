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
package br.com.sysmap.crux.tools.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.AllChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.TextChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.declarativeui.DeclarativeUIMessages;
import br.com.sysmap.crux.core.declarativeui.template.TemplateParser;
import br.com.sysmap.crux.core.declarativeui.template.Templates;
import br.com.sysmap.crux.core.declarativeui.template.TemplatesScanner;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxScreenBridge;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.StreamUtils;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessingException;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SchemaGenerator
{
	/**
	 * @return
	 */
	private static ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("SchemaGenerator");
		parametersProcessor.addSupportedParameter(new ConsoleParameter("projectBaseDir", "The project folder."));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("outputDir", "The folder where the files will be created."));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-generateModuleSchema", "Generates also the modules.xsd file.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;
	}
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void generateSchemas(File projectBaseDir, String outputDir, boolean generateModuleSchema) throws IOException
	{
		ConfigurationFactory.getConfigurations().setEnableHotDeploymentForWebDirs("false");
		ClassScanner.initialize(ClasspathUrlFinder.findClassPaths());
		TemplatesScanner.initialize(ClasspathUrlFinder.findClassPaths());
		SchemaGenerator generator = new SchemaGenerator(projectBaseDir, outputDir);
		generator.generateSchemas(generateModuleSchema);
		generator.generateCatalog();
	}
	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void generateSchemas(String projectBaseDir, String destRelativeDir, boolean generateModuleSchema) throws IOException
	{
		File projectDir = new File(projectBaseDir);
		generateSchemas(projectDir, destRelativeDir, generateModuleSchema);
	}
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			try
			{
				ConsoleParametersProcessor parametersProcessor = createParametersProcessor();
				Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

				if (parameters.containsKey("-help") || parameters.containsKey("-h"))
				{
					parametersProcessor.showsUsageScreen();
				}
				else
				{
					SchemaGenerator.generateSchemas(parameters.get("projectBaseDir").getValue(), 
													parameters.get("outputDir").getValue(), 
													parameters.containsKey("-generateModuleSchema"));
				}
			}
			catch (ConsoleParametersProcessingException e)
			{
				System.out.println("Program aborted");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private File projectBaseDir;
	private File destDir;
	private Map<String, Class<?>> enumTypes;
	private Map<String, File> namespacesForCatalog;
		
	private Stack<Class<? extends WidgetChildProcessor<?>>> subTagTypes;
	private static final Log logger = LogFactory.getLog(SchemaGenerator.class);
	
	private DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);
	
	private TemplateParser templateParser;
	
	/**
	 * 
	 * @param destDir
	 */
	private SchemaGenerator(File projectBaseDir, String destRelativeDir)
	{
		this.projectBaseDir = projectBaseDir;
		this.destDir = new File(projectBaseDir, destRelativeDir);
		this.destDir.mkdirs();
		this.enumTypes = new HashMap<String, Class<?>>();
		this.namespacesForCatalog = new HashMap<String, File>();
		this.subTagTypes = new Stack<Class<? extends WidgetChildProcessor<?>>>();
		this.templateParser = new TemplateParser();
		clearCruxBridgeProperties();
	}	
	
	/**
	 * 
	 * @param destRelativeDir
	 */
	public SchemaGenerator(String projectBaseDir, String destRelativeDir)
	{
		this(new File(projectBaseDir), destRelativeDir);
	}	

	/**
	 * 
	 */
	protected void clearCruxBridgeProperties()
    {
	    CruxScreenBridge.getInstance().registerScanAllowedPackages("");
		CruxScreenBridge.getInstance().registerScanIgnoredPackages("");
		CruxScreenBridge.getInstance().registerLastPageRequested("");
    }

	/**
	 * @throws IOException
	 */
	private void copyModuleSchema() throws IOException
	{
		File xhtmlFile = new File(destDir, "module.xsd");
		if (xhtmlFile.exists())
		{
			xhtmlFile.delete();
		}
		xhtmlFile.createNewFile();
		FileOutputStream out = new FileOutputStream(xhtmlFile);

		String targetNS = "http://www.sysmap.com.br/module";
		registerNamespaceForCatalog(targetNS, xhtmlFile);
		
		StreamUtils.write(getClass().getResourceAsStream("/META-INF/module.xsd"), out, true);
	}

	/**
	 * @throws IOException
	 */
	private void copyXHTMLSchema() throws IOException
	{
		File xhtmlFile = new File(destDir, "xhtml.xsd");
		if (xhtmlFile.exists())
		{
			xhtmlFile.delete();
		}
		xhtmlFile.createNewFile();
		FileOutputStream out = new FileOutputStream(xhtmlFile);

		String targetNS = "http://www.w3.org/1999/xhtml";
		registerNamespaceForCatalog(targetNS, xhtmlFile);
		
		StreamUtils.write(getClass().getResourceAsStream("/META-INF/xhtml.xsd"), out, true);
	}

	/**
	 * 
	 * @param widgetFactory
	 * @return
	 */
	private boolean factorySupportsInnerText(Class<? extends WidgetFactory<?>> widgetFactory)
	{
		try
		{
			Method method = widgetFactory.getMethod("processChildren", new Class[]{WidgetFactoryContext.class});
			return hasTextChild(method);
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
	private void generateAllChild(PrintStream out, TagChildAttributes attributes, TagChildren children, String library) throws NoSuchMethodException
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
	 * @param method
	 */
	private void generateAttributes(PrintStream out, String library, Set<String> added, Method method)
	{
		TagAttributesDeclaration attrsDecl = method.getAnnotation(TagAttributesDeclaration.class);
		if (attrsDecl != null)
		{
			for (TagAttributeDeclaration attr : attrsDecl.value())
			{
				if (!added.contains(attr.value()))
				{
					out.print("<xs:attribute name=\""+attr.value()+"\" type=\""+getSchemaType(attr.type(), library)+"\" ");
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
					
					out.println("/>");
					added.add(attr.value());
				}
			}
		}
		TagAttributes attrs = method.getAnnotation(TagAttributes.class);
		if (attrs != null)
		{
			for (TagAttribute attr : attrs.value())
			{
				if (!added.contains(attr.value()))
				{
					out.print("<xs:attribute name=\""+attr.value()+"\" type=\""+getSchemaType(attr.type(), library)+"\" ");
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
					
					out.println("/>");
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
	private void generateAttributesForFactory(PrintStream out, Class<?> widgetFactory, String library, Set<String> added)
	{
		Method method = getMethod(widgetFactory, "processAttributes");
		if (method != null)
		{
			generateAttributes(out, library, added, method);
		}
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
	private void generateAttributesForProcessor(PrintStream out, Class<?> processorClass, String library, Set<String> added)
	{
		try
		{
			Method method = getProcessChildrenMethod(processorClass);
			if (method != null)
			{
				generateAttributes(out, library, added, method);
			}
			
			Class<?> superclass = processorClass.getSuperclass();
			if (superclass!= null && !superclass.equals(Object.class))
			{
				generateAttributesForProcessor(out, superclass, library, added);
			}
		}
		catch (Exception e)
		{
			logger.error(messages.schemaGeneratorErrorGeneratingAttributesForProcessor(), e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param template
	 */
	private void generateAttributesForTemplate(PrintStream out, Document template)
	{
		Set<String> attributesForTemplate = templateParser.getParameters(template);
		for (String attrValue : attributesForTemplate)
		{
			out.println("<xs:attribute name=\""+attrValue+"\" type=\"xs:string\" use=\"required\" />");
		}
	}

	/**
	 * @param out
	 */
	private void generateCatalog() throws IOException
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
			stream.close();
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
	private void generateChild(PrintStream out, TagChild tagChild, boolean parentIsAnAgregator, String library) throws SecurityException, NoSuchMethodException
	{
		Class<? extends WidgetChildProcessor<?>> processorClass = tagChild.value();
		TagChildAttributes attributes = ClassUtils.getChildtrenAttributesAnnotation(processorClass);
		Method processorMethod = processorClass.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
		TagChildren children = processorMethod.getAnnotation(TagChildren.class);
		
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
				out.println("<xs:group ref=\"c:widgets\" />");
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param library
	 * @param method
	 * @throws NoSuchMethodException
	 */
	private void generateChildren(PrintStream out, String library, Method method) throws NoSuchMethodException
	{
		TagChildren annot = method.getAnnotation(TagChildren.class);
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
	private void generateChildrenForFactory(PrintStream out, Class<? extends WidgetFactory<?>> widgetFactory, String library)
	{
		try
		{
			Method method = widgetFactory.getMethod("processChildren", new Class[]{WidgetFactoryContext.class});
			generateChildren(out, library, method);
		}
		catch (Exception e)
		{
			logger.error(messages.schemaGeneratorErrorGeneratingChildrenForFactory(), e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 * @param library
	 */
	private void generateChildrenForProcessor(PrintStream out, Class<? extends WidgetChildProcessor<?>> processorClass, String library)
	{
		try
		{
			Method method = processorClass.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
			generateChildren(out, library, method);
		}
		catch (Exception e)
		{
			logger.error(messages.schemaGeneratorErrorProcessChildrenMethodNotFound(), e);
		}
	}
	
	/**
	 * 
	 * @param out
	 * @param template
	 */
	private void generateChildrenForTemplate(PrintStream out, Document template)
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
	private void generateChoiceChild(PrintStream out, TagChildAttributes attributes, TagChildren children, String library) throws NoSuchMethodException
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
	 * @param libraries
	 * @param templateLibraries 
	 * @throws IOException
	 */
	private void generateCoreSchema(Set<String> libraries) throws IOException
	{
		File coreFile = new File(destDir, "core.xsd");
		if (coreFile.exists())
		{
			coreFile.delete();
		}
		coreFile.createNewFile();
		
		String targetNS = "http://www.sysmap.com.br/crux";
		registerNamespaceForCatalog(targetNS, coreFile);
		
		PrintStream out = new PrintStream(coreFile);
		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/crux\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		for (String lib : libraries)
		{
			out.println("xmlns:_"+lib+"=\"http://www.sysmap.com.br/crux/"+lib+"\" ");
		}
		out.println("attributeFormDefault=\"unqualified\" ");
		out.println("elementFormDefault=\"qualified\" ");
		out.println("targetNamespace=\"" + targetNS + "\" >");
		
		generateCoreSchemasImport(libraries, out);
		generateCoreSplashScreenElement(out);
		generateCoreScreenElement(out);	
		generateCoreWidgetsType(out, libraries);	
		
		out.println("</xs:schema>");
		out.close();
	}

	/**
	 * 
	 * @param libraries
	 * @param templateLibraries 
	 * @param out
	 */
	private void generateCoreSchemasImport(Set<String> libraries, PrintStream out)
	{
		for (String lib : libraries)
		{
			out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.sysmap.com.br/crux/"+lib+"\"/>");
		}
	}

	/**
	 * 
	 * @param out
	 */
	private void generateCoreScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"screen\" type=\"Screen\"/>");

		out.println("<xs:group name=\"ScreenContent\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("</xs:group>");
		
		out.println("<xs:complexType name=\"Screen\" mixed=\"true\">");
		out.println("<xs:group ref=\"ScreenContent\" />");
		out.println("<xs:attribute name=\"manageHistory\" type=\"xs:boolean\" default=\"false\"/>");
		out.println("<xs:attribute name=\"title\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useSerializable\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClosing\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClose\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onResized\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onLoad\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onHistoryChanged\" type=\"xs:string\"/>");
		out.println("</xs:complexType>");
	}
	
	/**
	 * 
	 * @param out
	 */
	private void generateCoreSplashScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"splashScreen\" type=\"SplashScreen\"/>");

		out.println("<xs:complexType name=\"SplashScreen\">");
		out.println("<xs:attribute name=\"style\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"transactionDelay\" type=\"xs:integer\"/>");
		out.println("</xs:complexType>");
	}	
	
	/**
	 * 
	 * @param out
	 * @param libraries
	 * @param templateLibraries 
	 */
	private void generateCoreWidgetsType(PrintStream out, Set<String> libraries)
	{
		out.println("<xs:group name=\"widgets\">");
		out.println("<xs:choice>");
		
		for (String lib : libraries)
		{
			Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(lib);
			for (String factoryID : factories)
			{
				out.println("<xs:element ref=\"_"+lib+":"+factoryID+"\" />");
			}
		}
		
		out.println("</xs:choice>");
		out.println("</xs:group>");
	}

	/**
	 * 
	 * @param out
	 * @param added
	 * @param method
	 */
	private void generateEvents(PrintStream out, Set<String> added, Method method)
	{
		TagEvents evts = method.getAnnotation(TagEvents.class);
		if (evts != null)
		{
			for (TagEvent evt : evts.value())
			{
				Class<? extends EvtBinder<?>> evtBinder = evt.value();
				try
				{
					String eventName = evtBinder.newInstance().getEventName();
					if (!added.contains(eventName))
					{
						out.println("<xs:attribute name=\""+eventName+"\" />");
						added.add(eventName);
					}
				}
				catch (Exception e)
				{
					logger.error(messages.schemaGeneratorErrorGeneratingEventsForProcessor(), e);
				}
			}
		}
		TagEventsDeclaration evtsDecl = method.getAnnotation(TagEventsDeclaration.class);
		if (evtsDecl != null)
		{
			for (TagEventDeclaration evt : evtsDecl.value())
			{
				out.println("<xs:attribute name=\""+evt.value()+"\" />");
			}
		}
	}

	/**
	 * 
	 * @param out
	 * @param widgetFactory
	 */
	private void generateEventsForFactory(PrintStream out, Class<?> widgetFactory, Set<String> added)
	{
		Method method = getMethod(widgetFactory, "processEvents");
		if (method != null)
		{
			generateEvents(out, added, method);
		}
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
	private void generateEventsForProcessor(PrintStream out, Class<?> processorClass, Set<String> added)
	{
		try
		{
			Method method = processorClass.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
			if (method != null)
			{
				generateEvents(out, added, method);
			}
			Class<?> superclass = processorClass.getSuperclass();
			if (superclass!= null && !superclass.equals(Object.class))
			{
				generateEventsForProcessor(out, superclass, added);
			}
		}
		catch (Exception e)
		{
			logger.error(messages.schemaGeneratorErrorGeneratingEventsForProcessor(), e);
		}
	}

	/**
	 * 
	 * @param out
	 * @param library
	 * @param processorClass
	 * @param attributes
	 */
	private void generateGenericChildWithAttributes(PrintStream out, String library, Class<? extends WidgetChildProcessor<?>> processorClass, TagChildAttributes attributes)
	{
		Class<?> type = attributes.type();
		String tagName = attributes.tagName();
		if (AnyWidgetChildProcessor.class.isAssignableFrom(processorClass))
		{
			out.println("<xs:group ref=\"c:widgets\" minOccurs=\""+attributes.minOccurs()+
					    "\" maxOccurs=\""+attributes.maxOccurs()+"\"/>");
		}
		else if (type.equals(Void.class))
		{
			if (tagName.length() == 0)
			{
				logger.error(messages.schemaGeneratorErrorTagNameExpected(processorClass.getName()));
			}
			else
			{
				out.println("<xs:element name=\""+tagName+"\" minOccurs=\""+attributes.minOccurs()+
					    "\" maxOccurs=\""+attributes.maxOccurs()+
					    "\" type=\""+getSchemaType(processorClass, library)+"\"/>");
			}
		}
		else if (AnyWidget.class.isAssignableFrom(type))
		{
			out.println("<xs:group ref=\"c:widgets\" />");
		}
		else if (AnyTag.class.isAssignableFrom(type) && tagName.length() == 0)
		{
			out.println("<xs:any minOccurs=\""+attributes.minOccurs()+ "\" maxOccurs=\""+attributes.maxOccurs()+ "\" />");			
		}
		else
		{
			if ((tagName.length() == 0) && (WidgetFactory.class.isAssignableFrom(type)))
			{
				DeclarativeFactory annot = type.getAnnotation(DeclarativeFactory.class);
				if (annot != null)
				{
					tagName = annot.id();
				}
			}
			out.println("<xs:element name=\""+tagName+"\" minOccurs=\""+attributes.minOccurs()+
					    "\" maxOccurs=\""+attributes.maxOccurs()+
					    "\" type=\""+getSchemaType(type, library)+"\"/>");
		}
	}

	/**
     * 
     * @param library
     * @throws IOException 
     */
	private void generateSchemaForLibrary(String library, Set<String> allLibraries) throws IOException
	{
		File coreFile = new File(destDir, library+".xsd");
		if (coreFile.exists())
		{
			coreFile.delete();
		}
		coreFile.createNewFile();
		PrintStream out = new PrintStream(coreFile);

		String targetNS = "http://www.sysmap.com.br/crux/" + library;
		registerNamespaceForCatalog(targetNS, coreFile);
		
		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/crux/"+library+"\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		out.println("xmlns:c=\"http://www.sysmap.com.br/crux\" ");
		for (String lib : allLibraries)
		{
			if (!lib.equals(library))
			{
				out.println("xmlns:_"+lib+"=\"http://www.sysmap.com.br/crux/"+lib+"\" ");
			}
		}
		out.println("attributeFormDefault=\"unqualified\" ");
		out.println("elementFormDefault=\"qualified\" ");
		out.println("targetNamespace=\"" + targetNS + "\" >");
		
		generateSchemaImportsForLibrary(library, allLibraries, out);
		
		Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(library);
		for (String id : factories)
		{
			Class<? extends WidgetFactory<?>> widgetFactory = WidgetConfig.getClientClass(library, id);
			generateTypeForFactory(out, widgetFactory, library);
		}
		
		generateTypesForSubTags(out, library);
		generateTypesForEnumerations(out);
		
		out.println("</xs:schema>");
		out.close();
	}

	/**
	 * 
	 * @param library
	 */
	private void generateSchemaForTemplateLibrary(String library) throws IOException
	{
		File coreFile = new File(destDir, library+".xsd");
		if (coreFile.exists())
		{
			coreFile.delete();
		}
		coreFile.createNewFile();
		PrintStream out = new PrintStream(coreFile);
		
		String targetNS = "http://www.sysmap.com.br/templates/" + library;
		registerNamespaceForCatalog(targetNS, coreFile);

		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/templates/"+library+"\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		out.println("xmlns:c=\"http://www.sysmap.com.br/crux\" ");
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

	/**
	 * 
	 * @param library
	 * @param allLibraries
	 * @param out
	 */
	private void generateSchemaImportsForLibrary(String library, Set<String> allLibraries, PrintStream out)
	{
		out.println("<xs:import schemaLocation=\"core.xsd\" namespace=\"http://www.sysmap.com.br/crux\"/>");
		for (String lib : allLibraries)
		{
			if (!lib.equals(library))
			{
				out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.sysmap.com.br/crux/"+lib+"\"/>");
			}
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void generateSchemas(boolean generateModuleSchema) throws IOException
	{
		Set<String> libraries = WidgetConfig.getRegisteredLibraries();
		for (String library : libraries)
		{
			logger.info(messages.schemaGeneratorCreatingLibraryXSD(library));
			generateSchemaForLibrary(library, libraries);
		}

		logger.info(messages.schemaGeneratorCreatingTemplateXSD());
		generateTemplateSchema(libraries);

		Set<String> templateLibraries = Templates.getRegisteredLibraries();
		for (String library : templateLibraries)
		{
			logger.info(messages.schemaGeneratorCreatingTemplateLibrary(library));
			generateSchemaForTemplateLibrary(library);
		}
		
		logger.info(messages.schemaGeneratorCreatingCoreXSD());
		generateCoreSchema(libraries);
		
		copyXHTMLSchema();
		
		if (generateModuleSchema)
		{
			copyModuleSchema();
		}

		logger.info(messages.schemaGeneratorXSDFilesGenerated());
	}

	/**
	 * 
	 * @param out
	 * @param attributes
	 * @param children
	 * @throws NoSuchMethodException
	 */
	private void generateSequenceChild(PrintStream out, TagChildAttributes attributes, TagChildren children, String library) throws NoSuchMethodException
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

	/**
	 * 
	 * @param out
	 */
	private void generateTemplateElement(PrintStream out)
	{
		out.println("<xs:element name=\"template\" type=\"Template\" />");
		out.println("<xs:complexType name=\"Template\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"library\" type=\"xs:string\" use=\"required\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useSerializable\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("</xs:complexType>");
	}

	/**
	 * 
	 * @param libraries
	 * @throws IOException
	 */
	private void generateTemplateSchema(Set<String> libraries) throws IOException
	{
		File coreFile = new File(destDir, "template.xsd");
		if (coreFile.exists())
		{
			coreFile.delete();
		}
		coreFile.createNewFile();
		
		String targetNS = "http://www.sysmap.com.br/templates";
		registerNamespaceForCatalog(targetNS, coreFile);
		
		PrintStream out = new PrintStream(coreFile);
		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/templates\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		out.println("attributeFormDefault=\"unqualified\" ");
		out.println("elementFormDefault=\"qualified\" ");
		out.println("targetNamespace=\"" + targetNS + "\" >");
		
		generateCoreSchemasImport(libraries, out);

		generateTemplateElement(out);
		generateTemplateSectionElement(out);	
		
		out.println("</xs:schema>");
		out.close();
	}

	/**
	 * 
	 * @param out
	 */
	private void generateTemplateSectionElement(PrintStream out)
	{
		out.println("<xs:element name=\"section\" type=\"Section\" />");
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
	private void generateTypeForFactory(PrintStream out, Class<? extends WidgetFactory<?>> widgetFactory, String library)
	{
		DeclarativeFactory annot = widgetFactory.getAnnotation(DeclarativeFactory.class);
		String elementName = annot.id();
		
		out.println("<xs:element name=\""+elementName+"\" type=\"T"+elementName+"\"/>");
				
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
	private void generateTypeForTemplate(PrintStream out, Document template, String templateName)
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
	private void generateTypesForEnumerations(PrintStream out)
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
	private void generateTypesForSubTags(PrintStream out, String library)
	{
		Set<String> added = new HashSet<String>();
		while (subTagTypes.size() > 0)
		{
			Class<? extends WidgetChildProcessor<?>> type = subTagTypes.pop();
			String elementName = ClassUtils.getClassSourceName(type).replace('.', '_');
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
	 * 
	 * @param factoryClass
	 * @param methodName
	 * @return
	 */
	private Method getMethod(Class<?> factoryClass, String methodName)
	{
		try
		{
			return factoryClass.getDeclaredMethod(methodName, new Class[]{WidgetFactoryContext.class});
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @param factoryClass
	 * @param methodName
	 * @return
	 */
	private Method getProcessChildrenMethod(Class<?> factoryClass)
	{
		try
		{
			return factoryClass.getDeclaredMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * @param value
	 * @return
	 * @throws IOException 
	 */
	private String getRelativeName(File value) throws IOException
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
	private String getSchemaType(Class<?> type, String library)
	{
		if (String.class.isAssignableFrom(type))
		{
			return "xs:string";
		}
		else if (Boolean.class.isAssignableFrom(type))
		{
			return "xs:boolean";
		}
		else if (Integer.class.isAssignableFrom(type))
		{
			return "xs:int";
		}
		else if (Short.class.isAssignableFrom(type))
		{
			return "xs:short";
		}
		else if (Long.class.isAssignableFrom(type))
		{
			return "xs:long";
		}
		else if (Double.class.isAssignableFrom(type))
		{
			return "xs:double";
		}
		else if (Float.class.isAssignableFrom(type))
		{
			return "xs:float";
		}
		else if (Character.class.isAssignableFrom(type))
		{
			return "xs:string";
		}
		else if (type.isEnum())
		{
			String typeName = ClassUtils.getClassSourceName(type).replace('.', '_');
			this.enumTypes.put(typeName, type);
			return typeName;
		}
		else if (WidgetFactory.class.isAssignableFrom(type))
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
			String typeName = ClassUtils.getClassSourceName(type).replace('.', '_');
			this.subTagTypes.add((Class<? extends WidgetChildProcessor<?>>) type);
			return typeName;
		}
		else if (AnyTag.class.isAssignableFrom(type))
		{
			return "xs:anyType";
		}
		return null;
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	private boolean hasTextChild(Method method)
	{
		TagChildren tagChildren = method.getAnnotation(TagChildren.class);
		return (tagChildren != null && tagChildren.value().length == 1 && TextChildProcessor.class.isAssignableFrom(tagChildren.value()[0].value()));
	}
	
	/**
	 * 
	 * @param tagChild
	 * @return
	 */
	private boolean isChildAnAgregator(TagChild tagChild)
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
	private boolean processorSupportsInnerText(Class<? extends WidgetChildProcessor<?>> processorClass)
	{
		try
		{
			Method method = processorClass.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
			return hasTextChild(method);
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
	private void registerNamespaceForCatalog(String targetNS, File file)
	{
		this.namespacesForCatalog.put(targetNS, file);
	}
}