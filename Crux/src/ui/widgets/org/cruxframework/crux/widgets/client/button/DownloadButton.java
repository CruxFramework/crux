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
package org.cruxframework.crux.widgets.client.button;

import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * A cross device download button, that use touch events on touch enabled devices.
 * @author Samuel Almeida Cardoso
 *
 */
public class DownloadButton extends Composite implements HasSelectHandlers, HasHTML, HasSafeHtml, HasAllFocusHandlers, HasEnabled
{
	private DownloadButtonCommon impl;
	private Anchor downloadAnchor;
	
	public interface DownloadData
	{
		/**
		 * Base64 data to be downloaded.
		 */
		public abstract String getBase64Data();
		
		/**
		 * The filename with extension.
		 */
		public abstract String getFilename();
		
		/**
		 * The target where it should be rendered the file.
		 */
		public String getTarget();
//		{
//			return "_blank";
//		};
	}
	
	public void fireDownload(DownloadData downloadData) 
	{
		impl.fireDownload(downloadData, downloadAnchor);
	}
	
	public static class DownloadButtonCommon extends Button
	{
		private native void clickElement(Element elem) /*-{
    		elem.click();
    		//elem.onclick.call(elem);
		}-*/;
		
		public void fireDownload(DownloadData downloadData, final Anchor downloadAnchor) 
		{
			if(downloadData != null)
			{
				downloadAnchor.setHref(downloadData.getBase64Data());
				downloadAnchor.setTarget(downloadData.getTarget());
				downloadAnchor.getElement().setAttribute("download", downloadData.getFilename());
				Scheduler.get().scheduleDeferred(new ScheduledCommand() 
				{
					@Override
					public void execute() 
					{
						clickElement(downloadAnchor.getElement());
					}
				});
			}
		}
	}

	public DownloadButton()
	{
		FlowPanel wrapper = new FlowPanel();
		
		downloadAnchor = new Anchor();
		downloadAnchor.setVisible(false);
		
		impl = GWT.create(DownloadButtonCommon.class);
		wrapper.add(impl);
		wrapper.add(downloadAnchor);
		
		initWidget(wrapper);
		setStyleName("crux-DownloadButton");
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return impl.addSelectHandler(handler);
	}

	public void select()
	{
		impl.select();
	}

	@Override
	public String getText()
	{
		return impl.getText();
	}

	@Override
	public void setStyleName(String style) 
	{
		impl.setStyleName(style);
	}
	
	@Override
	public void setText(String text)
	{
		impl.setText(text);
	}

	@Override
	public String getHTML()
	{
		return impl.getHTML();
	}

	@Override
	public void setHTML(String html)
	{
		impl.setHTML(html);
	}

	@Override
	public void setHTML(SafeHtml html)
	{
		impl.setHTML(html);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return impl.addFocusHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addBlurHandler(handler);
	}

	@Override
	public boolean isEnabled()
	{
		return impl.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		impl.setEnabled(enabled);
	}
	
	public void setFocus(boolean focused)
	{
		impl.setFocus(focused);
	}
	
	public void setAccessKey(char key)
	{
		impl.setAccessKey(key);
	}
	
	public void setTabIndex(int index)
	{
		impl.setTabIndex(index);
	}
	
	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}
}
