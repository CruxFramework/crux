package org.cruxframework.crux.widgets.client.event;

import com.google.gwt.user.client.ui.IsWidget;

public class LoadImagesEvent<T extends IsWidget> extends org.cruxframework.crux.gwt.client.LoadImagesEvent<T>
{
	public LoadImagesEvent(String senderId)
	{
		super(senderId);
	}
}
