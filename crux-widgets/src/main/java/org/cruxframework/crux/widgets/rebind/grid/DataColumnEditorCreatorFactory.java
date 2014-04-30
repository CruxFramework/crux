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
package org.cruxframework.crux.widgets.rebind.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.widgets.client.grid.DataColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.DataColumnEditorCreators;
import org.json.JSONObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Knows how to create the source code of a GenericDataColumnEditorCreator class
 * @author Gesse Dafe
 */
public class DataColumnEditorCreatorFactory 
{
	static final String LAZY_PANEL_TYPE = "_CRUX_LAZY_PANEL_";
	
	private String loggerVariable;
	private final Map<String, String> declaredMessages;
	private final GridFactory gridFactory;
	private final SourcePrinter printer;
	private final String classSimpleName;
	private final JSONObject editorMetadata;
	private final WidgetCreatorContext context;
	
	/**
	 * Constructor
	 */
	public DataColumnEditorCreatorFactory(String classSimpleName, String packageName, GridFactory factory, WidgetCreatorContext context, JSONObject editorMetadata, String getLoggerVariable, Map<String, String> declaredMessages) 
	{
		this.classSimpleName = classSimpleName;
		this.gridFactory = factory;
		this.context = context;
		this.editorMetadata = editorMetadata;
		this.loggerVariable = getLoggerVariable;
		this.declaredMessages = declaredMessages;
		
		this.printer = gridFactory.getSubTypeWriter(packageName,
    		classSimpleName, DataColumnEditorCreators.class.getCanonicalName() + ".GenericDataColumnEditorCreator", 
    		null, getImports(), false);
		
	}

	/**
	 * Generates the body of the class file
	 */
	public void createEditorCreator() 
	{	
		generateFields();
		generateCreateEditorMethod();
		printer.commit();
	}
	
	/**
	 * Creates the instance fields 
	 */
	private void generateFields()
    {
		printer.println("private static Logger " + loggerVariable + " = Logger.getLogger(" + classSimpleName + ".class.getName());");
		
		for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = "+ GWT.class.getCanonicalName() + ".create("+messageClass+".class);");
	    }
    }
	
	/**
	 * Generates the code of the GenericDataColumnEditorCreator.createEditor method 
	 */
	private void generateCreateEditorMethod()
    {
		printer.println("@Override");
		printer.println("public Object createEditorWidget(DataColumnDefinition column) {");
		JSONObject childWidgetElem = gridFactory.ensureFirstChild(editorMetadata, false, context.getWidgetId());
		String childWidgetVarName = gridFactory.createChildWidget(printer, childWidgetElem, WidgetConsumer.EMPTY_WIDGET_CONSUMER, true, context);
		printer.println("return " + childWidgetVarName + ";");    
		printer.println("}");
	}

	/**
	 * Gets the list of classes used by the LazyPanel handler.
	 * @return
	 */
	private String[] getImports()
    {
		List<String> imports = new ArrayList<String>();
		
		imports.add(LogConfiguration.class.getCanonicalName());
		imports.add(Logger.class.getCanonicalName());
		imports.add(Level.class.getCanonicalName());
		imports.add(DataColumnDefinition.class.getCanonicalName());
		imports.add("com.google.gwt.core.client.GWT");
	    return imports.toArray(new String[imports.size()]);
    }
}
