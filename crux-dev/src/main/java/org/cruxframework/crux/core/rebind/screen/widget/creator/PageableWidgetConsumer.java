/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.DataWidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.ViewBindHandler;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.ViewWidgetConsumer;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.json.JSONObject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PageableWidgetConsumer extends DataWidgetConsumer implements WidgetConsumer
{
	private GeneratorContext context;
	private JClassType widgetClassType;
	private JClassType dataObjectType;
	private String viewId;
	private String parentWidgetId;
	private String valueVariableName;

	public PageableWidgetConsumer(GeneratorContext context, JClassType widgetClassType, JClassType dataObjectType, String valueVariableName, String viewId, String parentWidgetId)
    {
		this.context = context;
		this.widgetClassType = widgetClassType;
		this.dataObjectType = dataObjectType;
		this.valueVariableName = valueVariableName;
		this.viewId = viewId;
		this.parentWidgetId = parentWidgetId;
    }
	
	@Override
	public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem)
	{
		String bindPath = metaElem.optString("bindPath");
		String bindConverter = metaElem.optString("bindConverter");
				
		JClassType converterType = ViewBindHandler.getConverterType(context, bindConverter);
    	String converterVariable = null;
    	if (converterType != null)
    	{
	    	JType propertyType = JClassUtils.getTypeForProperty(bindPath, dataObjectType);
	    	String propertyClassName = JClassUtils.getGenericDeclForType(propertyType);
	    	ViewWidgetConsumer.validateConverter(converterType, context, widgetClassType, context.getTypeOracle().findType(propertyClassName));

    		converterVariable = "__converter";
    		out.println(converterType.getParameterizedQualifiedSourceName()+" "+converterVariable+" = new "+converterType.getParameterizedQualifiedSourceName()+"();");
    	}

    	try
    	{
    		generateCopyToCode(out, context, valueVariableName, widgetVariableName, dataObjectType, widgetClassType, bindPath, converterVariable, converterType, true);
		}
		catch (NoSuchFieldException e) 
		{
			throw new CruxGeneratorException("Invalid binding path ["+bindPath+"] on target dataobject ["+dataObjectType.getParameterizedQualifiedSourceName()+
											"]. Property not found. Widget ["+parentWidgetId+"] on View ["+viewId+"]");
		}
	}		
}
