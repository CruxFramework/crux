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

import org.cruxframework.crux.core.client.dataprovider.AbstractHasPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.DataChangedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataChangedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataFilterEvent;
import org.cruxframework.crux.core.client.dataprovider.DataFilterHandler;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadStoppedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndEvent;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

/**
 * Base implementation for Pageable widgets
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractPageable<T> extends AbstractHasPagedDataProvider<T> implements Pageable<T>
{
	protected HasPageable<T> pager;
	protected DataProvider.DataReader<T> reader = getDataReader();
	protected boolean allowRefreshAfterDataChange = true;
	protected HandlerRegistration pageLoadedHandler;
	protected HandlerRegistration loadStoppedHandler;
	protected HandlerRegistration transactionEndHandler;
	protected HandlerRegistration dataChangedHandler;
	protected HandlerRegistration dataFilterHandler;
	
	public void add(T object)
	{
		if (getDataProvider() != null)
		{
			getDataProvider().add(object);
		}
	}

	protected abstract void clear();

	protected abstract void clearRange(int startRecord);

	public void commit()
	{
		if (getDataProvider() != null)
		{
			getDataProvider().commit();
		}
	}

	protected abstract DataProvider.DataReader<T> getDataReader();

	private int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(getDataProvider().getCurrentPage() == 0)
			{
				getDataProvider().nextPage();
			}

			return getDataProvider().getCurrentPageSize();
		}

		return 0;
	}
	
	public int indexOf(T object)
	{
		if (getDataProvider() != null)
		{
			return getDataProvider().indexOf(object);
		}
		return -1;
	}
	
	/**
	 * @return true if is allowed to refresh the page after the data 
	 * provider changes some of their values. This generally happens after
	 * a commit.
	 */
	protected boolean isAllowRefreshAfterDataChange() 
	{
		return allowRefreshAfterDataChange;
	}

	public boolean isDirty()
	{
		return getDataProvider() != null && getDataProvider().isDirty();
	}
	
	public void loadData()
	{
		if (getDataProvider() != null)
		{
			getDataProvider().load();
		}
	}
	
	protected void onTransactionCompleted(boolean commited)
    {
		final int pageStartRecordOnTransactionEnd = getDataProvider().getCurrentPageStartRecord();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				refreshPage(pageStartRecordOnTransactionEnd);
			}
		});
    }
	
	public void refresh()
	{
		refresh(true);
	}
	
	protected void refresh(boolean clearPreviousData)
	{
		getDataProvider().firstOnPage();
		render(clearPreviousData, null);
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
	
	public void remove(int index)
	{
		if (getDataProvider() != null)
		{
			getDataProvider().remove(index);
		}
	}

	protected void render(boolean refresh, RenderCallback callback)
    {
		if (refresh)
		{
			clear();
		}
		int rowCount = getRowsToBeRendered();

		for (int i=0; i<rowCount; i++)
		{
			getDataProvider().read(reader);
			if (getDataProvider().hasNext())
			{
				getDataProvider().next();
			}
			else
			{
				break;
			}
		}
		if (callback != null)
		{
			callback.onRendered();
		}
    }

	public void reset()
	{
		reset(false);
	}

	public void reset(boolean reloadData)
	{
		if(getDataProvider() != null)
		{
			getDataProvider().reset();
		}

		if (reloadData)
		{
			loadData();
		}
	}

	public void rollback()
	{
		if (getDataProvider() != null)
		{
			getDataProvider().rollback();
		}
	}

	public void set(int index, T object)
	{
		if (getDataProvider() != null)
		{
			getDataProvider().set(index, object);
		}
	}
	
	/**
	 * @param allowRefreshAfterDataChange indicate if is allowed to 
	 * refresh the page after the data provider changes some of their values. 
	 * This generally happens after a commit. 
	 */
	protected void setAllowRefreshAfterDataChange(boolean allowRefreshAfterDataChange) 
	{
		this.allowRefreshAfterDataChange = allowRefreshAfterDataChange;
	}

	@Override
	protected void addDataProviderHandler()
	{
		pageLoadedHandler = getDataProvider().addPageLoadedHandler(new PageLoadedHandler()
		{
			@Override
			public void onPageLoaded(final PageLoadedEvent event)
			{
				boolean renewPagePanel = pager == null || !pager.supportsInfiniteScroll();
				if (renewPagePanel)
				{
			        final IsWidget pagePanel = (pager != null)?initializePagePanel():null;
			        if (getPagePanel() == null)
			        {
			        	IsWidget panel = initializePagePanel();
			        	getContentPanel().add(panel);
			        }
					render((pager == null), new RenderCallback()
					{
						@Override
						public void onRendered()
						{
							if (pager != null)
							{
								pager.updatePagePanel(pagePanel, event.getCurrentPage() > event.getPreviousPage());
							}
						}
					});
				}
				else
				{
					render(false, null);
				}
			}
		});
		loadStoppedHandler = getDataProvider().addLoadStoppedHandler(new DataLoadStoppedHandler()
		{
			@Override
			public void onLoadStopped(DataLoadStoppedEvent event)
			{
				render(true, null);
			}
		});
		
		transactionEndHandler = getDataProvider().addTransactionEndHandler(new TransactionEndHandler()
		{
			@Override
			public void onTransactionEnd(TransactionEndEvent event)
			{
				onTransactionCompleted(event.isCommited());
			}
		});
		
		dataChangedHandler = getDataProvider().addDataChangedHandler(new DataChangedHandler()
		{
			@Override
			public void onDataChanged(DataChangedEvent event)
			{
				final int pageStartRecordOnTransactionEnd = getDataProvider().getCurrentPageStartRecord();
				if(allowRefreshAfterDataChange)
				{
					refreshPage(pageStartRecordOnTransactionEnd);
				}
			}
		});
		
		if (getDataProvider() instanceof FilterableProvider<?>)
		{
			@SuppressWarnings("unchecked")
            FilterableProvider<T> filterable = (FilterableProvider<T>) this.getDataProvider();
			dataFilterHandler = filterable.addDataFilterHandler(new DataFilterHandler<T>()
			{
				@Override
				public void onFiltered(DataFilterEvent<T> event)
				{
					render(true, null);
				}
			});
		}
    }
	
	@Override
	protected void removeDataProviderHandler()
	{
		if (transactionEndHandler != null)
		{
			transactionEndHandler.removeHandler();
			transactionEndHandler = null;
		}
		if (loadStoppedHandler != null)
		{
			loadStoppedHandler.removeHandler();
			loadStoppedHandler = null;
		}
		if (dataChangedHandler != null)
		{
			dataChangedHandler.removeHandler();
			dataChangedHandler = null;
		}
		if (dataFilterHandler != null)
		{
			dataFilterHandler.removeHandler();
			dataFilterHandler = null;
		}
		if (pageLoadedHandler != null)
		{
			pageLoadedHandler.removeHandler();
			pageLoadedHandler = null;
		}
	}
	
	protected void initializeAndUpdatePagePanel(boolean forward)
    {
		IsWidget pagePanel = initializePagePanel();
        if (pager != null)
        {
        	pager.updatePagePanel(pagePanel, forward);
        }
    }

	/**
	 * Creates the panel that will contain all the page data.
	 * @return
	 */
	protected abstract IsWidget initializePagePanel();

	/**
	 * Retrieve the panel that will contain all the page data.
	 * @return
	 */
	protected abstract IsWidget getPagePanel();

	@Override
	public void setHeight(String height) 
	{
		if(pager != null && pager.supportsInfiniteScroll())
		{
			pager.asWidget().setHeight(height);
		} 
		else
		{
			super.setHeight(height);
		}
	}
	
	@Override
    public void setPager(HasPageable<T> pager)
    {
		this.pager = pager;
		if (pager != null)
		{
			if (getDataProvider() != null)
			{
				pager.setDataProvider(getDataProvider(), false);
			}
			pager.initializeContentPanel(getContentPanel());
			if (pager.supportsInfiniteScroll())
			{
				initializeAndUpdatePagePanel(true);
			}
			else if (getPagePanel() != null)
			{
				pager.updatePagePanel(getPagePanel(), true);
			}
		}
    } 
	
	protected abstract Panel getContentPanel();

	
	protected static interface RenderCallback
	{
		void onRendered();
	}
}
