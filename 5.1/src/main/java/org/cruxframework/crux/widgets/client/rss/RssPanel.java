/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.rss;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.rss.feed.Entry;
import org.cruxframework.crux.widgets.client.rss.feed.Feed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
//TODO refatorar isso. completamente. acredito que remover da biblioteca
public class RssPanel extends Composite
{
	private RssPanelImpl panelImpl;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class RssPanelImpl extends Composite
	{
		protected VerticalPanel rssPanel;
		protected HTML title;
		private String titleStyleName;
		private String entryTitleStyleName = "crux-entryTitle";
		private String entryDateStyleName = "crux-entryDate";
		protected Grid body;
		protected DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		protected int maxTitleSize;
		
		public abstract void addEntry(final Entry entry);

		public RssPanelImpl()
        {
			setMaxTitleSize(100);
			
			rssPanel = new VerticalPanel();
			title = new HTML();
			title.setStyleName("rssPanelTitle");
			
			rssPanel.add(title);
			
			body = new Grid();
			body.setStyleName("rssPanelBody");
			body.setCellSpacing(0);
			
			rssPanel.add(body);
			initWidget(rssPanel);
			
			setStyleName("crux-RssPanel");
        }
		
		/**
		 * 
		 * @param feed
		 */
		public void setFeed(Feed feed)
		{
			clear();
			if (feed != null)
			{
				JsArray<Entry> entries = feed.getEntries();
				if (entries != null)
				{
					for (int i=0; i< entries.length(); i++)
					{
						addEntry(entries.get(i));
					}
				}
			}
		}
		
		/**
		 * 
		 */
		public void clear()
		{
			body.clear();
			body.resizeRows(0);
		}

		/**
		 * 
		 */
		public String getTitle()
		{
			return title.getHTML();
		}
		
		/**
		 * 
		 */
		public void setTitle(String html)
		{
			title.setHTML(html);
		}

		/**
		 * 
		 * @return
		 */
		public int getMaxTitleSize()
		{
			return maxTitleSize;
		}
		
		/**
		 * 
		 * @param maxTitleSize
		 */
		public void setMaxTitleSize(int maxTitleSize)
		{
			this.maxTitleSize = maxTitleSize;
		}
		
		/**
		 * 
		 * @param title
		 * @return
		 */
		protected String getClipedTitle(String title)
		{
			if (title==null)
			{
				return null;
			}
			if (title.length() > maxTitleSize)
			{
				title = title.substring(0, maxTitleSize-4) + "...";
			}
			
			return title;
		}
		
		/**
		 * @param bodyStyleName the bodyStyleName to set
		 */
		public void setTitleStyleName(String titleStyleName)
		{
			this.titleStyleName = titleStyleName;
			this.title.setStyleName(this.titleStyleName);
		}

		/**
		 * @return the titleStyleName
		 */
		public String getTitleStyleName()
		{
			return titleStyleName;
		}

		/**
		 * @return
		 */
		public String getEntryTitleStyleName() 
		{
			return entryTitleStyleName;
		}

		/**
		 * @param entryTitleStyleName
		 */
		public void setEntryTitleStyleName(String entryTitleStyleName) 
		{
			this.entryTitleStyleName = entryTitleStyleName;
		}

		/**
		 * @return
		 */
		public String getEntryDateStyleName() 
		{
			return entryDateStyleName;
		}

		/**
		 * @param entryDateStyleName
		 */
		public void setEntryDateStyleName(String entryDateStyleName) 
		{
			this.entryDateStyleName = entryDateStyleName;
		}

		public void setDateTimeFormat(DateTimeFormat dateTimeFormat) 
		{
			this.dateTimeFormat = dateTimeFormat;
		}
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class RssPanelLargeImpl extends RssPanelImpl
	{
		public RssPanelLargeImpl()
		{
			super();
			body.resizeColumns(2);
			StyleUtils.addStyleDependentName(getElement(), "large");
		}
		
		/**
		 * 
		 * @param entry
		 */
		public void addEntry(final Entry entry)
		{
			int row = body.insertRow(body.getRowCount());
			HTML entryTitle = new HTML(getClipedTitle(entry.getTitle()));
			entryTitle.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					Window.open(entry.getLink(), "", null);
				}
			});
			entryTitle.setStyleName(getEntryTitleStyleName());
			entryTitle.setTitle(entry.getTitle());
			
			Label entryDate = new Label(dateTimeFormat.format(entry.getPublishedDate()));
			entryDate.setStyleName(getEntryDateStyleName());
			
