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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.ScreenBind;
import br.com.sysmap.crux.core.client.controller.Validate;
import br.com.sysmap.crux.core.client.controller.ValueObject;
import br.com.sysmap.crux.core.client.event.CruxEvent;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Creates a Mechanism to work around the lack of reflection support in GWT. This class provides
 * implementations of ClientHandlerInvoker and ClientCallbackInvoker. These implementations are used
 * by EventProcessorFactories to call methods by their names.
 * @author Thiago Bustamante
 */
public class RegisteredClientEventHandlersGenerator extends AbstractRegisteredElementsGenerator
{
	
	/**
	 * Generate the class
	 */
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, List<Screen> screens) 
	{
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImport("com.google.gwt.core.client.GWT");
		composer.addImport("com.google.gwt.json.client.JSONValue");
		composer.addImport("br.com.sysmap.crux.core.client.screen.Screen");
		composer.addImport("br.com.sysmap.crux.core.client.event.CruxEvent");
		composer.addImport("com.google.gwt.event.shared.GwtEvent");
		composer.addImport("com.google.gwt.user.client.ui.HasValue");
		composer.addImport("br.com.sysmap.crux.core.client.formatter.HasFormatter");
		composer.addImport("com.google.gwt.user.client.ui.HasText");
		composer.addImport("com.google.gwt.user.client.ui.Widget");
		
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.event.RegisteredClientEventHandlers");
		
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);
		sourceWriter.println("private java.util.Map clientHandlers = new java.util.HashMap();");

		Map<String, String> handlerClassNames = new HashMap<String, String>();
		for (Screen screen : screens)
		{
			generateEventHandlersForScreen(logger, sourceWriter, screen, handlerClassNames, packageName+"."+implClassName);
		}
		generateConstructor(logger, sourceWriter, implClassName, handlerClassNames);

		sourceWriter.println("public EventClientHandlerInvoker getEventHandler(String id){");
		sourceWriter.println("return (EventClientHandlerInvoker) clientHandlers.get(id);");
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}
	
	/**
	 * Generate constructor. At this point the maps with handlers and callbacks are built. The list is 
	 * constructed looping all widgets to just include controllers that are used on the screen.
	 * @param logger
	 * @param sourceWriter
	 * @param implClassName
	 * @para handlerClassNames
	 */
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, String implClassName, 
			Map<String, String> handlerClassNames) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		for (String handler : handlerClassNames.keySet()) 
		{
			sourceWriter.println("clientHandlers.put(\""+handler+"\", new " + handlerClassNames.get(handler) + "());");
		}
		sourceWriter.println("}");
	}
	
	/**
	 * generate wrapper classes for event handling.
	 * @param logger
	 * @param sourceWriter
	 * @param screen
	 */
	protected void generateEventHandlersForScreen(TreeLogger logger, SourceWriter sourceWriter, Screen screen, 
			Map<String, String> handlerClassNames, String implClassName)
	{
		Iterator<String> controllers = screen.iterateControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateEventHandlerBlock(logger, screen, sourceWriter, controller, handlerClassNames, implClassName);
		}		

		controllers = ClientControllers.iterateGlobalClientHandler();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateEventHandlerBlock(logger, screen, sourceWriter, controller, handlerClassNames, implClassName);
		}		
	}
	
	/**
	 * Generate the block to include event handler object.
	 * @param logger
	 * @param sourceWriter
	 * @param widgetId
	 * @param event
	 * @param added
	 */
	protected void generateEventHandlerBlock(TreeLogger logger, Screen screen, SourceWriter sourceWriter, String controller, 
			Map<String, String> added, String implClassName)
	{
		try
		{
			if (!added.containsKey(controller) && ClientControllers.getClientHandler(controller)!= null)
			{
				String genClass = generateEventHandlerInvokerClass(logger,screen,sourceWriter,ClientControllers.getClientHandler(controller), implClassName);
				added.put(controller, genClass);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientHandler(controller, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Create a new class to invoke the eventHandler method by its name
	 * @param logger
	 * @param sourceWriter
	 * @param handlerClass
	 * @return
	 */
	protected String generateEventHandlerInvokerClass(TreeLogger logger, Screen screen, SourceWriter sourceWriter, Class<?> handlerClass, String implClassName)
	{
		String className = handlerClass.getSimpleName();
		sourceWriter.println("public class "+className+"Wrapper extends " + getClassSourceName(handlerClass)
				+ " implements br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker{");
		
		Controller controllerAnnot = handlerClass.getAnnotation(Controller.class);
		boolean singleton = (controllerAnnot != null && controllerAnnot.statefull());
		if (singleton)
		{
			sourceWriter.println(className+"Wrapper wrapper = null;");
		}

		sourceWriter.println("public void invoke(String metodo, GwtEvent<?> sourceEvent, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("invokeEvent(metodo, sourceEvent, false, eventProcessor);");
		sourceWriter.println("}");

		sourceWriter.println("public void invoke(String metodo, CruxEvent<?> sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("invokeEvent(metodo, sourceEvent, fromOutOfModule, eventProcessor);");
		sourceWriter.println("}");
		
		sourceWriter.println("public void invokeEvent(String metodo, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("boolean __runMethod = true;");
		
		if (singleton)
		{
			sourceWriter.println("if (this.wrapper == null){");
			sourceWriter.println("this.wrapper = new "+className+"Wrapper();");
			generateAutoCreateFields(logger, handlerClass, sourceWriter);
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
			generateAutoCreateFields(logger, handlerClass, sourceWriter);
		}
		

		if (controllerAnnot != null && controllerAnnot.autoBind())
		{
			sourceWriter.println("wrapper.updateControllerObjects();");
		}

		
		Method[] methods = handlerClass.getMethods(); 

		boolean first = true;
		for (Method method: methods) 
		{
			if (isHandlerMethodSignatureValid(method))
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				
				if (method.getAnnotation(ExposeOutOfModule.class) != null)
				{
					sourceWriter.println("if (\""+method.getName()+"\".equals(metodo)) {");
				}
				else
				{
					sourceWriter.println("if (\""+method.getName()+"\".equals(metodo) && !fromOutOfModule) {");
				}
				Validate annot = method.getAnnotation(Validate.class);
				if (annot != null)
				{
					sourceWriter.println("try{");
					String validateMethod = annot.value();
					if (validateMethod == null || validateMethod.length() == 0)
					{
						String methodName = method.getName();
						methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
						validateMethod = "validate"+ methodName;
					}
					sourceWriter.println("wrapper."+validateMethod+"();");
					sourceWriter.println("}catch (Throwable e){");
					sourceWriter.println("__runMethod = false;");
					sourceWriter.println("eventProcessor._validationMessage = e.getMessage();");
					sourceWriter.println("}");
				}
				sourceWriter.println("if (__runMethod){");
				sourceWriter.println("try{");
				
				if (!method.getReturnType().getName().equals("void") && 
					!method.getReturnType().getName().equals("java.lang.Void"))
				{
					sourceWriter.println("eventProcessor._hasReturn = true;");
					sourceWriter.println("eventProcessor._returnValue = ");
				}
				generateMethodCall(method, sourceWriter);
				
				sourceWriter.println("}catch (Throwable e){");
				sourceWriter.println("eventProcessor._exception = e;");
				sourceWriter.println("}");
				
				sourceWriter.println("}");
				sourceWriter.println("}");

				first = false;
			}
		}
		if (!first)
		{
			sourceWriter.println(" else ");
		}
		sourceWriter.println("throw new Exception(\""+messages.errorInvokingGeneratedMethod()+" \"+metodo);");
		if (!first && controllerAnnot != null && controllerAnnot.autoBind())
		{
			sourceWriter.println("wrapper.updateScreenWidgets();");
		}
		sourceWriter.println("}");
		
		generateScreenUpdateWidgetsFunction(logger, screen, handlerClass, sourceWriter);
		generateControllerUpdateObjectsFunction(logger, screen, handlerClass, sourceWriter);
		
		sourceWriter.println("public boolean isAutoBindEnabled(){");
		sourceWriter.println("return "+(controllerAnnot != null && controllerAnnot.autoBind())+";");
		sourceWriter.println("}");
				
		sourceWriter.println("}");
		
		return className+"Wrapper";
	}
	
	/** 
	 * Generates the handler method call.
	 * @param method
	 * @param sourceWriter
	 */
	private void generateMethodCall(Method method, SourceWriter sourceWriter)
	{
		Class<?>[] params = method.getParameterTypes();
		if (params != null && params.length == 1)
		{
			sourceWriter.print("wrapper."+method.getName()+"(("+params[0].getName()+")sourceEvent);");
		}
		else 
		{
			sourceWriter.print("wrapper."+method.getName()+"();");
		}
	}
	
	/**
	 * Verify if a method must be included in the list of callable methods in the 
	 * generated invoker class
	 * @param method
	 * @return
	 */
	protected boolean isHandlerMethodSignatureValid(Method method)
	{
		if (!Modifier.isPublic(method.getModifiers()))
		{
			return false;
		}
		
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters != null && parameters.length != 0 && parameters.length != 1)
		{
			return false;
		}
		if (parameters != null && parameters.length == 1)
		{
			if (!GwtEvent.class.isAssignableFrom(parameters[0]) && !CruxEvent.class.isAssignableFrom(parameters[0]))
			{
				return false;
			}
		}
		
		if (method.getDeclaringClass().equals(Object.class))
		{
			return false;
		}
		
		if (method.getAnnotation(Expose.class) == null && method.getAnnotation(ExposeOutOfModule.class) == null)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Create objects for fields that are annotated with @Create
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateAutoCreateFields(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				String fieldTypeName = field.getType().getName();
				if (fieldTypeName.indexOf("$") > 0)
				{
					fieldTypeName = fieldTypeName.replace('$', '.');
				}

				Class<?> type = getTypeForField(logger, field);
				String typeName = type.getName();
				if (typeName.indexOf("$") > 0)
				{
					typeName = typeName.replace('$', '.');
				}
				sourceWriter.println(fieldTypeName+" _field"+field.getName()+"=GWT.create("+typeName+".class);");
				generateFieldValueSet(logger, controller, field, "wrapper", "_field"+field.getName(), sourceWriter);

				if (RemoteService.class.isAssignableFrom(type) && type.getAnnotation(RemoteServiceRelativePath.class) == null)
				{
					sourceWriter.println("(("+ServiceDefTarget.class.getName()+")_field"+field.getName()+").setServiceEntryPoint(\"crux.rpc\");");
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateAutoCreateFields(logger, controller.getSuperclass(), sourceWriter);
		}
	}

	/**
	 * Generates the code for DTO population from screen. 
	 * 
	 * @param logger
	 * @param resultVariable
	 * @param voClass
	 * @param sourceWriter
	 */
	protected void generateScreenOrDTOPopulation(TreeLogger logger, Screen screen, String resultVariable, Class<?> voClass, SourceWriter sourceWriter, boolean populateScreen)
	{
		for (Field field : voClass.getDeclaredFields()) 
		{
			if ((populateScreen && isPropertyVisibleToRead(voClass, field)) || 
				(!populateScreen && isPropertyVisibleToWrite(voClass, field)))
			{
				ValueObject valueObject = voClass.getAnnotation(ValueObject.class);
				ScreenBind screenBind = field.getAnnotation(ScreenBind.class); 
				if (valueObject != null && valueObject.bindWidgetByFieldName() || screenBind != null)
				{
					generateScreenOrDTOPopulationField(logger, screen, resultVariable, voClass, field, sourceWriter, populateScreen);
				}
			}
		}
	}
	
	/**
	 * Generates the code for DTO field population from a screen widget.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	protected void generateDTOFieldPopulation(TreeLogger logger, Screen screen, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, screen, parentVariable, voClass, field, sourceWriter, false);
	}

	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param sourceWriter
	 */
	protected String getFieldValueGet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			return parentVariable+"."+field.getName();
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(getterMethodName, new Class<?>[]{}) != null)
				{
					return (parentVariable+"."+getterMethodName+"()");
				}
			}
			catch (Exception e)
			{
				logger.log(TreeLogger.ERROR, messages.registeredClientEventHandlerPropertyNotFound(field.getName()));
			}
		}
		return null;
	}

	/**
	 * Generates a property set block. First try to set the field directly, then try to use a javabean setter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param valueVariable
	 * @param sourceWriter
	 */
	protected void generateFieldValueSet(TreeLogger logger, Class<?> voClass, Field field, String parentVariable,  String valueVariable, SourceWriter sourceWriter)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			sourceWriter.println(parentVariable+"."+field.getName()+"="+valueVariable+";");
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(setterMethodName, new Class<?>[]{field.getType()}) != null)
				{
					sourceWriter.println(parentVariable+"."+setterMethodName+"("+valueVariable+");");
				}
			}
			catch (Exception e)
			{
				logger.log(TreeLogger.ERROR, messages.registeredClientEventHandlerPropertyNotFound(field.getName()));
			}
		}
	}

	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	protected boolean isPropertyVisibleToWrite(Class<?> voClass, Field field)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			return true;
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				return (voClass.getMethod(setterMethodName, new Class<?>[]{field.getType()}) != null);
			}
			catch (Exception e)
			{
				return false;
			}
		}
	}

	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @return
	 */
	protected boolean isPropertyVisibleToRead(Class<?> voClass, Field field)
	{
		if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
		{
			return true;
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				return (voClass.getMethod(getterMethodName, new Class<?>[]{}) != null);
			}
			catch (Exception e)
			{
				return false;
			}
		}
	}

	/**
	 * 
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateScreenUpdateWidgetsFunction(TreeLogger logger, Screen screen, Class<?> controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateScreenWidgets(){");
		sourceWriter.println("Widget __wid = null;");
		generateScreenUpdateWidgets(logger, screen, "this", controller, sourceWriter);
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateControllerUpdateObjectsFunction(TreeLogger logger, Screen screen, Class<?> controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateControllerObjects(){");
		sourceWriter.println("Widget __wid = null;");
		generateControllerUpdateObjects(logger, screen, "this", controller, sourceWriter);
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param logger
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateControllerUpdateObjects(TreeLogger logger, Screen screen, String controllerVariable, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				Class<?> type = field.getType();

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateDTOFieldPopulation(logger, screen, controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateControllerUpdateObjects(logger, screen, controllerVariable, controller.getSuperclass(), sourceWriter);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateScreenUpdateWidgets(TreeLogger logger, Screen screen, String controllerVariable, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				Class<?> type = field.getType();

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateScreenWidgetPopulation(logger, screen, controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateScreenUpdateWidgets(logger, screen, controllerVariable, controller.getSuperclass(), sourceWriter);
		}
	}
	
	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	protected void generateScreenWidgetPopulation(TreeLogger logger, Screen screen, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, screen, parentVariable, voClass, field, sourceWriter, true);
	}
	
	/**
	 * Generates the code for screen widget population from a DTO field.
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 */
	protected void generateScreenOrDTOPopulationField(TreeLogger logger, Screen screen, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen)
	{
		Class<?> type = field.getType();
		String name = null;
		if (field.getAnnotation(ScreenBind.class) != null)
		{
			name = field.getAnnotation(ScreenBind.class).value();
		}
		if (name == null || name.length() == 0)
		{
			name = field.getName();
		}
		
		if ((type.isPrimitive()) ||
                (Number.class.isAssignableFrom(type)) ||
                (Boolean.class.isAssignableFrom(type)) || 
                (Character.class.isAssignableFrom(type)) ||
                (CharSequence.class.isAssignableFrom(type)) ||
                (Date.class.isAssignableFrom(type)) ||
                (type.isEnum())) 
		{
			if ("true".equals(ConfigurationFactory.getConfigurations().allowAutoBindWithNonDeclarativeWidgets()))
			{
				generateAutoBindHandlerForAllWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, name);
			}
			else
			{
				generateAutoBindHandlerForDeclarativeWidgetsOnly(logger, screen, parentVariable, voClass, field, sourceWriter, populateScreen, type, name);
			}
		}
		else if (type.getAnnotation(ValueObject.class) != null)
		{
			if (!populateScreen)
			{
				sourceWriter.println("if (" +getFieldValueGet(logger, voClass, field, parentVariable)+"==null){");
				generateFieldValueSet(logger, voClass, field, parentVariable, "new "+type.getName()+"()", sourceWriter);
				sourceWriter.println("}");
			}
			parentVariable = getFieldValueGet(logger, voClass, field, parentVariable);
			if (parentVariable != null)
			{
				generateScreenOrDTOPopulation(logger, screen, parentVariable, type, sourceWriter, populateScreen);
			}
		}
	}

	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param name
	 */
	private void generateAutoBindHandlerForAllWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String name)
	{
		String valueVariable = "__wid";
		sourceWriter.println(valueVariable + "= Screen.get(\""+name+"\");");
		sourceWriter.println("if ("+valueVariable+" != null){");
		sourceWriter.println("if ("+valueVariable+" instanceof HasFormatter){");
		generateHandleHasFormatterWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
		sourceWriter.println("}else if ("+valueVariable+" instanceof HasValue){");
		generateHandleHasValueWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
		sourceWriter.print("}");
		if (String.class.isAssignableFrom(type))
		{
			sourceWriter.println("else if ("+valueVariable+" instanceof HasText){");
			generateHandleHasTextWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
			sourceWriter.println("}");
		}
		sourceWriter.println("}");
	}

	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param name
	 */
	private void generateAutoBindHandlerForDeclarativeWidgetsOnly(TreeLogger logger, Screen screen, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter,
			boolean populateScreen, Class<?> type, String name)
	{
		try
		{
			Class<? extends Widget> widgetClass = getClientWidget(screen, name);
			if (widgetClass != null)
			{
				String valueVariable = "__wid";
				sourceWriter.println(valueVariable + "= Screen.get(\""+name+"\");");
				if (HasFormatter.class.isAssignableFrom(widgetClass))
				{
					generateHandleHasFormatterWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
				}
				else if (HasValue.class.isAssignableFrom(widgetClass))
				{
					generateHandleHasValueWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
				}
				else if (String.class.isAssignableFrom(type) && HasText.class.isAssignableFrom(widgetClass))
				{
					generateHandleHasTextWidgets(logger, parentVariable, voClass, field, sourceWriter, populateScreen, type, valueVariable);
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientHandlerWidgetNotFound(name), e);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param valueVariable
	 */
	private void generateHandleHasTextWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type, String valueVariable)
	{
		if (populateScreen)
		{
			sourceWriter.println("Object o = " +getFieldValueGet(logger, voClass, field, parentVariable)+";");
			sourceWriter.println("((HasText)"+valueVariable+").setText(String.valueOf(o!=null?o:\"\"));");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, "((HasText)"+valueVariable+").getText()", sourceWriter);
		}
	}

	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 */
	private void generateHandleHasValueWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String valueVariable)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").setValue("
					            + getFieldValueGet(logger, voClass, field, parentVariable)+");");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, "("+getGenericDeclForType(type)+")((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").getValue()", sourceWriter);
		}
	}
	
	/**
	 * 
	 * @param logger
	 * @param parentVariable
	 * @param voClass
	 * @param field
	 * @param sourceWriter
	 * @param populateScreen
	 * @param type
	 * @param valueVariable
	 */
	private void generateHandleHasFormatterWidgets(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen, Class<?> type,
			String valueVariable)
	{
		if (populateScreen)
		{
			sourceWriter.println("((HasFormatter)"+valueVariable+").setUnformattedValue("
					            + getFieldValueGet(logger, voClass, field, parentVariable)+");");
		}
		else
		{
			generateFieldValueSet(logger, voClass, field, parentVariable, "("+getGenericDeclForType(type)+")"
					            + "((HasFormatter)"+valueVariable+").getUnformattedValue()", sourceWriter);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends Widget> getClientWidget(Screen screen,  String name) throws ClassNotFoundException
	{
		br.com.sysmap.crux.core.rebind.screen.Widget rebindWidget = screen.getWidget(name);
		
		if (rebindWidget != null)
		{
			String className = WidgetConfig.getClientClass(rebindWidget.getType());
			return (Class<? extends Widget>) Class.forName(className);
		}
		
		return null;
	}
	
	/**
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	private String getGenericDeclForType(Class<?> type)
	{
		if (type.isEnum())
		{
			return "String";
		}
		else if (type.isPrimitive())
		{
			if (type.getName().equals("boolean"))
			{
				return "Boolean";
			}
			else if (type.getName().equals("char"))
			{
				return "Character";
			}
			else if (type.getName().equals("byte"))
			{
				return "Byte";
			}
			else if (type.getName().equals("short"))
			{
				return "Short";
			}
			else if (type.getName().equals("int"))
			{
				return "Integer";
			}
			else if (type.getName().equals("long"))
			{
				return "Long";
			}
			else if (type.getName().equals("float"))
			{
				return "Float";
			}
			else if (type.getName().equals("double"))
			{
				return "Double";
			}
			return "?";
		}
		else
		{
			return type.getName();
		}
	}

	/**
	 * Get the field type
	 * @param logger
	 * @param field
	 * @return
	 */
	private Class<?> getTypeForField(TreeLogger logger, Field field)
	{
		Class<?> type = field.getType();
		if (type.getName().endsWith("Async"))
		{
			try 
			{
				type = Class.forName(type.getName().substring(0,type.getName().length()-5));
			} 
			catch (Exception e) 
			{
				logger.log(TreeLogger.DEBUG, "Error reading field super type."); 
			}
		}
		return type;
	}
}
