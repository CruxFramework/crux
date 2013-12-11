package org.cruxframework.crux.widgets.client.uploader.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasUploadErrorHandlers extends HasHandlers 
{
	HandlerRegistration addUploadErrorHandler(UploadErrorHandler handler);
}

