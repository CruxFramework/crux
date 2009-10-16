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
package br.com.sysmap.crux.tools.htmltags.xsd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.htmltags.HTMLTagsMessages;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class SchemaGenerator
{
	private File destDir;
	private Map<String, Class<?>> enumTypes;
	private Deque<Class<? extends WidgetChildProcessor<?>>> subTagTypes;
	private static final Log logger = LogFactory.getLog(SchemaGenerator.class);
	private HTMLTagsMessages messages = MessagesFactory.getMessages(HTMLTagsMessages.class);
	
	/**
	 * 
	 * @param destDir
	 */
	public SchemaGenerator(String destDir)
	{
		this.destDir = new File(destDir);
		this.destDir.mkdirs();
		this.enumTypes = new HashMap<String, Class<?>>();
		this.subTagTypes = new LinkedList<Class<? extends WidgetChildProcessor<?>>>();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void generateSchemas() throws IOException
	{
		Set<String> libraries = WidgetConfig.getRegisteredLibraries();
		for (String library : libraries)
		{
			logger.info(messages.schemaGeneratorCreatingLibraryXSD(library));
			generateSchemaForLibrary(library, libraries);
		}
		logger.info(messages.schemaGeneratorCreatingCoreXSD());
		generateCoreSchema(libraries);
		logger.info(messages.schemaGeneratorXSDFilesGenerated());
	}
	
	/**
	 * 
	 * @param libraries
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
		PrintStream out = new PrintStream(coreFile);
		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/crux/1.0\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		for (String lib : libraries)
		{
			out.println("xmlns:_"+lib+"=\"http://www.sysmap.com.br/crux/1.0/"+lib+"\" ");
		}
		out.println("attributeFormDefault=\"unqualified\" ");
		out.println("elementFormDefault=\"qualified\" ");
		out.println("targetNamespace=\"http://www.sysmap.com.br/crux/1.0\" >");
		
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
	 * @param out
	 */
	private void generateCoreSchemasImport(Set<String> libraries, PrintStream out)
	{
		for (String lib : libraries)
		{
			out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.sysmap.com.br/crux/1.0/"+lib+"\"/>");
		}
	}

	/**
	 * 
	 * @param out
	 * @param libraries
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
	 */
	private void generateCoreSplashScreenElement(PrintStream out)
	{
		out.println("<xs:element name=\"splashScreen\" type=\"SplashScreen\"/>");

		out.println("<xs:complexType name=\"SplashScreen\">");
		out.println("<xs:attribute name=\"style\" type=\"xs:string\"/>");
		out.println("</xs:complexType>");
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

		out.println("<xs:schema ");
		out.println("xmlns=\"http://www.sysmap.com.br/crux/1.0/"+library+"\" ");
		out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		out.println("xmlns:c=\"http://www.sysmap.com.br/crux/1.0\" ");
		for (String lib : allLibraries)
		{
			if (!lib.equals(library))
			{
				out.println("xmlns:_"+lib+"=\"http://www.sysmap.com.br/crux/1.0/"+lib+"\" ");
			}
		}
		out.println("attributeFormDefault=\"unqualified\" ");
		out.println("elementFormDefault=\"qualified\" ");
		out.println("targetNamespace=\"http://www.sysmap.com.br/crux/1.0/"+library+"\" >");
		
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
	 * @param allLibraries
	 * @param out
	 */
	private void generateSchemaImportsForLibrary(String library, Set<String> allLibraries, PrintStream out)
	{
		out.println("<xs:import schemaLocation=\"core.xsd\" namespace=\"http://www.sysmap.com.br/crux/1.0\"/>");
		for (String lib : allLibraries)
		{
			if (!lib.equals(library))
			{
				out.println("<xs:import schemaLocation=\""+lib+".xsd\" namespace=\"http://www.sysmap.com.br/crux/1.0/"+lib+"\"/>");
			}
		}
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
	@SuppressWarnings("unchecked")
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
				generateChildrenForProcessor(out, type, library);
				generateAttributesForProcessor(out, type, library, new HashSet<String>());
				generateEventsForProcessor(out, type, new HashSet<String>());
				out.println("</xs:complexType>");
				added.add(elementName);
			}
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
	 * @param widgetFactory
	 */
	private void generateTypeForFactory(PrintStream out, Class<? extends WidgetFactory<?>> widgetFactory, String library)
	{
		DeclarativeFactory annot = widgetFactory.getAnnotation(DeclarativeFactory.class);
		String elementName = annot.id();
		
		out.println("<xs:element name=\""+elementName+"\" type=\"T"+elementName+"\"/>");
				
		out.println("<xs:complexType name=\"T"+elementName+"\">");
		generateChildrenForFactory(out, widgetFactory, library);
		generateAttributesForFactory(out, widgetFactory, library, new HashSet<String>());
		generateEventsForFactory(out, widgetFactory, new HashSet<String>());
		out.println("</xs:complexType>");
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
		else
		{
			out.println("<xs:element name=\""+tagName+"\" minOccurs=\""+attributes.minOccurs()+
					    "\" maxOccurs=\""+attributes.maxOccurs()+
					    "\" type=\""+getSchemaType(type, library)+"\"/>");
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
	 * @param args
	 */
	public static void main(String[] args)
	{
		ClassScanner.initialize(ClasspathUrlFinder.findClassPaths());
		SchemaGenerator generator = new SchemaGenerator("C:\\desenvolvimento\\ide\\workspaces\\myeclipse\\CruxHtmlTags\\xsd\\generated");
		try
		{
			generator.generateSchemas();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
