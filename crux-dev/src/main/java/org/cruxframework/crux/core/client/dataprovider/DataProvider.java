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

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A DataProvider is used to provide information to widgets that implements <code>HasDataProvider</code> interface, 
 * like Crux <code>Grid</code>.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public interface DataProvider<T>
{
	/**
	 * Points DataProvider to first record 
	 */
	void first();
	
	/**
	 * Verify if DataProvider has more records.
	 * @return true if more records exist.
	 */
	boolean hasNext();
	
	/**
	 * Points DataProvider to next record 
	 */
	void next();

	/**
	 * Verify if DataProvider has previous records.
	 * @return true if previous records exist.
	 */
	boolean hasPrevious();

	/**
	 * Points DataProvider to previous record 
	 */
	void previous();
	
	/**
	 * Sort DataProvider records, based on column informed
	 * @param comparator Comparator used for sorting.
	 */
	void sort(Comparator<T> comparator);

	/**
	 * Reset DataProvider, as if it was never loaded before.
	 */
	void reset();

	/** Return the DataProviderRecord object, representing the current record
	 * @return current DataProviderRecord object.
	 */
	DataProviderRecord<T> getRecord();
	
	/**
	 * Return the current record object. 
	 * @return record object
	 */
	T get();
		
	/**
	 * Update the DataProvider object at the given index 
	 * @param index object position
	 * @param object new object to set
	 * @return
	 */
	DataProviderRecord<T> set(int index, T object);
	
	/**
	 * Insert a new object on DataProvider
	 * @param object element to be insert
	 * @return
	 */
	DataProviderRecord<T> add(T object);

	/**
	 * Insert a new object on DataProvider
	 * @param beforeIndex position to insert
	 * @param object element to be insert
	 * @return
	 */
	DataProviderRecord<T> add(int beforeIndex, T object);

	/**
	 * Remove an object from DataProvider
	 * @param record
	 * @return
	 */
	DataProviderRecord<T> remove(int record);

	/**
	 * Mark the given object as selected
	 * @param index object position
	 * @param selected true if selected
	 * @return
	 */
	DataProviderRecord<T> select(int index, boolean selected);

	/**
	 * Mark the given record as selected
	 * @param object object to select
	 * @param selected true if selected
	 * @return
	 */
	DataProviderRecord<T> select(T object, boolean selected);
	
	/**
	 * Mark the given object as readOnly
	 * @param index object position
	 * @param readOnly true if readOnly
	 * @return
	 */
	DataProviderRecord<T> setReadOnly(int index, boolean readOnly);

	/**
	 * Mark the given record as readOnly
	 * @param object object to select
	 * @param readOnly true if selected
	 * @return
	 */
	DataProviderRecord<T> setReadOnly(T object, boolean readOnly);

	/**
	 * Return all records inserted on DataProvider
	 * @return all new records
	 */
	DataProviderRecord<T>[] getNewRecords();

	/**
	 * Return all records removed from DataProvider
	 * @return all removed records
	 */
	DataProviderRecord<T>[] getRemovedRecords();

	/**
	 * Return all records modified on DataProvider
	 * @return all modified records
	 */
	DataProviderRecord<T>[] getUpdatedRecords();

	/**
	 * Return all records selected on DataProvider
	 * @return all selected records
	 */
	DataProviderRecord<T>[] getSelectedRecords();
	
	/**
	 * Undo all changes 
	 */
	void rollback();

	/**
	 * Confirm all changes 
	 */
	void commit();

	/**
	 * Retrieve the index of the given object
	 * @param boundObject
	 * @return
	 */
	int indexOf(T boundObject);
	
	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void setData(T[] data);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void setData(List<T> data);

	/**
	 * Method called to bind some data to the DataProvider
	 * @param data
	 */
	void setData(Array<T> data);
	
	/**
	 * Load the DataProvider data.
	 */
	void load();
	
	/**
	 * Cancel the loading process, that is asynchronous.
	 */
	void stopLoading();
	
	/**
	 * Check if the DataProvider is loaded
	 * @return
	 */
	boolean isLoaded();
	
 	/**
 	 * Return a filtered set of data, applying the given filter to the whole set of data.
 	 * This operation does not affect the internal set of data. For this purpose, use addFilter instead.
 	 * @param filter filter to apply
 	 * @return data filtered
 	 */
	Array<T> filter(DataFilter<T> filter);
	  	
	/**
	 * Add a callback to be notified about DataProvider load events
	 * @param callback to be called when DataProvider is loaded
	 * @return a registration that allow handler to be removed 
	 */
	HandlerRegistration addDataLoadedHandler(DataLoadedHandler callback);

	/**
	 * Add a callback to be notified about DataProvider loading stop events
	 * @param callback to be called when DataProvider loading is aborted
	 * @return a registration that allow handler to be removed 
	 */
	HandlerRegistration addLoadStoppedHandler(DataLoadStoppedHandler callback);
}
