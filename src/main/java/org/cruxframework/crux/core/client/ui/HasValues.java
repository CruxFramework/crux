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
package org.cruxframework.crux.core.client.ui;

import java.util.List;

/**
 * This interface is used to inform that a component can have more than a single value
 * 
 * @author wesley.diniz
 *
 * @param <V> Value type
 */
public interface HasValues<V>
{
	/** 
	 * @return A collection of T objects 
	 */
	List<V> getValues();
	/**
	 * @param values A collection of T objects
	 */
	void setValues(List<V> values);
}
