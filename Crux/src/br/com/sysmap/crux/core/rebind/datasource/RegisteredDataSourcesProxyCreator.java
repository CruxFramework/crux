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
package br.com.sysmap.crux.core.rebind.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.DataSoureExcpetion;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.formatter.HasFormatter;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.AbstractRegisteredElementProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates a RegisteredControllers class.  
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredDataSourcesProxyCreator extends AbstractRegisteredElementProxyCreator
{
	private Map<String, String> dataSourcesClassNames = new HashMap<String, String>();

	public RegisteredDataSourcesProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredDataSources.class.getCanonicalName()));
    }

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		generateGetDataSourceMethod(sourceWriter);
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
		List<Screen> screens = getScreens();
		for (Screen screen : screens)
		{
			generateDataSourcesForScreen(srcWriter, screen);
		}
    }
	
	/**
	 * @return
	 */
	@Override
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		br.com.sysmap.crux.core.client.screen.Screen.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		HasValue.class.getCanonicalName(), 
    		HasText.class.getCanonicalName(),
    		HasFormatter.class.getCanonicalName(),
    		DataSoureExcpetion.class.getCanonicalName(),
    		DataSourceRecord.class.getCanonicalName()
		};
	    return imports;
    }		
	
	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param implClassName
	 * @param dataSourcesClassNames
	 */
	private void generateGetDataSourceMethod(SourceWriter sourceWriter) 
	{
		sourceWriter.println("public DataSource<?> getDataSource(String id){");
		boolean first = true;
		for (String dataSource : dataSourcesClassNames.keySet()) 
		{
			if (!first)
			{
				sourceWriter.print("else ");
			}
			else
			{
				first = false;
			}
			sourceWriter.println("if(\""+dataSource+"\".equals(id)){");
			sourceWriter.println("return new " + dataSourcesClassNames.get(dataSource) + "();");
			sourceWriter.println("}");
		}
		sourceWriter.println("throw new DataSoureExcpetion("+EscapeUtils.quote(messages.errorGeneratingRegisteredDataSourceNotFound())+"+id);");
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param screen
	 */
	private void generateDataSourcesForScreen(SourceWriter sourceWriter, Screen screen)
	{
		Iterator<String> dataSources = screen.iterateDataSources();
		
		while (dataSources.hasNext())
		{
			String dataSource = dataSources.next();
			generateDataSourceClassBlock(screen, sourceWriter, dataSource);
		}		
	}
	
	/**
	 * 
	 * @param logger
	 * @param screen
	 * @param sourceWriter
	 * @param dataSource
	 * @param added
	 */
	private void generateDataSourceClassBlock(Screen screen, SourceWriter sourceWriter, String dataSource)
	{
		if (!dataSourcesClassNames.containsKey(dataSource) && DataSources.getDataSource(dataSource)!= null)
		{
			try
            {
	            JClassType dataSourceClass = registeredIntf.getOracle().getType(DataSources.getDataSource(dataSource));
	            String genClass = new DataSourceProxyCreator(logger, context, dataSourceClass).create(); 
	            dataSourcesClassNames.put(dataSource, genClass);
            }
            catch (NotFoundException e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }
		}
	}
}
