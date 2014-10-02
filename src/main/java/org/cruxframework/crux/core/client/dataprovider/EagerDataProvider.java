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
package org.cruxframework.crux.core.client.dataprovider;

import org.cruxframework.crux.core.client.collection.Array;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An Eager and filterable {@link DataProvider}
 * @author Thiago da Rosa de Bustamante
 */
public class EagerDataProvider<T> extends AbstractScrollableDataProvider<T> 
													implements EagerProvider<T>, FilterableProvider<T>
{
	protected EagerDataLoader<T> dataLoader;

	public EagerDataProvider()
	{
	}

	public EagerDataProvider(EagerDataLoader<T> dataLoader)
    {
		this.dataLoader = dataLoader;
    }
	
	@Override
    public void setDataLoader(EagerDataLoader<T> dataLoader)
    {
		this.dataLoader = dataLoader;
    }

	@Override
    public EagerDataLoader<T> getDataLoader()
    {
	    return dataLoader;
    }
		
	@Override
	public Array<T> filter(DataFilter<T> filter)
	{
	    return operations.filter(filter);
	}
	
	@Override
	public void load()
	{
		if (!isLoaded() && dataLoader != null)
		{
			dataLoader.onLoadData(new EagerLoadEvent<T>(this));
		}
	}
	
	@Override
	public FilterRegistration<T> addFilter(final DataFilter<T> filter)
	{
		return operations.addFilter(filter);
	}
	
	@Override
	public void removeFilters()
	{
		operations.removeFilters();
	}

	@Override
	public HandlerRegistration addDataFilterHandler(DataFilterHandler<T> handler)
	{
		return operations.addDataFilterHandler(handler);
	}
	
	@Override
	public Array<T> getData()
	{
	    return operations.getData();
	}

	@Override
	protected void update(Array<DataProviderRecord<T>> records)
	{
		this.data = records;
		operations.saveInitialData(records);
	}
}