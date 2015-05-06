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

import org.cruxframework.crux.core.client.dataprovider.DataChangedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataChangedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataFilterEvent;
import org.cruxframework.crux.core.client.dataprovider.DataFilterHandler;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasurablePagedProvider;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndEvent;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndHandler;
import org.cruxframework.crux.core.client.dataprovider.TransactionStartEvent;
import org.cruxframework.crux.core.client.dataprovider.TransactionStartHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
	protected DataProvider.DataReader<T> reader = getDataReader();
	private boolean transactionRunning = false;
	
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
			updatePagerState();		
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
				boolean refresh = pager == null || !pager.supportsInfiniteScroll();
				render(refresh);
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
		
		this.dataProvider.addTransactionEndHandler(new TransactionEndHandler()
		{
			@Override
			public void onTransactionEnd(TransactionEndEvent event)
			{
				onTransactionCompleted(event.isCommited());
			}
		});
		
		this.dataProvider.addTransactionStartHandler(new TransactionStartHandler()
		{
			@Override
			public void onTransactionStart(TransactionStartEvent event)
			{
				onTransactionStarted(event.getStartRecord());				
			}
		});
		
		this.dataProvider.addDataChangedHandler(new DataChangedHandler()
		{
			@Override
			public void onDataChanged(DataChangedEvent event)
			{
				final int pageStartRecordOnTransactionEnd = AbstractPageable.this.dataProvider.getCurrentPageStartRecord();
				refreshPage(pageStartRecordOnTransactionEnd);
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
	
	public boolean isDirty()
	{
		return dataProvider != null && dataProvider.isDirty();
	}

	public void add(T object)
	{
		if (dataProvider != null)
		{
			dataProvider.add(object);
		}
	}
	
	public void remove(int index)
	{
		if (dataProvider != null)
		{
			dataProvider.remove(index);
		}
	}
	
	public void set(int index, T object)
	{
		if (dataProvider != null)
		{
			dataProvider.set(index, object);
		}
	}
	
	public int indexOf(T object)
	{
		if (dataProvider != null)
		{
			return dataProvider.indexOf(object);
		}
		return -1;
	}
	
	public void commit()
	{
		if (dataProvider != null)
		{
			dataProvider.commit();
		}
	}
	
	public void rollback()
	{
		if (dataProvider != null)
		{
			dataProvider.rollback();
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
	
	public void refresh()
	{
		refresh(true);
	}
	
	protected void refresh(boolean clearPreviousData)
	{
		dataProvider.firstOnPage();
		render(clearPreviousData);
	}

	protected void refreshPage(int startRecord)
    {
		boolean refreshAll = pager == null || !pager.supportsInfiniteScroll();
	    if (refreshAll)
	    {
	    	clear();
	    }
	    else
	    {
	    	clearRange(startRecord);
	    }
	    refresh(false);
    }

	protected void onTransactionCompleted(boolean commited)
    {
		final int pageStartRecordOnTransactionEnd = dataProvider.getCurrentPageStartRecord();
		transactionRunning = false;
		updatePagerState();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				refreshPage(pageStartRecordOnTransactionEnd);
			}
		});
    }

	protected void onTransactionStarted(int startRecord)
    {
		transactionRunning = true;
		updatePagerState();
		if(isDataLoaded() && pager != null)
		{
			pager.prepareTransaction(startRecord);
		}
    }

	protected void render(boolean refresh)
    {
		if (refresh)
		{
			clear();
		}
		int rowCount = getRowsToBeRendered();

		for (int i=0; i<rowCount; i++)
		{
			dataProvider.read(reader);
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
	
	protected void updatePagerState()
	{
		if(isDataLoaded() && pager != null)
		{
			pager.setEnabled(!transactionRunning);
		}
	}

	protected abstract DataProvider.DataReader<T> getDataReader();
	protected abstract void clearRange(int startRecord);
	protected abstract void clear(); 
	
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
}
