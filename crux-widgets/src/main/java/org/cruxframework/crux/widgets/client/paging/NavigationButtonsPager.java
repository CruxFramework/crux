package org.cruxframework.crux.widgets.client.paging;

import org.cruxframework.crux.widgets.client.event.paging.PageEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base implementation for navigation-buttons-based pager
 * @author Gesse S. F. Dafe
 */
public abstract class NavigationButtonsPager extends AbstractPager implements Pager
{
	private Widget previousButton;
	private Widget nextButton;
	private Widget firstButton;
	private Widget lastButton;
	private ButtonPanelCreator panelCreator = GWT.create(ButtonPanelCreator.class);
	
	/**
	 * @see org.cruxframework.crux.widgets.client.paging.AbstractPager#update(int, boolean)
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
	protected Widget createPreviousButton()
	{
		final NavigationButtonsPager pager = this;
		Widget button = createNavigationButton("previousButton", 
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
		
		this.previousButton = button;
		
		return button;
	}
	
	/**
	 * Creates the "next page" navigation button
	 * @return
	 */
	protected Widget createNextButton()
	{
		final NavigationButtonsPager pager = this;
		Widget button = createNavigationButton("nextButton", 
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
		
		this.nextButton = button;
		
		return button;
	}
	
	/**
	 * Creates the "first page" navigation button
	 * @return
	 */
	protected Widget createFirstPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Widget panel = createNavigationButton("firstButton", 
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
	protected Widget createLastPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Widget panel = createNavigationButton("lastButton", 
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
	private Widget createNavigationButton(String styleName, ClickHandler clickHandler)
	{
		Button navButton = new Button();
		navButton.setStyleName(styleName);
		navButton.addStyleDependentName("disabled");
		navButton.addClickHandler(clickHandler);
		return navButton;
	}
	
	protected static class ButtonPanelCreator
	{
		protected Widget createPanel()
		{
			return new FocusPanel();
		}
	}
	
	protected static class MobileButtonPanelCreator extends ButtonPanelCreator
	{
		@Override
		protected Widget createPanel()
		{
			return new Label();
		}
	}
}