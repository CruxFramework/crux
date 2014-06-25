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

import java.util.Comparator;
import java.util.List;

import org.cruxframework.crux.core.client.collection.Array;


/**
 * A DataProvider is used to provide information to widgets that implements <code>HasDataProvider</code> interface, 
 * like Crux <code>Grid</code>.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public interface DataProvider<E>
{
	/**
	 * Points DataProvider to first record 
	 */
	void firstRecord();
	
	/**
	 * Verify if DataProvider has more records.
	 * @return true if more records exist.
	 */
	boolean hasNextRecord();
	
	/**
	 * Points DataProvider to next record 
	 */
	void nextRecord();

	/**
	 * Verify if DataProvider has previous records.
	 * @return true if previous records exist.
	 */
	boolean hasPreviousRecord();

	/**
	 * Points DataProvider to previous record 
	 */
	void previousRecord();
	
	/**
	 * Sort DataProvider records, based on column informed
	 * @param comparator Comparator used for sorting.
	 */
	void sort(Comparator<E> comparator);

	/**
	 * Reset DataProvider, as if it was never loaded before.
	 */
	void reset();


	/** Return the DataProviderRecord object, representing the current record
	 * @return current DataProviderRecord object.
	 */
	DataProviderRecord<E> getRecord();
	
	/**
	 * Return a copy of the current record object. 
	 * @return
	 */
	E getBoundObject();
		
	/**
	 * Insert a new record on DataProvider
	 * @param beforeRecord
	 * @return
	 */
	DataProviderRecord<E> insertRecord(int beforeRecord);

	/**
	 * Remove a record from DataProvider
	 * @param record
	 * @return
	 */
	DataProviderRecord<E> removeRecord(int record);

	/**
	 * Update a record on DataProvider
	 * @param record
	 * @param previousState
	 */
	void updateState(DataProviderRecord<E> record, DataProviderRecord.DataProviderRecordState previousState);
	
	/**
	 * Return all records inserted on DataProvider
	 * @return all new records
	 */
	DataProviderRecord<E>[] getNewRecords();

	/**
	 * Return all records removed from DataProvider
	 * @return all removed records
	 */
	DataProviderRecord<E>[] getRemovedRecords();

	/**
	 * Return all records modified on DataProvider
	 * @return all modified records
	 */
	DataProviderRecord<E>[] getUpdatedRecords();

	/**
	 * Return all records selected on DataProvider
	 * @return all selected records
	 */
	DataProviderRecord<E>[] getSelectedRecords();
	
	/**
	 * Undo all changes 
	 */
	void clearChanges();

	/**
	 * Retrieve the index of the given object
	 * @param boundObject
	 * @return
	 */
	int getIndex(E boundObject);
	
	/**
	 * Mark the given record as selected
	 * @param index
	 * @param selected
	 */
	void selectRecord(int index, boolean selected);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void updateData(E[] data);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void updateData(List<E> data);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void updateData(Array<E> data);
}
