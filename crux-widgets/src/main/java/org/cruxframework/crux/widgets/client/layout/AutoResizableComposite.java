/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.layout;

import org.cruxframework.crux.core.client.executor.ThrottleExecutor;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class AutoResizableComposite extends Composite   
{
	/**
	 * What should be done when the container element gets resized
	 * @param containerHeight
	 * @param containerWidth
	 */
	protected abstract void onResize(int containerHeight, int containerWidth);
	
	private ResizeHandler resizeHandler = new ResizeHandler(1000);
	
	/** 
	 * Handles the common actions of resizing a widget 
	 */
	private class ResizeHandler extends ThrottleExecutor 
	{
		public ResizeHandler(int ratio) 
		{
			super(ratio);
		}

		@Override
		protected void doAction() 
		{
			AutoResizableComposite resizable = AutoResizableComposite.this;
			Widget parent = resizable.getParent();
			if(resizable.isVisible())
			{
				resizable.setVisible(false);
				int containerHeight = parent.getElement().getClientHeight();
				int containerWidth = parent.getElement().getClientWidth();
				resizable.onResize(containerHeight, containerWidth);
				resizable.setVisible(true);
			}
		} 
	}
	
	@Override
	protected void initWidget(Widget widget) 
	{
		addAttachHandler(new Handler()
		{
			HandlerRegistration registration;
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					registration = Screen.addResizeHandler(new com.google.gwt.event.logical.shared.ResizeHandler() 
					{
						public void onResize(ResizeEvent event) 
						{
							resizeHandler.throttle();
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
		super.initWidget(widget);
		setStyleName("crux-AutoResizableComposite");
	}	
}