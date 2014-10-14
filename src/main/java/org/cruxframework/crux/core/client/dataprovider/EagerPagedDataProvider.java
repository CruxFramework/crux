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
 * An eager, paged and filterable {@link DataProvider}
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class EagerPagedDataProvider<T> extends AbstractPagedDataProvider<T> 
				implements EagerProvider<T>, FilterableProvider<T> 
{
	protected EagerDataLoader<T> dataLoader;

	public EagerPagedDataProvider()
	{
	}

	public EagerPagedDataProvider(EagerDataLoader<T> dataLoader)
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
	public void load()
	{
		if (!isLoaded() && dataLoader != null)
		{
			dataLoader.onLoadData(new EagerLoadEvent<T>(this));
		}
	}

	@Override
	public boolean nextPage()
	{
		if (super.nextPage())
		{
			firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean previousPage()
	{
		if (super.previousPage())
		{
			firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
			return true;
		}
		return false;
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
	protected boolean setCurrentPage(int pageNumber, boolean fireEvents)
	{
		if (super.setCurrentPage(pageNumber, fireEvents))
		{
			if (fireEvents)
			{
				firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void update(Array<DataProviderRecord<T>> records)
	{
		this.data = records;
		operations.saveInitialData(records);
		firePageLoadedEvent(getPageStartRecord(), getPageEndRecord());
	}
}