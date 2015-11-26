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
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.DataBindingProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTarget;
import org.json.JSONObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetCreatorContext
{
	private JSONObject childElement;
	private DataBindingProcessor dataBindingProcessor;
	private List<ExpressionDataBinding> expressionDataBindings = new ArrayList<ExpressionDataBinding>();
	private Map<String, ObjectDataBinding> objectDataBindings = new HashMap<String, ObjectDataBinding>();
	private ProcessingTarget processingTarget = ProcessingTarget.widget;
	private String widget;
	private Deque<WidgetComponent> widgetComponents = new LinkedList<WidgetComponent>();
	private WidgetConsumer widgetConsumer;
	private JSONObject widgetElement;
	private String widgetId;
	
	public WidgetCreatorContext()
	{
	}
	
	public JSONObject getChildElement()
	{
		return childElement;
	}
	
	public DataBindingProcessor getDataBindingProcessor()
	{
		return dataBindingProcessor;
	}
	
	public ObjectDataBinding getObjectDataBinding(String dataObject)
	{
		return objectDataBindings.get(dataObject);
	}
		
	public ProcessingTarget getProcessingTarget()
	{
		return processingTarget;
	}
	
	public String getUIObjectVar()
	{
		switch (processingTarget)
        {
			case widget: return getWidget();
			default: return getWidgetComponent().getVariable();
		}
	}
	
	public String getWidget()
	{
		return widget;
	}
	
	public WidgetComponent getWidgetComponent()
	{
		return widgetComponents.getFirst();
	}
	
	public WidgetConsumer getWidgetConsumer()
	{
		return widgetConsumer;
	}
	
	public JSONObject getWidgetElement()
	{
		return widgetElement;
	}
	
	public String getWidgetId()
	{
		return widgetId;
	}
	
	public Iterator<ExpressionDataBinding> iterateExpressionBindings()
	{
		return expressionDataBindings.iterator();
	}
	
	public Iterator<String> iterateObjectDataBindingObjects()
	{
		return objectDataBindings.keySet().iterator();
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
	
	public int readIntChildProperty(String propertyName, int defaultValue)
	{
		String property = readChildProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return Integer.parseInt(property);
	}
	
	public int readIntWidgetProperty(String propertyName, int defaultValue)
	{
		String property = readWidgetProperty(propertyName);
		if (StringUtils.isEmpty(property))
		{
			return defaultValue;
		}
		return Integer.parseInt(property);
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
	
	public void registerExpressionDataBinding(ExpressionDataBinding expressionDataBinding)
	{
		expressionDataBindings.add(expressionDataBinding);
	}
	
	public void registerObjectDataBinding(PropertyBindInfo propertyBindInfo)
	{
		ObjectDataBinding objectDataBinding = getObjectDataBinding(propertyBindInfo.getDataObject());
		if (objectDataBinding == null)
		{
			objectDataBinding = new ObjectDataBinding(propertyBindInfo.getDataObjectClassName());
			objectDataBindings.put(propertyBindInfo.getDataObject(), objectDataBinding);
		}
		objectDataBinding.addPropertyBinding(propertyBindInfo);
	}
	
	WidgetComponent popWidgetComponent()
	{
		return widgetComponents.pop();
	}
	
	void pushWidgetComponent(String className, String variable)
	{
		widgetComponents.push(new WidgetComponent(className, variable));
	}
	
	void setChildElement(JSONObject childElement)
	{
		this.childElement = childElement;
	}
	
	void setDataBindingProcessor(DataBindingProcessor dataBindingProcessor)
	{
		this.dataBindingProcessor = dataBindingProcessor;
	}

	void setProcessingTarget(ProcessingTarget target)
	{
		this.processingTarget = target;
		
	}
	
	void setWidget(String widget) 
	{
		this.widget = widget;
	}
	
	void setWidgetConsumer(WidgetConsumer consumer)
    {
    	this.widgetConsumer = consumer;
    }
	
	void setWidgetElement(JSONObject widgetElement) 
	{
		this.widgetElement = widgetElement;
	}
	
	void setWidgetId(String widgetId) 
	{
		this.widgetId = widgetId;
	}
	
	public static class WidgetComponent
	{
		private String className;
		private String variable;

		WidgetComponent(String className, String variable)
        {
			this.className = className;
			this.variable = variable;
        }
		
		public String getClassName()
		{
			return className;
		}
		public String getVariable()
		{
			return variable;
		}
	}
}
