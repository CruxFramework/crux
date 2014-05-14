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

import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.core.client.file.DownloadWindow;
import org.cruxframework.crux.core.client.utils.Base64Utils;
import org.cruxframework.crux.core.client.utils.FileUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.anchor.Anchor;
import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;

/**
 * A cross device download button, that use touch events on touch enabled devices.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * 
 * @PartialSupport Depends on:
 * http://caniuse.com/bloburls
 * 
 * nice reference:
 * http://hackworthy.blogspot.com.br/2012/05/savedownload-data-generated-in.html
 */
public class DownloadButton extends Composite implements HasText, HasEnabled, HasSelectHandlers
{
	private DownloadButtonAll impl;
	
	public interface DownloadInfo
	{
		/**
		 * Base64 data to be downloaded.
		 */
		public abstract String getBase64Data();
		
		/**
		 * The filename with extension.
		 */
		public abstract String getFilename();
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.button.DownloadButton.DownloadInfo
	 */
	@Deprecated
	public interface DownloadData extends DownloadInfo
	{
		/**
		 * The target where it should be rendered the file.
		 */
		public String getTarget();
	}
	
	public void fireDownload(DownloadInfo downloadData) 
	{
		impl.fireDownload(downloadData);
	}
	
	public static class DownloadButtonAll extends Button
	{
		public void fireDownload(DownloadInfo downloadData) 
		{
		}
	}
	
	public static class DownloadButtonBlob extends DownloadButtonAll
	{
		@Override
		public void fireDownload(DownloadInfo downloadData) 
		{
			if(downloadData != null)
			{
				fireWindowDownload(downloadData.getBase64Data(), downloadData.getFilename(), 
						Base64Utils.getMimeTypeFromBase64Data(downloadData.getBase64Data()));
			}
		}
		
		private void fireWindowDownload(String base64Data, String filename, String mimeTypeFromBase64Data) 
		{
			String newFilename = StringUtils.isEmpty(filename) ? "download.bin" : filename;
			String newMimeType = StringUtils.isEmpty(mimeTypeFromBase64Data) ? "application/octet-stream" : mimeTypeFromBase64Data;
			
			if(Blob.isSupported() && DownloadWindow.isSupported())
			{
				Blob blob = FileUtils.fromDataURI(base64Data);
				DownloadWindow.createIfSupported().openSaveAsWindow(blob, newFilename);
			} else if(hasHTML5DownloadAttributeSupport())
			{
				Anchor a = new Anchor();
				a.setHref(base64Data);
				a.setTarget("_blank");
				a.getElement().setAttribute("download", newFilename);
				clickElement(a.getElement());
			} else
			{
				// Note that encodeURIComponent produces UTF-8 encoded text. The mime type should contain
				// the charset=UTF-8 parameter. In case you don't want the data to be encoded as UTF-8
				// you could use escape(data) instead.
				// Javascript: window.open("data:"+mimetype+","+encodeURIComponent(data), '_blank', '');
				// Is it working? If is not, read:
				//http://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu
				Window.open("data:"+newMimeType, URL.encodeQueryString(Base64Utils.ensurePlainBase64(base64Data)), "_blank");
			}
		}
		
		private native void clickElement(Element element) /*-{
			var event = document.createEvent('MouseEvents');
			event.initMouseEvent('click', true, true, $wnd, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
			element.dispatchEvent(event);
		}-*/;

		private native boolean hasHTML5DownloadAttributeSupport() /*-{
			return 'download' in document.createElement('a');
		}-*/;
	}
	
	public DownloadButton()
	{
		impl = GWT.create(DownloadButtonAll.class);
		initWidget(impl);
		setStyleName("crux-DownloadButton");
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
	public boolean isEnabled()
	{
		return impl.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		impl.setEnabled(enabled);
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler) 
	{
		return impl.addSelectHandler(handler);
	}
	
	protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		impl.setPreventDefaultTouchEvents(preventDefaultTouchEvents);
	}
}
