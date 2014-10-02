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
 * Defines a data loader for {@link EagerProvider}
 * @author Thiago da Rosa de Bustamante
 */
public interface EagerDataLoader<T>
{
	/**
	 * Loader method. Called when the {@link EagerProvider} needs to be loaded.
	 * @param event provides access to the source {@link EagerProvider} that is being loaded
	 */
	void onLoadData(EagerLoadEvent<T> event);
}
