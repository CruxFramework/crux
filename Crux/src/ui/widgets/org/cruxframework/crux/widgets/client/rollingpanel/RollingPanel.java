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
package org.cruxframework.crux.widgets.client.rollingpanel;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@SuppressWarnings("deprecation")
public class RollingPanel extends Composite implements InsertPanel, HasHorizontalAlignment, HasVerticalAlignment
{
	public static final String DEFAULT_NEXT_STYLE_NAME = "crux-RollingPanelNext";
	public static final String DEFAULT_PREVIOUS_STYLE_NAME = "crux-RollingPanelPrevious";
	public static final String DEFAULT_BODY_HORIZONTAL_STYLE_NAME = "crux-RollingPanelBody";
	public static final String DEFAULT_STYLE_NAME = "crux-RollingPanel";

	private String nextButtonStyleName;
	private String previousButtonStyleName;
	private String bodyStyleName;

	protected CellPanel itemsPanel;
    protected DockPanel layoutPanel;
	
	private Button nextButton = null;
	private Button previousButton = null;
	
	private SimplePanel itemsScrollPanel;
	private boolean scrollToAddedWidgets = false;

	private HorizontalAlignmentConstant horizontalAlign;
	private VerticalAlignmentConstant verticalAlign;
	
	/**
	 * @param vertical
	 */
	public RollingPanel()
	{
		this.layoutPanel = new DockPanel();
		this.itemsScrollPanel = new SimplePanel();
		
		DOM.setStyleAttribute(this.itemsScrollPanel.getElement(), "overflowX", "hidden");
		DOM.setStyleAttribute(this.itemsScrollPanel.getElement(), "overflowY", "hidden");
		
		this.layoutPanel.setWidth("100%");
		this.itemsScrollPanel.setWidth("100%");
		this.itemsScrollPanel.setStyleName(DEFAULT_BODY_HORIZONTAL_STYLE_NAME);
		this.itemsPanel = new HorizontalPanel();
		createNavigationButtons();
		this.itemsScrollPanel.add(this.itemsPanel);
		
		this.layoutPanel.add(this.itemsScrollPanel, DockPanel.CENTER);
		this.layoutPanel.getElement().getStyle().setProperty("tableLayout", "fixed");
	
		initWidget(layoutPanel);
		setSpacing(0);
		setStyleName(DEFAULT_STYLE_NAME);
		
		Screen.addResizeHandler(new ResizeHandler()
		{
			public void onResize(ResizeEvent event)
			{
				checkNavigationButtons();
			}
		});

		maybeShowNavigationButtons();
	}
	
