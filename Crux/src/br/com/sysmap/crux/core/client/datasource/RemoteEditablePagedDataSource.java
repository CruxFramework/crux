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

import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord.EditableDataSourceRecordState;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class RemoteEditablePagedDataSource extends AbstractRemotePagedDataSource<EditableDataSourceRecord, EditableDataSourceRecord>
										   implements EditablePagedDataSource
{	
	protected EditableDataSourceOperations<EditableDataSourceRecord> editableOperations = 
		new EditableDataSourceOperations<EditableDataSourceRecord>(this);

	@Override
	protected EditableDataSourceRecord[] createDataObject(int count)
	{
		return new EditableDataSourceRecord[count];
	}

	@Override
	public void fetch(int startPageRecord, int endPageRecord)
	{
		super.fetch(startPageRecord, endPageRecord);
	}
	
	@Override
	public boolean nextPage()
	{
		checkChanges();
		return super.nextPage();
	}

	@Override
	public boolean previousPage()
	{
		checkChanges();
		return super.previousPage();
	}
	
	@Override
	public boolean setCurrentPage(int pageNumber)
	{
		if (this.currentPage != pageNumber)
		{
			checkChanges();
		}
		return super.setCurrentPage(pageNumber);
	}
	
	@Override
	public void firstRecord()
	{
		if (currentPage != 1)
		{
			checkChanges();
		}
		super.firstRecord();
	}
	
	@Override
	public void lastRecord()
	{
		if (currentPage != getPageCount())
		{
			checkChanges();
		}
		super.lastRecord();
	}
	
	@Override
	protected int getPageEndRecord()
	{
		int endPageRecord = super.getPageEndRecord();
		return endPageRecord + editableOperations.getNewRecordsCount() - editableOperations.getRemovedRecordsCount();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		editableOperations.reset();
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#insertRecord(int)
	 */
	public EditableDataSourceRecord insertRecord(int index)
	{
		return editableOperations.insertRecord(index);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#removeRecord(int)
	 */
	public EditableDataSourceRecord removeRecord(int index)
	{
		return editableOperations.removeRecord(index);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#updateState(br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord, br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord.EditableDataSourceRecordState)
	 */
	public void updateState(EditableDataSourceRecord record, EditableDataSourceRecordState previousState)
	{
		editableOperations.updateState(record, previousState);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getNewRecords()
	 */
	public EditableDataSourceRecord[] getNewRecords()
	{
		return editableOperations.getNewRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getRemovedRecords()
	 */
	public EditableDataSourceRecord[] getRemovedRecords()
	{
		return editableOperations.getRemovedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getUpdatedRecords()
	 */
	public EditableDataSourceRecord[] getUpdatedRecords()
	{
		return editableOperations.getUpdatedRecords();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getSelectedRecords()
	 */
	public EditableDataSourceRecord[] getSelectedRecords()
	{
		return editableOperations.getSelectedRecords();
	}	
	
	private void checkChanges()
	{
		if (editableOperations.isDirty())
		{
			throw new DataSoureExcpetion();//TODO: mensagem
		}
	}	
}
