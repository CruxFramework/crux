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
package br.com.sysmap.crux.widgets.client.rollingpanel;

import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.wizard.InternalDockPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class RollingPanel extends Composite implements InsertPanel
{
	public static final String DEFAULT_NEXT_HORIZONTAL_STYLE_NAME = "crux-RollingPanelHNext";

	public static final String DEFAULT_NEXT_VERTICAL_STYLE_NAME = "crux-RollingPanelVNext";
	public static final String DEFAULT_PREVIOUS_HORIZONTAL_STYLE_NAME = "crux-RollingPanelHPrevious";
	public static final String DEFAULT_PREVIOUS_VERTICAL_STYLE_NAME = "crux-RollingPanelVPrevious";
	public static final String DEFAULT_STYLE_NAME = "crux-RollingPanel";

	protected CellPanel itemsPanel;
	protected InternalDockPanel layoutPanel;
	
	private String horizontalNextButtonStyleName = DEFAULT_NEXT_HORIZONTAL_STYLE_NAME;
	private Label horizontalNextLabel = null;
	private String horizontalPreviousButtonStyleName = DEFAULT_PREVIOUS_HORIZONTAL_STYLE_NAME;
	private Label horizontalPreviousLabel = null;
	
	private SimplePanel itemsScrollPanel;
	private boolean scrollToAddedWidgets = false;
	private boolean vertical;
	private String verticalNextButtonStyleName = DEFAULT_NEXT_VERTICAL_STYLE_NAME;

	private Label verticalNextLabel = null;
	private String verticalPreviousButtonStyleName = DEFAULT_PREVIOUS_VERTICAL_STYLE_NAME;
	private Label verticalPreviousLabel = null;
	
	/**
	 * @param vertical
	 */
	public RollingPanel(boolean vertical)
	{
		this.vertical = vertical;
		
		this.layoutPanel = new InternalDockPanel();
		this.itemsScrollPanel = new SimplePanel();
	    DOM.setStyleAttribute(this.itemsScrollPanel.getElement(), "overflow", "hidden");
		
		if (vertical)
		{
			this.layoutPanel.setHeight("100%");
			this.itemsScrollPanel.setHeight("100%");
			this.itemsPanel = new VerticalPanel();
		}
		else
		{
			this.layoutPanel.setWidth("100%");
			this.itemsScrollPanel.setWidth("100%");
			this.itemsPanel = new HorizontalPanel();
		}
		
		this.itemsScrollPanel.add(this.itemsPanel);
		
		this.layoutPanel.add(this.itemsScrollPanel, DockPanel.CENTER);
		this.layoutPanel.getElement().getStyle().setProperty("tableLayout", "fixed");
		this.layoutPanel.getBody().getStyle().setProperty("height", "100%");
		
		if (vertical)
		{
			this.layoutPanel.setCellHeight(this.itemsScrollPanel, "100%");
		}
		initWidget(layoutPanel);
		setSpacing(5);
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
	public String getHorizontalNextButtonStyleName()
    {
    	return horizontalNextButtonStyleName;
    }
	
	/**
	 * @return
	 */
	public String getHorizontalPreviousButtonStyleName()
    {
    	return horizontalPreviousButtonStyleName;
    }
	
	/**
	 * @return
	 */
	public int getHorizontalScrollPosition()
	{
		return DOM.getElementPropertyInt(itemsScrollPanel.getElement(), "scrollLeft");
	}

	/**
	 * @return
	 */
	public int getSpacing()
	{
		return itemsPanel.getSpacing();
	}

	/**
	 * @return
	 */
	public String getVerticalNextButtonStyleName()
    {
    	return verticalNextButtonStyleName;
    }

	/**
	 * @return
	 */
	public String getVerticalPreviousButtonStyleName()
    {
    	return verticalPreviousButtonStyleName;
    }
	
	/**
	 * @return
	 */
	public int getVerticalScrollPosition()
	{
		return DOM.getElementPropertyInt(itemsScrollPanel.getElement(), "scrollTop");
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
	 * @return
	 */
	public boolean isVertical()
	{
		return vertical;
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
			if (isVertical())
			{
				verticalScrollToWidget(itemsScrollPanel.getElement(), widget.getElement());
			}
			else
			{
				horizontalScrollToWidget(itemsScrollPanel.getElement(), widget.getElement());
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
		this.layoutPanel.setCellHorizontalAlignment(this.itemsScrollPanel, align);
    }
	
	/**
	 * @param horizontalNextButtonStyleName
	 */
	public void setHorizontalNextButtonStyleName(String horizontalNextButtonStyleName)
    {
    	this.horizontalNextButtonStyleName = horizontalNextButtonStyleName;
    }

	/**
	 * @param horizontalPreviousButtonStyleName
	 */
	public void setHorizontalPreviousButtonStyleName(String horizontalPreviousButtonStyleName)
    {
    	this.horizontalPreviousButtonStyleName = horizontalPreviousButtonStyleName;
    }
	
	/**
	 * @param position
	 */
	public void setHorizontalScrollPosition(int position)
	{
		if (position <0)
		{
			position = 0;
		}
		else if (position > itemsScrollPanel.getOffsetWidth())
		{
			position = itemsScrollPanel.getOffsetWidth();
		}
		DOM.setElementPropertyInt(itemsScrollPanel.getElement(), "scrollLeft", position);
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
		this.layoutPanel.setCellVerticalAlignment(this.itemsScrollPanel, verticalAlign);
    }

	/**
	 * @param verticalNextButtonStyleName
	 */
	public void setVerticalNextButtonStyleName(String verticalNextButtonStyleName)
    {
    	this.verticalNextButtonStyleName = verticalNextButtonStyleName;
    }
	
	/**
	 * @param verticalPreviousButtonStyleName
	 */
	public void setVerticalPreviousButtonStyleName(String verticalPreviousButtonStyleName)
    {
    	this.verticalPreviousButtonStyleName = verticalPreviousButtonStyleName;
    }

	/**
	 * @param position
	 */
	public void setVerticalScrollPosition(int position)
	{
		if (position <0)
		{
			position = 0;
		}
		else if (position > itemsScrollPanel.getOffsetHeight())
		{
			position = itemsScrollPanel.getOffsetHeight();
		}
	    DOM.setElementPropertyInt(itemsScrollPanel.getElement(), "scrollTop", position);
	}
	
	/**
	 * 
	 */
	protected void addHorizontalNavigationButtons()
    {
		if (horizontalPreviousLabel == null)
		{
			horizontalPreviousLabel = new Label(" ");
			horizontalPreviousLabel.setStyleName(horizontalPreviousButtonStyleName);
			HorizontalNavButtonEvtHandler handler = new HorizontalNavButtonEvtHandler(-20, -5);
			horizontalPreviousLabel.addMouseDownHandler(handler);
			horizontalPreviousLabel.addMouseUpHandler(handler);

			this.layoutPanel.add(horizontalPreviousLabel, DockPanel.WEST);
			this.layoutPanel.setCellWidth(horizontalPreviousLabel, "16");
		}
	    if (horizontalNextLabel == null)
	    {
	    	horizontalNextLabel = new Label(" ");
	    	horizontalNextLabel.setStyleName(horizontalNextButtonStyleName);
	    	HorizontalNavButtonEvtHandler handler = new HorizontalNavButtonEvtHandler(20, 5);
	    	horizontalNextLabel.addMouseDownHandler(handler);
	    	horizontalNextLabel.addMouseUpHandler(handler);

	    	this.layoutPanel.add(horizontalNextLabel, DockPanel.EAST);
	    	this.layoutPanel.setCellWidth(horizontalNextLabel, "16");
	    }
    }

	/**
	 * 
	 */
	protected void addVerticalNavigationButtons()
    {
	    if (verticalPreviousLabel == null)
	    {
	    	verticalPreviousLabel = new Label(" ");
	    	verticalPreviousLabel.setStyleName(verticalPreviousButtonStyleName);
	    	VerticalNavButtonEvtHandler handler = new VerticalNavButtonEvtHandler(-20, -5);
	    	verticalPreviousLabel.addMouseDownHandler(handler);
	    	verticalPreviousLabel.addMouseUpHandler(handler);
	    	this.layoutPanel.add(verticalPreviousLabel, DockPanel.NORTH);
	    	this.layoutPanel.setCellHeight(verticalPreviousLabel, "16");
	    }
	    if (verticalNextLabel == null)
	    {
	    	verticalNextLabel = new Label(" ");
	    	VerticalNavButtonEvtHandler handler = new VerticalNavButtonEvtHandler(20, 5);
	    	verticalNextLabel.addMouseDownHandler(handler);
	    	verticalNextLabel.addMouseUpHandler(handler);
	    	verticalNextLabel.setStyleName(verticalNextButtonStyleName);
	    	this.layoutPanel.add(verticalNextLabel, DockPanel.SOUTH);
	    	this.layoutPanel.setCellHeight(verticalNextLabel, "16");
	    }
    }
	
	/**
	 * 
	 */
	protected void checkNavigationButtons()
	{
		if (isVertical())
		{
			if (itemsPanel.getOffsetHeight() > layoutPanel.getOffsetHeight())
			{
				addVerticalNavigationButtons();
			}
			else
			{
				removeVerticalNavigationButtons();
			}
		}
		else
		{
			if (itemsPanel.getOffsetWidth() > layoutPanel.getOffsetWidth())
			{
				addHorizontalNavigationButtons();
			}
			else
			{
				removeHorizontalNavigationButtons();
			}
		}
	}

	/**
	 * 
	 */
	protected void maybeShowNavigationButtons()
    {
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			public void execute()
			{
				checkNavigationButtons();
			}
		});
    }

	/**
	 * 
	 */
	protected void removeHorizontalNavigationButtons()
	{
	    if (horizontalPreviousLabel != null)
	    {
	    	horizontalPreviousLabel.removeFromParent();
	    	horizontalPreviousLabel = null;
	    }
		
	    if (horizontalNextLabel != null)
	    {
	    	horizontalNextLabel.removeFromParent();
	    	horizontalNextLabel = null;
	    }
	}
	
	/**
	 * 
	 */
	protected void removeVerticalNavigationButtons()
	{
	    if (verticalPreviousLabel != null)
	    {
	    	verticalPreviousLabel.removeFromParent();
	    	verticalPreviousLabel = null;
	    }
		
	    if (verticalNextLabel != null)
	    {
	    	verticalNextLabel.removeFromParent();
	    	verticalNextLabel = null;
	    }
	}

	/**
	 * @param scroll
	 * @param item
	 */
	private void horizontalScrollToWidget(com.google.gwt.dom.client.Element scroll, com.google.gwt.dom.client.Element item)
    {
		if (itemsPanel.getOffsetWidth() > layoutPanel.getOffsetWidth())
		{		
			int realOffset = 0;
			while (item != null && (!item.equals(scroll)))
			{
				realOffset += item.getOffsetLeft();
				item = item.getOffsetParent();
			}
			scroll.setScrollLeft(realOffset - scroll.getOffsetWidth() / 2);
		}
    }

	/**
	 * @param scroll
	 * @param item
	 */
	private void verticalScrollToWidget(com.google.gwt.dom.client.Element scroll, com.google.gwt.dom.client.Element item)
    {
		if (itemsPanel.getOffsetHeight() > layoutPanel.getOffsetHeight())
		{
			int realOffset = 0;
			while (item != null && (!item.equals(scroll)))
			{
				realOffset += item.getOffsetTop();
				item = item.getOffsetParent();
			}
			scroll.setScrollTop(realOffset - scroll.getOffsetHeight() / 2);
		}
    }

	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
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
			int position = getHorizontalScrollPosition() + adjust;
			setHorizontalScrollPosition(position);
	    }
	}

	/**
	 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
	 *
	 */
	class VerticalNavButtonEvtHandler extends HorizontalNavButtonEvtHandler
	{

		VerticalNavButtonEvtHandler(int adjust, int incrementalAdjust)
        {
	        super(adjust, incrementalAdjust);
        }

		@Override
		protected void adjustScrollPosition(int adjust)
	    {
	        int position = getVerticalScrollPosition() + adjust;
			setVerticalScrollPosition(position);
	    }
	}
}
