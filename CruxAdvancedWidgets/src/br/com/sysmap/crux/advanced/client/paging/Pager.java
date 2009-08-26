package br.com.sysmap.crux.advanced.client.paging;

import br.com.sysmap.crux.advanced.client.event.paging.HasPageHandlers;


public interface Pager extends HasPageHandlers
{
	void update(int currentPage, boolean isLastPage);
}
