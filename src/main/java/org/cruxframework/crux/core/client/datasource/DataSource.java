/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.client.datasource;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;

import com.google.gwt.user.client.ui.HasValue;

/**
 * A Datasource is used to provide information to widgets that implements <code>HasDataSource</code> interface, 
 * like Crux <code>Grid</code>.
 * 
 * @author Thiago da Rosa de Bustamante
 * @deprecated Use DataProvider instead.
 */
@Deprecated
@Legacy
public interface DataSource<E>
{
	/**
	 * Points DataSource to first record 
	 */
	void firstRecord();
	
	/**
	 * Verify if DataSource has more records.
	 * @return true if more records exist.
	 */
	boolean hasNextRecord();
	
	/**
	 * Points DataSource to next record 
	 */
	void nextRecord();

	/**
	 * Verify if DataSource has previous records.
	 * @return true if previous records exist.
	 */
	boolean hasPreviousRecord();

	/**
	 * Points DataSource to previous record 
	 */
	void previousRecord();
	
	/**
	 * Sort dataSource records, based on column informed
	 * @param columnName Base column for sorting.
	 * @param ascending If true, sort ascending.
	 */
	void sort(String columnName, boolean ascending);

	/**
	 * Sort dataSource records, based on column informed
	 * @param columnName Base column for sorting.
	 * @param ascending If true, sort ascending.
	 * @param caseSensitive indicate if the columns sort are or not key sensitive  
	 */
	void sort(String columnName, boolean ascending, boolean caseSensitive);
	
	/**
	 * Reset DataSource, as if it was never loaded before.
	 */
	void reset();

	/**
	 * Return the DataSource definitions object.
	 * @return Definitions object
	 */
	ColumnDefinitions<E> getColumnDefinitions();

	
	/**
	 * Sets the DataSource definitions object.
	 * @param columnDefinitions
	 */
	void setColumnDefinitions(ColumnDefinitions<E> columnDefinitions);
	
	/**
	 * Return the column value
	 * @param columnName name of the column
	 * @return value of the column
	 */
	Object getValue(String columnName);
	
	/**
	 * Return the column value
	 * @param columnName
	 * @param record
	 * @return
	 */
	Object getValue(String columnName, DataSourceRecord<?> record);

	/** Return the DataSourceRecord object, representing the current record
	 * @return current DataSourceRecord object.
	 */
	DataSourceRecord<E> getRecord();
	
	/**
	 * Return a copy of the current record object. 
	 * @return
	 */
	E getBoundObject();
	
	/**
	 * Return a copy of the record object. 
	 * @param record
	 * @return
	 */
	E getBoundObject(DataSourceRecord<E> record);
	
	/**
	 * Insert a new record on DataSource
	 * @param beforeRecord
	 * @return
	 */
	DataSourceRecord<E> insertRecord(int beforeRecord);

	/**
	 * Remove a record from DataSource
	 * @param record
	 * @return
	 */
	DataSourceRecord<E> removeRecord(int record);

	/**
	 * Update a record on DataSource
	 * @param record
	 * @param previousState
	 */
	void updateState(DataSourceRecord<E> record, DataSourceRecordState previousState);
	
	/**
	 * Return all records inserted on DataSource
	 * @return all new records
	 */
	DataSourceRecord<E>[] getNewRecords();

	/**
	 * Return all records removed from DataSource
	 * @return all removed records
	 */
	DataSourceRecord<E>[] getRemovedRecords();

	/**
	 * Return all records modified on DataSource
	 * @return all modified records
	 */
	DataSourceRecord<E>[] getUpdatedRecords();

	/**
	 * Return all records selected on DataSource
	 * @return all selected records
	 */
	DataSourceRecord<E>[] getSelectedRecords();
	
	/**
	 * Undo all changes 
	 */
	void clearChanges();
	
	/**
	 * Copies the value from data record to the given widget
	 * @param widget
	 * @param key
	 * @param dataSourceRecord
	 */
	void copyValueToWidget(HasValue<?> widget, String key, DataSourceRecord<?> dataSourceRecord);
	
	/**
	 * Sets the value on the given column of the give  record
	 * @param value 
	 * @param columnKey
	 * @param dataSourceRecord
	 */
	void setValue(Object value, String columnKey, DataSourceRecord<?> dataSourceRecord);
}
