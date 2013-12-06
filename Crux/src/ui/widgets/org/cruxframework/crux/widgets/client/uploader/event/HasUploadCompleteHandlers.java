package org.cruxframework.crux.widgets.client.uploader.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasUploadCompleteHandlers extends HasHandlers 
{
	HandlerRegistration addUploadCompleteHandler(UploadCompleteHandler handler);
}

