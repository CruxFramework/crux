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
package org.cruxframework.crux.crossdevice.client.toptoolbar;

import java.util.Iterator;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.Screen.OrientationChangeOrResizeHandler;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeOpenEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeOpenHandler;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("topToolBarArrowsSmallController")
public class TopToolBarArrowsSmallController extends DeviceAdaptiveController implements TopToolBar
{
	static final int ANIMATION_DURATION = 400;

	protected FlowPanel canvas;
	protected FlowPanel floatPanel;
	protected boolean opened;
	protected Element placeHolder;
	protected FocusPanel grip;
	protected int gripHeight;
	protected boolean alreadySettingPanelPosition = false;
	protected int pos;
	protected int canvasHeight;
	
	@Create
	protected PanelAnimation panelAnimation;

	@Override
    public Widget getWidget(int index)
    {
	    return canvas.getWidget(index);
    }

	@Override
    public int getWidgetCount()
    {
	    return canvas.getWidgetCount();
    }

	@Override
    public int getWidgetIndex(Widget child)
    {
	    return canvas.getWidgetIndex(child);
    }

	@Override
    public boolean remove(int index)
    {
	    return canvas.remove(index);
    }

	@Override
    public void add(Widget w)
    {
		canvas.add(w);
	    w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	    setFloatPanelPosition();
    }

	@Override
    public void clear()
    {
		canvas.clear();
    }

	@Override
    public Iterator<Widget> iterator()
    {
	    return canvas.iterator();
    }

	@Override
    public boolean remove(Widget w)
    {
	    return canvas.remove(w);
    }

	@Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
    {
	    return addHandler(handler, BeforeCloseEvent.getType());
    }

	@Override
    public HandlerRegistration addBeforeOpenHandler(BeforeOpenHandler handler)
    {
	    return addHandler(handler, BeforeOpenEvent.getType());
    }

	@Override
    public HandlerRegistration addOpenHandler(OpenHandler<TopToolBar> handler)
    {
	    return addHandler(handler, OpenEvent.getType());
    }

	@Override
    public HandlerRegistration addCloseHandler(CloseHandler<TopToolBar> handler)
    {
	    return addHandler(handler, CloseEvent.getType());
    }

	@Override
    public void setGripWidget(Widget widget)
    {
		Widget gripWidget = grip.getWidget();
		if (gripWidget != null)
		{
			grip.remove(gripWidget);
		}
		grip.add(widget);
    }

	@Override
    public Widget getGripWidget()
    {
	    return grip.getWidget();
    }

	@Override
    public void close()
    {
	    if (this.opened)
	    {
	    	BeforeCloseEvent event = BeforeCloseEvent.fire(this);
	    	if (!event.isCanceled())
	    	{
	    		doClose();
	    		CloseEvent.fire(this, this);
	    	}
		}
    }

	@Override
    public void open()
    {
	    if (!this.opened)
	    {
	    	BeforeOpenEvent event = BeforeOpenEvent.fire(this);
	    	if (!event.isCanceled())
	    	{
	    		doOpen();
	    		OpenEvent.fire(this, this);
	    	}
	    }
    }
	
	@Expose
	@Override
    public void toggle()
    {
	    if (this.opened)
	    {
	    	close();
	    }
	    else
	    {
	    	open();
	    }
    }

	@Override
    public void setGripHeight(int height)
    {
		gripHeight = height;
		grip.setHeight(height+"px");
		setFloatPanelPosition();
    }

	@Override
    public int getGripHeight()
    {
	    return gripHeight;
    }	

	@Override
	protected void init()
	{
		RootPanel.get().add(this);
		canvas = getChildWidget("canvas");
		grip =  getChildWidget("grip");
		prepareGripPanel();
		floatPanel = getChildWidget("topToolBarFloatingPanel");
		panelAnimation.prepareElement(floatPanel.getElement());
		createPlaceHolderPanel();
		Screen.addOrientationChangeOrResizeHandler(new OrientationChangeOrResizeHandler() 
		{
			@Override
			public void onOrientationChangeOrResize() 
			{
				setFloatPanelPosition();
			}
		});
	}
	
	protected void prepareGripPanel()
    {
    }
	
