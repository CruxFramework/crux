package org.cruxframework.crux.widgets.client.event.row;

import com.google.gwt.event.shared.EventHandler;

public interface BeforeCancelRowEditionHandler extends EventHandler
{
	void onBeforeCancelRowEdition(BeforeCancelRowEditionEvent event);
}
