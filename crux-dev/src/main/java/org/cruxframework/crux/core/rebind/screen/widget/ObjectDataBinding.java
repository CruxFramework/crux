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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ObjectDataBinding
{
	private String dataObjectClassName;
	private List<PropertyBindInfo> bindings = new ArrayList<PropertyBindInfo>();
	
	public ObjectDataBinding(String dataObjectClassName)
    {
		this.dataObjectClassName = dataObjectClassName;
    }

	public String getDataObjectClassName()
    {
	    return dataObjectClassName;
    }
	
	public Iterator<PropertyBindInfo> iterateBindings()
	{
		return bindings.iterator();
	}
	
	void addPropertyBinding(PropertyBindInfo propertyBindInfo)
	{
		bindings.add(propertyBindInfo);
	}
	
	public static class PropertyBindInfo
	{
		private static String DATA_OBJECT_VAR_REF = "{0}";
    	private static String WIDGET_VAR_REF = "widget";
		private static String CONVERTER_VARIABLE = "__converter";
		
		private String dataObjectClassName;
		private String widgetClassName;
		private String writeExpression;
		private String readExpression;
		private String dataObject;
		private String converterClassName;
		private String converterParams;

		public PropertyBindInfo(String widgetPropertyPath, String bindPath, JClassType widgetType, JClassType dataObjectType, 
								JClassType converterType, String dataObject, String converterParams) throws NoSuchFieldException
        {
			this.dataObjectClassName = dataObjectType.getQualifiedSourceName();
			this.widgetClassName = widgetType.getQualifiedSourceName();
			this.dataObject = dataObject;
			this.converterClassName = converterType != null ? converterType.getParameterizedQualifiedSourceName() : null;
			this.converterParams = converterParams == null ? "": converterParams;
			
			this.writeExpression = getWriteExpression(widgetPropertyPath, widgetType, dataObjectType, bindPath);
			this.readExpression = getReadExpression(widgetPropertyPath, widgetType, dataObjectType, bindPath);;
        }

		public String getWriteExpression(String dataObjectVariable)
		{
			return writeExpression.replace(DATA_OBJECT_VAR_REF, dataObjectVariable);
		}

		public String getReadExpression(String dataObjectVariable)
		{
			return readExpression.replace(DATA_OBJECT_VAR_REF, dataObjectVariable);
		}

		public String getDataObjectClassName()
	    {
		    return dataObjectClassName;
	    }
		
		public String getWidgetClassName()
		{
			return widgetClassName;
		}
		
		public String getConverterClassName()
		{
			return converterClassName;
		}
		
		public String getConverterVariable()
		{
			return (converterClassName != null ? CONVERTER_VARIABLE : null); 
		}
		
		public String getDataObject()
		{
			return dataObject;
		}
		
		protected String getReadExpression(String widgetPropertyPath, JClassType widgetType, JClassType dataObjectType, 
					String bindPath) throws NoSuchFieldException
        {
			StringBuilder writeExpression = new StringBuilder();
			StringBuilder getExpression = new StringBuilder();
			
			JClassUtils.buildGetValueExpression(getExpression, widgetType, widgetPropertyPath, WIDGET_VAR_REF, false);
			
			String converterVariable = getConverterVariable();
			if (converterVariable != null)
			{
				getExpression.insert(0, converterVariable + ".from(").append(")");
			}
			JClassUtils.buildSetValueExpression(writeExpression, dataObjectType, bindPath, DATA_OBJECT_VAR_REF, getExpression.toString());
	        return writeExpression.toString();
        }

		protected String getWriteExpression(String widgetPropertyPath, JClassType widgetType, JClassType dataObjectType, 
					String bindPath) throws NoSuchFieldException
		{
			StringBuilder writeExpression = new StringBuilder();
			StringBuilder getExpression = new StringBuilder();
			
			JClassUtils.buildGetValueExpression(getExpression, dataObjectType, bindPath, DATA_OBJECT_VAR_REF, false);
			
			String converterVariable = getConverterVariable();
			if (converterVariable != null)
			{
				getExpression.insert(0, converterVariable + ".to(").append(")");
			}
			
			JClassUtils.buildSetValueExpression(writeExpression, widgetType, widgetPropertyPath, WIDGET_VAR_REF, getExpression.toString());
			
			return writeExpression.toString();
		}

		public String getConverterDeclaration()
        {
			if (getConverterVariable() != null)
			{
				return getConverterClassName() + " " + getConverterVariable() +	" = new " + getConverterClassName() + "("+converterParams+");";
			}
	        return null;
        }
	}
}
