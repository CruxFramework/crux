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
package org.cruxframework.crux.core.client.datasource.pager;

import org.cruxframework.crux.core.client.datasource.LocalDataSource;
import org.cruxframework.crux.core.client.datasource.LocalDataSourceCallback;
import org.cruxframework.crux.core.client.datasource.MeasurableDataSource;
import org.cruxframework.crux.core.client.datasource.MeasurablePagedDataSource;
import org.cruxframework.crux.core.client.datasource.MeasurableRemoteDataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSourceCallback;

import com.google.gwt.user.client.ui.Composite;

/**
 * Base implementation for Pageable widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractPageable<T> extends Composite implements Pageable<PagedDataSource<T>>
{
	protected Pager pager;
	protected PagedDataSource<T> dataSource;
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
		if (dataSource != null)
		{
			dataSource.setPageSize(pageSize);
		}
	}

	@Override
	public void nextPage()
	{
		if(isDataLoaded())
		{
			dataSource.nextPage();
		}
	}

	@Override
    public void previousPage()
    {
		if(isDataLoaded())
		{
			dataSource.previousPage();
		}
    }

	@Override
    public int getPageCount()
    {
		if(isDataLoaded() && dataSource instanceof MeasurablePagedDataSource<?>)
		{
			MeasurablePagedDataSource<?> ds = (MeasurablePagedDataSource<?>) dataSource;
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
		Pageable<PagedDataSource<T>> pagerPageable = pager.getPageable();
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
			if(dataSource instanceof MeasurablePagedDataSource<?>)
			{
				((MeasurablePagedDataSource<?>) dataSource).setCurrentPage(page);
			}
			else
			{
				throw new UnsupportedOperationException("This operation is only supported when using DataSources that are instances of MeasurablePagedDataSource");
			}
		}
    }
	
	@Override
    public PagedDataSource<T> getDataSource()
    {
	    return dataSource;
    }

	@Override
    public void setDataSource(PagedDataSource<T> dataSource, boolean autoLoadData)
    {
		this.dataSource = dataSource;
		this.dataSource.setPageSize(pageSize);

		if(this.dataSource instanceof RemoteDataSource<?>)
		{
			RemoteDataSource<?> remote = (RemoteDataSource<?>) this.dataSource;

			remote.setCallback(new RemoteDataSourceCallback()
			{
				public void execute(int startRecord, int endRecord)
				{
					loaded = true;
					render();
				}

				public void cancelFetching()
				{
					render();
				}
			});

			if(autoLoadData)
			{
				loadData();
			}
		}
		else if(this.dataSource instanceof LocalDataSource<?>)
		{
			LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;

			local.setCallback(new LocalDataSourceCallback()
			{
				public void execute()
				{
					loaded = true;
				}

				@Override
                public void pageChanged(int startRecord, int endRecord)
                {
					render();
                }
			});

			if(autoLoadData)
			{
				loadData();
			}
		}
    }
	
	public void loadData()
	{
		if(!loaded)
		{
			if(dataSource instanceof RemoteDataSource)
			{
				if(dataSource instanceof MeasurableDataSource)
				{
					((MeasurableRemoteDataSource<?>) this.dataSource).load();
				}
				else
				{
					dataSource.nextPage();
				}
			}
			else if(dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?> local = (LocalDataSource<?>) dataSource;
				local.load();
			}
		}
	}
	
	public boolean isDataLoaded()
	{
		return dataSource != null && loaded;
	}

	public void reset()
	{
		reset(false);
	}
	
	public void reset(boolean reloadData)
	{
		if(dataSource != null)
		{
			dataSource.reset();
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
			renderer.render(dataSource.getBoundObject());
			if (dataSource.hasNextRecord())
			{
				dataSource.nextRecord();
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
			pager.update(dataSource.getCurrentPage(),  !dataSource.hasNextPage());
		}
	}
	
	private int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(dataSource.getCurrentPage() == 0)
			{
				dataSource.nextPage();
			}

			return dataSource.getCurrentPageSize();
		}

		return 0;
	}
	
	protected abstract Renderer<T> getRenderer();
	
	/**
	 * Define a rederer, called when a record from datasource needs to be renderer by this widget 
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface Renderer<T>
	{
		void render(T value);
	}
}
