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

import org.cruxframework.crux.core.client.collection.Array;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Classes that implement this interface allow filters on the entire {@link DataProvider} set of data.
 *  
 * @author Thiago da Rosa de Bustamante
 */
public interface FilterableProvider<T> extends DataProvider<T>
{
	/**
	 * Add a new {@link DataFilter} to this {@link DataProvider}. All the filters are considered 
	 * when iterating over the data set. 
	 * @param filter a filter to verify all {@link DataProvider} records
	 * @return a registration handler to enable filter removes.
	 */
	FilterRegistration<T> addFilter(DataFilter<T> filter);

 	/** 
 	 * Remove all filters form the DataProvider
 	 */
 	void removeFilters();

 	/**
 	 * Return a filtered set of data, applying the given filter to the whole set of data.
 	 * This operation does not affect the internal set of data. For this purpose, use addFilter instead.
 	 * @param filter filter to apply
 	 * @return data filtered
 	 */
	Array<T> filter(DataFilter<T> filter);
	  	
 	/**
	 * Add an handler to be called when filter operations are applied
	 * @param handler called when filter perform some operation on data
	 */
	HandlerRegistration addDataFilterHandler(DataFilterHandler<T> handler);
	
	/**
	 * A Filter used to filter data on {@link DataProvider}
	 * @author Thiago da Rosa de Bustamante
	 */
	public interface DataFilter<T>
	{
		/**
		 * Check if the given dataObject should be accepted by the current filter
		 * @param dataObject record object on {@link DataProvider}
		 * @return true if accepted.
		 */
		boolean accept(T dataObject);
	}
	
	/**
	 * A registration handler for this filter. Created when a filter is bound to a {@link DataProvider}
	 * and can be used to remove this link.
	 * @author Thiago da Rosa de Bustamante
	 */
	public interface FilterRegistration<T>
	{
		/**
		 * Remove the bound filter from the parent {@link DataProvider}.
		 */
		void remove();

		/**
		 * Replace the bound filter on parent {@link DataProvider} by the new filter provided. This operation 
		 * will remove the old filter and add the new one in its place. It is most efficient than call remove 
		 * and add directly, because it will fire only one refresh operation.
		 * @param filter the new filter to replace the old one
		 * @param incremetalFiltering If false, after replace the filter, the entire set of data will be revalidated by the new filter.
		 * If true, the new filter will consider the previous filtered data as its input for data filtering, allowing that widgets
		 * make incremental filtering.
		 */
		void replace(DataFilter<T> filter, boolean incremetalFiltering);
	}
}
