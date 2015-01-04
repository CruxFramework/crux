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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.widget.ObjectDataBinding.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.json.JSONObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetCreatorContext
{
	private JSONObject childElement;
	private JSONObject widgetElement;
	private String widget;
	private String widgetId;
	private WidgetConsumer widgetConsumer;
	private Map<String, ObjectDataBinding> objectDataBindings = new HashMap<String, ObjectDataBinding>();
	private List<ExpressionDataBinding> expressionDataBindings = new ArrayList<ExpressionDataBinding>();
	
	public WidgetCreatorContext()
	{
	}
	
	public JSONObject getWidgetElement()
	{
		return widgetElement;
	}
	
	public String getWidget()
	{
		return widget;
	}
	
	public String getWidgetId()
	{
		return widgetId;
	}
	
	public String readWidgetProperty(String propertyName)
	{
        return widgetElement.optString(propertyName);
	}
	
	public String readWidgetProperty(String propertyName, String defaultValue)
	{
		String property = readWidgetProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return property;
	}
	
	public boolean readBooleanWidgetProperty(String propertyName, boolean defaultValue)
	{
		String property = readWidgetProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return Boolean.parseBoolean(property);
	}
	
	public String readChildProperty(String propertyName)
	{
		return childElement.optString(propertyName);
	}
	
	public String readChildProperty(String propertyName, String defaultValue)
	{
		String property = readChildProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return property;
	}
	
	public boolean readBooleanChildProperty(String propertyName, boolean defaultValue)
	{
		String property = readChildProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return Boolean.parseBoolean(property);
	}
	
	public JSONObject getChildElement()
	{
		return childElement;
	}
	
	public ObjectDataBinding getObjectDataBinding(String dataObject)
	{
		return objectDataBindings.get(dataObject);
	}
	
	public Iterator<String> iterateDataObjects()
	{
		return objectDataBindings.keySet().iterator();
	}
	
	public Iterator<ExpressionDataBinding> iterateExpressionBindings()
	{
		return expressionDataBindings.iterator();
	}
	
	public WidgetConsumer getWidgetConsumer()
	{
		return widgetConsumer;
	}
	
	void registerObjectDataBinding(PropertyBindInfo propertyBindInfo)
	{
		ObjectDataBinding objectDataBinding = getObjectDataBinding(propertyBindInfo.getDataObject());
		if (objectDataBinding == null)
		{
			objectDataBinding = new ObjectDataBinding(propertyBindInfo.getDataObjectClassName());
			objectDataBindings.put(propertyBindInfo.getDataObject(), objectDataBinding);
		}
		objectDataBinding.addPropertyBinding(propertyBindInfo);
	}
	
	void setChildElement(JSONObject childElement)
	{
		this.childElement = childElement;
	}
	
	void setWidgetElement(JSONObject widgetElement) 
	{
		this.widgetElement = widgetElement;
	}
	
	void setWidget(String widget) 
	{
		this.widget = widget;
	}
	
	void setWidgetId(String widgetId) 
	{
		this.widgetId = widgetId;
	}
	
	void setWidgetConsumer(WidgetConsumer consumer)
    {
    	this.widgetConsumer = consumer;
    }
}
