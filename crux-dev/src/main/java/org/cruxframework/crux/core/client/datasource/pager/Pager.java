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
package org.cruxframework.crux.core.client.datasource.pager;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A pager is a component to navigate on pages of a {@link Pageable} widget.
 * @author Thiago da Rosa de Bustamante
 * @deprecated Use DataProvider instead.
 */
@Deprecated
@Legacy
public interface Pager extends IsWidget, HasPageHandlers, HasVisibility, HasEnabled
{
	/**
	 * Update the pager after a page navigation on pageable datasource.
	 * @param currentPage current page on datasource
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
	<T extends PagedDataSource<?>> Pageable<T> getPageable();
}
