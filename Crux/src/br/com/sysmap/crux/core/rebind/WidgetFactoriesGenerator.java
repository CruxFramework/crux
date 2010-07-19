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
package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.AllChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.TextChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.GenericUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetFactoriesGenerator extends AbstractGenerator
{
	private static final int UNBOUNDED = -1;
	private static Map<Class<?>, String> attributesFromClass = new HashMap<Class<?>, String>();
	private static Map<Class<?>, EventBinderData> eventsFromClass = new HashMap<Class<?>, EventBinderData>();
	private int variableNameSuffixCounter = 0;
	
	/**
	 * 
	 */
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		try 
		{
			TypeOracle typeOracle = context.getTypeOracle(); 
			JClassType classType = typeOracle.getType(typeName);
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName() + "Impl";
			generateClass(logger, context, classType);
			return packageName + "." + className;
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElement(e.getLocalizedMessage()), e);
			throw new UnableToCompleteException();
		}
	}

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param classType
	 * @throws UnableToCompleteException 
	 */
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType) throws UnableToCompleteException
	{
		try
		{
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName();
			String implClassName = className + "Impl";

			PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
			// if printWriter is null, source code has ALREADY been generated, return
			if (printWriter == null) return;

			Class<?> factoryClass = Class.forName(getClassBinaryName(classType));

			ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
			composer.setSuperclass(getClassSourceName(factoryClass));
			composer.addImport(GWT.class.getName());
			composer.addImport(ScreenFactory.class.getName());
			composer.addImport(Element.class.getName());
			composer.addImport(List.class.getName());
			composer.addImport(InterfaceConfigException.class.getName());

			SourceWriter sourceWriter = null;
			sourceWriter = composer.createSourceWriter(context, printWriter);

			Type widgetType = getWidgetTypeFromClass(logger, factoryClass);
			
			generateProcessAttributesMethod(logger, sourceWriter, factoryClass, widgetType);
			generateProcessEventsMethod(logger, sourceWriter, factoryClass, widgetType);
			generateProcessChildrenMethod(logger, sourceWriter, factoryClass, widgetType);

			sourceWriter.outdent();
			sourceWriter.println("}");

			context.commit(logger, printWriter);
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactory(e.getLocalizedMessage()), e);
			throw new UnableToCompleteException();
		}
	}
	
	/**
	 * @param type
	 * @return
	 */
	private Class<?> getClassFormType(Type type)
	{
		if(type instanceof Class<?>)
		{
			return (Class<?>)type;
		}
		else if (type instanceof ParameterizedType)
		{
			return (Class<?>) ((ParameterizedType)type).getRawType();
		}
		return null;//TODO throw error;
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 * @throws Exception 
	 */
	private void generateProcessChildrenMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Type widgetType) throws Exception
	{
		Method method = factoryClass.getMethod("processChildren", new Class[]{WidgetFactoryContext.class});

		// TODO - Thiago -  tratar instanciamento qdo for inner classe não estatica.
		String contextDeclaration = getClassSourceName(WidgetChildProcessorContext.class)+"<"+ getClassSourceName(widgetType) + ">";
		Map<String, String> methodsForInnerProcessing = new HashMap<String, String>();
		Map<String, String> processorVariables = new HashMap<String, String>();

		generatingChildrenProcessingBlock(logger, sourceWriter, widgetType, method, contextDeclaration, 
										  methodsForInnerProcessing, processorVariables);
		generateMethodsForInnerProcessingChildren(sourceWriter, contextDeclaration, methodsForInnerProcessing);
		generateInnerProcessorsvariables(sourceWriter, processorVariables);
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param processorVariables
	 */
	private void generateInnerProcessorsvariables(SourceWriter sourceWriter, Map<String, String> processorVariables)
	{
		for (String processorVar : processorVariables.keySet())
		{
			String binderClass = processorVariables.get(processorVar);
			sourceWriter.println(binderClass + " " + processorVar + "= new " + binderClass + "();");
		}
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param contextDeclaration
	 * @param methodsForInnerProcessing
	 */
	private void generateMethodsForInnerProcessingChildren(SourceWriter sourceWriter, String contextDeclaration, Map<String, String> methodsForInnerProcessing)
	{
		for (String methodName : methodsForInnerProcessing.keySet())
		{
			sourceWriter.println("protected void "+ methodName+"("+contextDeclaration+" c) throws InterfaceConfigException{");
			sourceWriter.println(methodsForInnerProcessing.get(methodName));
			sourceWriter.println("}");
		}
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param widgetType
	 * @param method
	 * @param contextDeclaration
	 * @param methodsForInnerProcessing
	 * @param processorVariables
	 * @throws Exception
	 */
	private void generatingChildrenProcessingBlock(TreeLogger logger, SourceWriter sourceWriter, Type widgetType, Method method, String contextDeclaration,
			Map<String, String> methodsForInnerProcessing, Map<String, String> processorVariables) throws Exception
	{
		String childrenProcessorMethodName = generateProcessChildrenBlockFromMethod(logger, methodsForInnerProcessing, method,
				                                                                     widgetType, processorVariables);

		sourceWriter.println("@Override");
		sourceWriter.println("public void processChildren("+getClassSourceName(WidgetFactoryContext.class)
		         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processChildren(context);");
		if (childrenProcessorMethodName != null)
		{
			sourceWriter.println(contextDeclaration+" c = new "+contextDeclaration+"(context);");
			sourceWriter.println("c.setChildElement(context.getElement());");
			sourceWriter.println(childrenProcessorMethodName+"(c);");
		}
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 * @throws Exception
	 */
	private void generateProcessEventsMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Type widgetType) throws Exception
	{
		Map<String, String> evtBinderVariables = new HashMap<String, String>();
		
		sourceWriter.println("@Override");
		sourceWriter.println("public void processEvents("+getClassSourceName(WidgetFactoryContext.class)
				         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processEvents(context);");
		sourceWriter.print(generateProcessEventsBlock(logger, factoryClass, evtBinderVariables));
		sourceWriter.println("}");
		
		generateInnerProcessorsvariables(sourceWriter, evtBinderVariables);
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 */
	private void generateProcessAttributesMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Type widgetType)
	{
		sourceWriter.println("@Override");
		sourceWriter.println("public void processAttributes("+getClassSourceName(WidgetFactoryContext.class)
		         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processAttributes(context);");
		sourceWriter.print(generateProcessAttributesBlock(logger, factoryClass, getClassFormType(widgetType)));
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 */
	private String generateProcessAttributesBlock(TreeLogger logger, Class<?> factoryClass, Class<?> widgetType)
	{
		if (attributesFromClass.containsKey(factoryClass))
		{
			return attributesFromClass.get(factoryClass);
		}
		StringBuilder result = new StringBuilder();
		
		Method method = getMethod(factoryClass, "processAttributes");
		if (method != null)
		{
			TagAttributes attrs = method.getAnnotation(TagAttributes.class);
			if (attrs != null)
			{
				for (TagAttribute attr : attrs.value())
				{
					String attrName = attr.value();
					if (isValidName(attrName))
					{
						String setterMethod = ClassUtils.getSetterMethod(attrName);
						if (ClassUtils.hasValidSetter(widgetType, setterMethod, attr.type()))
						{
							String expression;
							if (attr.type().equals(String.class) && attr.supportsI18N())
							{
								expression = "ScreenFactory.getInstance().getDeclaredMessage("+attrName+")";
							}
							else
							{
								expression = ClassUtils.getParsingExpressionForSimpleType(attrName, attr.type());
							}
							if (expression == null)
							{
								logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidProperty(attrName));
							}
							else
							{
								result.append("String "+attrName+" = context.getElement().getAttribute(\"_"+attrName+"\");\n");
								if (attr.defaultValue().length() > 0)
								{
									result.append("if ("+attrName+" == null || "+attrName+".length() == 0){\n");
									result.append(attrName + " = \"" + attr.defaultValue() + "\";");
									result.append("}\n");
									result.append("else {\n");
								}
								else
								{
									result.append("if ("+attrName+" != null && "+attrName+".length() > 0){\n");
								}
								result.append("context.getWidget()."+setterMethod+"("+expression+");\n");
								result.append("}\n");
							}
						}
						else
						{
							logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidProperty(attrName));
						}
					}
					else
					{
						logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidAttrName(attrName));
					}
				}
			}
		}
		Class<?> superclass = factoryClass.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			result.append(generateProcessAttributesBlock(logger, superclass, widgetType)+"\n");
		}
		Class<?>[] interfaces = factoryClass.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			result.append(generateProcessAttributesBlock(logger, interfaceClass, widgetType)+"\n");
		}
		String attributes = result.toString();
		attributesFromClass.put(factoryClass, attributes);
		return attributes;
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	private String generateProcessEventsBlock(TreeLogger logger, 
											   Class<?> factoryClass, 
											   Map<String, String> evtBinderVariables) throws Exception
	{
		if (eventsFromClass.containsKey(factoryClass))
		{
			evtBinderVariables.putAll(eventsFromClass.get(factoryClass).evtBinderVariables);
			return eventsFromClass.get(factoryClass).evtBinderCalls;
		}
		
		StringBuilder result = new StringBuilder();
		
		Method method = getMethod(factoryClass, "processEvents");
		if (method != null)
		{
			TagEvents evts = method.getAnnotation(TagEvents.class);
			if (evts != null)
			{
				for (TagEvent evt : evts.value())
				{
					Class<? extends EvtBinder<?>> binderClass = evt.value();
					String binderClassName = getClassSourceName(binderClass);
					String evtBinderVar = getEvtBinderVariableName("ev", evtBinderVariables, binderClassName);
					result.append(evtBinderVar+".bindEvent(context.getElement(), context.getWidget());\n");
				}
			}
		}
		Class<?> superclass = factoryClass.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			Map<String, String> evtBinderVariablesSubClasses = new HashMap<String, String>();
			String superClassBlock = generateProcessEventsBlock(logger, superclass, evtBinderVariablesSubClasses);
			if (result.indexOf(superClassBlock) < 0)
			{
				result.append(superClassBlock+"\n");
				evtBinderVariables.putAll(evtBinderVariablesSubClasses);
			}
		}
		Class<?>[] interfaces = factoryClass.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			Map<String, String> evtBinderVariablesSubClasses = new HashMap<String, String>();
			String superClassBlock = generateProcessEventsBlock(logger, interfaceClass, evtBinderVariablesSubClasses);
			if (result.indexOf(superClassBlock) < 0)
			{
				result.append(superClassBlock+"\n");
				evtBinderVariables.putAll(evtBinderVariablesSubClasses);
			}
		}
		
		String events = result.toString();
		eventsFromClass.put(factoryClass, new EventBinderData(events, evtBinderVariables));
		return events;
	}

	/**
	 * 
	 * @param variables
	 * @param binderClassName
	 * @return
	 */
	private String getEvtBinderVariableName(String varPrefix, Map<String, String> variables, String binderClassName)
	{
		String evtBinderVar = null;
		if (variables.containsValue(binderClassName))
		{
			for (String key : variables.keySet())
			{
				if (binderClassName.equals(variables.get(key)))
				{
					evtBinderVar = key;
					break;
				}
			}
		}
		else
		{
			evtBinderVar = varPrefix+getVariableNameSuffixCounter();
			variables.put(evtBinderVar, binderClassName);
		}
		return evtBinderVar;
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param processChildrenMethod
	 * @param widgetType
	 * @throws Exception
	 */
	private String generateProcessChildrenBlockFromMethod(TreeLogger logger, 
														   Map<String, String> methodsForInnerProcessing, 
														   Method processChildrenMethod, Type widgetType, 
														   Map<String, String> processorVariables) throws Exception
	{
		String processingMethodName = null;
		TagChildren children = processChildrenMethod.getAnnotation(TagChildren.class);
		if (children != null)
		{
			StringBuilder source = new StringBuilder();
			processingMethodName = getProcessingMethodNameForProcessorMethod(processChildrenMethod);
			if (!methodsForInnerProcessing.containsKey(processingMethodName))
			{
				methodsForInnerProcessing.put(processingMethodName, null);
				AllowedOccurences allowedChildren = getAllowedChildrenNumber(children);
				boolean acceptNoChildren = (allowedChildren.minOccurs == 0);

				source.append(generateProcessingCallForAgregators(logger, children, processorVariables));

				if (allowedChildren.maxOccurs == UNBOUNDED || allowedChildren.maxOccurs >= 1)
				{
					boolean hasChildElement = true;
					if (allowedChildren.maxOccurs == 1)
					{
						if(TextChildProcessor.class.isAssignableFrom(children.value()[0].value()))
						{
							source.append("String child = ensureTextChild(c.getChildElement(), "+acceptNoChildren+");\n");
							hasChildElement = false;
						}
						else
						{
							source.append("Element child = ensureFirstChildSpan(c.getChildElement(), "+acceptNoChildren+");\n");
						}
						source.append("if (child != null){\n");
					}
					else
					{
						source.append("List<Element> children = ensureChildrenSpans(c.getChildElement(), "+acceptNoChildren+");\n");
						source.append("if (children != null){\n");
						source.append("for(Element child: children){\n");
					}
					if (hasChildElement)
					{
						source.append("c.setChildElement(child);\n");
					}

					source.append(generateChildrenBlockFromAnnotation(logger, methodsForInnerProcessing, widgetType, children, 
							                                                                       processorVariables, true));

					// TODO - Thiago - tratar validação de filhos obrigatorios .... espeficamente qdo parent for um agregador....
					if (allowedChildren.maxOccurs == UNBOUNDED || allowedChildren.maxOccurs > 1)
					{
						source.append("}\n");
					}
					source.append("}\n");
				}
				methodsForInnerProcessing.put(processingMethodName, source.toString());
			}
		}
		return processingMethodName;
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param children
	 */
	private String generateProcessingCallForAgregators(TreeLogger logger, TagChildren children, Map<String, String> processorVariables)
	{
		StringBuilder source = new StringBuilder();
		for (TagChild child : children.value())
		{
			if (child.autoProcess())
			{
				Class<? extends WidgetChildProcessor<?>> childProcessor = child.value();
				
				if (ChoiceChildProcessor.class.isAssignableFrom(childProcessor) ||
					SequenceChildProcessor.class.isAssignableFrom(childProcessor) ||
					AllChildProcessor.class.isAssignableFrom(childProcessor))
				{
					String processorName = getClassSourceName(childProcessor);
					String evtBinderVar = getEvtBinderVariableName("p", processorVariables, processorName);
					source.append(evtBinderVar+".processChildren(c);\n");
				}
			}
		}
		return source.toString();
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param widgetType
	 * @param children
	 * @param childrenSuffix 
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	private String generateChildrenBlockFromAnnotation(TreeLogger logger, 
													 Map<String, String> methodsForInnerProcessing, 
			                                         Type widgetType, 
			                                         TagChildren children,
			                                         Map<String, String> processorVariables, boolean first) throws NoSuchMethodException, Exception
	{
		StringBuilder source = new StringBuilder();
		for (TagChild child : children.value())
		{
			if (child.autoProcess())
			{
				Class<? extends WidgetChildProcessor<?>> childProcessor = child.value();
				
				if (TextChildProcessor.class.isAssignableFrom(childProcessor))
				{
					source.append(generateTextProcessingBlock(logger, widgetType, childProcessor));
				}
				else if (ChoiceChildProcessor.class.isAssignableFrom(childProcessor) ||
					SequenceChildProcessor.class.isAssignableFrom(childProcessor) ||
					AllChildProcessor.class.isAssignableFrom(childProcessor))
				{
					source.append(generateAgregatorTagProcessingBlock(logger, methodsForInnerProcessing, widgetType, 
							                                          children, childProcessor, processorVariables, first));
				}
				else
				{
					source.append(generateTagIdentifierBlock(first, childProcessor));
					if (AnyWidgetChildProcessor.class.isAssignableFrom(childProcessor))
					{
						source.append(generateAnyWidgetProcessingBlock(childProcessor));
					}
					else
					{
						source.append(generateGenericProcessingBlock(logger, methodsForInnerProcessing, widgetType, 
								                                                  childProcessor, processorVariables));
					}
					source.append("}\n");
				}
				first = false;
			}
		}
		return source.toString();
	}

	/**
	 * 
	 * @param logger
	 * @param widgetType
	 * @param childProcessor
	 * @param processorVariables
	 * @return
	 */
	private String generateTextProcessingBlock(TreeLogger logger, Type widgetType, 
											   Class<? extends WidgetChildProcessor<?>> childProcessor)
	{
		TagChildAttributes processorAttributes = ClassUtils.getChildtrenAttributesAnnotation(childProcessor);
		if (processorAttributes.widgetProperty().length() > 0)
		{
			return "if (child.trim().length() > 0) c.getRootWidget()."+ClassUtils.getSetterMethod(processorAttributes.widgetProperty())+"(child);";
		}
		else if (HasText.class.isAssignableFrom(getClassFormType(widgetType)))
		{
			return "if (child.trim().length() > 0) c.getRootWidget().setText(child);";
			
		}
		return "";
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param childrenSuffix
	 * @param first
	 * @param childProcessor
	 */
	private String generateTagIdentifierBlock(boolean first, Class<? extends WidgetChildProcessor<?>> childProcessor)
	{
		StringBuilder source = new StringBuilder();
		TagChildAttributes processorAttributes = ClassUtils.getChildtrenAttributesAnnotation(childProcessor);
		if (first)
		{
			source.append("String  __tag = child.getAttribute(\"__tag\");\n");
		}
		else
		{
			source.append("else ");
		}
		
		if (processorAttributes == null || WidgetFactory.class.isAssignableFrom(processorAttributes.type())  
			|| processorAttributes.tagName().equals(""))
		{
			source.append("if (__tag == null || __tag.length() == 0){\n");
		}
		else
		{
			source.append("if (\""+processorAttributes.tagName()+"\".equals(__tag)){\n");
		}
		
		return source.toString();
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param widgetType
	 * @param children
	 * @param childrenSuffix
	 * @param childProcessor
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	private String generateAgregatorTagProcessingBlock(TreeLogger logger, 
													 Map<String, String> methodsForInnerProcessing, 
													 Type widgetType, 
													 TagChildren children,
													 Class<? extends WidgetChildProcessor<?>> childProcessor, 
													 Map<String, String> processorVariables, boolean first) 
	             throws NoSuchMethodException, Exception
	{
		StringBuilder source = new StringBuilder();
		
		Method processorMethod = childProcessor.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
		TagChildren tagChildren = processorMethod.getAnnotation(TagChildren.class);
		if (children != null)
		{
			source.append(generateChildrenBlockFromAnnotation(logger, methodsForInnerProcessing, widgetType, tagChildren, processorVariables, first));
		}
		return source.toString();
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param widgetType
	 * @param childProcessor
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	private String generateGenericProcessingBlock(TreeLogger logger,  
												  Map<String, String> methodsForInnerProcessing, 
												  Type widgetType, 
												  Class<? extends WidgetChildProcessor<?>> childProcessor, 
												  Map<String, String> processorVariables)
			throws NoSuchMethodException, Exception
	{
		// TODO - Thiago - colocar todos as amarracoes de eventos do client em evtbinder 
		StringBuilder source = new StringBuilder();
		
		String processorName = getClassSourceName(childProcessor);
		String evtBinderVar = getEvtBinderVariableName("p", processorVariables, processorName);
		source.append(evtBinderVar+".processChildren(c);\n");
		
		Method processorMethod = childProcessor.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
		String childrenProcessorMethodName = generateProcessChildrenBlockFromMethod(logger, methodsForInnerProcessing, 
				                                            processorMethod, widgetType, processorVariables);
		if (childrenProcessorMethodName != null)
		{
			source.append(childrenProcessorMethodName+"(c);\n");
		}
		return source.toString();
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param childProcessor
	 */
	private String generateAnyWidgetProcessingBlock(Class<? extends WidgetChildProcessor<?>> childProcessor)
	{
		StringBuilder source = new StringBuilder();
		TagChildAttributes processorAttributes = ClassUtils.getChildtrenAttributesAnnotation(childProcessor);
		
		source.append(Widget.class.getName()+" _w = createChildWidget(c.getChildElement(), c.getChildElement().getId());\n");						
		if (processorAttributes != null && processorAttributes.widgetProperty().length() > 0)
		{
			source.append("c.getRootWidget()."+ClassUtils.getSetterMethod(processorAttributes.widgetProperty())+"(_w);\n");						
		}
		else
		{
			source.append("c.getRootWidget().add(_w);\n");						
		}
		
		return source.toString();
	}
	
	/**
	 * 
	 * @param children
	 * @return
	 * @throws Exception 
	 */
	private AllowedOccurences getAllowedChildrenNumber(TagChildren children) throws Exception
	{
		AllowedOccurences allowed = new AllowedOccurences();
		
		for (TagChild child: children.value())
		{
			if (children.value().length > 1 && TextChildProcessor.class.isAssignableFrom(child.value()))
			{
				throw new Exception(messages.errorGeneratingWidgetFactoryMixedContentNotAllowed());
			}
			
			AllowedOccurences allowedForChild = getAllowedOccurrencesForChild(child);
			mergeAllowedOccurrences(allowed, allowedForChild);
		}
		return allowed;
	}

	/**
	 * @param allowed
	 * @param allowedForChild
	 */
	private void mergeAllowedOccurrences(AllowedOccurences allowed,
            AllowedOccurences allowedForChild)
    {
	    if (allowedForChild.minOccurs == UNBOUNDED)
	    {
	    	allowed.minOccurs = UNBOUNDED;
	    }
	    else if (allowed.minOccurs != UNBOUNDED)
	    {
	    	allowed.minOccurs += allowedForChild.minOccurs;	
	    }
	    if (allowedForChild.maxOccurs == UNBOUNDED)
	    {
	    	allowed.maxOccurs = UNBOUNDED;
	    }
	    else if (allowed.maxOccurs != UNBOUNDED)
	    {
	    	allowed.maxOccurs += allowedForChild.maxOccurs;	
	    }
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws Exception 
	 */
	private AllowedOccurences getAllowedOccurrencesForChild(TagChild child) throws Exception
	{
		AllowedOccurences allowed = new AllowedOccurences();
		Class<? extends WidgetChildProcessor<?>> childProcessor = child.value();
		TagChildAttributes processorAttributes = ClassUtils.getChildtrenAttributesAnnotation(childProcessor);

		if (processorAttributes != null)
		{
			String minOccurs = processorAttributes.minOccurs();
			if (minOccurs.equals("unbounded"))
			{
				allowed.minOccurs = UNBOUNDED;
			}
			else
			{
				allowed.minOccurs = Integer.parseInt(minOccurs);
			}

			String maxOccurs = processorAttributes.maxOccurs();
			if (maxOccurs.equals("unbounded"))
			{
				allowed.maxOccurs = UNBOUNDED;
			}
			else
			{
				allowed.maxOccurs = Integer.parseInt(maxOccurs);
			}
		}
		else if (AllChildProcessor.class.isAssignableFrom(child.value()) || SequenceChildProcessor.class.isAssignableFrom(child.value()))
		{
            Method processorMethod = child.value().getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
            TagChildren tagChildren = processorMethod.getAnnotation(TagChildren.class);
            if (tagChildren != null)
            {
            	AllowedOccurences allowedChildren = getAllowedChildrenNumber(tagChildren);
            	mergeAllowedOccurrences(allowed, allowedChildren);
            }
		}
		else
		{
			allowed.minOccurs = 1;
			allowed.maxOccurs = 1;
		}
		return allowed;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean isValidName(String name)
	{
		return name != null && name.length() > 0 && RegexpPatterns.REGEXP_WORD.matcher(name).matches() 
		                                         && !Character.isDigit(name.charAt(0));
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
	 * @param logger
	 * @param factoryClass
	 * @return
	 */
	private Type getWidgetTypeFromClass(TreeLogger logger, Class<?> factoryClass)
	{
        try
        {
	        Method method = factoryClass.getMethod("instantiateWidget", 
	        		new Class[]{Element.class, String.class});
	        Type genericReturnType = method.getGenericReturnType();
	        if (genericReturnType instanceof TypeVariable<?>)
	        {
	        	return GenericUtils.resolveReturnType(factoryClass, "instantiateWidget", new Class[]{Element.class, String.class});
	        }
	        else
	        {
	        	return genericReturnType;
	        }
        }
        catch (Exception e)
        {
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryCanNotRealizeGenericType(factoryClass.getName()), e);
        }
	}
	
	/**
	 * 
	 * @return
	 */
	private int getVariableNameSuffixCounter()
	{
		return variableNameSuffixCounter++;
	}

	private String getProcessingMethodNameForProcessorMethod(Method processMethod)
	{
		return RegexpPatterns.REGEXP_DOT.matcher(processMethod.getDeclaringClass().getName()+processMethod.getName()).replaceAll("_");
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class AllowedOccurences
	{
		int minOccurs = 0;
		int maxOccurs = 0;
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class EventBinderData
	{
		String evtBinderCalls;
		Map<String, String> evtBinderVariables;

		public EventBinderData(String evtBinderCalls, Map<String, String> evtBinderVariables)
		{
			this.evtBinderCalls = evtBinderCalls;
			this.evtBinderVariables = evtBinderVariables;
		}
	}
}
