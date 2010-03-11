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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord.EditableDataSourceRecordState;


/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
class EditableDataSourceOperations<E>
{
	protected List<EditableDataSourceRecord> newRecords = new ArrayList<EditableDataSourceRecord>();
	protected List<EditableDataSourceRecord> removedRecords = new ArrayList<EditableDataSourceRecord>();
	protected List<EditableDataSourceRecord> changedRecords = new ArrayList<EditableDataSourceRecord>();
	protected List<EditableDataSourceRecord> selectedRecords = new ArrayList<EditableDataSourceRecord>();	

	protected AbstractScrollableDataSource<EditableDataSourceRecord, E> editableDataSource;
	
	public EditableDataSourceOperations(AbstractScrollableDataSource<EditableDataSourceRecord, E> editableDataSource)
	{
		this.editableDataSource = editableDataSource;
	}
	

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#insertRecord(int)
	 */
	public EditableDataSourceRecord insertRecord(int index)
	{
		this.editableDataSource.ensureLoaded();
		checkRange(index);
		EditableDataSourceRecord record = new EditableDataSourceRecord((EditableDataSource)this.editableDataSource, 
																		"_newRecord"+newRecords.size());
		record.setCreated(true);
		insertDataRecord(index, record);
		newRecords.add(record);
		return record;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#removeRecord(int)
	 */
	public EditableDataSourceRecord removeRecord(int index)
	{
		this.editableDataSource.ensureLoaded();
		checkRange(index);
		EditableDataSourceRecord record = this.editableDataSource.data[index];
		EditableDataSourceRecordState previousState = record.getCurrentState();
		record.setRemoved(true);
		removeDataRecord(index);
		updateState(record, previousState);
		return record;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#updateState(br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord, br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord.EditableDataSourceRecordState)
	 */
	public void updateState(EditableDataSourceRecord record, EditableDataSourceRecordState previousState)
	{
		this.editableDataSource.ensureLoaded();
		if (record.isCreated())
		{
			if (record.isRemoved())
			{
				newRecords.remove(record);
			}
		}
		else if (record.isRemoved())
		{
			if (!previousState.isRemoved())
			{
				removedRecords.add(record);
				if (previousState.isDirty())
				{
					changedRecords.remove(record);
				}
			}
		}
		else if (record.isDirty() && !previousState.isDirty())
		{
			changedRecords.add(record);
		}
		
		if (record.isSelected() && !previousState.isSelected())
		{
			selectedRecords.add(record);
		}
		else if (!record.isSelected() && previousState.isSelected())
		{
			selectedRecords.remove(record);
		}
	}
	
	/**
	 * @param index
	 * @param record
	 */
	protected void insertDataRecord(int index, EditableDataSourceRecord record)
	{
		int size = this.editableDataSource.data.length;
		this.editableDataSource.data = copyOf(this.editableDataSource.data, size+1);
		System.arraycopy(this.editableDataSource.data, index, this.editableDataSource.data, index + 1, size - index);
		this.editableDataSource.data[index] = record;
	}
	
	/**
	 * @param index
	 */
	protected void removeDataRecord(int index)
	{
		int size = this.editableDataSource.data.length;
		int numMoved = size - index - 1;
		if (numMoved > 0)
		    System.arraycopy(this.editableDataSource.data, index+1, this.editableDataSource.data, index, numMoved);
		this.editableDataSource.data = copyOf(this.editableDataSource.data, size-1);
	}

	/**
	 * 
	 * @param original
	 * @param newLength
	 * @return
	 */
	protected EditableDataSourceRecord[] copyOf(EditableDataSourceRecord[] original, int newLength)
	{
		EditableDataSourceRecord[] copy = new EditableDataSourceRecord[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}
	
	/**
	 * @param index
	 */
	protected void checkRange(int index)
	{
		if (index < 0 || index >= this.editableDataSource.data.length)
		{
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getNewRecords()
	 */
	public EditableDataSourceRecord[] getNewRecords()
	{
		return newRecords.toArray(new EditableDataSourceRecord[0]);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getRemovedRecords()
	 */
	public EditableDataSourceRecord[] getRemovedRecords()
	{
		return removedRecords.toArray(new EditableDataSourceRecord[0]);
	}
	
	/**
	 * @return
	 */
	public int getNewRecordsCount()
	{
		return newRecords.size();
	}

	/**
	 * @return
	 */
	public int getRemovedRecordsCount()
	{
		return removedRecords.size();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getUpdatedRecords()
	 */
	public EditableDataSourceRecord[] getUpdatedRecords()
	{
		return changedRecords.toArray(new EditableDataSourceRecord[0]);
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.EditableDataSource#getSelectedRecords()
	 */
	public EditableDataSourceRecord[] getSelectedRecords()
	{
		return selectedRecords.toArray(new EditableDataSourceRecord[0]);
	}
	
	public void reset()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear();
		selectedRecords.clear();
	}
	
	public boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
	}
}
