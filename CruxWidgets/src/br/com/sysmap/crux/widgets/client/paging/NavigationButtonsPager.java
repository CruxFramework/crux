package br.com.sysmap.crux.widgets.client.paging;

import br.com.sysmap.crux.widgets.client.event.paging.PageEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Base implementation for navigation-buttons-based pager
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class NavigationButtonsPager extends AbstractPager implements Pager
{
	private FocusPanel previousButton;
	private FocusPanel nextButton;
	private FocusPanel firstButton;
	private FocusPanel lastButton;
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.AbstractPager#update(int, boolean)
	 */
	@Override
	public void update(int currentPage, boolean isLastPage)
	{
		super.update(currentPage, isLastPage);
		
		if(this.previousButton != null)
		{
			if(this.getCurrentPage() <= 1 || !isEnabled())
			{
				this.previousButton.addStyleDependentName("disabled");
			}
			else
			{
				this.previousButton.removeStyleDependentName("disabled");
			}
		}
		
		if(this.nextButton != null)
		{
			if(isLastPage() || !isEnabled())
			{
				this.nextButton.addStyleDependentName("disabled");
			}
			else
			{
				this.nextButton.removeStyleDependentName("disabled");
			}
		}
		
		if(this.firstButton != null)
		{
			if(this.getCurrentPage() <= 1 || !isEnabled())
			{
				this.firstButton.addStyleDependentName("disabled");
			}
			else
			{
				this.firstButton.removeStyleDependentName("disabled");
			}
		}
		
		if(this.lastButton != null)
		{
			if(isLastPage() || !isEnabled())
			{
				this.lastButton.addStyleDependentName("disabled");
			}
			else
			{
				this.lastButton.removeStyleDependentName("disabled");
			}
		}
	}
	
	/**
	 * Creates the "previous page" navigation button
	 * @return
	 */
	protected FocusPanel createPreviousButton()
	{
		final NavigationButtonsPager pager = this;
		
		FocusPanel panel = createNavigationButton("previousButton", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getCurrentPage() - 1);
						if(!pageEvent.isCanceled())
						{
							if(getCurrentPage() > 1)
							{
								previousPage();
							}
						}
					}
				}
			}
		);
		
		this.previousButton = panel;
		
		return panel;
	}
	
	/**
	 * Creates the "next page" navigation button
	 * @return
	 */
	protected FocusPanel createNextButton()
	{
		final NavigationButtonsPager pager = this;
		
		FocusPanel panel = createNavigationButton("nextButton", 
			new ClickHandler()
			{			
				public void onClick(ClickEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getCurrentPage() + 1);
						if(!pageEvent.isCanceled())
						{
							if(!isLastPage())
							{
								nextPage();
							}
						}
					}
				}
			}
		);
		
		this.nextButton = panel;
		
		return panel;
	}
	
	/**
	 * Creates the "first page" navigation button
	 * @return
	 */
	protected FocusPanel createFirstPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		FocusPanel panel = createNavigationButton("firstButton", 
			new ClickHandler()
			{			
				public void onClick(ClickEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, 1);
						if(!pageEvent.isCanceled())
						{
							firstPage();
						}
					}
				}
			}
		);
		
		this.firstButton = panel;
		
		return panel;
	}
	
	/**
	 * Creates the "last page" navigation button
	 * @return
	 */
	protected FocusPanel createLastPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		FocusPanel panel = createNavigationButton("lastButton", 
			new ClickHandler()
			{			
				public void onClick(ClickEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(pager, getPageCount());
						if(!pageEvent.isCanceled())
						{
							lastPage();
						}
					}
				}
			}
		);
		
		this.lastButton = panel;
		
		return panel;
	}
	
	/**
	 * Creates a generic navigation button
	 * @param styleName
	 * @param clickHandler
	 * @return
	 */
	private FocusPanel createNavigationButton(String styleName, ClickHandler clickHandler)
	{
		FocusPanel panel = new FocusPanel();
		panel.setStyleName(styleName);
		panel.addStyleDependentName("disabled");
		panel.addClickHandler(clickHandler);
		return panel;
	}
}