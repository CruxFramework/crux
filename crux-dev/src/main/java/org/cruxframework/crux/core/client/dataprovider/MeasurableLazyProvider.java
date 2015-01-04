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
 * A {@link MeasurablePagedProvider} and {@link LazyProvider}
 * @author Thiago da Rosa de Bustamante
 */
public interface MeasurableLazyProvider<T> extends MeasurablePagedProvider<T>, LazyProvider<T> 																
{
	/**
	 * Inform an {@link LazyDataLoader} to be used to load data 
	 * @param dataLoader loader
	 */
	void setDataLoader(LazyDataLoader<T> dataLoader);
	
	/**
	 * Retrieve the {@link LazyDataLoader} used to load data
	 * @return loader
	 */
	LazyDataLoader<T> getDataLoader();
	
	/**
	 * Inform the size of the {@link DataProvider}
	 * @param recordCount
	 */
	void setSize(int recordCount);
}