	protected void setFloatPanelPosition()
    {
		if (!alreadySettingPanelPosition)
		{
			alreadySettingPanelPosition = true;
			setPanelDefaultPosition();
			floatPanel.getElement().getStyle().setTop(-1000, Unit.PX);

			new Timer()
			{
				@Override
				public void run()
				{
					final int closedPosition = -floatPanel.getOffsetHeight();
					floatPanel.getElement().getStyle().setTop(closedPosition, Unit.PX);
					new Timer()
					{
						@Override
						public void run()
						{
							int gripHeight = - grip.getAbsoluteTop();
							placeHolder.getStyle().setHeight(gripHeight, Unit.PX);
							floatPanel.getElement().getStyle().setTop(closedPosition + gripHeight , Unit.PX);
							alreadySettingPanelPosition = false;
							canvasHeight = (-closedPosition) - gripHeight;
						}

					}.schedule(1);
				}
			}.schedule(200);
		}
    }
	
	protected void createPlaceHolderPanel()
    {
	    placeHolder = DOM.createDiv();
	    Document.get().getBody().insertFirst(placeHolder);
    }

	@Override
    protected void initWidgetDefaultStyleName()
    {
		setStyleName("xdev-TopToolBar");
    }

	@Override
    protected void applyWidgetDependentStyleNames()
    {
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.small.toString());
    }

	protected Widget prepareGripWidget(Widget widget)
    {
		return widget;
    }

	protected void doOpen()
    {
		setPosition(this.canvasHeight);
    }
	
	protected void doClose()
    {
	    setPosition(0);
    }
	
	protected void setPanelDefaultPosition()
	{
		if (this.opened)
		{
			this.pos = 0;
			panelAnimation.setDefaultPosition(floatPanel.getElement(), this.canvasHeight);
			this.opened = false;
		}
	}
	
	protected void setPosition(int pos)
	{
		this.pos = pos;
		panelAnimation.changePosition(floatPanel.getElement(), pos, this.canvasHeight);
		if (this.pos == this.canvasHeight) 
		{
			this.opened = true;
		}
		else if (this.pos == 0) 
		{
			this.opened = false;
		}
	}
	
	static interface PanelAnimation
	{
		void changePosition(Element elem, int pos, int canvasHeight);
		void setDefaultPosition(Element element, int canvasHeight);
		void prepareElement(Element elem);
	}
	
	static class WebkitPanelAnimation implements PanelAnimation
	{
		public native void changePosition(Element elem, int pos, int canvasHeight)/*-{
			elem.style.webkitTransform = 'translate3d(0,' + pos + 'px,0)';
		}-*/;
			
		@Override
		public void setDefaultPosition(final Element elem, int canvasHeight){
	        setStyleTransitionDuration(elem, 0);
	        changePosition(elem, 0, canvasHeight);
	        Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					setStyleTransitionDuration(elem, ANIMATION_DURATION);
				}
			});
		};

		@Override
        public void prepareElement(Element elem)
        {
	        setStyleTransition(elem);
	        setStyleTransitionDuration(elem, ANIMATION_DURATION);
        }

		protected native void setStyleTransition(Element elem)/*-{
			elem.style.webkitTransitionProperty = '-webkit-transform';
		}-*/;
		
		protected native void setStyleTransitionDuration(Element elem, int duration)/*-{
			elem.style.webkitTransitionDuration = duration+'ms';
		}-*/;
	}
	
	static class JSPanelAnimation implements PanelAnimation
	{
		@Override
        public void setDefaultPosition(Element elem, int canvasHeight)
        {
			JSAnimation animation = getAnimation(elem, 0, canvasHeight);
			animation.onComplete();
        }

		public void changePosition(Element elem, int pos, int canvasHeight)
		{
			JSAnimation animation = getAnimation(elem, pos, canvasHeight);
			animation.run(ANIMATION_DURATION);
		}
			
		@Override
        public void prepareElement(Element elem)
        {
        }

		private JSAnimation getAnimation(Element elem, int pos, int canvasHeight)
        {
	        JSAnimation animation;
			if (pos == canvasHeight)
			{
				animation = new JSAnimation(elem, pos);
			}
			else
			{
				animation = new JSAnimation(elem, -canvasHeight);
			}
	        return animation;
        }

		private static class JSAnimation extends Animation
		{
			private int delta;
			private int initialPos;
			private Style style;

			public JSAnimation(Element elem, int delta)
            {
				this.initialPos = elem.getAbsoluteTop();
				this.delta = delta;
				this.style = elem.getStyle();
            }
			
			@Override
			protected void onUpdate(double progress)
			{
				style.setTop(initialPos+(delta*progress), Unit.PX);
			}
			
			@Override
			protected void onComplete()
			{
			    onUpdate(1);
			}
		}
	}
}
