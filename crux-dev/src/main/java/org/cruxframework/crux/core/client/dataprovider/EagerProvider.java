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

/**
 * A {@link DataProvider} that loads all the data eagerly and store it locally on client browser
 * @author Thiago da Rosa de Bustamante
 */
public interface EagerProvider<T> extends DataProvider<T>
{
	/**
	 * Inform an {@link EagerDataLoader} to be used to load data 
	 * @param dataLoader loader
	 */
	void setDataLoader(EagerDataLoader<T> dataLoader);
	
	/**
	 * Retrieve the {@link EagerDataLoader} used to load data
	 * @return loader
	 */
	EagerDataLoader<T> getDataLoader();
	
	/**
	 * Retrieve all data loaded into this {@link DataProvider}
	 * @return
	 */
	Array<T> getData();
}
