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
 * Interface to be implemented by classes that are capable of using DataProviders.  
 * @author Thiago da Rosa de Bustamante
 */
public interface HasDataProvider<T extends DataProvider<?>>
{
	/**
	 * Bind a dataProvider to this component
	 * @param dataProvider dataProvider to use
	 * @param autoLoadData if true fire the dataProvider load event.
	 */
	void setDataProvider(T dataProvider, boolean autoLoadData);
	
	/**
	 * Retrieve the dataProvider bound to this component
	 * @return the dataProvider
	 */
	T getDataProvider();
}
