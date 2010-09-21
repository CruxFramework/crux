/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.filter;

import java.util.List;

/**
 * Interface for objects (typically widgets) whose contents can be filtered using a textual expression.
 * @author Gessé S. F. Dafé
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
	 * @author Gessé S. F. Dafé
	 */
	public static class FilterResult<T>
	{
		private T value;
		private String label;
		
		/**
		 * Constructor.
		 * @param label
		 * @param value
		 */
		public FilterResult(T value, String label)
		{
			this.value = value;
			this.label = label;		
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
	}	
}