package org.cruxframework.crux.widgets.client.event.row;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeSaveRowEditionHandler extends EventHandler{
	
	void onBeforeSaveRowEdition(BeforeSaveRowEditionEvent event);
}
