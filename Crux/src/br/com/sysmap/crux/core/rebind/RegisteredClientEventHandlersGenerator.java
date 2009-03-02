package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.server.event.clienthandlers.ClientControllers;
import br.com.sysmap.crux.core.server.screen.Component;
import br.com.sysmap.crux.core.server.screen.Container;
import br.com.sysmap.crux.core.server.screen.Event;
import br.com.sysmap.crux.core.server.screen.Screen;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class RegisteredClientEventHandlersGenerator extends AbstractRegisteredElementsGenerator
{
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
	protected void generateEventHandlersForComponent(TreeLogger logger,SourceWriter sourceWriter, Component component, Map<String, Boolean> addedHandler, Map<String, Boolean> addedCallback)
	{
		Iterator<Event> events = component.iterateEvents();
		
		while (events.hasNext())
		{
			Event event = events.next();
			generateEventHandlerBlock(logger,sourceWriter, component.getId(), event, addedHandler);
			generateEventCallbackBlock(logger,sourceWriter, component.getId(), event, addedCallback);
		}
		if (component instanceof Container)
		{
			Iterator<Component> iterator = ((Container)component).iterateComponents();
			while (iterator.hasNext())
			{
				Component child = iterator.next();
				generateEventHandlersForComponent(logger, sourceWriter, child, addedHandler, addedCallback);
			}
		}
	}
	
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
	
	protected String generateEventHandlerInvokerClass(TreeLogger logger, SourceWriter sourceWriter, Class<?> handlerClass)
	{
		String className = handlerClass.getSimpleName();
		sourceWriter.println("class "+className+"Wrapper extends " + handlerClass.getName()
				+ " implements br.com.sysmap.crux.core.client.event.EventClientHandlerInvoker{");
		sourceWriter.println("public void invoke(String metodo, br.com.sysmap.crux.core.client.component.Screen screen, String idSender) throws Exception{ ");
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
		   method.getName().equals("wait") ||
		   method.getName().startsWith("get") || method.getName().startsWith("is"))
		{
			return false;
		}
		return true;
	}

	protected String generateEventCallbackInvokerClass(TreeLogger logger, SourceWriter sourceWriter, Class<?> callbackClass)
	{
		String className = callbackClass.getSimpleName();
		sourceWriter.println("class "+className+"Wrapper extends " + callbackClass.getName() 
							+ " implements br.com.sysmap.crux.core.client.event.EventClientCallbackInvoker{");
		sourceWriter.println("public void invoke(String metodo, br.com.sysmap.crux.core.client.component.Screen screen, String idSender, com.google.gwt.json.client.JSONValue result) throws Exception{ ");
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
				sourceWriter.println("wrapper."+method.getName()+"(result);");
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

		if(!parameters[0].getName().equals("com.google.gwt.json.client.JSONValue"))
		{
			return false;
		}
		return true;
	}
	
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
			method = controller.getMethod("setScreen", new Class[]{Screen.class});
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
