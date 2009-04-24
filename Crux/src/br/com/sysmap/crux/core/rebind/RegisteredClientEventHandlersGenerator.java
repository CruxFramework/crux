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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.rebind.screen.Component;
import br.com.sysmap.crux.core.rebind.screen.Event;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
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
	 * constructed looping all components to just include controllers that are used on the screen.
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
		Iterator<Component> iterator = screen.iterateComponents();
		while (iterator.hasNext())
		{
			Component component = iterator.next();
			generateEventHandlersForComponent(logger, sourceWriter, component, handlerClassNames, implClassName);

		}
	}
	/**
	 * For each component, create the inclusion block for controllers used by it.
	 * @param logger
	 * @param sourceWriter
	 * @param component
	 * @param addedHandler
	 * @param addedCallback
	 */
	protected void generateEventHandlersForComponent(TreeLogger logger,SourceWriter sourceWriter, Component component, 
			Map<String, String> addedHandler, String implClassName)
	{
		Iterator<Event> events = component.iterateEvents();
		
		while (events.hasNext())
		{
			Event event = events.next();
			generateEventHandlerBlock(logger,sourceWriter, component.getId(), event, addedHandler, implClassName);
		}
	}
	
	/**
	 * Generate the block to include event handler object.
	 * @param logger
	 * @param sourceWriter
	 * @param componentId
	 * @param event
	 * @param added
	 */
	protected void generateEventHandlerBlock(TreeLogger logger, SourceWriter sourceWriter, String componentId, Event event, 
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
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientHandler(componentId, e.getLocalizedMessage()), e);
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
		
		generateSetFieldsMethods(logger, handlerClass, sourceWriter, implClassName);
		sourceWriter.println("public void invoke(String metodo, Screen screen, String idSender, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("boolean __runMethod = true;");
		sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
		generateParametersSetters(logger, handlerClass, sourceWriter);
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
					sourceWriter.println("eventProcessor._returnValue = wrapper."+method.getName()+"();");
				}
				else
				{
					sourceWriter.println("wrapper."+method.getName()+"();");
				}
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
		sourceWriter.println("}");
		sourceWriter.println("}");
		
		return className+"Wrapper";
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
		if (parameters != null && parameters.length != 0)
		{
			return false;
		}
		if (method.getDeclaringClass().equals(Object.class))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Generate the property setters block in the generated classes
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 */
	protected void generateParametersSetters(TreeLogger logger, Class<?> controller, SourceWriter sourceWriter)
	{
		Method method;
		try 
		{
			method = controller.getMethod("setIdSender", new Class[]{String.class});
			if (method != null && (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())))
			{
				sourceWriter.println("wrapper.setIdSender(idSender);");
			}
		} 
		catch (Exception e) 
		{
			logger.log(TreeLogger.DEBUG, "idSender ignored."); 
		}
		try 
		{
			method = controller.getMethod("setScreen", new Class[]{br.com.sysmap.crux.core.client.component.Screen.class});
			if (method != null && (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())))
			{
				sourceWriter.println("wrapper.setScreen(screen);");
			}
		} 
		catch (Exception e) 
		{
			logger.log(TreeLogger.DEBUG, "screen ignored."); 
		}
	}
	
	/**
	 * Generate methods for set fields in delegate object that are declared as private or default visibility 
	 * 
	 * @param logger
	 * @param controller
	 * @param sourceWriter
	 * @param implClassName
	 */
	protected void generateSetFieldsMethods(TreeLogger logger,	Class<?> controller, SourceWriter sourceWriter, String implClassName) 
	{
		for (Field field : controller.getDeclaredFields()) 
		{
			if (field.getAnnotation(Create.class) != null)
			{
				if ((!Modifier.isPublic(field.getModifiers()) && !Modifier.isProtected(field.getModifiers())))
				{
					generateSetFieldMethod(logger, controller, field.getName(), sourceWriter, implClassName);
				}
			}
		}
		
	}
	/**
	 * Generate a method for set field in delegate object that are declared as private or default visibility 
	 * @param logger
	 * @param handlerClass
	 * @param sourceWriter
	 * @param implClassName
	 */
	protected void generateSetFieldMethod(TreeLogger logger, Class<?> controller, String fieldName,
			SourceWriter sourceWriter, String implClassName) 
	{
		String className = implClassName+"$"+controller.getSimpleName()+"Wrapper";
		
		sourceWriter.print("public native void _setField"+fieldName+"(Object fieldValue)/*-");
		sourceWriter.print("{");
		sourceWriter.print("this.@"+className+"::"+fieldName+"=fieldValue;");
		sourceWriter.print("}-*/;");
		
	}

	/**
	 * Create objects for fields that are annoteded with @Create
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

				sourceWriter.println(field.getType().getName()+" _field"+field.getName()+"=GWT.create("+type.getName()+".class);");
				if ((Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
				{
					sourceWriter.println("wrapper."+field.getName()+"=_field"+field.getName()+";");
				}
				else
				{
					sourceWriter.print("wrapper._setField"+field.getName()+"(_field"+field.getName()+");");
				}
				
				if (RemoteService.class.isAssignableFrom(type) && type.getAnnotation(RemoteServiceRelativePath.class) == null)
				{
					sourceWriter.println("(("+ServiceDefTarget.class.getName()+")_field"+field.getName()+").setServiceEntryPoint(\"rpc\");");
				}
			}
		}
		
		if (controller.getSuperclass() != null)
		{
			generateAutoCreateFields(logger, controller.getSuperclass(), sourceWriter);
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
