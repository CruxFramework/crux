package org.cruxframework.crux.smartfaces.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCancelHandlers extends HasHandlers 
{
	HandlerRegistration addCancelHandler(CancelHandler handler);
}

