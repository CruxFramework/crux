package br.com.sysmap.crux.widgets.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasOkHandlers extends HasHandlers 
{
	HandlerRegistration addOkHandler(OkHandler handler);
}

