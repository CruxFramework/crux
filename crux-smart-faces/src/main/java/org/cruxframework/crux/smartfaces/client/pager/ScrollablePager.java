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
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A {@link Pager} that change pages from a {@link Pageable} when user scrolls down the pager.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ScrollablePager extends AbstractPager
{
	private static final String DEFAULT_STYLE_NAME = "faces-ScollablePager";

	private DivElement loadingElement;
	private int lastRequestedPage = 0;
	private int lastScrollPos = 0;
	private ScrollPanel scrollable;
	
	public ScrollablePager()
    {
		scrollable = new ScrollPanel();
		
		initWidget(scrollable);
		setStyleName(DEFAULT_STYLE_NAME);
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
					if (!isLastPage() && isEnabled())
					{
						int nextRequestedPage = getCurrentPage() + 1;
						if (lastRequestedPage != nextRequestedPage)
						{
							lastRequestedPage = nextRequestedPage;
							PageEvent pageEvent = PageEvent.fire(ScrollablePager.this, nextRequestedPage);
							if(!pageEvent.isCanceled())
							{
								nextPage();
							}
						}
					}
				}
			}
		});
    }

	@Override
	public void setPageable(Pageable<?> pageable)
	{
	    super.setPageable(pageable);
	    scrollable.setWidget(pageable);
	}
	
	@Override
	public boolean supportsInfiniteScroll()
	{
	    return true;
	}
	
	@Override
    protected void onUpdate()
    {
	    // Do nothing
    }

	@Override
	public void prepareTransaction(int startRecord)
	{
		int pageSize = getPageable().getPageSize();
		int index = startRecord + 1;
		lastRequestedPage = (index / pageSize) + (index%pageSize==0?0:1);
	}
	
	@Override
    protected void showLoading()
    {
		if (loadingElement == null)
		{
			loadingElement = Document.get().createDivElement();
			loadingElement.setClassName("faces-ScollablePager--pagerLoading");
			Document.get().getBody().appendChild(loadingElement);
		}
    }

	@Override
    protected void hideLoading()
    {
		if (loadingElement != null)
		{
			loadingElement.removeFromParent();
			loadingElement = null;
		}
    }
}
