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
package org.cruxframework.crux.widgets.client.uploader;

import java.util.Iterator;

import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.widgets.client.uploader.event.AddFileHandler;
import org.cruxframework.crux.widgets.client.uploader.event.HasAddFileHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.HasRemoveFileHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.HasUploadCanceledHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.HasUploadCompleteHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.HasUploadErrorHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.HasUploadStartHandlers;
import org.cruxframework.crux.widgets.client.uploader.event.RemoveFileHandler;
import org.cruxframework.crux.widgets.client.uploader.event.UploadCanceledHandler;
import org.cruxframework.crux.widgets.client.uploader.event.UploadCompleteHandler;
import org.cruxframework.crux.widgets.client.uploader.event.UploadErrorHandler;
import org.cruxframework.crux.widgets.client.uploader.event.UploadStartHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * An HTML5 based file uploader widget.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public class FileUploader extends Composite implements HasEnabled, HasAddFileHandlers, 
				HasRemoveFileHandlers, HasUploadStartHandlers, HasUploadErrorHandlers, 
				HasUploadCompleteHandlers, HasUploadCanceledHandlers 
{
	private AbstractFileUploader impl;
	
	/**
	 * Protected Constructor. Use createIfSupported() to instantiate.
	 */
	protected FileUploader()
	{
		impl = GWT.create(AbstractFileUploader.class);
		initWidget(impl);
		setStyleName("crux-FileUploader");
	}

	public String getUrl()
    {
    	return impl.getUrl();
    }

	public void setUrl(String url)
	{
		impl.setUrl(url);
	}
	
	public void setFileInputText(String text)
	{
		impl.setFileInputText(text);
	}
	
	public void setSendButtonText(String text)
	{
		impl.setSendButtonText(text);
	}
	
	public boolean isAutoUploadFiles()
    {
    	return impl.isAutoUploadFiles();
    }

	public void setAutoUploadFiles(boolean autoUploadFiles)
    {
		impl.setAutoUploadFiles(autoUploadFiles);
    }

	public boolean isMultiple()
	{
		return impl.isMultiple();
	}

	public void setMultiple(boolean multiple)
	{
		impl.setMultiple(multiple);
	}

	public Iterator<Blob> iterateFiles()
	{
		return impl.iterateFiles();
	}

	public void uploadFile(String fileName)
	{
		impl.uploadFile(fileName);
	}

	public void uploadFile(String fileName, String url)
	{
		impl.uploadFile(fileName, url);
	}

	public void uploadFile(Blob file, String fileName)
	{
		impl.uploadFile(file, fileName);
	}

	public void uploadFile(final Blob file, String fileName, String url)
	{
		impl.uploadFile(file, fileName, url);
	}

	public void uploadAllFiles()
	{
		impl.uploadAllFiles();
	}
	
	public void uploadAllFiles(String url)
	{
		impl.uploadAllFiles(url);
	}

	public void removeFile(String fileName)
	{
		impl.removeFile(fileName);
	}

	public void clear()
	{
		impl.clear();
	}
	
	public void addFile(Blob file, String fileName)
	{
		impl.addFile(file, fileName);
	}

	public static FileUploader createIfSupported()
	{
		if (isSupported())
		{
			return new FileUploader();
		}
		return null;
	}

	public static boolean isSupported()
	{
		return AbstractFileUploader.isSupported();
	}
	
	public boolean isEnabled() 
	{
		return impl.isEnabled();
	}

	public void setEnabled(boolean enabled) 
	{
		impl.setEnabled(enabled);
	}
	
	public boolean isShowProgressBar() 
	{
		return impl.isShowProgressBar();
	}

	public void setShowProgressBar(boolean showProgressBar) 
	{
		impl.setShowProgressBar(showProgressBar);
	}

	@Override
    public HandlerRegistration addUploadCanceledHandler(UploadCanceledHandler handler)
    {
	    return impl.addUploadCanceledHandler(handler);
    }

	@Override
    public HandlerRegistration addUploadCompleteHandler(UploadCompleteHandler handler)
    {
	    return impl.addUploadCompleteHandler(handler);
    }

	@Override
    public HandlerRegistration addUploadErrorHandler(UploadErrorHandler handler)
    {
	    return impl.addUploadErrorHandler(handler);
    }

	@Override
    public HandlerRegistration addUploadStartHandler(UploadStartHandler handler)
    {
	    return impl.addUploadStartHandler(handler);
    }

	@Override
    public HandlerRegistration addRemoveFileHandler(RemoveFileHandler handler)
    {
	    return impl.addRemoveFileHandler(handler);
    }

	@Override
    public HandlerRegistration addAddFileHandler(AddFileHandler handler)
    {
	    return impl.addAddFileHandler(handler);
    }
	
	public Blob getFile(String fileName)
	{
		return impl.getFile(fileName);
	}
	
}
//TODO tratar resubimssao.... marcar arquivos como enviados...
//TODO por a url como required na factory