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
package br.com.sysmap.crux.core.rebind.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.sysmap.crux.core.client.screen.RegisteredWidgetFactories;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.AbstractRegisteredElementProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.Widget;
import br.com.sysmap.crux.core.rebind.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredWidgetFactoriesProxyCreator extends AbstractRegisteredElementProxyCreator
{
	private Map<String, Boolean> widgetFactories = new HashMap<String, Boolean>();

	/**
	 * @param logger
	 * @param context
	 */
	public RegisteredWidgetFactoriesProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredWidgetFactories.class.getCanonicalName()));
    }
	
	/**
	 * 
	 * @param sourceWriter
	 * @param widget
	 */
	protected void generateCreateWidgetBlock(SourceWriter sourceWriter, Widget widget)
	{
		try
        {
	        String type = widget.getType();
	        if (!widgetFactories.containsKey(type))
	        {
	        	JClassType widgetClass = registeredIntf.getOracle().getType(WidgetConfig.getClientClass(type));
	        	if (widgetClass != null)
	        	{
	        		sourceWriter.println("widgetFactories.put(\""+type+"\", (WidgetFactory<? extends Widget>)GWT.create("+
	        				widgetClass.getQualifiedSourceName()+".class));");
	        		widgetFactories.put(type, true);
	        	}
	        	else
	        	{
	        		logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredWidgetFactoryNotRegistered()+type);
	        	}
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @param sourceWriter
	 */
	protected void generateCreateWidgetBlockForAllWidgets(SourceWriter sourceWriter)
	{
		try
        {
	        Set<String> libraries = WidgetConfig.getRegisteredLibraries();
	        
	        for (String library : libraries)
	        {
	        	Set<String> registeredLibraryFactories = WidgetConfig.getRegisteredLibraryFactories(library);
	        	for (String factory : registeredLibraryFactories)
	        	{
	        		String type = library+"_"+factory;
	        		JClassType widgetClass = registeredIntf.getOracle().getType(WidgetConfig.getClientClass(type));
	        		sourceWriter.println("widgetFactories.put(\""+type+"\", new " + 
	        				new WidgetFactoryProxyCreator(logger, context, widgetClass).create() + "());");
	        	}
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public "+getProxySimpleName()+"(){ ");
		
		List<Screen> screens = getScreens();
		if (Environment.isProduction() || !Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableHotDeploymentForWidgetFactories()))
		{
			for (Screen screen : screens)
			{
				Iterator<Widget> iterator = screen.iterateWidgets();
				while (iterator.hasNext())
				{
					Widget widget = iterator.next();
					generateCreateWidgetBlock(srcWriter, widget);
				}
			}
		}
		else
		{
			generateCreateWidgetBlockForAllWidgets(srcWriter);
		}
		srcWriter.println("}");
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private java.util.Map<String, WidgetFactory<? extends Widget>> widgetFactories = new java.util.HashMap<String, WidgetFactory<? extends Widget>>();");
    }

	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		sourceWriter.println("public WidgetFactory<? extends Widget> getWidgetFactory(String type) throws InterfaceConfigException{ ");
		sourceWriter.indent();
		sourceWriter.println("if (!widgetFactories.containsKey(type)) {");
		sourceWriter.indent();
		sourceWriter.println("throw new InterfaceConfigException(\""+messages.errorGeneratingRegisteredWidgetFactoryNotRegistered()+" \" +type);");
		sourceWriter.outdent();
		sourceWriter.println("}");
		sourceWriter.println("return widgetFactories.get(type);");
		sourceWriter.outdent();
		sourceWriter.println("}");
    }	
	
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
	protected String[] getImports()
	{
		String[] imports = new String[] {
				GWT.class.getCanonicalName(), 
				RegisteredWidgetFactories.class.getCanonicalName(),
				com.google.gwt.user.client.ui.Widget.class.getCanonicalName()
		};
		return imports;
	}
}
