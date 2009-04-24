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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.LoadEvtBind;
import br.com.sysmap.crux.core.client.event.bind.MouseEvtBind;

import com.google.gwt.dom.client.Element;

/**
 * Represents an Image component
 * @author Thiago Bustamante
 */
public class Image extends Component
{
	protected com.google.gwt.user.client.ui.Image imageWidget;
	
	public Image(String id) 
	{
		this(id, new com.google.gwt.user.client.ui.Image());
	}

	public Image(String id, com.google.gwt.user.client.ui.Image widget) 
	{
		super(id, widget);
		this.imageWidget = widget;
	}
	
	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);

		String url = element.getAttribute("_url");
		if (url != null && url.length() > 0)
		{
			setUrl(url);
		}

		String leftStr = element.getAttribute("_leftRect");
		String topStr = element.getAttribute("_topRect");
		String widthStr = element.getAttribute("_widthRect");
		String heightStr = element.getAttribute("_heightRect");
		if (leftStr != null && topStr != null && widthStr != null && heightStr != null)
		{
			setVisibleRect(Integer.parseInt(leftStr),Integer.parseInt(topStr), 
					Integer.parseInt(widthStr), Integer.parseInt(heightStr));
		}
	}
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);

		ClickEvtBind.bindEvent(element, imageWidget, getId());
		MouseEvtBind.bindEvents(element, imageWidget, getId());
		LoadEvtBind.bindLoadEvent(element, imageWidget, getId());
		LoadEvtBind.bindErrorEvent(element, imageWidget, getId());
	}

	/**
	 * Gets the height of the image. When the image is in the unclipped state, the
	 * height of the image is not known until the image has been loaded (i.e. load
	 * event has been fired for the image).
	 * 
	 * @return the height of the image, or 0 if the height is unknown
	 */
	public int getHeight() 
	{
		return imageWidget.getHeight();
	}

	/**
	 * Gets the horizontal co-ordinate of the upper-left vertex of the image's
	 * visibility rectangle. If the image is in the unclipped state, then the
	 * visibility rectangle is assumed to be the rectangle which encompasses the
	 * entire image, which has an upper-left vertex of (0,0).
	 * 
	 * @return the horizontal co-ordinate of the upper-left vertex of the image's
	 *         visibility rectangle
	 */
	public int getOriginLeft() 
	{
		return imageWidget.getOriginLeft();
	}

	/**
	 * Gets the vertical co-ordinate of the upper-left vertex of the image's
	 * visibility rectangle. If the image is in the unclipped state, then the
	 * visibility rectangle is assumed to be the rectangle which encompasses the
	 * entire image, which has an upper-left vertex of (0,0).
	 * 
	 * @return the vertical co-ordinate of the upper-left vertex of the image's
	 *         visibility rectangle
	 */
	public int getOriginTop() 
	{
		return imageWidget.getOriginTop();
	}

	/**
	 * Gets the URL of the image. The URL that is returned is not necessarily the
	 * URL that was passed in by the user. It may have been transformed to an
	 * absolute URL.
	 * 
	 * @return the image URL
	 */
	public String getUrl() 
	{
		return imageWidget.getUrl();
	}

	/**
	 * Gets the width of the image. When the image is in the unclipped state, the
	 * width of the image is not known until the image has been loaded (i.e. load
	 * event has been fired for the image).
	 * 
	 * @return the width of the image, or 0 if the width is unknown
	 */
	public int getWidth() 
	{
		return imageWidget.getWidth();
	}

	/**
	 * Sets the URL of the image to be displayed. If the image is in the clipped
	 * state, a call to this method will cause a transition of the image to the
	 * unclipped state. Regardless of whether or not the image is in the clipped
	 * or unclipped state, a load event will be fired.
	 * 
	 * @param url the image URL
	 */
	public void setUrl(String url) 
	{
		imageWidget.setUrl(url);
	}

	/**
	 * Sets the url and the visibility rectangle for the image at the same time. A
	 * single load event will be fired if either the incoming url or visiblity
	 * rectangle co-ordinates differ from the image's current url or current
	 * visibility rectangle co-ordinates. If the image is currently in the
	 * unclipped state, a call to this method will cause a transition to the
	 * clipped state.
	 * 
	 * @param url the image URL
	 * @param left the horizontal coordinate of the upper-left vertex of the
	 *          visibility rectangle
	 * @param top the vertical coordinate of the upper-left vertex of the
	 *          visibility rectangle
	 * @param width the width of the visibility rectangle
	 * @param height the height of the visibility rectangle
	 */
	public void setUrlAndVisibleRect(String url, int left, int top, int width, int height) 
	{
		imageWidget.setUrlAndVisibleRect(url, left, top, width, height);
	}

	/**
	 * Sets the visibility rectangle of an image. The visibility rectangle is
	 * declared relative to the the rectangle which encompasses the entire image,
	 * which has an upper-left vertex of (0,0). Provided that any of the left,
	 * top, width, and height parameters are different than the those values that
	 * are currently set for the image, a load event will be fired. If the image
	 * is in the unclipped state, a call to this method will cause a transition of
	 * the image to the clipped state. This transition will cause a load event to
	 * fire.
	 * 
	 * @param left the horizontal coordinate of the upper-left vertex of the
	 *          visibility rectangle
	 * @param top the vertical coordinate of the upper-left vertex of the
	 *          visibility rectangle
	 * @param width the width of the visibility rectangle
	 * @param height the height of the visibility rectangle
	 */
	public void setVisibleRect(int left, int top, int width, int height)
	{
		imageWidget.setVisibleRect(left, top, width, height);
	}

}
