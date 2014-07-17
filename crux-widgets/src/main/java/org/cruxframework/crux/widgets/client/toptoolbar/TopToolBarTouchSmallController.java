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
package org.cruxframework.crux.widgets.client.toptoolbar;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.ioc.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("topToolBarTouchSmallController")
public class TopToolBarTouchSmallController extends TopToolBarArrowsSmallController 
{
	@Inject
	protected GripHandler gripHandler;
	
	public void setGripHandler(GripHandler gripHandler)
    {
    	this.gripHandler = gripHandler;
    }

	@Override
	protected void prepareGripPanel()
	{
		gripHandler.prepare(grip, this);
	}
	
	public static class GripHandler 
	{
		public void prepare(FocusPanel grip, final TopToolBarTouchSmallController controller)
		{
			grip.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					controller.toggle();
				}
			});
		}
	}
	
	static class WebkitGripHandler extends GripHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private int startPos;
		private int startDelta;
		private TopToolBarTouchSmallController controller;
		private FocusPanel grip;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;
		
		@Override
		public void prepare(FocusPanel grip, final TopToolBarTouchSmallController controller)
		{
			this.grip = grip;
			this.controller = controller;			
			grip.addTouchStartHandler(this);
		}
		
		public void onTouchStart(TouchStartEvent event)
		{
			blockBodyTouchEvent();
			event.preventDefault();
			event.stopPropagation();
			setStyleTransitionDuration(controller.floatPanel.getElement(), 0);
			this.startPos = controller.pos;
			this.startDelta = event.getTouches().get(0).getPageY() - controller.pos;
			touchMoveHandler = grip.addTouchMoveHandler(this);
			touchEndHandler = grip.addTouchEndHandler(this);
		}

		public void onTouchMove(TouchMoveEvent event)
		{
			int deltaY = event.getTouches().get(0).getPageY() - this.startDelta;
			if (deltaY < 0) 
			{
				deltaY = 0;
			} 
			else if (deltaY > controller.canvasHeight) 
			{
				deltaY = controller.canvasHeight;
			}

			controller.setPosition(deltaY);
		}
		
		public void onTouchEnd(TouchEndEvent event)
		{
			int strokeLength = controller.pos - this.startPos;
			strokeLength *= strokeLength < 0 ? -1 : 1;
			
			if (strokeLength > 3) 
			{		// It seems that on Android is almost impossibile to have a tap without a minimal shift, 3 pixels seems a good compromise
				setStyleTransitionDuration(controller.floatPanel.getElement(), 200);
				if (controller.pos==controller.canvasHeight || !controller.opened) 
				{
					controller.setPosition(controller.pos > controller.canvasHeight/3 ? controller.canvasHeight : 0);
				} 
				else 
				{
					controller.setPosition(controller.pos > controller.canvasHeight ? controller.canvasHeight : 0);
				}
			} 
			else 
			{
				setStyleTransitionDuration(controller.floatPanel.getElement(), ANIMATION_DURATION);
				controller.setPosition(!controller.opened ? controller.canvasHeight : 0);
			}

			if(touchMoveHandler != null)
			{
				touchMoveHandler.removeHandler();
			}
			if(touchEndHandler != null)
			{
				touchEndHandler.removeHandler();
			}
			unblockBodyTouchEvent();
		}

		protected native void setStyleTransitionDuration(Element elem, int duration)/*-{
			elem.style.webkitTransitionDuration = duration+'ms';
		}-*/;
		
		protected native void blockBodyTouchEvent()/*-{
			$wnd._move = function(e) {
				e.preventDefault(); 
				e.stopPropagation();
			};
			$doc.addEventListener('touchmove', $wnd._move);
		}-*/;
		
		protected native void unblockBodyTouchEvent()/*-{
			$doc.removeEventListener('touchmove', $wnd._move);
		}-*/;
	}
}
