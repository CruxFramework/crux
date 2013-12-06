/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.filter;

import java.util.List;

/**
 * Interface for objects (typically widgets) whose contents can be filtered using a textual expression.
 * @author Gesse S. F. Dafe
 */
public interface Filterable<T>
{
	/**
	 * Returns the fiterable's contents that match the query.
	 * @param query
	 * @return
	 */
	List<FilterResult<T>> filter(String query);
	
	/**
	 * A chance for executing some logic when a item is selected after a filtering operation.
	 * @param selectedItem
	 */
	void onSelectItem(T selectedItem);
	
	/**
	 * A single result of a filtering operation. 
	 * @author Gesse S. F. Dafe
	 */
	public static class FilterResult<T>
	{
		private T value;
		private String label;
		private String shortLabel;
		
		/**
		 * Constructor.
		 * @param label A label to be shown in the suggestion panel
		 * @param value The value to be bound to this item
		 * @param shortLabel A short label to be displayed on the filter box when this result is selected
		 */
		public FilterResult(T value, String label, String shortLabel)
		{
			this.value = value;
			this.label = label;
			this.shortLabel = shortLabel;		
		}
		
		/**
		 * Returns the label of the result.
		 * @return the label
		 */
		public String getLabel()
		{
			return label;
		}

		/**
		 * Returns the filterable's item bound in this result.
		 * @return the value
		 */
		public T getValue()
		{
			return value;
		}
		
		public String getShortLabel() 
		{
			return shortLabel;
		}
		
		public void setShortLabel(String shortLabel) 
		{
			this.shortLabel = shortLabel;
		}
	}	
}