			body.setWidget(row, 0, entryTitle);
			body.setWidget(row, 1, entryDate);
		}
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class RssPanelSmallImpl extends RssPanelImpl
	{
		public RssPanelSmallImpl()
		{
			super();
			body.resizeColumns(1);
			StyleUtils.addStyleDependentName(getElement(), "small");
		}
		
		/**
		 * 
		 * @param entry
		 */
		public void addEntry(final Entry entry)
		{
			int row = body.insertRow(body.getRowCount());
			HTML entryTitle = new HTML(getClipedTitle(entry.getTitle()));
			entryTitle.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					Window.open(entry.getLink(), "", null);
				}
			});
			entryTitle.setStyleName(getEntryTitleStyleName());
			entryTitle.setTitle(entry.getTitle());
			
			Label entryDate = new Label(dateTimeFormat.format(entry.getPublishedDate()));
			entryDate.setStyleName(getEntryDateStyleName());
			
			FlowPanel entryPanel = new FlowPanel();
			entryPanel.add(entryTitle);
			entryPanel.add(entryDate);
			
			body.setWidget(row, 0, entryPanel);
		}
	}

	public RssPanel()
	{
		this(100,null,null);
	}
	
	public RssPanel(Integer maxTitleSize, String entryTitleStyleName, String entryDateStyleName)
    {
	    this.panelImpl = GWT.create(RssPanelImpl.class);
	    setConstructorProperties(maxTitleSize, entryTitleStyleName, entryDateStyleName);
	    initWidget(panelImpl);
    }

	private void setConstructorProperties(Integer maxTitleSize,
			String entryTitleStyleName, String entryDateStyleName) 
	{
		if(maxTitleSize == null)
	    {
	    	panelImpl.setMaxTitleSize(100);
	    }
	    else
	    {
	    	panelImpl.setMaxTitleSize(maxTitleSize);
	    }
	    
	    if(!StringUtils.isEmpty(entryTitleStyleName))
	    {
	    	panelImpl.setEntryTitleStyleName(entryTitleStyleName);
	    }
	    
	    if(!StringUtils.isEmpty(entryDateStyleName))
	    {
	    	panelImpl.setEntryDateStyleName(entryDateStyleName);
	    }
	}
	
	/**
	 * 
	 */
	public void clear()
	{
		panelImpl.clear();
	}
	
	/**
	 * 
	 * @param entry
	 */
	public void addEntry(final Entry entry)
	{
		panelImpl.addEntry(entry);
	}
	
	/**
	 * 
	 */
	public String getTitle()
	{
		return panelImpl.getTitle();
	}
	
	/**
	 * 
	 */
	public void setTitle(String html)
	{
		panelImpl.setTitle(html);
	}

	/**
	 * 
	 * @param feed
	 */
	public void setFeed(Feed feed)
	{
		panelImpl.setFeed(feed);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxTitleSize()
	{
		return panelImpl.getMaxTitleSize();
	}
	
	/**
	 * 
	 * @param maxTitleSize
	 */
	public void setMaxTitleSize(int maxTitleSize)
	{
		panelImpl.setMaxTitleSize(maxTitleSize);
	}
	
	/**
	 * @param bodyStyleName the bodyStyleName to set
	 */
	public void setTitleStyleName(String titleStyleName)
	{
		panelImpl.setTitleStyleName(titleStyleName);
	}

	/**
	 * @return the titleStyleName
	 */
	public String getTitleStyleName()
	{
		return panelImpl.getTitleStyleName();
	}
	
	/**
	 * @return
	 */
	public String getEntryTitleStyleName() 
	{
		return panelImpl.getEntryTitleStyleName();
	}

	/**
	 * @param entryTitleStyleName
	 */
	public void setEntryTitleStyleName(String entryTitleStyleName) 
	{
		panelImpl.setEntryTitleStyleName(entryTitleStyleName);
	}

	/**
	 * @return
	 */
	public String getEntryDateStyleName() 
	{
		return panelImpl.getEntryDateStyleName();
	}

	/**
	 * @param entryDateStyleName
	 */
	public void setEntryDateStyleName(String entryDateStyleName) 
	{
		panelImpl.setEntryDateStyleName(entryDateStyleName);
	}

	/**
	 * @param dateTimeFormat
	 */
	public void setDateTimeFormat(DateTimeFormat dateTimeFormat)
	{
		panelImpl.setDateTimeFormat(dateTimeFormat);
	}
	
}