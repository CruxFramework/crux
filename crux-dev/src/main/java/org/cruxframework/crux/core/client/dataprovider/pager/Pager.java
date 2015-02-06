/*
 * Copyright 2014 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.client.dataprovider.pager;

import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A pager is a component to navigate on pages of a {@link Pageable} widget.
 * @author Thiago da Rosa de Bustamante
 */
public interface Pager extends IsWidget, HasPageHandlers, HasVisibility, HasEnabled
{
	/**
	 * Update the pager after a page navigation on pageable DataProvider.
	 * @param currentPage current page on DataProvider
	 * @param isLastPage true if this is the last page
	 */
	void update(int currentPage, boolean isLastPage);
	
	/**
	 * Bind this pager to a Pageable widget 
	 * @param pageable
	 */
	void setPageable(Pageable<?> pageable);
	
	/**
	 * Retrieves the bound pageable
	 * @return the pageable bound to this pager
	 */
	<T extends PagedDataProvider<?>> Pageable<T> getPageable();
	
	/**
	 * Inform if the pager supports that multiple pages are rendered into
	 * the pageable widget. If this method returns false, the pageable widget
	 * will first clear its content panel before render any new page data. If 
	 * it return true, this panel will not be cleared, allowing infinit scrolling.
	 * @return true if infinite scroll is supported
	 */
	boolean supportsInfiniteScroll();

	/**
	 * Allow the pager to prepare itself for a new transaction, starting on the given record
	 * @param startRecord first record on the transaction 
	 */
	void prepareTransaction(int startRecord);
}
