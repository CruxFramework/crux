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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.views.LazyPanel;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler.SingleControllerAccessHandler;
import org.json.JSONObject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.ClientBundle;

/**
 * A Factory that wraps an element with a panel which content is only rendered when it is accessed for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class LazyPanelFactory 
{
	static final String LAZY_PANEL_TYPE = "_CRUX_LAZY_PANEL_";
	
	private final ViewFactoryCreator factory;
	
	/**
	 * Singleton constructor
	 */
	public LazyPanelFactory(ViewFactoryCreator factory) 
	{
		this.factory = factory;
	}
	
	/**
	 * Create an wrapper lazyPanel capable of creating an widget for the given CruxMetaData element. 
	 * 
	 * @param factoryPrinter Printer for the calling factory method.
	 * @param element CruxMetaData element that will be used to create the wrapped widget
	 * @param targetPanelId Identifier of the parent panel, that required the lazy wrapping operation.
	 * @param wrappingType the lazyPanel wrapping model.
	 * @return
	 */
	public String getLazyPanel(SourcePrinter factoryPrinter, final JSONObject element, String targetPanelId, LazyPanelWrappingType wrappingType) 
	{
		String lazyId = ViewFactoryUtils.getLazyPanelId(targetPanelId, wrappingType);
		
		String lazyPanel = ViewFactoryCreator.createVariableName("lazy");

		SourcePrinter lazyPrinter = factory.getSubTypeWriter(lazyPanel+"Class", LazyPanel.class.getCanonicalName(), 
														null, 
														getImports());
		
		generateConstructor(lazyPrinter, lazyPanel+"Class", lazyId);
		generateCreateWidgetMethod(lazyPrinter, element, lazyId);
		generateFields(lazyPrinter, lazyPanel+"Class");
		generateGetResourceMethod(lazyPrinter);
		
		lazyPrinter.commit();
		
		if (factory.getControllerAccessHandler() instanceof SingleControllerAccessHandler)
		{
			SingleControllerAccessHandler controllerAccessHandler = (SingleControllerAccessHandler) factory.getControllerAccessHandler();
		factoryPrinter.println(lazyPanel+"Class " + lazyPanel + " = new "+lazyPanel+"Class("+ViewFactoryCreator.getViewVariable()+", "+controllerAccessHandler.getSingleControllerVariable()+");");
		}
		else
		{
			factoryPrinter.println(lazyPanel+"Class " + lazyPanel + " = new "+lazyPanel+"Class("+ViewFactoryCreator.getViewVariable()+");");
		}
		
		return lazyPanel;
	}
	
	private void generateGetResourceMethod(SourcePrinter printer)
    {
		printer.println("public "+ClientBundle.class.getCanonicalName()+" getResource(String id){");
		printer.println("return "+ViewFactoryCreator.getViewVariable()+".getResource(id);");
		printer.println("}");
    }

	/**
	 * @param printer
	 * @param className
	 */
	private void generateFields(SourcePrinter printer, String className)
    {
		printer.println("private static Logger "+factory.getLoggerVariable()+" = Logger.getLogger("+className+".class.getName());");
		printer.println("private "+factory.getViewSuperClassName()+" "+ViewFactoryCreator.getViewVariable()+";");
	    Map<String, String> declaredMessages = factory.getDeclaredMessages();
		for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
		if (factory.getControllerAccessHandler() instanceof SingleControllerAccessHandler)
		{
			SingleControllerAccessHandler controllerAccessHandler = (SingleControllerAccessHandler) factory.getControllerAccessHandler();
	    	printer.println("private "+controllerAccessHandler.getSingleControllerImplClassName()+" "+controllerAccessHandler.getSingleControllerVariable()+";");
		}
    }

	/**
	 * @param printer
	 * @param className
	 * @param widgetId
	 */
	private void generateConstructor(SourcePrinter printer, String className, String widgetId)
    {
		if (factory.getControllerAccessHandler() instanceof SingleControllerAccessHandler)
		{
			SingleControllerAccessHandler controllerAccessHandler = (SingleControllerAccessHandler) factory.getControllerAccessHandler();
			printer.println("public "+className+"("+factory.getViewSuperClassName()+" view, "+controllerAccessHandler.getSingleControllerImplClassName()+" "+controllerAccessHandler.getSingleControllerVariable()+"){");
			printer.println("super(view, "+EscapeUtils.quote(widgetId)+");");
			printer.println("this."+ViewFactoryCreator.getViewVariable()+" = view;");
			printer.println("this."+controllerAccessHandler.getSingleControllerVariable()+" = "+controllerAccessHandler.getSingleControllerVariable()+";");
		}
		else
		{
			printer.println("public "+className+"("+factory.getViewSuperClassName()+" view){");
			printer.println("super(view, "+EscapeUtils.quote(widgetId)+");");
			printer.println("this."+ViewFactoryCreator.getViewVariable()+" = view;");
		}
		printer.println("}");
    }

	/**
	 * @param printer
	 * @param element
	 * @param lazyId
	 */
	private void generateCreateWidgetMethod(SourcePrinter printer, JSONObject element, String lazyId)
    {
		printer.println("@Override");
		printer.println("public Widget createWidget(){");

		printer.println("if (LogConfiguration.loggingIsEnabled()){");
		printer.println(factory.getLoggerVariable()+".log(Level.FINE, \"Creating ["+lazyId+"] wrapped widget...\");");
		printer.println("}");
    	
		factory.createPostProcessingScope();
		
		String newWidget = factory.newWidget(printer, element, element.optString("id"), factory.getMetaElementType(element));

		factory.commitPostProcessing(printer);

		printer.println("if (LogConfiguration.loggingIsEnabled()){");
		printer.println(factory.getLoggerVariable()+".log(Level.FINE, \"["+lazyId+"]  wrapped widget created.\");");
		printer.println("}");

		printer.println("return " + newWidget+";");    
		printer.println("}");    
	}

	/**
	 * Gets the list of classes used by the LazyPanel handler.
	 * @return
	 */
	private String[] getImports()
    {
		List<String> imports = new ArrayList<String>();
		
		String[] factoryImports = factory.getImports();
		for (String imp : factoryImports)
        {
	        imports.add(imp);
        }
		
		imports.add(LogConfiguration.class.getCanonicalName());
		imports.add(Logger.class.getCanonicalName());
		imports.add(Level.class.getCanonicalName());
		
	    return imports.toArray(new String[imports.size()]);
    }
}
