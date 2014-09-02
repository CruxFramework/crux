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

import org.cruxframework.crux.core.client.event.BaseEvent;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MeasurableAsyncDataProviderEvent<T> extends BaseEvent<MeasurableAsyncDataProvider<T>>
{
	protected MeasurableAsyncDataProviderEvent(MeasurableAsyncDataProvider<T> source)
    {
	    super(source);
    }
	
	public void setRecordCount(int recordCount)
	{
		getSource().setRecordCount(recordCount);
	}
}
