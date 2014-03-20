package org.cruxframework.crux.smartfaces.client.list;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.datasource.HasDataSource;
import org.cruxframework.crux.core.client.datasource.LocalDataSource;
import org.cruxframework.crux.core.client.datasource.LocalDataSourceCallback;
import org.cruxframework.crux.core.client.datasource.MeasurableDataSource;
import org.cruxframework.crux.core.client.datasource.MeasurableRemoteDataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSource;
import org.cruxframework.crux.core.client.datasource.RemoteDataSourceCallback;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScrollableList<T> extends Composite implements HasDataSource<PagedDataSource<T>>
{
	private PagedDataSource<T> dataSource;
	private int pageSize = 25;
	private boolean loaded;
	private boolean autoLoadData;
	private int lastScrollPos = 0;
	private ScrollPanel scrollable = new ScrollPanel();
	private FlowPanel contentPanel = new FlowPanel();
	private ListRenderer<T> renderer;

	public ScrollableList(ListRenderer<T> renderer)
    {
		assert(renderer != null);
		this.renderer = renderer;
		scrollable.add(contentPanel);
		initWidget(scrollable);
		// Do not let the scrollable take tab focus.
		scrollable.getElement().setTabIndex(-1);

		// Handle scroll events.
		scrollable.addScrollHandler(new ScrollHandler()
		{
			public void onScroll(ScrollEvent event)
			{
				// If scrolling up, ignore the event.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = scrollable.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos)
				{
					return;
				}

				int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop)
				{
					if (dataSource.hasNextPage())
					{
						nextPage();
					}
				}
			}
		});
    }
	
	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public boolean isAutoLoadData()
	{
		return autoLoadData;
	}

	public void setAutoLoadData(boolean autoLoadData)
	{
		this.autoLoadData = autoLoadData;
	}

	@Override
    public PagedDataSource<T> getDataSource()
    {
	    return dataSource;
    }

	@Override
    public void setDataSource(PagedDataSource<T> dataSource)
    {
		this.dataSource = dataSource;
		this.dataSource.setPageSize(this.pageSize);

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
		if(!this.loaded)
		{
			if(this.dataSource instanceof RemoteDataSource)
			{
				if(this.dataSource instanceof MeasurableDataSource)
				{
					((MeasurableRemoteDataSource<?>) this.dataSource).load();
				}
				else
				{
					this.dataSource.nextPage();
				}
			}
			else if(this.dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;
				local.load();
			}
		}
	}
	
	public boolean isDataLoaded()
	{
		return this.dataSource != null && loaded;
	}

	public void nextPage()
	{
		if(isDataLoaded())
		{
			this.dataSource.nextPage();

			if(!(this.dataSource instanceof RemoteDataSource<?>))
			{
				render();
			}
		}
	}

	public void clear()
	{
		contentPanel.clear();
		if(this.dataSource != null)
		{
			this.dataSource.reset();
		}
	}
	
	protected void render()
    {
	    int rowCount = getRowsToBeRendered();
	    
	    for (int i=0; i<rowCount; i++)
	    {
	    	IsWidget widget = renderer.render(dataSource.getBoundObject());
	    	contentPanel.add(widget);
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

	protected int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(this.dataSource.getCurrentPage() == 0)
			{
				this.dataSource.nextPage();
			}

			return this.dataSource.getCurrentPageSize();
		}

		return 0;
	}
	
	public static interface ListRenderer<T>
	{
		IsWidget render(T value);
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Renderer 
	{
		String value();
	}
}
