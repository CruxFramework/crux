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

import org.cruxframework.crux.core.client.dataprovider.DataFilterEvent;
import org.cruxframework.crux.core.client.dataprovider.DataFilterHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProviderExcpetion;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasurablePagedProvider;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedHandler;

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
    public PagedDataProvider<T> getDataProvider()
    {
	    return dataProvider;
    }

	@SuppressWarnings("unchecked")
    @Override
    public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
    {
		this.dataProvider = dataProvider;
		this.dataProvider.setPageSize(pageSize);
		this.dataProvider.addPageLoadedHandler(new PageLoadedHandler()
		{
			@Override
			public void onPageLoaded(PageLoadedEvent event)
			{
				render(false);
			}
		});
		this.dataProvider.addLoadStoppedHandler(new DataLoadStoppedHandler()
		{
			@Override
			public void onLoadStopped(DataLoadStoppedEvent event)
			{
				render(true);
			}
		});
		
		if (this.dataProvider instanceof FilterableProvider<?>)
		{
			FilterableProvider<T> filterable = (FilterableProvider<T>) this.dataProvider;
			filterable.addDataFilterHandler(new DataFilterHandler<T>()
			{
				@Override
				public void onFiltered(DataFilterEvent<T> event)
				{
					render(true);
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
		if (dataProvider != null)
		{
			dataProvider.load();
		}
	}
	
	public boolean isDataLoaded()
	{
		return dataProvider != null && dataProvider.isLoaded();
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
	
	protected abstract void clear(); 
	
	protected void render(boolean refresh)
    {
		if (refresh)
		{
			clear();
		}
		int rowCount = getRowsToBeRendered();

		for (int i=0; i<rowCount; i++)
		{
			T value = dataProvider.get();
			if (value == null)
			{
				throw new DataProviderExcpetion("Erro indice="+i);
			}
			renderer.render(value);
			if (dataProvider.hasNext())
			{
				dataProvider.next();
			}
			else
			{
				break;
			}
		}
		updatePager();
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
	 * Define a renderer, called when a record from DataProvider needs to be rendered by this widget 
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface Renderer<T>
	{
		void render(T value);
	}
}
