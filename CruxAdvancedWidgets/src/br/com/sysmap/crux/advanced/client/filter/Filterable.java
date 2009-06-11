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
package br.com.sysmap.crux.advanced.client.filter;

import java.util.List;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public interface Filterable<T>
{
	List<FilterResult<T>> filter(String query);
	void onSelectItem(T selectedItem);
	
	/**
	 * TODO - Gessé - Comment this
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
	public static class FilterResult<T>
	{
		private T value;
		private String label;
		
		/**
		 * @param label
		 * @param value
		 */
		public FilterResult(T value, String label)
		{
			this.value = value;
			this.label = label;		
		}
		
		/**
		 * @return the label
		 */
		public String getLabel()
		{
			return label;
		}

		/**
		 * @return the value
		 */
		public T getValue()
		{
			return value;
		}	
	}	
}