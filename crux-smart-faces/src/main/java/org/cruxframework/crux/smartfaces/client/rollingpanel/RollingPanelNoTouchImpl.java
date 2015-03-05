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
package org.cruxframework.crux.smartfaces.client.rollingpanel;

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RollingPanelNoTouchImpl extends Composite implements RollingPanel.PanelImplementation
{
	private static final String NEXT_STYLE_NAME = "faces-RollingPanel-next";
	private static final String PREVIOUS_STYLE_NAME = "faces-RollingPanel-previous";
	private static final String NEXT_PANEL_STYLE_NAME = "faces-RollingPanel-nextPanel";
	private static final String PREVIOUS_PANEL_STYLE_NAME = "faces-RollingPanel-previousPanel";
	private static final String BODY_STYLE_NAME = "faces-RollingPanel-body";

	private String nextButtonStyleName;
	private String previousButtonStyleName;
	private String bodyStyleName;

	protected FlowPanel itemsPanel;
	protected FlowPanel layoutPanel;
	
	private Button nextButton = null;
	private Button previousButton = null;
	
	private ScrollPanel itemsScrollPanel;
	private boolean scrollToAddedWidgets = false;
	private SimplePanel previousButtonPanel;
	private SimplePanel nextButtonPanel;

	public RollingPanelNoTouchImpl()
	{
		this.layoutPanel = new FlowPanel();

        createPreviousButton();
		createBodyPanel();
		createNextButton();
	
		initWidget(layoutPanel);
		handleWindowResize();
		maybeShowNavigationButtons();
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
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

	@Override
	public Widget getWidget(int i)
	{
		return itemsPanel.getWidget(i);
	}

	@Override
	public int getWidgetCount()
	{
		return itemsPanel.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget child)
	{
		return itemsPanel.getWidgetIndex(child);
	}

	@Override
	public void insert(final Widget widget, int i)
	{
		itemsPanel.insert(widget, i);
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

	@Override
	public boolean remove(int index)
	{
		boolean ret = itemsPanel.remove(index);
		maybeShowNavigationButtons();
		return ret;
	}
	
	/**
	 * @param toRemove
	 */
	public boolean remove(Widget toRemove)
	{
		boolean removed = itemsPanel.remove(toRemove);
		maybeShowNavigationButtons();
		return removed;
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
	 * @param position
	 */
	public void setScrollPosition(int position)
	{
		if (position < 0)
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
		itemsScrollPanel.getElement().setPropertyInt("scrollLeft", position);
	}

	/**
	 * @param scrollToAddedWidgets
	 */
	public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
	{
		this.scrollToAddedWidgets = scrollToAddedWidgets;
	}

	protected void createPreviousButton()
    {
		NavButtonEvtHandler handler = new NavButtonEvtHandler(-20, -5);

		previousButton = new Button();
		previousButton.setText("<");
		previousButton.setStyleName(PREVIOUS_STYLE_NAME);
		previousButton.addMouseDownHandler(handler);
		previousButton.addMouseUpHandler(handler);
		previousButtonPanel = new SimplePanel();
		previousButtonPanel.setStyleName(PREVIOUS_PANEL_STYLE_NAME);
		previousButtonPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxFirstChild());
		previousButtonPanel.add(previousButton);
		layoutPanel.add(previousButtonPanel);
    }

	protected void createNextButton()
    {
		NavButtonEvtHandler handler = new NavButtonEvtHandler(20, 5);
	    nextButton = new Button();
		nextButton.setText(">");
		nextButton.setStyleName(NEXT_STYLE_NAME);
		nextButton.addMouseDownHandler(handler);
		nextButton.addMouseUpHandler(handler);
		nextButtonPanel = new SimplePanel();
		nextButtonPanel.setStyleName(NEXT_PANEL_STYLE_NAME);
		nextButtonPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxThirdChild());
		nextButtonPanel.add(nextButton);
		layoutPanel.add(nextButtonPanel);
    }

	protected void createBodyPanel()
    {
	    itemsScrollPanel = new ScrollPanel();
		itemsScrollPanel.setStyleName(BODY_STYLE_NAME);
		itemsScrollPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().rollingPanelBody());
		itemsPanel = new FlowPanel();
		itemsPanel.setStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxInlineContainer());
		itemsScrollPanel.add(itemsPanel);
		layoutPanel.add(itemsScrollPanel); 
    }

	protected void checkNavigationButtons()
	{
		if (itemsPanel.getOffsetWidth() > layoutPanel.getOffsetWidth() - (nextButton.getOffsetWidth() + previousButton.getOffsetWidth()))
		{
			enableNavigationButtons();
		}
		else
		{
			disableNavigationButtons();
			setScrollPosition(0);
		}
	}

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

	protected void disableNavigationButtons()
	{
		previousButtonPanel.addStyleDependentName("-disabled");
		nextButtonPanel.addStyleDependentName("-disabled");
	}
	
	protected void enableNavigationButtons()
	{
		previousButtonPanel.removeStyleDependentName("-disabled");
		nextButtonPanel.removeStyleDependentName("-disabled");
	}
	
	protected void handleWindowResize()
	{
		addAttachHandler(new Handler()
		{
			HandlerRegistration registration;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					registration = Screen.addResizeHandler(new ResizeHandler()
					{
						public void onResize(ResizeEvent event)
						{
							checkNavigationButtons();
						}
					});
				} 
				else if (registration != null)
				{
					registration.removeHandler();
					registration = null;
				}
			}
		});
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante -
	 *
	 */
	class NavButtonEvtHandler implements MouseDownHandler, MouseUpHandler
	{
		private int adjust;
		private boolean buttonPressed = false;
		private int delta;
		private int incrementalAdjust;
		private int originalIncrementalAdjust;

		NavButtonEvtHandler(int adjust, int incrementalAdjust)
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

	/**
	 * @param nextButtonStyleName
	 */
	public void setNextButtonStyleName(String nextButtonStyleName)
	{
		this.nextButtonStyleName = nextButtonStyleName;
		this.nextButton.setStyleName(this.nextButtonStyleName);
	}

	/**
	 * @param previousButtonStyleName
	 */
	public void setPreviousButtonStyleName(String previousButtonStyleName)
	{
		this.previousButtonStyleName = previousButtonStyleName;
		this.previousButton.setStyleName(this.previousButtonStyleName);
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
