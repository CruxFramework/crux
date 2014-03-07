package org.cruxframework.crux.cruxfaces.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCancelHandlers extends HasHandlers 
{
	HandlerRegistration addCancelHandler(CancelHandler handler);
}

