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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
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
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WidgetFactoriesGenerator extends AbstractGenerator
{
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
			composer.addImport(InterfaceConfigException.class.getName());

			SourceWriter sourceWriter = null;
			sourceWriter = composer.createSourceWriter(context, printWriter);

			Class<?> widgetType = getWidgetTypeFromClass(logger, factoryClass);
			
			generateProccessAttributesMethod(logger, sourceWriter, factoryClass, widgetType);
			generateProccessEventsMethod(logger, sourceWriter, factoryClass, widgetType);
			generateProccessChildrenMethod(logger, sourceWriter, factoryClass, widgetType);

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
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 * @throws Exception 
	 */
	private void generateProccessChildrenMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Class<?> widgetType) throws Exception
	{
/*		try
		{
    		Method method = factoryClass.getDeclaredMethod("processChildren", new Class[]{widgetType, Element.class, String.class});
	
			sourceWriter.println("@Override");
    		sourceWriter.println("public void processChildren("+getClassSourceName(WidgetFactoryContext.class)
	    	         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
			generateProccessChildrenBlock(logger, sourceWriter, method);
			sourceWriter.println("}");
		}
		catch (NoSuchMethodException e) 
		{
			// Does nothing.... If method not present, We don't need to generate any processing logic.
		}
*/	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 * @throws Exception
	 */
	private void generateProccessEventsMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Class<?> widgetType) throws Exception
	{
		sourceWriter.println("@Override");
		sourceWriter.println("public void processEvents("+getClassSourceName(WidgetFactoryContext.class)
				         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processEvents(context);");
		generateProccessEventsBlock(logger, sourceWriter, factoryClass);
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @param widgetType
	 */
	private void generateProccessAttributesMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Class<?> widgetType)
	{
		sourceWriter.println("@Override");
		sourceWriter.println("public void processAttributes("+getClassSourceName(WidgetFactoryContext.class)
		         +"<"+ getClassSourceName(widgetType)+"> context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processAttributes(context);");
		generateProccessAttributesBlock(logger, sourceWriter, factoryClass, widgetType);
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 */
	private void generateProccessAttributesBlock(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass, Class<?> widgetType)
	{
		Method method = getMethod(factoryClass, "processAttributes");
		if (method != null)
		{
			TagAttributes attrs = method.getAnnotation(TagAttributes.class);
			if (attrs != null)
			{
				for (TagAttribute attr : attrs.value())
				{
					if (attr.autoProcess())
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
									sourceWriter.println("String "+attrName+" = context.getElement().getAttribute(\"_"+attrName+"\");");
									sourceWriter.println("if ("+attrName+" != null && "+attrName+".length() > 0){");
									sourceWriter.println("context.getWidget()."+setterMethod+"("+expression+");");
									sourceWriter.println("}");
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
		}
		Class<?> superclass = factoryClass.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			generateProccessAttributesBlock(logger, sourceWriter, superclass, widgetType);
		}
		Class<?>[] interfaces = factoryClass.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			generateProccessAttributesBlock(logger, sourceWriter, interfaceClass, widgetType);
		}
	
	}
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	private void generateProccessEventsBlock(TreeLogger logger, SourceWriter sourceWriter, Class<?> factoryClass) throws Exception
	{
		Method method = getMethod(factoryClass, "processEvents");
		if (method != null)
		{
			TagEvents evts = method.getAnnotation(TagEvents.class);
			if (evts != null)
			{
				for (TagEvent evt : evts.value())
				{
					Class<? extends EvtBinder<?>> binderClass = evt.value();
					//TODO gerar codigo para instanciar binder....colocar um cahce
					String binderClassName = getClassSourceName(binderClass);
					sourceWriter.println("new " + binderClassName+"().bindEvent(context.getElement(), context.getWidget());");
				}
			}
		}
		Class<?> superclass = factoryClass.getSuperclass();
		if (superclass!= null && !superclass.equals(Object.class))
		{
			generateProccessEventsBlock(logger, sourceWriter, superclass);
		}
		Class<?>[] interfaces = factoryClass.getInterfaces();
		for (Class<?> interfaceClass : interfaces)
		{
			generateProccessEventsBlock(logger, sourceWriter, interfaceClass);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	private void generateProccessChildrenBlock(TreeLogger logger, SourceWriter sourceWriter, Method processChildrenMethod) throws Exception
	{
/*		TagChildren children = processChildrenMethod.getAnnotation(TagChildren.class);
		if (children != null)
		{
			for (TagChild child : children.value())
			{
				Class<? extends TagChildProcessor<?>> childProcessor = child.value();
				
			}
		}
*/
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
	private Class<?> getWidgetTypeFromClass(TreeLogger logger, Class<?> factoryClass)
	{
		Class<?> returnType = GenericUtils.resolveReturnType(factoryClass, "instantiateWidget", 
				new Class[]{Element.class, String.class});
		if (returnType == null)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryCanNotRealizeGenericType(factoryClass.getName()));
		}
		return returnType;
	}
}
