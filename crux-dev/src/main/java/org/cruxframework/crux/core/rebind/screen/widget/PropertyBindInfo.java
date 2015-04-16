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

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PropertyBindInfo extends BindInfo
{
	protected static String WIDGET_VAR_REF = "widget";
	
	protected String widgetClassName;
	protected String writeExpression;
	protected String readExpression;
	protected boolean boundToAttribute;

	public PropertyBindInfo(String widgetPropertyPath, boolean boundToAttribute, String bindPath, JClassType widgetType, JClassType dataObjectType, 
							JClassType converterType, String dataObject, String converterParams) throws NoSuchFieldException
    {
		super(bindPath, dataObjectType, converterType, dataObject, converterParams);
		this.boundToAttribute = boundToAttribute;
		this.widgetClassName = widgetType.getQualifiedSourceName();
		if (!StringUtils.isEmpty(widgetPropertyPath))
		{
			this.readExpression = getReadExpression(widgetPropertyPath, widgetType, dataObjectType, bindPath);
			this.writeExpression = getWriteExpression(widgetPropertyPath, widgetType);
		}
    }

	public String getWriteExpression(String dataObjectVariable)
	{
		return getExpression(writeExpression, dataObjectVariable);
	}

	public String getReadExpression(String dataObjectVariable)
	{
		return getExpression(readExpression, dataObjectVariable);
	}
	
	public String getDataObjectReadExpression(String dataObjectVariable)
	{
		return getExpression(getDataObjectReadExpression(), dataObjectVariable);
	}

	public String getWidgetClassName()
	{
		return widgetClassName;
	}

	public boolean isBoundToAttribute()
    {
	    return boundToAttribute;
    }
	
	/**
	 * Expression to read FROM widget and write TO dataObject
	 * @param widgetPropertyPath
	 * @param widgetType
	 * @param dataObjectType
	 * @param bindPath
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String getReadExpression(String widgetPropertyPath, JClassType widgetType, JClassType dataObjectType, 
				String bindPath) throws NoSuchFieldException
    {
		StringBuilder getExpression = new StringBuilder();
		JClassUtils.buildGetValueExpression(getExpression, widgetType, widgetPropertyPath, WIDGET_VAR_REF, false);
		return getDataObjectWriteExpression(dataObjectType, bindPath, getExpression.toString());
    }

	/**
	 * Expression to read FROM dataObject and write TO widget
	 * 
	 * @param widgetPropertyPath
	 * @param widgetType
	 * @return
	 * @throws NoSuchFieldException
	 */
	protected String getWriteExpression(String widgetPropertyPath, JClassType widgetType) throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();
		JClassUtils.buildSetValueExpression(writeExpression, widgetType, widgetPropertyPath, WIDGET_VAR_REF, getDataObjectReadExpression());
		return writeExpression.toString();
	}
}