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
package org.cruxframework.crux.core.client.image;

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;

/**
 * Issue submitted to GWT:
 * Issue 8325: 	GWT Image: using 'VisibleRect' method erases previous stylesheets declared
 * 
 * Until version 2.5.1 GWT doesn't copy any old 
 * properties to the new element created inside a Clipped 
 * or NotClipped constructor. So they are lost at some point.
 * 
 * In order to copy the properties I had to use something like: 
 * impl.setProperty(...);
 * for each one of them because the following method 
 * (intended to copy all properties) doesn't work here:
 * getElement().setAttribute("style", currentStyle + oldStyle);
 * 
 * @author samuel.cardoso
 */
public abstract class GWTFixImage 
{
	private Image image;

	/**
	 * @param image the image itself.
	 */
	public GWTFixImage(Image image) 
	{
		this.image = image;
		setVisibleRect();
	}
	
	/**
	 * Indicate how to implement your type of setVisibleRect.
	 */
	public abstract void callHowToImplementInnerSetVisibleRect();
	
	/**
	 * Define how to set the visibleRect.
	 */
	public void setVisibleRect()
	{
		boolean oldStyleHasDisplayNone = StringUtils.unsafeEquals(image.getElement().getStyle().getDisplay(), "none");
		String title = image.getElement().getTitle();
		String styleName = image.getStyleName();
		callHowToImplementInnerSetVisibleRect();
		image.setVisible(!oldStyleHasDisplayNone);
		image.setTitle(title);
		image.setStyleName(styleName);
	}
	
	private native void setStyle(Element element, Style style) /*-{
		element.style = style;
	}-*/;
}
