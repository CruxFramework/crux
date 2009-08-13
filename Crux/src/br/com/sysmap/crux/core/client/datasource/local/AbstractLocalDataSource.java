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
package br.com.sysmap.crux.core.client.datasource.local;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.Metadata;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
abstract class AbstractLocalDataSource<T extends DataSourceRecord> implements DataSource<T>
{
	protected Metadata metadata;
	protected T[] data;
	protected int currentRecord = -1;
	protected boolean loaded = false;

	public Metadata getMetadata()
	{
		return metadata;
	}
	
	public Object getValue(String columnName)
	{
		if (currentRecord > -1)
		{
			DataSourceRecord dataSourceRow = data[currentRecord];
			int position = metadata.getColumnPosition(columnName);
			if (position > -1)
			{
				return dataSourceRow.get(position);
			}
		}
		return null;
	}	
	
	public boolean hasNextRecord()
	{
		if (ensureLoaded())
		{
			return (data != null && currentRecord < data.length -1);
		}
		else
		{
			return false;
		}
	}

	public void nextRecord()
	{
		if (hasNextRecord())
		{
			currentRecord++;
		}
	}

	public void reset()
	{
		if(data != null)
		{
			data = null;
		}
		currentRecord = -1;
		loaded = false;
	}
	
	public T getRecord()
	{
		if (ensureLoaded() && currentRecord > -1)
		{
			return data[currentRecord];
		}
		else
		{
			return null;
		}
	}
	
	protected boolean ensureLoaded()
	{
		if (!loaded)
		{
			try
			{
				this.data = loadData();
				loaded = true;
			}
			catch (RuntimeException e)
			{
				//TODO: colocar mensagem
				Crux.getErrorHandler().handleError("", e);
			}
		}
		return loaded;
	}
		
	protected abstract T[] loadData();

}
