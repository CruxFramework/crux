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
package org.cruxframework.crux.core.client.screen.binding;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.utils.StringUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataBindingHandler
{
	protected Map<DataObjectBinder<?>> binders = CollectionFactory.createMap();
	protected Map<String> binderAlias = CollectionFactory.createMap();
	protected BindableContainer bindableContainer;
	
	public DataBindingHandler(BindableContainer bindableContainer)
    {
		this.bindableContainer = bindableContainer;
    }
	
	public void addDataObjectBinder(DataObjectBinder<?> dataObjectBinder)
	{
		addDataObjectBinder(dataObjectBinder, null);
	}
	
	public void addDataObjectBinder(DataObjectBinder<?> dataObjectBinder, String dataObjectAlias)
	{
		String className = dataObjectBinder.createDataObject().getClass().getName();
		assert(!binders.containsKey(className)) : "This container already contains a dataBinding for this type of object";
		assert(dataObjectAlias == null || !binderAlias.containsKey(dataObjectAlias)) : "This container already contains "
																					 + "a dataBinding for this dataObject alias";
		
		binders.put(className, dataObjectBinder);
		if (!StringUtils.isEmpty(dataObjectAlias))
		{
			binderAlias.put(dataObjectAlias, className);
		}
	}
	
	public void addExpressionBinder(ExpressionBinder<?> expressionBinder, String widgetId, Array<String> referedDataObjects)
	{
		assert(checkDataObjectsExist(referedDataObjects)):"Can not add the given binding expression. It refers to "
														+ "DataObjects that are not registerid on this container.";
		
		int size = referedDataObjects.size();
		for (int i = 0; i < size; i++)
		{
			DataObjectBinder<Object> objectBinder = getDataObjectBinder(referedDataObjects.get(i));
			objectBinder.addExpressionBinder(widgetId, expressionBinder);
		}
	}
	
	public void remove(String widgetId)
    {
	    Array<String> keys = binders.keys();
		int size = keys.size();
	    
	    for (int i = 0; i < size; i++)
	    {
	    	DataObjectBinder<?> binder = binders.get(keys.get(i));
	    	binder.removeBinders(widgetId);
	    }
    }
	
	public void write(Object dataObject)
    {
		DataObjectBinder<?> objectBinder = getDataObjectBinder(dataObject.getClass());
		if (objectBinder != null)
		{
			objectBinder.write(dataObject);
		}
    }

	public void writeAll(Object... dataObjects)
    {
		for (Object dataObject : dataObjects)
        {
			DataObjectBinder<?> objectBinder = getDataObjectBinder(dataObject.getClass());
			if (objectBinder != null)
			{
				objectBinder.write(dataObject, false);
			}
        }
		for (Object dataObject : dataObjects)
        {
			DataObjectBinder<?> objectBinder = getDataObjectBinder(dataObject.getClass());
			if (objectBinder != null)
			{
				objectBinder.updateExpressions();
			}
        }
    }
	
	public void copyTo(Object dataObject)
    {
		DataObjectBinder<?> objectBinder = getDataObjectBinder(dataObject.getClass());
		if (objectBinder != null)
		{
			objectBinder.copyTo(dataObject);
		}
    } 
	
	public <T> T read(Class<T> dataObjectClass)
    {
		DataObjectBinder<T> objectBinder = getDataObjectBinder(dataObjectClass);
		if (objectBinder != null)
		{
			return objectBinder.read();
		}
		return null;
    }

	public <T> T read(String dataObjectAlias)
    {
		DataObjectBinder<T> objectBinder = getDataObjectBinder(dataObjectAlias);
		if (objectBinder != null)
		{
			return objectBinder.read();
		}
		return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> DataObjectBinder<T> getDataObjectBinder(String dataObjectAlias)
	{
		String dataObjectClass = binderAlias.get(dataObjectAlias);
		if (!StringUtils.isEmpty(dataObjectClass))
		{
			return (DataObjectBinder<T>) binders.get(dataObjectClass);
		}
		return (DataObjectBinder<T>) binders.get(dataObjectAlias);
	}

	@SuppressWarnings("unchecked")
	public <T> DataObjectBinder<T> getDataObjectBinder(Class<T> dataObjectClass)
    {
	    return (DataObjectBinder<T>) binders.get(dataObjectClass.getName());
    }
	
	private boolean checkDataObjectsExist(Array<String> referedDataObjects)
    {
	    if (referedDataObjects != null)
	    {
	    	int size = referedDataObjects.size();
	    	for (int i = 0; i < size; i++)
	    	{
	    		if (getDataObjectBinder(referedDataObjects.get(i)) == null)
	    		{
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	    return false;
    }
}
