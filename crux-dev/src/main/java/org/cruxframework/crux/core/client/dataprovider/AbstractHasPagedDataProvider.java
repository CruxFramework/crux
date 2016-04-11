/*
 * Copyright 2015 cruxframework.org.
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

import com.google.gwt.user.client.ui.Composite;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHasPagedDataProvider<T> extends Composite implements HasPagedDataProvider<PagedDataProvider<T>>
{
	private PagedDataProvider<T> dataProvider;

	@Override
    public PagedDataProvider<T> getDataProvider()
    {
	    return dataProvider;
    }

	@Override
	public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		if (this.dataProvider != dataProvider)
		{
			if (this.dataProvider != null)
			{
				removeDataProviderHandler();
			}
			this.dataProvider = dataProvider;
			if (dataProvider != null)
			{
				addDataProviderHandler();
				if (autoLoadData)
				{
					dataProvider.load();
				}
				if (!autoLoadData && dataProvider.isLoaded())
				{
					onDataProviderSet();
				}
			}
		}
	}

	protected abstract void onDataProviderSet();

	@Override
	public boolean isDataLoaded()
	{
		return dataProvider != null && dataProvider.isLoaded();
	}

	@Override
	public void nextPage()
	{
		if(isDataLoaded())
		{
			dataProvider.nextPage();
		}
	}

	@Override
	public void setPageSize(int pageSize)
	{
		checkDataProvider();
		dataProvider.setPageSize(pageSize);
	}
	
	public int getPageSize()
	{
		checkDataProvider();
		return dataProvider.getPageSize();
	}

	@Override
    public void goToPage(int page)
    {
		if(isDataLoaded())
		{
			if(dataProvider instanceof MeasurablePagedProvider<?>)
			{
				((MeasurablePagedProvider<?>) dataProvider).setCurrentPage(page);
			}
			else
			{
				throw new UnsupportedOperationException("This operation is only supported when using DataProviders that are instances of MeasurablePagedDataProvider");
			}
		}
    }
	
	@Override
    public int getPageCount()
    {
		if(isDataLoaded() && dataProvider instanceof MeasurablePagedProvider<?>)
		{
			MeasurablePagedProvider<?> ds = (MeasurablePagedProvider<?>) dataProvider;
			return ds.getPageCount();
		}
		else
		{
			return -1;
		}
    }

	@Override
    public void previousPage()
    {
		if(isDataLoaded())
		{
			dataProvider.previousPage();
		}
    }
	
	@Override
	public int getCurrentPage()
	{
		if(isDataLoaded())
		{
			return dataProvider.getCurrentPage();
		}
	    return 0;
	}

	@Override
	public boolean hasNextPage()
	{
		if(isDataLoaded())
		{
			return dataProvider.hasNextPage();
		}
	    return false;
	}
	
	@Override
	public boolean hasPreviousPage()
	{
		if(isDataLoaded())
		{
			return dataProvider.hasPreviousPage();
		}
	    return false;
	}
	
	@Override
	public void firstPage()
	{
		if(isDataLoaded())
		{
			goToPage(1);
		}
	}

	@Override
	public void lastPage()
	{
		if(isDataLoaded())
		{
			goToPage(getPageCount());
		}
	}
	
	protected abstract void addDataProviderHandler();

	protected abstract void removeDataProviderHandler();

	/**
	 * If there is no dataProvider set, raises an error
	 */
	protected void checkDataProvider()
	{
		assert(this.dataProvider != null) :"No dataProvider set for this component.";
	}
}