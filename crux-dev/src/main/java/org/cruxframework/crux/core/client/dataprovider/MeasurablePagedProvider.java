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
package org.cruxframework.crux.core.client.dataprovider;


/**
 * A {@link MeasurableLazyProvider} and {@link PagedDataProvider}
 * @author Thiago da Rosa de Bustamante
 */
public interface MeasurablePagedProvider<T> extends PagedDataProvider<T>, MeasurableProvider<T>
{
	/**
	 * Retrieve the number of pages available
	 * @return number of pages
	 */
	int getPageCount();
	
	/**
	 * Points the {@link DataProvider} to the given page
	 * @param pageNumber
	 * @return
	 */
	boolean setCurrentPage(int pageNumber);
}