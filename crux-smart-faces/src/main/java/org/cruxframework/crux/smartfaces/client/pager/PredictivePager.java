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

import org.cruxframework.crux.core.client.datasource.pager.PageEvent;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A pager which knows the total number of pages.  
 * @author Gesse S. F. Dafe
 */
public class PredictivePager extends NavigationButtonsPager
{
	private static final String DEFAULT_STYLE_NAME = "faces-PredictivePager";
	private NavPanel panel;
	private ListBox listBox;
	private int pageCount;
	
	/**
	 * Constructor
	 */
	public PredictivePager()
	{
		this.listBox = createListBox();	
		
		this.panel = new NavPanel();
//		this.panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		this.panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		this.panel.setSpacing(2);
				
		this.panel.add(createFirstPageButton());
		this.panel.add(createPreviousButton());
		this.panel.add(listBox);
		this.panel.add(createNextButton());
		this.panel.add(createLastPageButton());	
		
		this.panel.setStyleName(DEFAULT_STYLE_NAME);
		initWidget(this.panel);		
	}

	@Override
	protected void onUpdate()
	{
		if(this.pageCount != getPageCount())
		{
			this.pageCount = getPageCount();
			this.listBox.clear();
			
			for (int i = 1; i <= getPageCount(); i++)
			{
				String page = "" + i;
				listBox.addItem(page, page);
			}
		}
		
		if(this.listBox.getItemCount() > 0)
		{
			listBox.setEnabled(true);
			listBox.setSelectedIndex(getCurrentPage() - 1);
		} else
		{
			listBox.setEnabled(false);
		}
	}

	@Override
	protected void showLoading()
	{
		listBox.setEnabled(false);
	}
	
	@Override
	protected void hideLoading()
	{
		listBox.setEnabled(true);		
	}
	
	/**
	 * Creates a list box with page numbers
	 * @return
	 */
	private ListBox createListBox()
	{
		final ListBox list = new ListBox();
		list.setEnabled(false);
		list.addChangeHandler
		(
			new ChangeHandler()
			{
				public void onChange(ChangeEvent event)
				{
					if(isEnabled())
					{
						PageEvent pageEvent = PageEvent.fire(PredictivePager.this, getCurrentPage() + 1);
						if(!pageEvent.isCanceled())
						{
							int selected = list.getSelectedIndex();
							String page = list.getValue(selected);
							goToPage(Integer.valueOf(page));
						}
						else
						{
							list.setSelectedIndex(getCurrentPage() - 1);
						}
					}
				}				
			}
		);
		
		return list;
	}
}