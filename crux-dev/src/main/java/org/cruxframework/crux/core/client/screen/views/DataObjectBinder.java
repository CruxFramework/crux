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
package org.cruxframework.crux.core.client.screen.views;

import java.util.Date;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.Map;

import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DataObjectBinder<T>
{
	private Map<Array<PropertyBinder<T, ?>>> binders = CollectionFactory.createMap();
	private Map<Array<ExpressionBinder<?>>> expressionBinders = CollectionFactory.createMap();
	private T dataObject;
	private View view;

	public DataObjectBinder(View view)
	{
		this.view = view;
	}

	public void addPropertyBinder(String widgetId, PropertyBinder<T, ?> propertyBinder)
	{
		Array<PropertyBinder<T, ?>> properties = binders.get(widgetId);
		if (properties == null)
		{
			properties = CollectionFactory.createArray();
			binders.put(widgetId, properties);
		}
		properties.add(propertyBinder);
		if (dataObject != null)
		{
			Widget widget = view.getWidget(widgetId, false);
			if (widget != null)
			{
				propertyBinder.bind(widget);
			}
			propertyBinder.copyTo(dataObject);
		}
	}

	public void addExpressionBinder(String widgetId, ExpressionBinder<?> expressionBinder)
	{
		Array<ExpressionBinder<?>> properties = expressionBinders.get(widgetId);
		if (properties == null)
		{
			properties = CollectionFactory.createArray();
			expressionBinders.put(widgetId, properties);
		}
		properties.add(expressionBinder);
		if (dataObject != null)
		{
			Widget widget = view.getWidget(widgetId, false);
			if (widget != null)
			{
				expressionBinder.bind(widget);
			}
			expressionBinder.execute(new UpdatedStateBindingContext(view, new Date().getTime()));
		}
	}

	protected abstract T createDataObject();

	void removeBinders(String widgetId)
	{
		binders.remove(widgetId);
		expressionBinders.remove(widgetId);
	}

	@SuppressWarnings("unchecked")
	void copyTo(Object object)
	{
		T dataObject = (T) object;
		readGeneric(dataObject);
	}

	T read()
	{
		if (dataObject == null)
		{
			dataObject = createDataObject();
		}

		readGeneric(dataObject);

		return dataObject;
	}

	void write(Object object)
	{
		write(object, true);
	}
	
	@SuppressWarnings("unchecked")
	void write(Object object, boolean updateExpressions)
	{
		T dataObject = (T) object;

		writeGeneric(dataObject);
		this.dataObject = dataObject;
		if (updateExpressions)
		{
			updateExpressions(new UpdatedStateBindingContext(view, new Date().getTime()));
		}
	}

	void updateExpressions(ExpressionBinder.BindingContext context)
	{
		Array<String> keys = expressionBinders.keys();
		int size = keys.size();
		for (int i = 0; i < size; i++)
		{
			String id = keys.get(i);
			Array<ExpressionBinder<?>> binders = expressionBinders.get(id);
			int expressionsSize = binders.size();
			for (int j = 0; j < expressionsSize; j++)
			{
				ExpressionBinder<?> expressionBinder = binders.get(j);
				if (!expressionBinder.isBound())
				{
					Widget widget = view.getWidget(id, false);
					expressionBinder.bind(widget);
				}
				expressionBinder.execute(context);
			}
		}
	}
	
	private void readGeneric(T dataObject)
	{
		Array<String> keys = binders.keys();
		int size = keys.size();
		for (int i=0; i< size; i++)
		{
			String id = keys.get(i);
			Array<PropertyBinder<T, ?>> propertyBinders = binders.get(id);
			int propertiesSize = propertyBinders.size();
			for (int j = 0; j < propertiesSize; j++)
			{
				PropertyBinder<T, ?> propertyBinder = propertyBinders.get(j);
				if (!propertyBinder.isBound())
				{
					Widget widget = view.getWidget(id, false);
					propertyBinder.bind(widget);
				}
				
				propertyBinder.copyFrom(dataObject);
			}
		}
	}

	private void writeGeneric(T dataObject)
	{
		Array<String> keys = binders.keys();
		int size = keys.size();
		for (int i = 0; i < size; i++)
		{
			String id = keys.get(i);
			Array<PropertyBinder<T, ?>> propertyBinders = binders.get(id);
			int propertiesSize = propertyBinders.size();
			for (int j = 0; j < propertiesSize; j++)
			{
				PropertyBinder<T, ?> propertyBinder = propertyBinders.get(j);
				if (!propertyBinder.isBound())
				{
					Widget widget = view.getWidget(id, false);
					propertyBinder.bind(widget);
				}
				propertyBinder.copyTo(dataObject);
			}
		}
	}

	/**
	 * This binding context can be used only when we know that there is no write operation waiting to be 
	 * performed on the current view. It assume that the dataObjects are updated.
	 *  
	 * @author Thiago da Rosa de Bustamante
	 */
	static class UpdatedStateBindingContext implements ExpressionBinder.BindingContext
	{
		private View view;
		private long executionTimestamp;

		public UpdatedStateBindingContext(View view, long executionTimestamp)
        {
			this.view = view;
			this.executionTimestamp = executionTimestamp;
        }
		
		@Override
        public <T> T getDataObject(String dataObject)
        {
			DataObjectBinder<T> dataObjectBinder = view.getDataObjectBinder(dataObject);
			if (dataObjectBinder.dataObject != null)
			{
				return dataObjectBinder.dataObject;
			}
			return dataObjectBinder.read();
        }

		@Override
        public long getExecutionTimestamp()
        {
	        return executionTimestamp;
        }
	}
}
