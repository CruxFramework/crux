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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.event.CruxEvent;
import br.com.sysmap.crux.core.client.event.EventProcessor;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.rebind.controller.ControllerProxyCreator;
import br.com.sysmap.crux.core.rebind.module.Modules;
import br.com.sysmap.crux.core.rebind.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates a RegisteredControllers class.
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredControllersGenerator extends AbstractRegisteredElementsGenerator
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
		composer.addImport(GWT.class.getName());
		composer.addImport(br.com.sysmap.crux.core.client.screen.Screen.class.getName());
		composer.addImport(CruxEvent.class.getName());
		composer.addImport(GwtEvent.class.getName());
		composer.addImport(HasValue.class.getName());
		composer.addImport(HasText.class.getName());
		composer.addImport(HasFormatter.class.getName());
		composer.addImport(Widget.class.getName());
		composer.addImport(RunAsyncCallback.class.getName());
		composer.addImport(EventProcessor.class.getName());
		composer.addImport(Crux.class.getName());
		
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.event.RegisteredControllers");
		
		SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
		sourceWriter.println("private java.util.Map<String, ControllerInvoker> controllers = new java.util.HashMap<String, ControllerInvoker>();");

		Map<String, String> controllerClassNames = new HashMap<String, String>();
		for (Screen screen : screens)
		{
			generateControllersForScreen(logger, sourceWriter, screen, controllerClassNames, packageName+"."+implClassName, context);
		}

		generateConstructor(sourceWriter, implClassName, controllerClassNames);
		generateValidateControllerMethod(sourceWriter);
		generateControllerInvokeMethod(sourceWriter, controllerClassNames);
		generateCrossDocInvokeMethod(sourceWriter, controllerClassNames);//TODO filtrar as classes (controllerClassNames)
		generateRegisterControllerMethod(sourceWriter); 
		
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}


	/**
	 * @param sourceWriter
	 */
	private void generateValidateControllerMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public boolean __validateController(String controllerId){");
		sourceWriter.println("String[] controllers = Screen.getControllers();");
		sourceWriter.println("for (String c: controllers){");
		sourceWriter.println("if (c.equals(controllerId)){");
		sourceWriter.println("return true;");
		sourceWriter.println("}");
		sourceWriter.println("}");
		sourceWriter.println("return false;");
		sourceWriter.println("}");
	}
	
	/**
	 * @param sourceWriter
	 * @param implClassName
	 * @param controllerClassNames
	 */
	private void generateConstructor(SourceWriter sourceWriter, String implClassName, Map<String, String> controllerClassNames)
	{
		
		sourceWriter.println("public "+implClassName+"(){");
		for (String controller : controllerClassNames.keySet()) 
		{
			Class<?> controllerClass = ClientControllers.getController(controller);
			Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
			if (controllerAnnot != null && !controllerAnnot.lazy())
			{
				Global globalAnnot = controllerClass.getAnnotation(Global.class);
				if (globalAnnot == null)
				{
					sourceWriter.println("if (__validateController(\""+controller+"\"))");
				}
				sourceWriter.println("controllers.put(\""+controller+"\", new " + controllerClassNames.get(controller) + "());");
			}
		}
		sourceWriter.println("}");
	}

	private void generateRegisterControllerMethod(SourceWriter sourceWriter)
    {
		sourceWriter.println("public void registerController(String controller, ControllerInvoker controllerInvoker){");
		sourceWriter.println("if (!controllers.containsKey(controller)){");
		sourceWriter.println("controllers.put(controller, controllerInvoker);");
		sourceWriter.println("}");
		sourceWriter.println("}");
	}
	
	private void generateCrossDocInvokeMethod(SourceWriter sourceWriter, Map<String, String> controllerClassNames)
	{
		sourceWriter.println("public String invokeCrossDocument(String serializedData){");
		sourceWriter.println("return null;");
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param controllerClassNames
	 */
	private void generateControllerInvokeMethod(SourceWriter sourceWriter, Map<String, String> controllerClassNames)
	{
		sourceWriter.println("public void invokeController(final String controllerName, final String method, final boolean fromOutOfModule, final Object sourceEvent, final EventProcessor eventProcessor){");
		sourceWriter.println("ControllerInvoker controller = controllers.get(controllerName);");
		sourceWriter.println("if (controller != null){");
		sourceWriter.println("try{");
		sourceWriter.println("controller.invoke(method, sourceEvent, fromOutOfModule, eventProcessor);");
		sourceWriter.println("}");
		sourceWriter.println("catch (Exception e)"); 
		sourceWriter.println("{");
		sourceWriter.println("eventProcessor._exception = e;");
		sourceWriter.println("}");
		sourceWriter.println("return;");
		sourceWriter.println("}");

		boolean first = true;
		for (String controller : controllerClassNames.keySet()) 
		{
			Class<?> controllerClass = ClientControllers.getController(controller);
			Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
			if (controllerAnnot == null || controllerAnnot.lazy())
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				else
				{
					first = false;
				}
				sourceWriter.println("if (\""+controller+"\".equals(controllerName)){");
				if (controllerAnnot != null && Fragments.getFragmentClass(controllerAnnot.fragment()) != null)
				{
					sourceWriter.println("GWT.runAsync("+Fragments.getFragmentClass(controllerAnnot.fragment())+".class, new RunAsyncCallback(){");
					sourceWriter.println("public void onFailure(Throwable reason){");
					sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerCanNotBeLoaded(controller));");
					sourceWriter.println("}");
					sourceWriter.println("public void onSuccess(){");
					sourceWriter.println("if (!controllers.containsKey(\""+controller+"\")){");
					sourceWriter.println("controllers.put(\""+controller+"\", new " + controllerClassNames.get(controller) + "());");
					sourceWriter.println("}");
					sourceWriter.println("invokeController(controllerName, method, fromOutOfModule, sourceEvent, eventProcessor);");
					sourceWriter.println("}");
					sourceWriter.println("});");
				}
				else
				{
					sourceWriter.println("if (!controllers.containsKey(\""+controller+"\")){");
					sourceWriter.println("controllers.put(\""+controller+"\", new " + controllerClassNames.get(controller) + "());");
					sourceWriter.println("}");
					sourceWriter.println("invokeController(controllerName, method, fromOutOfModule, sourceEvent, eventProcessor);");
				}
				sourceWriter.println("}");
			}
		}
		//TODO - Thiago - aparentemente, se o controller nao existir, nao notifica mais isso pro usuario.... não lançar exceção... apenas logar no console
		
		sourceWriter.println("}");
	}
	
	/**
	 * generate wrapper classes for event handling.
	 * @param logger
	 * @param sourceWriter
	 * @param screen
	 * @param context 
	 */
	private void generateControllersForScreen(TreeLogger logger, SourceWriter sourceWriter, Screen screen, 
			Map<String, String> controllerClassNames, String implClassName, GeneratorContext context)
	{
		Iterator<String> controllers = screen.iterateControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateControllerBlock(logger, sourceWriter, controller, controllerClassNames, context);
		}		

		controllers = ClientControllers.iterateGlobalControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			Class<?> controllerClass = ClientControllers.getController(controller);
			if (controllerClass != null)
			{
				String controllerClassName = getClassSourceName(controllerClass).replace('.', '/');
				if (Modules.getInstance().isClassOnModulePath(controllerClassName, screen.getModule()))
				{
					generateControllerBlock(logger, sourceWriter, controller, controllerClassNames, context);
				}
			}
		}		
	}

	/**
	 * Generate the block to include controller object.
	 * @param logger
	 * @param sourceWriter
	 * @param added
	 * @param context 
	 */
	private void generateControllerBlock(TreeLogger logger, SourceWriter sourceWriter, String controller, 
			Map<String, String> added, GeneratorContext context)
	{
		try
		{
			if (!added.containsKey(controller) && ClientControllers.getController(controller)!= null)
			{
				String genClass = new ControllerProxyCreator(logger, context, ClientControllers.getController(controller)).create();
				added.put(controller, genClass);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredController(controller, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Create a new class to invoke the caller method by its name
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @return
	 *
	private String generateControllerInvokerClass(TreeLogger logger, Screen screen, SourceWriter sourceWriter, Class<?> controllerClass)
	{
		String className = controllerClass.getSimpleName();
		
		boolean isCrossDoc = (CrossDocument.class.isAssignableFrom(controllerClass));
		String baseInterface = getClassSourceName(isCrossDoc?CrossDocumentInvoker.class:ControllerInvoker.class);
		sourceWriter.print("protected class "+className+"Wrapper extends " + getClassSourceName(controllerClass)
				+ " implements " + baseInterface+"{");
		
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		boolean singleton = (controllerAnnot == null || controllerAnnot.statefull());
		boolean autoBindEnabled = (controllerAnnot == null || controllerAnnot.autoBind());
		if (singleton)
		{
			sourceWriter.println(className+"Wrapper wrapper = null;");
		}
		sourceWriter.println(Map.class.getName()+"<String, Boolean> __runningMethods = new "+HashMap.class.getName()+"<String, Boolean>();");

		generateInvokeMethod(logger, sourceWriter, controllerClass, className, singleton, autoBindEnabled);
		generateScreenUpdateWidgetsFunction(logger, screen, controllerClass, sourceWriter);
		generateControllerUpdateObjectsFunction(logger, screen, controllerClass, sourceWriter);
		generateIsAutoBindEnabledMethod(sourceWriter, autoBindEnabled);
		
		if (isCrossDoc)
		{
			generateCrossDocInvokeMethod(logger, sourceWriter, controllerClass, className, singleton, autoBindEnabled);
		}
		sourceWriter.println("}");
		
		return className+"Wrapper";
	}
*/
	
	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @param className
	 * @param methods
	 * @param singleton
	 * @param autoBindEnabled
	 *
	private void generateInvokeMethod(TreeLogger logger, SourceWriter sourceWriter, Class<?> controllerClass, 
			          String className, boolean singleton, boolean autoBindEnabled)
    {
	    sourceWriter.println("public void invoke(String metodo, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception{ ");
		sourceWriter.println("boolean __runMethod = true;");
		
		if (singleton)
		{
			sourceWriter.println("if (this.wrapper == null){");
			sourceWriter.println("this.wrapper = new "+className+"Wrapper();");
			generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println(className+"Wrapper wrapper = new "+className+"Wrapper();");
			generateAutoCreateFields(logger, controllerClass, sourceWriter, "wrapper");
		}
		

		if (autoBindEnabled)
		{
			sourceWriter.println("if (!__runningMethods.containsKey(metodo)){");
			sourceWriter.println("wrapper.updateControllerObjects();");
			sourceWriter.println("}");
		}
		
		boolean first = true;
		Method[] methods = controllerClass.getMethods(); 
		for (Method method: methods) 
		{
			if (isControllerMethodSignatureValid(method))
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				
				generateInvokeBlockForMethod(logger, sourceWriter, controllerClass, method);

				first = false;
			}
		}
		if (!first)
		{
			sourceWriter.println(" else ");
		}
		sourceWriter.println("throw new Exception(\""+messages.errorInvokingGeneratedMethod()+" \"+metodo);");

		if (!first && autoBindEnabled)
		{
			sourceWriter.println("wrapper.updateScreenWidgets();");
		}		
		
		sourceWriter.println("}");
    }

	/**
	 * @param logger
	 * @param sourceWriter
	 * @param controllerClass
	 * @param method
	 *
	private void generateInvokeBlockForMethod(TreeLogger logger,
            SourceWriter sourceWriter, Class<?> controllerClass, Method method)
    {
	    if (method.getAnnotation(ExposeOutOfModule.class) != null)
	    {
	    	sourceWriter.println("if (\""+method.getName()+"\".equals(metodo)) {");
	    }
	    else
	    {
	    	sourceWriter.println("if (\""+method.getName()+"\".equals(metodo) && !fromOutOfModule) {");
	    }
	    
	    boolean allowMultipleClicks = isAllowMultipleClicks(method);
	    if (!allowMultipleClicks)
	    {
			sourceWriter.println("if (!__runningMethods.containsKey(metodo)){");
	    	sourceWriter.println("__runningMethods.put(metodo,true);");
	    	sourceWriter.println("try{");
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
	    	generateValidateMethodCall(logger, controllerClass, method, validateMethod, sourceWriter);
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

	    if (!allowMultipleClicks)
	    {
	    	sourceWriter.println("}finally{");
	    	sourceWriter.println("__runningMethods.remove(metodo);");
	    	sourceWriter.println("}");
	    	sourceWriter.println("}");
	    }
	    
	    sourceWriter.println("}");
    }
	
	/**
	 * @param method
	 * @return
	 *
	private boolean isAllowMultipleClicks(Method method)
    {
	    Expose exposeAnnot = method.getAnnotation(Expose.class);
	    if (exposeAnnot != null)
	    {
	    	return exposeAnnot.allowMultipleCalls();
	    }
		
	    ExposeOutOfModule exposeOutAnnot = method.getAnnotation(ExposeOutOfModule.class);
	    if (exposeOutAnnot != null)
	    {
	    	return exposeOutAnnot.allowMultipleCalls();
	    }
		return false;
    }

	/**
	 * 
	 * @param logger
	 * @param controllerClass
	 * @param method
	 * @param validateMethod
	 * @param sourceWriter
	 *
	private void generateValidateMethodCall(TreeLogger logger, Class<?> controllerClass, Method method, String validateMethod, SourceWriter sourceWriter)
	{
		Class<?>[] params = method.getParameterTypes();
		try
		{
			Method validate = null;
			if (params != null && params.length == 1)
			{
				validate = ClassUtils.getMethod(controllerClass, validateMethod, params[0]);
				if(validate == null)
				{
					validate = ClassUtils.getMethod(controllerClass, validateMethod, new Class[]{});
				}
			}
			else
			{
				validate = ClassUtils.getMethod(controllerClass, validateMethod, new Class[]{});
			}
			generateMethodCall(validate, sourceWriter);
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredControllerInvalidValidateMethod(validateMethod), e);
		}
	}

	/** 
	 * Generates the controller method call.
	 * @param method
	 * @param sourceWriter
	 *
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
	 *
	private boolean isControllerMethodSignatureValid(Method method)
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
	}*/
}
