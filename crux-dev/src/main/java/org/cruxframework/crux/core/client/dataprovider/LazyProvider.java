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

import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;


/**
 * A {@link DataProvider} that loads the data lazily. Every time a new paged not loaded yet is needed
 * fecthData is called to allow the  loading of that fragment of data 
 * @author Thiago da Rosa de Bustamante
 */
public interface LazyProvider<T> extends PagedDataProvider<T>
{
	/**
	 * Method called to bind some data to the DataProvider
	 * @param data fragment data
	 * @param startRecord the start position of the fragment
	 */
	void setData(T[] data, int startRecord);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data fragment data
	 * @param startRecord the start position of the fragment
	 */
	void setData(List<T> data, int startRecord);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data fragment data
	 * @param startRecord the start position of the fragment
	 */
	void setData(Array<T> data, int startRecord);
	
}
