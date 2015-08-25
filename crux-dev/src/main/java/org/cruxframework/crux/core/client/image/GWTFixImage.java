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

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeUri;
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
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public abstract class GWTFixImage 
{
	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * This implementation is to avoid ...
	 */
	public interface ConditionalImageRenderer
	{
		/**
		 * @param image the image itself.
		 * @param url the url to set.
		 * @param left position.
		 * @param top position.
		 * @param width width.
		 * @param height height.
		 */
		public void renderImage(com.google.gwt.user.client.ui.Image image, 
				String url, int left, int top, int width, int height);
		
		/**
		 * @param image the image itself.
		 * @param left position.
		 * @param top position.
		 * @param width width.
		 * @param height height.
		 */
		public void renderImage(com.google.gwt.user.client.ui.Image image, 
				int left, int top, int width, int height);
		
		/**
		 * @param image the image itself.
		 * @param url the url to set.
		 * @param left position.
		 * @param top position.
		 * @param width width.
		 * @param height height.
		 */
		public void renderImage(com.google.gwt.user.client.ui.Image image, 
				SafeUri url, int left, int top, int width, int height);
	}

	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * For IE8.
	 */
	public static class IE8ConditionalImageRenderer implements ConditionalImageRenderer
	{
		@Override
		public void renderImage(final com.google.gwt.user.client.ui.Image image, final 
				String url, int left, int top, int width, int height) 
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			{
				@Override
				public void execute() 
				{
					new CopyImageProperties(image) 
					{
						@Override
						public void callHowToImplementInnerSetVisibleRect() 
						{
							image.setUrl(Screen.rewriteUrl(url));		
						}
					};
				}
			});
		}

		@Override
		public void renderImage(final Image image, final SafeUri url, int left, int top,
				int width, int height) 
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			{
				@Override
				public void execute() 
				{
					new CopyImageProperties(image) 
					{
						@Override
						public void callHowToImplementInnerSetVisibleRect() 
						{
							image.setUrl(url);		
						}
					};
				}
			});
		}

		@Override
		public void renderImage(final Image image, final int left, final int top, final int width,
				final int height) 
		{
			new CopyImageProperties(image) 
			{
				@Override
				public void callHowToImplementInnerSetVisibleRect() 
				{
					image.setVisibleRect(left, top, width, height);		
				}
			};
		}
	}

	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * For all other browsers.
	 */
	public static class DefaultConditionalImageRenderer implements ConditionalImageRenderer
	{
		@Override
		public void renderImage(final com.google.gwt.user.client.ui.Image image, 
				final String url, final int left, final int top, final int width, final int height)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			{
				@Override
				public void execute() 
				{
					new CopyImageProperties(image) 
					{
						@Override
						public void callHowToImplementInnerSetVisibleRect() 
						{
							image.setUrlAndVisibleRect(url, left, top, width, height);		
						}
					};
				}
			});
		}

		@Override
		public void renderImage(final Image image, final SafeUri url, final int left, final int top,
				final int width, final int height) 
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand() 
			{
				@Override
				public void execute() 
				{
					new CopyImageProperties(image) 
					{
						@Override
						public void callHowToImplementInnerSetVisibleRect() 
						{
							image.setUrlAndVisibleRect(url, left, top, width, height);
						}
					};
				}
			});
		}
		
		@Override
		public void renderImage(final Image image, final int left, final int top, final int width,
				final int height) 
		{
			new CopyImageProperties(image) 
			{
				@Override
				public void callHowToImplementInnerSetVisibleRect() 
				{
					image.setVisibleRect(left, top, width, height);		
				}
			};
		}
	}
	
	private abstract static class CopyImageProperties
	{
		private Image image;

		/**
		 * @param image the image itself.
		 * This fix is to avoid a GWT Image limitation described as:
		 * If an image transitions between clipped mode and unclipped mode, any 
		 * Element-specific attributes added by the user (including style attributes, 
		 * style names, and style modifiers), except for event listeners, will be lost. 
		 */
		public CopyImageProperties(Image image) 
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
}
