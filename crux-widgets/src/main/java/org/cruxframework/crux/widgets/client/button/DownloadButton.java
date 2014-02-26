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

import org.cruxframework.crux.core.client.utils.Base64Utils;
import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;

/**
 * A cross device download button, that use touch events on touch enabled devices.
 * @author Samuel Almeida Cardoso
 * 
 * @PartialSupport Depends on:
 * http://caniuse.com/blobbuilder
 * 
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
				fireDownloadWindow(downloadData.getBase64Data(), downloadData.getFilename(), 
						Base64Utils.getMimeTypeFromBase64Data(downloadData.getBase64Data()));
			}
		}
		
		private native void fireDownloadWindow(String data, String name, String mimeType)  /*-{
			var showSave;
			var DownloadAttributeSupport = 'download' in document.createElement('a');
			var BlobBuilder = window.BlobBuilder || window.WebKitBlobBuilder || window.MozBlobBuilder || window.MSBlobBuilder;
			navigator.saveBlob = navigator.saveBlob || navigator.msSaveBlob || navigator.mozSaveBlob || navigator.webkitSaveBlob;
			window.saveAs = window.saveAs || window.webkitSaveAs || window.mozSaveAs || window.msSaveAs;
			
			// Blobs and saveAs (or saveBlob) :
			if (BlobBuilder && (window.saveAs || navigator.saveBlob)) 
			{
				// Currently only IE 10 supports this, but I hope other browsers will also implement the saveAs/saveBlob method eventually.
				showSave = function (data, name, mimetype) {
					data = data.substring(data.indexOf("base64,") + "base64,".length,data.length);

					// Convert from base64 to an ArrayBuffer
					var byteString = atob(data);
					var buffer = new ArrayBuffer(byteString.length);
					var intArray = new Uint8Array(buffer);
					for (var i = 0; i < byteString.length; i++) {
					    intArray[i] = byteString.charCodeAt(i);
					}
					
					// Use the native blob constructor
					var blob = new Blob([buffer], {type: mimeType});
					
					if (!name) name = "Download.bin";
					
					if (window.saveAs) 
					{
						window.saveAs(blob, name);
					} else 
					{
						navigator.saveBlob(blob, name);
					}
				};
			}
			// Blobs and object URLs:
			else if (DownloadAttributeSupport) 
			{
				// Currently WebKit and Gecko support BlobBuilder and object URLs.
				showSave = function (data, name, mimetype) {
					if (!mimetype) mimetype = "application/octet-stream";
					if (DownloadAttributeSupport) {
						// Currently only Chrome (since 14-dot-something) supports the download attribute for anchor elements.
						var link = document.createElement("a");
						link.setAttribute("href",data);
						link.setAttribute("download",name||"Download.bin");
						// Now I need to simulate a click on the link.
						// IE 10 has the better msSaveBlob method and older IE versions do not support the BlobBuilder interface
						// and object URLs, so we don't need the MS way to build an event object here.
						var event = document.createEvent('MouseEvents');
						event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
						link.dispatchEvent(event);
					}
				};
			}
			// data:-URLs:
			else if (!/\bMSIE\b/.test(navigator.userAgent)) 
			{
				// IE does not support URLs longer than 2048 characters (actually bytes), so it is useless for data:-URLs.
				// Also it seems not to support window.open in combination with data:-URLs at all.
				showSave = function (data, name, mimetype) {
					if (!mimetype) mimetype = "application/octet-stream";
					// Again I need to filter the mime type so a download is forced.
					// Note that encodeURIComponent produces UTF-8 encoded text. The mime type should contain
					// the charset=UTF-8 parameter. In case you don't want the data to be encoded as UTF-8
					// you could use escape(data) instead.
					window.open("data:"+mimetype+","+encodeURIComponent(data), '_blank', '');
				};
			}
			
			if (!showSave) 
			{
				alert("Your browser does not support any method of saving JavaScript gnerated data to files.");
				return;
			}
			showSave(data,name,mimeType);
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
