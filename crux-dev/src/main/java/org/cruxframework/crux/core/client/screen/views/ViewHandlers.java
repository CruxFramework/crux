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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.executor.BeginEndExecutor;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewHandlers
{
	private static boolean initialized = false;
	private static FastList<ViewContainer> boundContainers = null;
	private static boolean hasWindowResizeHandler = false;
	private static boolean hasOrientationChangeHandler = false;
	private static boolean hasWindowCloseHandler = false;
	private static boolean hasWindowClosingHandler = false;
	private static boolean hasHistoryHandler = false;
	private static boolean historyFrameInitialized = false;
	private static HandlerRegistration resizeHandler;
	private static HandlerRegistration orientationChangeHandler;
	private static HandlerRegistration closeHandler;
	private static HandlerRegistration closingHandler;
	private static HandlerRegistration historyHandler;

	protected static void initializeWindowContainers()
	{
		if (!initialized)
		{
			boundContainers = new FastList<ViewContainer>();
			initialized = true;
		}
	}

	/**
	 * This method must be called when the container is attached to DOM
	 */
	protected static void bindToDOM(ViewContainer container)
	{
		boundContainers.add(container);
		ensureViewContainerHandlers();
	}
	
	/**
	 * This method must be called when the container is detached from DOM
	 */
	protected static void unbindToDOM(ViewContainer container)
	{
		boundContainers.remove(boundContainers.indexOf(container));
	}
	
	/**
	 * 
	 * @param viewContainer
	 */
	protected static void ensureViewContainerHandlers()
    {
	    ensureViewContainerResizeHandler();
	    ensureViewContainerOrientationChangeHandler();
	    ensureViewContainerCloseHandler();
	    ensureViewContainerClosingHandler();
	    ensureViewContainerHistoryHandler();
    }

	/**
	 * 
	 * @param viewContainer
	 */
	protected static void ensureViewContainerResizeHandler()
    {
	    if (!hasWindowResizeHandler)
	    {
	    	hasWindowResizeHandler = true;
	    	resizeHandler = addWindowResizeHandler(new ResizeHandler()
			{
				@Override
				public void onResize(ResizeEvent event)
				{
					for (int i=0; i< boundContainers.size(); i++)
					{
						boundContainers.get(i).notifyViewsAboutWindowResize(event);
					}
				}
			}, false);
	    }
    }

	/**
	 * 
	 * @param viewContainer
	 */
	protected static void ensureViewContainerHistoryHandler()
    {
	    if (!hasHistoryHandler)
	    {
	    	prepareHistoryFrame();
	    	hasHistoryHandler = true;
	    	historyHandler = History.addValueChangeHandler(new ValueChangeHandler<String>()
			{
				@Override
				public void onValueChange(ValueChangeEvent<String> event)
				{
					for (int i=0; i< boundContainers.size(); i++)
					{
						boundContainers.get(i).notifyViewsAboutHistoryChange(event);
					}
				}
			}); 
	    }
    }

	/**
	 * 
	 * @param viewContainer
	 * @return 
	 */
	protected static void ensureViewContainerOrientationChangeHandler()
    {
	    if (!hasOrientationChangeHandler)
	    {
	    	hasOrientationChangeHandler = true;
	    	orientationChangeHandler = addWindowOrientationChangeHandler(new OrientationChangeCallback()
			{
				@Override
				public void onOrientationChange(String orientation)
				{
					for (int i=0; i< boundContainers.size(); i++)
					{
						boundContainers.get(i).notifyViewsAboutOrientationChange(orientation);
					}
				}
			});
	    }
    }

	/**
	 * 
	 * @param viewContainer
	 */
	protected static void ensureViewContainerCloseHandler()
    {
	    if (!hasWindowCloseHandler)
	    {
	    	hasWindowCloseHandler = true;
	    	closeHandler = Window.addCloseHandler(new CloseHandler<Window>()
			{
				@Override
				public void onClose(CloseEvent<Window> event)
				{
					for (int i=0; i< boundContainers.size(); i++)
					{
						boundContainers.get(i).notifyViewsAboutWindowClose(event);
					}
				}
			});
	    }
    }

	/**
	 * 
	 * @param viewContainer
	 */
	protected static void ensureViewContainerClosingHandler()
    {
	    if (!hasWindowClosingHandler)
	    {
	    	hasWindowClosingHandler = true;
	    	closingHandler = Window.addWindowClosingHandler(new ClosingHandler()
			{
				@Override
				public void onWindowClosing(ClosingEvent event)
				{
					for (int i=0; i< boundContainers.size(); i++)
					{
						boundContainers.get(i).notifyViewsAboutWindowClosing(event);
					}
				}
			});
	    }
    }
	
	/**
	 * @param callbak
	 * @return
	 */
	private static HandlerRegistration addWindowOrientationChangeHandler(final OrientationChangeCallback callbak) 
	{
		final JavaScriptObject orientationHandler = attachOrientationChangeHandler(callbak);
		
		if(orientationHandler == null)
		{
			return null;
		}
		
		return new HandlerRegistration() 
		{
			public void removeHandler() 
			{
				if(orientationHandler != null)
				{
					removeOrientationChangeHandler(orientationHandler);
				}
			}
		};
	}
	
	private abstract static class ResizeBeginEndExecutor extends BeginEndExecutor 
	{
		private ResizeEvent resizeEvent;

		public ResizeBeginEndExecutor(int maxIntervalBetweenStartAndEnd, ResizeEvent resizeEvent) {
			super(maxIntervalBetweenStartAndEnd);
			this.resizeEvent = resizeEvent;
		}

		public ResizeEvent getResizeEvent() 
		{
			return resizeEvent;
		}
		
	};
	
	/**
	 * @param handler
	 * @return
	 */
	private static HandlerRegistration addWindowResizeHandler(final ResizeHandler handler, final boolean lazyCheck) 
	{
		ResizeHandler resizeHandler = new ResizeHandler() 
		{
			public void onResize(ResizeEvent event) 
			{
				if(!lazyCheck)
				{
					handler.onResize(event);
					return;
				}
				
				final ResizeBeginEndExecutor executor = new ResizeBeginEndExecutor(100, event) 
				{
					private int clientHeight = Window.getClientHeight();
					private int clientWidth = Window.getClientWidth();

					@Override
					protected void doEndAction() 
					{
						if (!Screen.getCurrentDevice().equals(Device.largeDisplayMouse))
						{
							int newClientHeight = Window.getClientHeight();
							int newClientWidth = Window.getClientWidth();

							if (this.clientHeight != newClientHeight || clientWidth != newClientWidth || Screen.isIos())
							{
								handler.onResize(getResizeEvent());
							}
							clientHeight = newClientHeight;
							clientWidth  = newClientWidth;
						}
						else
						{
							handler.onResize(getResizeEvent());
						}
					}
					
					@Override
					protected void doBeginAction() 
					{
						// nothing
					}
				};
				
				executor.execute();					
			}
		};
		
		final HandlerRegistration resizeHandlerRegistration = Window.addResizeHandler(resizeHandler);
		
		return new HandlerRegistration() 
		{
			public void removeHandler() 
			{
				if(resizeHandlerRegistration != null)
				{
					resizeHandlerRegistration.removeHandler();
				}
			}
		};
	}
	
	/**
	 * @param orientationHandler
	 */
	private static native void removeOrientationChangeHandler(JavaScriptObject orientationHandler) /*-{
		var orientationEvent = 
			"onorientationchange" in $wnd ? "onorientationchange" :
			"orientationchange"   in $wnd ? "orientationchange"   :
			"ondeviceorientation" in $wnd ? "ondeviceorientation" :
			"deviceorientation"   in $wnd ? "deviceorientation"   : null;
		if (orientationEvent != null)
		{
			$wnd.removeEventListener(orientationEvent, orientationHandler);
		}
	}-*/;
	
	/**
	 * @param executor
	 * @return
	 */
	private static native JavaScriptObject attachOrientationChangeHandler(OrientationChangeCallback handler)/*-{
		if (! @org.cruxframework.crux.core.client.screen.views.View::isOrientationChangeSupported()())
		{
			return null;
		}
		
		var attachEvent,
			eventListener,
			orientationHandler;
	 
		if("onorientationchange" in $wnd || "orientationchange" in $wnd)
		{
			attachEvent = "onorientationchange";
			eventListener = "orientationchange";
			orientationHandler = function() { handler.@org.cruxframework.crux.core.client.screen.views.ViewHandlers.OrientationChangeCallback::onOrientationChange(Ljava/lang/String;)('unknow'); };
			
		} else if("ondeviceorientation" in $wnd || "deviceorientation" in $wnd)
		{
			var delta = 15;
			attachEvent = "ondeviceorientation";
			eventListener = "deviceorientation";
			orientationHandler = function(event) 
			{
				var position = Math.abs(event.gamma);
				if( (position >= -delta) && (position <= delta) )
				{
					$wnd.orientation = 'horizontal';
					if($wnd.orientation !== $wnd.previousOrientation)
					{
						$wnd.previousOrientation = $wnd.orientation;
						handler.@org.cruxframework.crux.core.client.screen.views.ViewHandlers.OrientationChangeCallback::onOrientationChange(Ljava/lang/String;)($wnd.orientation);
					}
				} else if( (position >= (90 - delta) ) && (position <= (90 + delta) ) )
				{
					$wnd.orientation = 'vertical';
					if($wnd.orientation !== $wnd.previousOrientation)
					{
						$wnd.previousOrientation = $wnd.orientation;
						handler.@org.cruxframework.crux.core.client.screen.views.ViewHandlers.OrientationChangeCallback::onOrientationChange(Ljava/lang/String;)($wnd.orientation);
					}
				}
			};
		}
	
		if($wnd.attachEvent) 
		{
			$wnd.attachEvent(attachEvent, orientationHandler);
		} else if($wnd.addEventListener) 
		{
			$wnd.addEventListener(eventListener, orientationHandler, false);
		}
	}-*/;
	
	private static interface OrientationChangeCallback
	{
		void onOrientationChange(String orientation);
	}
	
	/**
	 * 
	 */
	private static void prepareHistoryFrame() 
	{
		if (!historyFrameInitialized)
		{
			Element body = RootPanel.getBodyElement();
			IFrameElement historyFrame = DOM.createIFrame().cast();
			historyFrame.setSrc("javascript:''");
			historyFrame.setId("__gwt_historyFrame");
			historyFrame.getStyle().setProperty("position", "absolute");
			historyFrame.getStyle().setProperty("width", "0");
			historyFrame.getStyle().setProperty("height", "0");
			historyFrame.getStyle().setProperty("border", "0");
			body.appendChild(historyFrame);
		    History.fireCurrentHistoryState();
		    historyFrameInitialized = true;
		}
	}

}
