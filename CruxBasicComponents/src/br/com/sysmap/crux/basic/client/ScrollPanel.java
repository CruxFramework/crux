/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.component.UIObject;
import br.com.sysmap.crux.core.client.event.bind.ScrollEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Represents a ScrollPanel
 * @author Thiago Bustamante
 */
public class ScrollPanel extends SimplePanel 
{
	protected com.google.gwt.user.client.ui.ScrollPanel scrollPanelWidget;

	public ScrollPanel(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.ScrollPanel());
	}

	protected ScrollPanel(String id, com.google.gwt.user.client.ui.ScrollPanel widget) 
	{
		super(id, widget);
		this.scrollPanelWidget = widget;
	}

	/**
	 * Creates a new scroll panel with the given child widget.
	 * 
	 * @param child the widget to be wrapped by the scroll panel
	 */
	public ScrollPanel(String id, Component component) 
	{
		this(id);
		setComponent(component);
	}

	/**
	 * Ensures that the specified item is visible, by adjusting the panel's scroll
	 * position.
	 * 
	 * @param item the item whose visibility is to be ensured
	 */
	public void ensureVisible(UIObject item)
	{
		scrollPanelWidget.ensureVisible(getWrappedUIObject(item));
	}

	/**
	 * Gets the horizontal scroll position.
	 * 
	 * @return the horizontal scroll position, in pixels
	 */
	public int getHorizontalScrollPosition() 
	{
		return scrollPanelWidget.getHorizontalScrollPosition();
	}

	/**
	 * Gets the vertical scroll position.
	 * 
	 * @return the vertical scroll position, in pixels
	 */
	public int getScrollPosition() 
	{
		return scrollPanelWidget.getScrollPosition();
	}

	/**
	 * Scroll to the bottom of this panel.
	 */
	public void scrollToBottom() 
	{
		scrollPanelWidget.scrollToBottom();
	}

	/**
	 * Scroll to the far left of this panel.
	 */
	public void scrollToLeft() 
	{
		scrollPanelWidget.scrollToLeft();
	}

	/**
	 * Scroll to the far right of this panel.
	 */
	public void scrollToRight() 
	{
		scrollPanelWidget.scrollToRight();
	}

	/**
	 * Scroll to the top of this panel.
	 */
	public void scrollToTop() 
	{
		scrollPanelWidget.scrollToTop();
	}

	/**
	 * Sets whether this panel always shows its scroll bars, or only when
	 * necessary.
	 * 
	 * @param alwaysShow <code>true</code> to show scroll bars at all times
	 */
	public void setAlwaysShowScrollBars(boolean alwaysShow) 
	{
		scrollPanelWidget.setAlwaysShowScrollBars(alwaysShow);
	}

	/**
	 * Sets the horizontal scroll position.
	 * 
	 * @param position the new horizontal scroll position, in pixels
	 */
	public void setHorizontalScrollPosition(int position) 
	{
		scrollPanelWidget.setHorizontalScrollPosition(position);
	}

	/**
	 * Sets the vertical scroll position.
	 * 
	 * @param position the new vertical scroll position, in pixels
	 */
	public void setScrollPosition(int position) 
	{
		scrollPanelWidget.setScrollPosition(position);
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		String alwaysShowScrollBars = element.getAttribute("_alwaysShowScrollBars");
		if (alwaysShowScrollBars != null && alwaysShowScrollBars.length() > 0)
		{
			setAlwaysShowScrollBars(Boolean.parseBoolean(alwaysShowScrollBars));
		}
		String verticalScrollPosition = element.getAttribute("_verticalScrollPosition");
		if (verticalScrollPosition != null && verticalScrollPosition.length() > 0)
		{
			if ("top".equals(verticalScrollPosition))
			{
				scrollToTop();
			}
			else if ("bottom".equals(verticalScrollPosition))
			{
				scrollToBottom();
			}
		}
		String horizontalScrollPosition = element.getAttribute("_horizontalScrollPosition");
		if (horizontalScrollPosition != null && horizontalScrollPosition.length() > 0)
		{
			if ("left".equals(horizontalScrollPosition))
			{
				scrollToLeft();
			}
			else if ("right".equals(horizontalScrollPosition))
			{
				scrollToRight();
			}
		}
	
		final String ensureVisible = element.getAttribute("_ensureVisible");
		if (ensureVisible != null && ensureVisible.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				@Override
				public void onLoad() 
				{
					Component c = screen.getComponent(ensureVisible);
					if (c == null)
					{
						throw new NullPointerException();
						//TODO: colocar mensagem
					}
					ensureVisible(c);
				}
			});
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		ScrollEvtBind.bindEvent(element, scrollPanelWidget, getId());
	}

}
