/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;
import org.cruxframework.crux.core.client.event.CrossDocumentInvoker;
import org.cruxframework.crux.core.client.event.RegisteredControllers;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.Screen;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates a RegisteredControllers class. 
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredControllersProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, String> controllerClassNames = new HashMap<String, String>();
	private Map<String, String> crossDocsClassNames = new HashMap<String, String>();
	private final Screen screen;
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredControllersProxyCreator(TreeLogger logger, GeneratorContextExt context, Screen screen)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredControllers.class.getCanonicalName()), false);
		this.screen = screen;
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		sourceWriter.println("public "+getProxySimpleName()+"(){");
		sourceWriter.indent();
		for (String controller : controllerClassNames.keySet()) 
		{
			JClassType controllerClass = getControllerClass(controller);
			if (!isControllerLazy(controllerClass))
			{
				sourceWriter.println("controllers.put(\""+controller+"\", new " + controllerClassNames.get(controller) + "());");
			}
		}
		sourceWriter.outdent();
		sourceWriter.println("}");
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<ControllerInvoker> controllers = new FastMap<ControllerInvoker>();");
    }	

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		generateControllerInvokeMethod(sourceWriter);
		generateCrossDocInvokeMethod(sourceWriter);
		generateRegisterControllerMethod(sourceWriter);
		generateGetControllertMethod(sourceWriter);
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
	{
		Set<String> usedWidgets = new HashSet<String>();
		String module = screen.getModule();
		generateControllersForScreen(screen);
		Iterator<org.cruxframework.crux.core.rebind.screen.Widget> screenWidgets = screen.iterateWidgets();
		while (screenWidgets.hasNext())
		{
			String widgetType = screenWidgets.next().getType();
			usedWidgets.add(widgetType);
		}
		if (module != null)
		{
			generateControllersForWidgets(usedWidgets, module);
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.EventProcessor.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		FastMap.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.ControllerInvoker.class.getCanonicalName(),
    		CrossDocumentInvoker.class.getCanonicalName(), 
    		StringUtils.class.getCanonicalName()
		};
	    return imports;
    }	
	
	/**
	 * Generate the block to include controller object.
	 * @param controller
	 * @param module
	 */
	private void generateControllerBlock(String controller, String module)
	{
		try
		{
			JClassType controllerClass = getControllerClass(controller);
			if (!controllerClassNames.containsKey(controller) && controllerClass!= null)
			{
				String controllerClassName = controllerClass.getQualifiedSourceName();
				if (Modules.getInstance().isClassOnModulePath(controllerClassName, module))
				{
					String genClass = new ControllerProxyCreator(logger, context, controllerClass).create();
					controllerClassNames.put(controller, genClass);
					JClassType crossDocumentType = controllerClass.getOracle().getType(CrossDocument.class.getCanonicalName());
					if (crossDocumentType.isAssignableFrom(controllerClass))
					{
						crossDocsClassNames.put(controller, genClass);
					}
				}
			}
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException("Error for register client event handler. Controller: ["+controller+"].", e);
		}
	}	
	
	/**
	 * 
	 * @param sourceWriter
	 * @param controllerClassNames
	 * @throws CruxGeneratorException 
	 */
	private void generateControllerInvokeMethod(SourceWriter sourceWriter) throws CruxGeneratorException
	{
		sourceWriter.println("public void invokeController(final String controllerName, final String method, final boolean fromOutOfModule, final Object sourceEvent, final EventProcessor eventProcessor){");
		sourceWriter.indent();

		if (isCrux2OldInterfacesCompatibilityEnabled())
		{
		    sourceWriter.println("ControllerInvoker controller = getController(controllerName);");
			sourceWriter.println("if (controller != null){");
			sourceWriter.indent();
			sourceWriter.println("try{");
			sourceWriter.indent();
			sourceWriter.println("controller.invoke(method, sourceEvent, fromOutOfModule, eventProcessor);");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("catch (Exception e)"); 
			sourceWriter.println("{");
			sourceWriter.indent();
			sourceWriter.println("eventProcessor.setException(e);");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("return;");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("else {");
			sourceWriter.indent();
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote("To use this feature you need to enable crux2 old interfaces compatibility.")+");");
		}
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * Generate wrapper classes for event handling.
	 * @param screen
	 */
	private void generateControllersForScreen(Screen screen)
	{
		Iterator<String> controllers = screen.iterateControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateControllerBlock(controller, screen.getModule());
		}		

		controllers = ClientControllers.iterateGlobalControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			JClassType controllerClass = getControllerClass(controller);
			if (controllerClass != null)
			{
				generateControllerBlock(controller, screen.getModule());
			}
		}		
	}

	/**
	 * @param usedWidgets
	 * @param module
	 */
	private void generateControllersForWidgets(Set<String> usedWidgets, String module)
	{
		
		Iterator<String> widgets = usedWidgets.iterator();
		while (widgets.hasNext())
		{
			Iterator<String> controllers = ClientControllers.iterateWidgetControllers(widgets.next());
			if (controllers != null)
			{
				while (controllers.hasNext())
				{
					String controller = controllers.next();
					JClassType controllerClass = getControllerClass(controller);
					if (controllerClass != null)
					{
						generateControllerBlock(controller, module);
					}
				}
			}		
		}
	}

	/**
	 * @param sourceWriter
	 */
	private void generateCrossDocInvokeMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public String invokeCrossDocument(String serializedData){");
		sourceWriter.indent();

		if (!this.crossDocsClassNames.isEmpty())
		{
			sourceWriter.println("if (serializedData != null){");
			sourceWriter.indent();

			sourceWriter.println("int idx = serializedData.indexOf('|');");
			sourceWriter.println("if (idx > 0){");
			sourceWriter.indent();

			sourceWriter.println("String controllerName = null;");
			sourceWriter.println("try{");
			sourceWriter.indent();

			sourceWriter.println("controllerName = serializedData.substring(0,idx);");
			sourceWriter.println("serializedData = serializedData.substring(idx+1);");
			sourceWriter.println("CrossDocumentInvoker crossDoc = (CrossDocumentInvoker)getController(controllerName);");
			sourceWriter.println("if (crossDoc==null){");
			sourceWriter.indent();
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.outdent();
			sourceWriter.println("} else {");
			sourceWriter.indent();

			sourceWriter.println("return crossDoc.invoke(serializedData);");

			sourceWriter.outdent();
			sourceWriter.println("}");

			sourceWriter.outdent();
			sourceWriter.println("} catch(ClassCastException ex){");
			sourceWriter.indent();
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().crossDocumentInvalidCrossDocumentController(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.outdent();
			sourceWriter.println("}");

			sourceWriter.outdent();
			sourceWriter.println("}");

			sourceWriter.outdent();
			sourceWriter.println("}");
		}
		sourceWriter.println("return null;");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	private void generateGetControllertMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public <T> T getController(String controller){");
		sourceWriter.indent();
		sourceWriter.println("T ret = (T)controllers.get(controller);");
		sourceWriter.println("if (ret == null){");
		sourceWriter.indent();
		
		boolean first = true;
		for (String controller : controllerClassNames.keySet())
        {
			JClassType controllerClass = getControllerClass(controller);
			if (isControllerLazy(controllerClass))
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".unsafeEquals(controller, "+EscapeUtils.quote(controller)+")){");
				sourceWriter.indent();

				if (isControllerStatefull(controllerClass))
				{
					sourceWriter.println("controllers.put("+EscapeUtils.quote(controller)+", new "+controllerClassNames.get(controller)+"());");
				}
				else
				{
					sourceWriter.println("ret = (T) new "+controllerClassNames.get(controller)+"();");
				}

				sourceWriter.outdent();
				sourceWriter.println("}");
			}
        }

		sourceWriter.println("if (ret == null){");
		sourceWriter.indent();
		sourceWriter.println("ret = (T)controllers.get(controller);");
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		sourceWriter.outdent();
		sourceWriter.println("}");
		
		sourceWriter.println("return ret;");

		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	private void generateRegisterControllerMethod(SourceWriter sourceWriter)
    {
		sourceWriter.println("public void registerController(String controller, ControllerInvoker controllerInvoker){");
		sourceWriter.indent();
		sourceWriter.println("if (!controllers.containsKey(controller)){");
		sourceWriter.indent();
		sourceWriter.println("controllers.put(controller, controllerInvoker);");
		sourceWriter.outdent();
		sourceWriter.println("}");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * @param controller
	 * @return
	 */
	private JClassType getControllerClass(String controller) 
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);

		return typeOracle.findType(ClientControllers.getController(controller));
	}

	/**
	 * @param controllerClass
	 * @return true if this controller can be loaded in lazy mode
	 * @throws CruxGeneratorException 
	 */
	private boolean isControllerLazy(JClassType controllerClass) throws CruxGeneratorException
    {
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
        return (controllerAnnot == null || controllerAnnot.lazy());
    }
	
	/**
	 * @param controllerClass
	 * @return true if this controller can be loaded in lazy mode
	 * @throws CruxGeneratorException 
	 */
	private boolean isControllerStatefull(JClassType controllerClass) throws CruxGeneratorException
    {
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
        return (controllerAnnot == null || controllerAnnot.stateful());
    }

	@Override
	public String getProxySimpleName()
	{
		String className = screen.getModule()+"_"+screen.getRelativeId(); 
		className = className.replaceAll("[\\W]", "_");
		return "RegisteredControllers_"+className;
	}	
}
