package org.cruxframework.crux.widgets.client.paging;

import org.cruxframework.crux.widgets.client.event.paging.HasPageHandlers;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;


public interface Pager extends IsWidget, HasPageHandlers, HasVisibility, HasEnabled
{
	void update(int currentPage, boolean isLastPage);
	void setPageable(Pageable pageable);
}
