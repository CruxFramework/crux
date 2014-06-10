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
package org.cruxframework.crux.core.client.dataprovider.pager;

import org.cruxframework.crux.core.client.dataprovider.AsyncDataProvider;
import org.cruxframework.crux.core.client.dataprovider.AsyncDataProviderCallback;
import org.cruxframework.crux.core.client.dataprovider.MeasurableAsyncDataProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasurableDataProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasurablePagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProviderCallback;
import org.cruxframework.crux.core.client.dataprovider.SyncDataProvider;
import org.cruxframework.crux.core.client.dataprovider.SyncDataProviderCallback;

import com.google.gwt.user.client.ui.Composite;

/**
 * Base implementation for Pageable widgets
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractPageable<T> extends Composite implements Pageable<PagedDataProvider<T>>
{
	protected Pager pager;
	protected PagedDataProvider<T> dataProvider;
	protected int pageSize = 25;
	protected boolean loaded;
	protected Renderer<T> renderer = getRenderer();
	
	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
		if (dataProvider != null)
		{
			dataProvider.setPageSize(pageSize);
		}
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
    public void previousPage()
    {
		if(isDataLoaded())
		{
			dataProvider.previousPage();
		}
    }

	@Override
    public int getPageCount()
    {
		if(isDataLoaded() && dataProvider instanceof MeasurablePagedDataProvider<?>)
		{
			MeasurablePagedDataProvider<?> ds = (MeasurablePagedDataProvider<?>) dataProvider;
			return ds.getPageCount();
		}
		else
		{
			return -1;
		}
    }

	@Override
    public void setPager(Pager pager)
    {
		Pageable<PagedDataProvider<T>> pagerPageable = pager.getPageable();
		if (pagerPageable != this)
		{
			pager.setPageable(this);
		}
		else
		{
			this.pager = pager;
			updatePager();
		}
    }

	@Override
    public void goToPage(int page)
    {
		if(isDataLoaded())
		{
			if(dataProvider instanceof MeasurablePagedDataProvider<?>)
			{
				((MeasurablePagedDataProvider<?>) dataProvider).setCurrentPage(page);
			}
			else
			{
				throw new UnsupportedOperationException("This operation is only supported when using DataProviders that are instances of MeasurablePagedDataProvider");
			}
		}
    }
	
	@Override
    public PagedDataProvider<T> getDataProvider()
    {
	    return dataProvider;
    }

	@Override
    public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
    {
		this.dataProvider = dataProvider;
		this.dataProvider.setPageSize(pageSize);
		this.dataProvider.setCallback(new PagedDataProviderCallback()
		{
			public void onPageFetched(int startRecord, int endRecord)
			{
				loaded = true;
				render();
			}
		});

		if(this.dataProvider instanceof AsyncDataProvider<?>)
		{
			AsyncDataProvider<?> async = (AsyncDataProvider<?>) this.dataProvider;
			async.setCallback(new AsyncDataProviderCallback()
			{
				public void onCancelFetching()
				{
					render();
				}
			});
		}
		else if(this.dataProvider instanceof SyncDataProvider<?>)
		{
			SyncDataProvider<?> sync = (SyncDataProvider<?>) this.dataProvider;
			sync.setCallback(new SyncDataProviderCallback()
			{
				public void onLoaded()
				{
					loaded = true;
				}
			});
		}
		if(autoLoadData)
		{
			loadData();
		}
    }
	
	public void loadData()
	{
		if(!loaded)
		{
			if(dataProvider instanceof AsyncDataProvider)
			{
				if(dataProvider instanceof MeasurableDataProvider)
				{
					((MeasurableAsyncDataProvider<?>) this.dataProvider).initialize();
				}
				else
				{
					dataProvider.nextPage();
				}
			}
			else if(dataProvider instanceof SyncDataProvider)
			{
				SyncDataProvider<?> sync = (SyncDataProvider<?>) dataProvider;
				sync.load();
			}
		}
	}
	
	public boolean isDataLoaded()
	{
		return dataProvider != null && loaded;
	}

	public void reset()
	{
		reset(false);
	}
	
	public void reset(boolean reloadData)
	{
		if(dataProvider != null)
		{
			dataProvider.reset();
		}

		if (pager != null)
		{
			pager.update(0, true);
		}
		
		if (reloadData)
		{
			loadData();
		}
	}
	
	protected void render()
    {
		int rowCount = getRowsToBeRendered();

		for (int i=0; i<rowCount; i++)
		{
			renderer.render(dataProvider.getBoundObject());
			if (dataProvider.hasNextRecord())
			{
				dataProvider.nextRecord();
			}
			else
			{
				break;
			}
		}
    }

	protected void updatePager()
	{
		if(isDataLoaded() && pager != null)
		{
			pager.update(dataProvider.getCurrentPage(),  !dataProvider.hasNextPage());
		}
	}
	
	private int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(dataProvider.getCurrentPage() == 0)
			{
				dataProvider.nextPage();
			}

			return dataProvider.getCurrentPageSize();
		}

		return 0;
	}
	
	protected abstract Renderer<T> getRenderer();
	
	/**
	 * Define a rederer, called when a record from DataProvider needs to be renderer by this widget 
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface Renderer<T>
	{
		void render(T value);
	}
}
