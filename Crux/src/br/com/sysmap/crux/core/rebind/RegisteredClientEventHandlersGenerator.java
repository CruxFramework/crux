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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.rebind.jsonparser.JSONParser;
import br.com.sysmap.crux.core.server.event.clienthandlers.ClientControllers;
import br.com.sysmap.crux.core.server.screen.Component;
import br.com.sysmap.crux.core.server.screen.Event;
import br.com.sysmap.crux.core.server.screen.Screen;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
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
		sourceWriter.println("private java.util.Map clientCallbacks = new java.util.HashMap();");

		generateConstructor(logger, sourceWriter, screen, implClassName);

		sourceWriter.println("public EventClientHandlerInvoker getEventHandler(String id){");
		sourceWriter.println("return (EventClientHandlerInvoker) clientHandlers.get(id);");
		sourceWriter.println("}");
		
		sourceWriter.println("public EventClientCallbackInvoker getEventCallback(String id){");
		sourceWriter.println("return (EventClientCallbackInvoker) clientCallbacks.get(id);");
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
	 */
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, Screen screen, String implClassName) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");
		
		Iterator<Component> iterator = screen.iterateComponents();
		Map<String, Boolean> addedHandler = new HashMap<String, Boolean>();
		Map<String, Boolean> addedCallback = new HashMap<String, Boolean>();
		while (iterator.hasNext())
		{
			Component component = iterator.next();
			generateEventHandlersForComponent(logger, sourceWriter, component, addedHandler, addedCallback);

		}
		sourceWriter.println("}");
	}
	
	/**
	 * For each component, create the inclusion block for controllers used by it.
	 * @param logger
	 * @param sourceWriter
	 * @param component
	 * @param addedHandler
	 * @param addedCallback
	 */
	protected void generateEventHandlersForComponent(TreeLogger logger,SourceWriter sourceWriter, Component component, Map<String, Boolean> addedHandler, Map<String, Boolean> addedCallback)
	{
		Iterator<Event> events = component.iterateEvents();
		
		while (events.hasNext())
		{
			Event event = events.next();
			generateEventHandlerBlock(logger,sourceWriter, component.getId(), event, addedHandler);
			generateEventCallbackBlock(logger,sourceWriter, component.getId(), event, addedCallback);
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
	protected void generateEventHandlerBlock(TreeLogger logger, SourceWriter sourceWriter, String componentId, Event event, Map<String, Boolean> added)
	{
		String evtCall = event.getEvtCall();
		String handler;
		try
		{
			handler = RegexpPatterns.REGEXP_DOT.split(evtCall)[0];
			if (!added.containsKey(handler) && ClientControllers.getClientHandler(handler)!= null)
			{
				String genClass = generateEventHandlerInvokerClass(logger,sourceWriter,ClientControllers.getClientHandler(handler));
				sourceWriter.print("clientHandlers.put(\""+handler+"\", new " + genClass + "());");
				added.put(handler, true);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientHandler(componentId, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Generate the block to include event callback handler object.
	 * @param logger
	 * @param sourceWriter
	 * @param componentId
	 * @param event
	 * @param added
	 */
	protected void generateEventCallbackBlock(TreeLogger logger, SourceWriter sourceWriter, String componentId, Event event, Map<String, Boolean> added)
	{
		String evtCallback = event.getEvtCallback();
		if (evtCallback != null)
		{
			String callback;
			try
			{
				callback = RegexpPatterns.REGEXP_DOT.split(evtCallback)[0];
				if (!added.containsKey(callback) && ClientControllers.getClientCallback(callback)!= null)
				{
					String genClass = generateEventCallbackInvokerClass(logger,sourceWriter,ClientControllers.getClientCallback(callback));
					sourceWriter.println("clientCallbacks.put(\""+callback+"\", new " + genClass + "());");
					added.put(callback, true);
				}
			}
			catch (Throwable e) 
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredClientCallback(componentId, e.getLocalizedMessage()), e);
			}
		}
	}
	
	/**
	 * Create a new class to invoke the eventHandler method by its name
	 * @param logger
	 * @param sourceWriter
	 * @param handlerClass
	 * @return
	 */
	protected String generateEventHandlerInvokerClass(TreeLogger logger, SourceWriter sourceWriter, Class<?> handlerClass)
	{
		String className = handlerClass.getSimpleName();
		sourceWriter.println("class "+className+"Wrapper extends " + handlerClass.getName()
				+ " implements br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker{");
		sourceWriter.println("public void invoke(String metodo, Screen screen, String idSender) throws Exception{ ");
		sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
		generateParametersSetters(logger, handlerClass, sourceWriter);
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
						validateMethod = "validate"+ method.getName();
					}
					sourceWriter.println("wrapper."+validateMethod+"();");
				}
				sourceWriter.println("wrapper."+method.getName()+"();");
				if (annot != null)
				{
					sourceWriter.println("}catch (Throwable e){");
					sourceWriter.println("com.google.gwt.user.client.Window.alert(e.getMessage());");
					sourceWriter.println("}");
				}
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
		if (!method.getReturnType().getName().equals("java.lang.Void") &&
			!method.getReturnType().getName().equals("void"))
		{
			return false;
		}
		
		if(method.getName().equals("notify") ||
           method.getName().equals("notifyAll") ||
		   method.getName().equals("wait"))
		{
			return false;
		}
		return true;
	}

	/**
	 * Create a new class to invoke the event callback method by its name
	 * @param logger
	 * @param sourceWriter
	 * @param callbackClass
	 * @return
	 */
	protected String generateEventCallbackInvokerClass(TreeLogger logger, SourceWriter sourceWriter, Class<?> callbackClass)
	{
		String className = callbackClass.getSimpleName();
		sourceWriter.println("class "+className+"Wrapper extends " + callbackClass.getName() 
							+ " implements br.com.sysmap.crux.core.client.event.EventClientCallbackInvoker{");
		sourceWriter.println("public void invoke(String metodo, Screen screen, String idSender, JSONValue result) throws Exception{ ");
		sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
		generateParametersSetters(logger, callbackClass, sourceWriter);
		
		Method[] methods = callbackClass.getMethods();
		
		boolean first = true;
		for (Method method: methods) 
		{
			if (isCallbackMethodSignatureValid(method))
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				sourceWriter.println("if (\""+method.getName()+"\".equals(metodo)) {");
				JSONParser.getInstance().generateParameterDeserialisationBlock(method, sourceWriter, "result", "_v");
				sourceWriter.println("wrapper."+method.getName()+"(_v);");
				//sourceWriter.println("wrapper."+method.getName()+"(result);");
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
	protected boolean isCallbackMethodSignatureValid(Method method)
	{
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters == null || parameters.length != 1)
		{
			return false;
		}

		if (!method.getReturnType().getName().equals("java.lang.Void") &&
			!method.getReturnType().getName().equals("void"))
		{
			return false;
		}

		if (method.getName().equals("wait"))
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
			if (method != null)
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
			if (method != null)
			{
				sourceWriter.println("wrapper.setScreen(screen);");
			}
		} 
		catch (Exception e) 
		{
			logger.log(TreeLogger.DEBUG, "screen ignored."); 
		}
	}
}
