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
package org.cruxframework.crux.core.client.screen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Classes that implement this interface are rendered differently according to the client device type. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface DeviceAdaptive extends IsWidget
{
	/**
	 * The screen sizes supported by crux
	 * @author Thiago da Rosa de Bustamante
	 */
	public static enum Size{small, large}
	
	/**
	 * The supported features for cross device widgets
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum Input{touch, mouse, arrows}

	/**
	 * All devices supported by Crux CrossDevice engine
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum Device
	{
		largeDisplayMouse(Input.mouse, Size.large), 
		largeDisplayTouch(Input.touch, Size.large), 
		largeDisplayArrows(Input.arrows, Size.large), 
		smallDisplayTouch(Input.touch, Size.small), 
		smallDisplayArrows(Input.arrows, Size.small), 
		all(null, null);
		
		private final Size size;
		private final Input input;

		Device(Input input, Size size)
		{
			this.input = input;
			this.size = size;
		}
		
		public Input getInput()
		{
			return this.input;
		}
		
		public Size getSize()
		{
			return size;
		}
	}
	
	/**
	 * Used to map the all templates used by the target deviceAdaptive Widget
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Templates
	{
		Template[] value();
	}
	
	/**
	 * Used to map the template used by a specific device
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public static @interface Template
	{
		String name();
		Device device();
	}
	
	void setWidth(String width);
	void setVisible(boolean visible);
	boolean isVisible();
	void setStyleName(String style);
	String getStyleName();
	void setTitle(String title);
	String getTitle();
	void setHeight(String height);
	Element getElement();
	HandlerRegistration addAttachHandler(Handler handler);
	<H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type);
}
