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
package org.cruxframework.crux.widgets.client.paging;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A pager which does not know the total number of pages. So, it can only move the cursor to next or to previous page.  
 * @author Gesse S. F. Dafe
 */
public class SequentialPager extends NavigationButtonsPager
{
	private HorizontalPanel panel;
	private SimplePanel infoPanel;
	
	/**
	 * Constructor
	 */
	public SequentialPager()
	{
		this.panel = new HorizontalPanel();
		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.infoPanel = new SimplePanel();
		this.infoPanel.setWidget(createCurrentPageLabel("" + 0));
		
		this.panel.add(createPreviousButton());
		this.panel.add(infoPanel);
		this.panel.add(createNextButton());		
		
		this.panel.setStyleName("crux-SequentialPager");
		
		initWidget(this.panel);		
	}

	@Override
	protected void onUpdate()
	{
		Label currentPageLabel = createCurrentPageLabel("" + getCurrentPage());
		this.infoPanel.clear();
		this.infoPanel.add(currentPageLabel);
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.paging.AbstractPager#showLoading()
	 */
	@Override
	protected void showLoading()
	{
		this.infoPanel.clear();
		this.infoPanel.add(createCurrentPageLabel("..."));
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.paging.AbstractPager#hideLoading()
	 */
	@Override
	protected void hideLoading()
	{
		// does nothing
	}
	
	/**
	 * Creates the label that shows the current showing page
	 * @param currentPageNumber
	 * @return
	 */
	private Label createCurrentPageLabel(String currentPageNumber)
	{
		Label label = new Label(currentPageNumber);
		label.setStyleName("currentPageLabel");
		return label;
	}
}