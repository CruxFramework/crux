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
