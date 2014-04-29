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

import org.cruxframework.crux.core.client.datasource.pager.AbstractPager;
import org.cruxframework.crux.core.client.datasource.pager.PageEvent;
import org.cruxframework.crux.core.client.datasource.pager.Pager;
import org.cruxframework.crux.smartfaces.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.core.client.GWT;

/**
 * Base implementation for navigation-buttons-based pager
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe
 */
public abstract class NavigationButtonsPager extends AbstractPager implements Pager
{
	private Label previousButton;
	private Label nextButton;
	private Label firstButton;
	private Label lastButton;
	private ButtonCreator buttonCreator = GWT.create(ButtonCreator.class);
	
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
	protected Label createPreviousButton()
	{
		final NavigationButtonsPager pager = this;
		
		Label panel = createNavigationButton("previousButton", 
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
	protected Label createNextButton()
	{
		final NavigationButtonsPager pager = this;
		
		Label panel = createNavigationButton("nextButton", 
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
		
		return panel;
	}
	
	/**
	 * Creates the "first page" navigation button
	 * @return
	 */
	protected Label createFirstPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Label panel = createNavigationButton("firstButton", 
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
	protected Label createLastPageButton()
	{
		final NavigationButtonsPager pager = this;
		
		Label label = createNavigationButton("lastButton", 
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
	private Label createNavigationButton(String styleName, SelectHandler selectHandler)
	{
		Label button = buttonCreator.createButton();
		button.setStyleName(styleName);
		button.addStyleDependentName("disabled");
		button.addSelectHandler(selectHandler);
		return button;
	}
	
	
	protected static class ButtonCreator
	{
		protected Label createButton()
		{
			Label label = new Label();
			label.getElement().setTabIndex(0);//make it focusable
			return label;
		}
	}
	
	protected static class TouchButtonCreator extends ButtonCreator
	{
		@Override
		protected Label createButton()
		{
			return new Label();
		}
	}
}