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
import org.cruxframework.crux.core.client.dataprovider.FilterableProvider;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedHandler;
import org.cruxframework.crux.core.client.dataprovider.ResetEvent;
import org.cruxframework.crux.core.client.dataprovider.ResetHandler;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndEvent;
import org.cruxframework.crux.core.client.dataprovider.TransactionEndHandler;
import org.cruxframework.crux.core.client.dataprovider.TransactionStartEvent;
import org.cruxframework.crux.core.client.dataprovider.TransactionStartHandler;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Base implementation for a Pager
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractPager<T> extends AbstractHasPagedDataProvider<T> implements Pager<T>
{
	protected static final String DISABLED = "-disabled";
	
	protected HandlerRegistration dataChangedHandler;
	protected HandlerRegistration dataFilterHandler;
	protected boolean enabled = true;
	protected HandlerRegistration loadStoppedHandler;
	protected HandlerRegistration pageLoadedHandler;
	protected HandlerRegistration pageRequestedHandler;
	protected HandlerRegistration resetHandler;
	protected HandlerRegistration transactionEndHandler;
	protected boolean transactionRunning;
	protected HandlerRegistration transactionStartHandler;
	
	public HandlerRegistration addPageHandler(PageHandler handler)
	{
		return addHandler(handler, PageEvent.getType());
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		setInteractionEnabled(enabled);
	}

	@Override
	protected void addDataProviderHandler()	
	{
		transactionEndHandler = getDataProvider().addTransactionEndHandler(new TransactionEndHandler()
		{
			@Override
			public void onTransactionEnd(TransactionEndEvent event)
			{
				onTransactionCompleted(event.isCommited());
			}
		});
		
		transactionStartHandler = getDataProvider().addTransactionStartHandler(new TransactionStartHandler()
		{
			@Override
			public void onTransactionStart(TransactionStartEvent event)
			{
				onTransactionStarted(event.getStartRecord());				
			}
		});
		
		loadStoppedHandler = getDataProvider().addLoadStoppedHandler(new DataLoadStoppedHandler()
		{
			@Override
			public void onLoadStopped(DataLoadStoppedEvent event)
			{
				onUpdate();
			}
		});
		dataChangedHandler = getDataProvider().addDataChangedHandler(new DataChangedHandler()
		{
			@Override
			public void onDataChanged(DataChangedEvent event)
			{
				onUpdate();
			}
		});
		if (getDataProvider() instanceof FilterableProvider<?>)
		{
			@SuppressWarnings("unchecked")
            FilterableProvider<T> filterable = (FilterableProvider<T>) getDataProvider();
			dataFilterHandler = filterable.addDataFilterHandler(new DataFilterHandler<T>()
			{
				@Override
				public void onFiltered(DataFilterEvent<T> event)
				{
					onUpdate();
				}
			});
		}
		
		pageLoadedHandler = getDataProvider().addPageLoadedHandler(new PageLoadedHandler()
		{
			@Override
			public void onPageLoaded(final PageLoadedEvent event)
			{
				onUpdate();
				hideLoading();
			}
		});
		
		pageRequestedHandler = getDataProvider().addPageRequestedHandler(new PageRequestedHandler()
		{
			@Override
			public void onPageRequested(PageRequestedEvent event)
			{
				showLoading();
			}
		});
		
		resetHandler = getDataProvider().addResetHandler(new ResetHandler()
		{
			@Override
			public void onReset(ResetEvent event)
			{
				onUpdate();
			}
		});
	}
	
	/**
	 * Hides the loading information
	 */
	protected abstract void hideLoading();
	
	protected boolean isInteractionEnabled()
	{
		return isEnabled() && !transactionRunning;
	}
	
	@Override
	protected void onDataProviderSet()
	{
    	onUpdate();
	}
	
	protected void onTransactionCompleted(boolean commited)
    {
		transactionRunning = false;
		if(isEnabled())
		{
			setInteractionEnabled(true);
		}
    }

	protected void onTransactionStarted(int startRecord)
    {
		transactionRunning = true;
		setInteractionEnabled(false);
    }
	
	/**
	 * This should be overridden in order to handle
	 * the children components states. 
	 * @param enabled true will enable all composite widgets
	 * and false otherwise. 
	 */
	protected void setInteractionEnabled(boolean enabled)
	{
		if (enabled)
		{
			removeStyleDependentName(DISABLED);
		}
		else
		{
			addStyleDependentName(DISABLED);
		}
	}
	
	/**
	 * Refreshes the pager
	 */
	protected abstract void onUpdate();

	@Override
	protected void removeDataProviderHandler()
	{
		if (transactionEndHandler != null)
		{
			transactionEndHandler.removeHandler();
			transactionEndHandler = null;
		}
		if (transactionStartHandler != null)
		{
			transactionStartHandler.removeHandler();
			transactionStartHandler = null;
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
		if (pageRequestedHandler != null)
		{
			pageRequestedHandler.removeHandler();
			pageRequestedHandler = null;
		}
		if (resetHandler != null)
		{
			resetHandler.removeHandler();
			resetHandler = null;
		}
	}
	
	/**
	 * Shows some information to tell user that operation is in progress
	 */
	protected abstract void showLoading();
}