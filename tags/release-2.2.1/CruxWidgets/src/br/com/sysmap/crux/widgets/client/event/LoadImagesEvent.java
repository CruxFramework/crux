package br.com.sysmap.crux.widgets.client.event;

import com.google.gwt.user.client.ui.Widget;

public class LoadImagesEvent<T extends Widget> extends br.com.sysmap.crux.gwt.client.LoadImagesEvent<T>
{
	public LoadImagesEvent(String senderId)
	{
		super(senderId);
	}
}
