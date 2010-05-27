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
package br.com.sysmap.crux.core.client.datasource;

import br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataSourceAsyncCallbackAdapter<T> extends AsyncCallbackAdapter<T[]>
{
	public DataSourceAsyncCallbackAdapter(Object dataSource)
	{
		super(dataSource);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter#onComplete(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onComplete(T[] result)
	{
		if (this.caller instanceof RemoteDataSource)
		{
			((RemoteDataSource<?, T>)this.caller).updateData(result);
		}
		else
		{
			((LocalDataSource<?, T>)this.caller).updateData(result);
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.rpc.AsyncCallbackAdapter#onError(java.lang.Throwable)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onError(Throwable e)
	{
		if (this.caller instanceof RemoteDataSource)
		{
			((RemoteDataSource<?, T>)this.caller).cancelFetching();
		}
		
		super.onError(e);
	}

}
