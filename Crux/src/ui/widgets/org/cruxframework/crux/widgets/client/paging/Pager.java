package org.cruxframework.crux.widgets.client.paging;

import org.cruxframework.crux.widgets.client.event.paging.HasPageHandlers;


public interface Pager extends HasPageHandlers
{
	void update(int currentPage, boolean isLastPage);
	void setPageable(Pageable pageable);
}
