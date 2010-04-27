package br.com.sysmap.crux.widgets.client.paging;

import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.paging.PageEvent;
import br.com.sysmap.crux.widgets.client.event.paging.PageHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * Base implementation for a Pager
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractPager extends Composite implements Pager
{
	private Pageable pageable;
	private int currentPage = 0;
	private boolean isLastPage = true;
	private boolean enabled = true;
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.paging.HasPageHandlers#addPageHandler(br.com.sysmap.crux.widgets.client.event.paging.PageHandler)
	 */
	public HandlerRegistration addPageHandler(PageHandler handler)
	{
		return addHandler(handler, PageEvent.getType());
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.Pager#update(int, boolean)
	 */
	public void update(int currentPage, boolean isLastPage)
	{
		this.currentPage = currentPage;
		this.isLastPage = isLastPage;
		hideLoading();		
		onUpdate();
	}
	
	/**
	 * Refreshes the pager
	 */
	protected abstract void onUpdate();

	/**
	 * @param pageable the pageable to set
	 */
	public final void setPageable(Pageable pageable)
	{
		this.pageable = pageable;
		pageable.setPager(this);
	}

	/**
	 * Moves the pageable's cursor to the previous page
	 */
	protected void previousPage()
	{
		checkPageable();
		showLoading();
		getPageable().previousPage();
	}
	
	/**
	 * Moves the pageable's cursor to the next page
	 */
	protected void nextPage()
	{
		checkPageable();
		showLoading();
		getPageable().nextPage();
	}
	
	/**
	 * Moves the pageable's cursor to the first page
	 */
	protected void firstPage()
	{
		checkPageable();
		showLoading();
		getPageable().goToPage(1);
	}
	
	/**
	 * Moves the pageable's cursor to the last page
	 */
	protected void lastPage()
	{
		checkPageable();
		showLoading();
		getPageable().goToPage(getPageable().getPageCount());
	}
	
	/**
	 * Moves the pageable's cursor to the an arbitrary page
	 */
	protected void goToPage(int page)
	{
		checkPageable();
		showLoading();
		getPageable().goToPage(page);
	}
	
	/**
	 * Shows some information to tell user that operation is in progress
	 */
	protected abstract void showLoading();
	
	/**
	 * Hides the loading information
	 */
	protected abstract void hideLoading();

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
	public final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		update(this.currentPage, this.isLastPage);
	}

	/**
	 * If there is no pageable set, throws <code>IllegalStateException</code>
	 */
	protected void checkPageable()
	{
		if(this.pageable == null)
		{
			throw new IllegalStateException(WidgetMsgFactory.getMessages().pagerNoPageableSet());
		}		
	}

	/**
	 * @return the pageable
	 */
	private Pageable getPageable()
	{
		return pageable;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * @return the isLastPage
	 */
	public boolean isLastPage()
	{
		return isLastPage;
	}

	/**
	 * Returns -1 if unknown
	 */
	public int getPageCount()
	{
		return this.pageable != null && this.pageable.isDataLoaded() ? pageable.getPageCount() : -1;
	}	
}