	/**
	 * @param child
	 */
	public void add(final Widget child)
	{
		this.itemsPanel.add(child);
		maybeShowNavigationButtons();
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(child);
				}
			});
		}
	}

	/**
	 * 
	 */
	public void clear()
	{
		this.itemsPanel.clear();
		maybeShowNavigationButtons();
	}

	/**
	 * @return
	 */
	public int getScrollPosition()
	{
		return itemsScrollPanel.getElement().getScrollLeft();
	}

	/**
	 * @return
	 */
	public int getSpacing()
	{
		return itemsPanel.getSpacing();
	}

	/**
	 * @param i
	 * @return
	 */
	public Widget getWidget(int i)
    {
	    return itemsPanel.getWidget(i);
    }

	/**
	 * @return
	 */
	public int getWidgetCount()
    {
	    return itemsPanel.getWidgetCount();
    }

	/**
	 * @see com.google.gwt.user.client.ui.IndexedPanel#getWidgetIndex(com.google.gwt.user.client.ui.Widget)
	 */
	public int getWidgetIndex(Widget child)
    {
	    return ((InsertPanel)itemsPanel).getWidgetIndex(child);
    }

	/**
	 * @param widget
	 * @param i
	 */
	public void insert(final Widget widget, int i)
    {
	    ((InsertPanel)itemsPanel).insert(widget, i);
		maybeShowNavigationButtons();
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(widget);
				}
			});
		}
    }
	
	/**
	 * @return
	 */
	public boolean isScrollToAddedWidgets()
    {
    	return scrollToAddedWidgets;
    }

	/**
	 * @see com.google.gwt.user.client.ui.IndexedPanel#remove(int)
	 */
	public boolean remove(int index)
    {
	    boolean ret = ((InsertPanel)itemsPanel).remove(index);
		maybeShowNavigationButtons();
		return ret;
    }
	
	/**
	 * @param toRemove
	 */
	public void remove(Widget toRemove)
    {
		itemsPanel.remove(toRemove);
		maybeShowNavigationButtons();
    }
	
	/**
	 * @param widget
	 */
	public void scrollToWidget(Widget widget)
	{
		if (widget != null)
		{
			Element scroll = itemsScrollPanel.getElement();
			Element item = widget.getElement();
			
			if (itemsPanel.getOffsetWidth() > layoutPanel.getOffsetWidth())
			{		
				int realOffset = 0;
				int itemOffsetWidth = item.getOffsetWidth();
				while (item != null && item != scroll)
				{
					realOffset += item.getOffsetLeft();
					item = item.getParentElement();
				}
				int scrollLeft = getScrollPosition();
				int scrollOffsetWidth = scroll.getOffsetWidth();
				int right = realOffset + itemOffsetWidth;
				int visibleWidth = scrollLeft + scrollOffsetWidth;
				
				if (realOffset < scrollLeft)	
				{
					setScrollPosition(realOffset);
				}
				else if (right > visibleWidth)
				{
					setScrollPosition(scrollLeft + right - visibleWidth);
				}
			}
		}
	}
	
	/**
	 * @param child
	 * @param cellHeight
	 */
	public void setCellHeight(Widget child, String cellHeight)
    {
		this.itemsPanel.setCellHeight(child, cellHeight);
    }
	
	/**
	 * @param w
	 * @param align
	 */
	public void setCellHorizontalAlignment(Widget w, HorizontalAlignmentConstant align)
	{
		this.itemsPanel.setCellHorizontalAlignment(w, align);
	}	
	
	/**
	 * @param verticalAlign
	 */
	public void setCellVerticalAlignment(Widget w, VerticalAlignmentConstant verticalAlign)
    {
		this.itemsPanel.setCellVerticalAlignment(w, verticalAlign);
    }	

	/**
	 * @param child
	 * @param cellWidth
	 */
	public void setCellWidth(Widget child, String cellWidth)
    {
		this.itemsPanel.setCellWidth(child, cellWidth);
    }
	
	/**
	 * @param align
	 */
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
    {
		this.horizontalAlign = align;
		this.layoutPanel.setCellHorizontalAlignment(this.itemsScrollPanel, align);
    }
	
	/**
	 * @param position
	 */
	public void setScrollPosition(int position)
	{
		if (position <0)
		{
			position = 0;
		}
        else
        {
	        int offsetWidth = itemsPanel.getOffsetWidth();
	        if (position > offsetWidth)
	        {
	        	position = offsetWidth;
	        }
        }
	    DOM.setElementPropertyInt(itemsScrollPanel.getElement(), "scrollLeft", position);
//		itemsScrollPanel.getElement().setScrollLeft(position);
	}

	/**
	 * @param scrollToAddedWidgets
	 */
	public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
    	this.scrollToAddedWidgets = scrollToAddedWidgets;
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		itemsPanel.setSpacing(spacing);
	}
	
	/**
	 * @param verticalAlign
	 */
	public void setVerticalAlignment(VerticalAlignmentConstant verticalAlign)
    {
		this.verticalAlign = verticalAlign;
		this.layoutPanel.setCellVerticalAlignment(this.itemsScrollPanel, verticalAlign);
    }
	
	/**
	 * 
	 */
	protected void createNavigationButtons()
	{
		previousButton = new Button();
		previousButton.setText("<");
		previousButton.setStyleName(DEFAULT_PREVIOUS_STYLE_NAME);
		HorizontalNavButtonEvtHandler handler = new HorizontalNavButtonEvtHandler(-20, -5);
		previousButton.addMouseDownHandler(handler);
		previousButton.addMouseUpHandler(handler);

		this.layoutPanel.add(previousButton, DockPanel.WEST);
		
		nextButton = new Button();
		nextButton.setText(">");
		nextButton.setStyleName(DEFAULT_NEXT_STYLE_NAME);
		handler = new HorizontalNavButtonEvtHandler(20, 5);
		nextButton.addMouseDownHandler(handler);
		nextButton.addMouseUpHandler(handler);

		this.layoutPanel.add(nextButton, DockPanel.EAST);

		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			public void execute()
			{
				Element prevWrapper = getWrapperElement(previousButton);
				Element nextWrapper = getWrapperElement(nextButton);
				
				prevWrapper.setClassName(DEFAULT_PREVIOUS_STYLE_NAME + "Wrapper");
				nextWrapper.setClassName(DEFAULT_NEXT_STYLE_NAME + "Wrapper");
				
				((TableCellElement)prevWrapper).setVAlign("middle");
				((TableCellElement)nextWrapper).setVAlign("middle");
			}
		});
	}

	/**
	 * @return
	 */
	private Element getWrapperElement(Button button)
	{
		return button.getElement().getParentElement();
	}
	
	
	/**
	 * 
	 */
	protected void checkNavigationButtons()
	{
		if (itemsPanel.getOffsetWidth() > layoutPanel.getOffsetWidth())
		{
			enableNavigationButtons();
		}
		else
		{
			disableNavigationButtons();
			setScrollPosition(0);
		}
	}

	/**
	 * 
	 */
	protected void maybeShowNavigationButtons()
    {
		new Timer()
		{
			@Override
			public void run()
			{
				checkNavigationButtons();
			}
		}.schedule(30);
    }

	/**
	 * 
	 */
	protected void disableNavigationButtons()
	{
		StyleUtils.addStyleDependentName(getWrapperElement(previousButton), "disabled");
		StyleUtils.addStyleDependentName(getWrapperElement(nextButton), "disabled");
	}
	
	/**
	 * 
	 */
	protected void enableNavigationButtons()
	{
		StyleUtils.removeStyleDependentName(getWrapperElement(previousButton), "disabled");
		StyleUtils.removeStyleDependentName(getWrapperElement(nextButton), "disabled");
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	class HorizontalNavButtonEvtHandler implements MouseDownHandler, MouseUpHandler
	{
		private int adjust;
		private boolean buttonPressed = false;
		private int delta;
		private int incrementalAdjust;
		private int originalIncrementalAdjust;

		HorizontalNavButtonEvtHandler(int adjust, int incrementalAdjust)
		{
			this.adjust = adjust;
			this.incrementalAdjust = incrementalAdjust;
			this.originalIncrementalAdjust = incrementalAdjust;
			this.delta = incrementalAdjust / 4;
		}
		
		public void onMouseDown(MouseDownEvent event)
		{
			buttonPressed = true;
			adjustScrollPosition(adjust);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
			{
				public boolean execute()
				{
					if (buttonPressed)
					{
						adjustScrollPosition(incrementalAdjust+=delta);
					}
					return buttonPressed;
				}
			}, 50);
		}

		public void onMouseUp(MouseUpEvent event)
		{
			buttonPressed = false;
			incrementalAdjust = originalIncrementalAdjust;
		}
		
		/**
		 * @param adjust
		 */
		protected void adjustScrollPosition(int adjust)
	    {
			int position = getScrollPosition() + adjust;
			setScrollPosition(position);
	    }
	}

	public VerticalAlignmentConstant getVerticalAlignment()
    {
	    return this.verticalAlign;
    }

	public HorizontalAlignmentConstant getHorizontalAlignment()
    {
	    return this.horizontalAlign;
    }

	/**
	 * @param nextButtonStyleName
	 */
	public void setNextButtonStyleName(String nextButtonStyleName)
    {
    	this.nextButtonStyleName = nextButtonStyleName;
    	
    	this.nextButton.setStyleName(this.nextButtonStyleName);
    	
    	Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
    		public void execute()
			{
    			Button btn = nextButton;
				getWrapperElement(btn).setClassName(RollingPanel.this.nextButtonStyleName + "Wrapper");
			}
		});
    }

	/**
	 * @param previousButtonStyleName
	 */
	public void setPreviousButtonStyleName(String previousButtonStyleName)
    {
    	this.previousButtonStyleName = previousButtonStyleName;
    	
    	this.previousButton.setStyleName(this.nextButtonStyleName);
    	Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
    		public void execute()
			{
    			Button btn = previousButton;
				getWrapperElement(btn).setClassName(RollingPanel.this.previousButtonStyleName + "Wrapper");
			}
		});
    }

	/**
	 * @param bodyStyleName the bodyStyleName to set
	 */
	public void setBodyStyleName(String bodyStyleName)
	{
		this.bodyStyleName = bodyStyleName;
		this.itemsScrollPanel.setStyleName(this.bodyStyleName);
	}

	/**
	 * @return the bodyStyleName
	 */
	public String getBodyStyleName()
	{
		return bodyStyleName;
	}

	/**
	 * @return the nextButtonStyleName
	 */
	public String getNextButtonStyleName()
	{
		return nextButtonStyleName;
	}

	/**
	 * @return the previousButtonStyleName
	 */
	public String getPreviousButtonStyleName()
	{
		return previousButtonStyleName;
	}
}
