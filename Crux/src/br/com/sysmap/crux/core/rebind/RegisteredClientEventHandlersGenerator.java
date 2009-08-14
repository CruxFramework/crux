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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Validate;
import br.com.sysmap.crux.core.client.event.CruxEvent;
import br.com.sysmap.crux.core.rebind.screen.Screen;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Creates a Mechanism to work around the lack of reflection support in GWT. This class provides
 * implementations of ClientHandlerInvoker and ClientCallbackInvoker. These implementations are used
 * by EventProcessorFactories to call methods by their names.
 * @author Thiago Bustamante
 */
public class RegisteredClientEventHandlersGenerator extends AbstractRegisteredClientInvokableGenerator
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
			generateEventHandlerBlock(logger, screen, sourceWriter, controller, handlerClassNames);
		}		

		controllers = ClientControllers.iterateGlobalClientHandler();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateEventHandlerBlock(logger, screen, sourceWriter, controller, handlerClassNames);
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
			Map<String, String> added)
	{
		try
		{
			if (!added.containsKey(controller) && ClientControllers.getClientHandler(controller)!= null)
			{
				String genClass = generateEventHandlerInvokerClass(logger,screen,sourceWriter,ClientControllers.getClientHandler(controller));
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
	protected String generateEventHandlerInvokerClass(TreeLogger logger, Screen screen, SourceWriter sourceWriter, Class<?> handlerClass)
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
			sourceWriter.print("wrapper."+method.getName()+"(("+getClassSourceName(params[0])+")sourceEvent);");
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
}
