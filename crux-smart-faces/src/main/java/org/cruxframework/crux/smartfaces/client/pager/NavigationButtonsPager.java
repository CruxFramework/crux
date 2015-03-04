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
package org.cruxframework.crux.smartfaces.client.pager;

import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPager;
import org.cruxframework.crux.core.client.dataprovider.pager.PageEvent;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.button.Button;

import com.google.gwt.core.client.GWT;

/**
 * Base implementation for navigation-buttons-based pager
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public abstract class NavigationButtonsPager extends AbstractPager implements Pager
{
	private Button previousButton;
	private Button nextButton;
	private Button firstButton;
	private Button lastButton;
	private ButtonCreator buttonCreator = GWT.create(ButtonCreator.class);
	
	@Override
	public void update(int currentPage, boolean isLastPage)
	{
		super.update(currentPage, isLastPage);
		
		if(this.previousButton != null)
		{
			if(this.getCurrentPage() <= 1 || !isEnabled())
			{
				this.previousButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.previousButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.nextButton != null)
		{
			if(isLastPage() || !isEnabled())
			{
				this.nextButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.nextButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.firstButton != null)
		{
			if(this.getCurrentPage() <= 1 || !isEnabled())
			{
				this.firstButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.firstButton.removeStyleDependentName(DISABLED);
			}
		}
		
		if(this.lastButton != null)
		{
			if(isLastPage() || !isEnabled())
			{
				this.lastButton.addStyleDependentName(DISABLED);
			}
			else
			{
				this.lastButton.removeStyleDependentName(DISABLED);
			}
		}
	}
	
	/**
	 * Creates the "previous page" navigation button
	 * @return
	 */
	protected Button createPreviousButton()
	{
		final NavigationButtonsPager pager = this;
		
		Button panel = createNavigationButton("previousButton", 
			new SelectHandler() 
			{
				public void onSelect(SelectEvent event)
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
	protected Button createNextButton()
	{
		final NavigationButtonsPager pager = this;
		
		Button panel = createNavigationButton("nextButton", 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
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
		nextButton.setText(" ");
		
		return panel;
	}
	
	/**
	 * Creates the "first page" navigation button
	 * @return
	 */
	protected Button createFirstPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Button panel = createNavigationButton("firstButton", 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
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
	protected Button createLastPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Button label = createNavigationButton("lastButton", 
			new SelectHandler()
			{			
				public void onSelect(SelectEvent event)
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
		
		this.lastButton = label;
		
		return label;
	}
	
	/**
	 * Creates a generic navigation button
	 * @param styleName
	 * @param selectHandler
	 * @return
	 */
	private Button createNavigationButton(String styleName, SelectHandler selectHandler)
	{
		Button button = buttonCreator.createButton();
		button.setStyleName(styleName);
		button.addStyleDependentName(DISABLED);
		button.addSelectHandler(selectHandler);
		return button;
	}
	
	
	protected static class ButtonCreator
	{
		protected Button createButton()
		{
			Button label = new Button();
			label.getElement().setTabIndex(0);//make it focusable
			return label;
		}
	}
	
	protected static class TouchButtonCreator extends ButtonCreator
	{
		@Override
		protected Button createButton()
		{
			return new Button();
		}
	}
}