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
import java.util.Map;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.ScreenBind;
import br.com.sysmap.crux.core.client.controller.ValueObject;
import br.com.sysmap.crux.core.client.event.CruxEvent;
import br.com.sysmap.crux.core.client.event.annotation.Controller;
import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.rebind.screen.Event;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.Widget;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
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
	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, Screen screen) 
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
		composer.addImport("br.com.sysmap.crux.core.client.component.Screen");
		composer.addImport("br.com.sysmap.crux.core.client.event.CruxEvent");
		composer.addImport("com.google.gwt.event.shared.GwtEvent");
		composer.addImport("com.google.gwt.user.client.ui.HasValue");
		composer.addImport("com.google.gwt.user.client.ui.Widget");
		
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.event.RegisteredClientEventHandlers");
		
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);
		sourceWriter.println("private java.util.Map clientHandlers = new java.util.HashMap();");

		Map<String, String> handlerClassNames = new HashMap<String, String>();
		generateEventHandlersForScreen(logger, sourceWriter, screen, handlerClassNames, packageName+"."+implClassName);
		generateConstructor(logger, sourceWriter, screen, implClassName, handlerClassNames);

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
	 * @param screen
	 * @param implClassName
	 * @para handlerClassNames
	 */
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, Screen screen, String implClassName, 
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
	protected void generateEventHandlersForScreen(TreeLogger logger,SourceWriter sourceWriter, Screen screen, 
			Map<String, String> handlerClassNames, String implClassName)
	{
		Iterator<Widget> iterator = screen.iterateWidgets();
		while (iterator.hasNext())
		{
			Widget widget = iterator.next();
			generateEventHandlersForWidget(logger, sourceWriter, widget, handlerClassNames, implClassName);

		}
	}
	/**
	 * For each widget, create the inclusion block for controllers used by it.
	 * @param logger
	 * @param sourceWriter
	 * @param widget
	 * @param addedHandler
	 * @param addedCallback
	 */
	protected void generateEventHandlersForWidget(TreeLogger logger,SourceWriter sourceWriter, Widget widget, 
			Map<String, String> addedHandler, String implClassName)
	{
		Iterator<Event> events = widget.iterateEvents();
		
		while (events.hasNext())
		{
			Event event = events.next();
			generateEventHandlerBlock(logger,sourceWriter, widget.getId(), event, addedHandler, implClassName);
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
	protected void generateEventHandlerBlock(TreeLogger logger, SourceWriter sourceWriter, String widgetId, Event event, 
			Map<String, String> added, String implClassName)
	{
		String evtCall = event.getEvtCall();
		String handler;
		try
		{
			handler = RegexpPatterns.REGEXP_DOT.split(evtCall)[0];
			if (!added.containsKey(handler) && ClientControllers.getClientHandler(handler)!= null)
			{
				String genClass = generateEventHandlerInvokerClass(logger,sourceWriter,ClientControllers.getClientHandler(handler), implClassName);
				added.put(handler, genClass);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientHandler(widgetId, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Create a new class to invoke the eventHandler method by its name
	 * @param logger
	 * @param sourceWriter
	 * @param handlerClass
	 * @return
	 */
	protected String generateEventHandlerInvokerClass(TreeLogger logger, SourceWriter sourceWriter, Class<?> handlerClass, String implClassName)
	{
		String className = handlerClass.getSimpleName();
		sourceWriter.println("public class "+className+"Wrapper extends " + handlerClass.getName()
				+ " implements br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker{");
		
		Controller controllerAnnot = handlerClass.getAnnotation(Controller.class);
		boolean singleton = (controllerAnnot != null && controllerAnnot.statefull());
		if (singleton)
		{
			sourceWriter.println(className+"Wrapper wrapper = null;");
		}

		sourceWriter.println("public void invoke(String metodo, GwtEvent<?> sourceEvent, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("invokeEvent(metodo, sourceEvent, eventProcessor);");
		sourceWriter.println("}");

		sourceWriter.println("public void invoke(String metodo, CruxEvent<?> sourceEvent, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("invokeEvent(metodo, sourceEvent, eventProcessor);");
		sourceWriter.println("}");
		
		sourceWriter.println("public void invokeEvent(String metodo, Object sourceEvent, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("boolean __runMethod = true;");
		
		if (singleton)
		{
			sourceWriter.println("if (this.wrapper == null)");
			sourceWriter.println("this.wrapper = new "+className+"Wrapper();");
		}
		else
		{
			sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
		}
		
		sourceWriter.println("Widget __wid = null;");
		
		generateAutoCreateFields(logger, handlerClass, sourceWriter);
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
				sourceWriter.println("if (\""+method.getName()+"\".equals(metodo)) {");
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
		sourceWriter.println("throw new Exception(\""+messages.errorinvokingGeneratedMethod()+" \"+metodo);");
		if (!first)
		{
			//generateScreenUpdateWidgets(logger, handlerClass, sourceWriter);
			sourceWriter.println("wrapper.updateScreenWidgets();");
		}
		sourceWriter.println("}");
		
		generateScreenUpdateWidgetsFunction(logger, handlerClass, sourceWriter);
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
				Class<?> type = getTypeForField(logger, field);

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateDTOFieldPopulation(logger, "wrapper", controller, field,sourceWriter);
				}
				else
				{
					sourceWriter.println(field.getType().getName()+" _field"+field.getName()+"=GWT.create("+type.getName()+".class);");
					generateFieldValueSet(logger, controller, field, "wrapper", "_field"+field.getName(), sourceWriter);

					if (RemoteService.class.isAssignableFrom(type) && type.getAnnotation(RemoteServiceRelativePath.class) == null)
					{
						sourceWriter.println("(("+ServiceDefTarget.class.getName()+")_field"+field.getName()+").setServiceEntryPoint(\"crux.rpc\");");
					}
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
	protected void generateScreenOrDTOPopulation(TreeLogger logger, String resultVariable, Class<?> voClass, SourceWriter sourceWriter, boolean populateScreen)
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
					generateScreenOrDTOPopulationField(logger, resultVariable, voClass, field, sourceWriter, populateScreen);
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
	protected void generateDTOFieldPopulation(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, parentVariable, voClass, field, sourceWriter, false);
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
	protected void generateScreenUpdateWidgetsFunction(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter)
	{
		sourceWriter.println("public void updateScreenWidgets(){");
		sourceWriter.println("Widget __wid = null;");
		generateScreenUpdateWidgets(logger, "this", controller, sourceWriter);
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param logger
	 * @param controllerVariable
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateScreenUpdateWidgets(TreeLogger logger, String controllerVariable, Class<?> controller, SourceWriter sourceWriter)
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				Class<?> type = field.getType();

				if (type.getAnnotation(ValueObject.class) != null)
				{
					generateScreenWidgetPopulation(logger, controllerVariable, controller, field,sourceWriter);
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateScreenUpdateWidgets(logger, controllerVariable, controller.getSuperclass(), sourceWriter);
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
	protected void generateScreenWidgetPopulation(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter)
	{
		generateScreenOrDTOPopulationField(logger, parentVariable, voClass, field, sourceWriter, true);
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
	protected void generateScreenOrDTOPopulationField(TreeLogger logger, String parentVariable, Class<?> voClass, Field field, SourceWriter sourceWriter, boolean populateScreen)
	{
		Class<?> type = field.getType();
		String name = null;
		if (field.getAnnotation(ScreenBind.class) != null)
		{
			name = field.getAnnotation(ScreenBind.class).value();
		}
		if (name == null)
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
			String valueVariable = "__wid";
			sourceWriter.println(valueVariable + "= Screen.get().getWidget(\""+name+"\");");
			sourceWriter.println("if ("+valueVariable+" != null && "+valueVariable+" instanceof HasValue){");
			if (populateScreen)
			{
				sourceWriter.println("((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").setValue("
						            + getFieldValueGet(logger, voClass, field, parentVariable)+");");
			}
			else
			{
				generateFieldValueSet(logger, voClass, field, parentVariable, "((HasValue<"+getGenericDeclForType(type)+">)"+valueVariable+").getValue()", sourceWriter);
			}
			sourceWriter.println("}");
		}
		else if (type.getAnnotation(ValueObject.class) != null)
		{
			if (!populateScreen)
			{
				generateFieldValueSet(logger, voClass, field, parentVariable, "new "+type.getName()+"()", sourceWriter);
			}
			parentVariable = getFieldValueGet(logger, voClass, field, parentVariable);
			if (parentVariable != null)
			{
				generateScreenOrDTOPopulation(logger, parentVariable, type, sourceWriter, populateScreen);
			}
		}
	}
	
	
	/**
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	protected String getGenericDeclForType(Class<?> type)
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
	protected Class<?> getTypeForField(TreeLogger logger, Field field)
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
