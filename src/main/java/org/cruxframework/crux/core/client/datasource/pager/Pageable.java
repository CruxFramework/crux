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

import org.cruxframework.crux.core.client.datasource.HasDataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Define a base interface for classes that are capable to be paged by a {@link Pager}.  
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Pageable<T extends PagedDataSource<?>> extends HasDataSource<T>, IsWidget
{
	/**
	 * Moves the pageable's cursor to the next page 
	 */
	void nextPage();
	
	/**
	 * Moves the pageable's cursor to the previous page 
	 */	
	void previousPage();
	
	/**
	 * Return the total number of pages
	 * @return number of pages, -1 if unknown.
	 */
	int getPageCount();
	
	/**
	 * Sets the pager for call back
	 * @param pager
	 */
	void setPager(Pager pager);
	
	/**
	 * Moves the pageable's cursor to an arbitrary page
	 * @param page page number
	 */
	void goToPage(int page);
	
	/**
	 * Checks if pageable data is already available
	 * @param page
	 */
	boolean isDataLoaded();	
}
