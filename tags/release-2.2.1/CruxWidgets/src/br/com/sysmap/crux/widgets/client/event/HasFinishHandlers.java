package br.com.sysmap.crux.widgets.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasFinishHandlers extends HasHandlers 
{
	HandlerRegistration addFinishHandler(FinishHandler handler);
}

