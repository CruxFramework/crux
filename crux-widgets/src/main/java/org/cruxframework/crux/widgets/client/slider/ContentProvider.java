package org.cruxframework.crux.widgets.client.slider;

import com.google.gwt.user.client.ui.Widget;

public interface ContentProvider 
{
	int size();
	Widget loadWidget(int index);
}
