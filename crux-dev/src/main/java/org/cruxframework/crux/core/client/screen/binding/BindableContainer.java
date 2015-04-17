/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen.binding;

import org.cruxframework.crux.core.client.dto.DataObject;

/**
 * Any class that implements this interface will support data binding between classes annotated with
 * {@code @}{@link DataObject} annotation and widgets  inserted into this container. 
 * @author Thiago da Rosa de Bustamante
 */
public interface BindableContainer
{
	/**
	 * Write the given dataObject into this bindable container. Any UI element bound to any property of this object 
	 * will be updated.
	 *   
	 * @param dataObject the data object to write
	 */
	void write(Object dataObject);
	
	/**
	 * Write the given dataObjects into this bindable container. Any UI element bound to any property of these objects 
	 * will be updated.
	 *   
	 * @param dataObjects the data objects to write
	 */
	void writeAll(Object... dataObjects);
	
	/**
	 * Update the given dataObject with the values contained on UI elements of this bindable container. 
	 * @param dataObject the object that will be updated
	 */
    void copyTo(Object dataObject);
    
    /**
     * Read the dataObject bound to the given class from this bindable container. Its state is updated according to any 
     * value binding declaration to this dataObject   
     * @param dataObjectClass dataObject class
     * @return an updated dataObject
     */
    <T> T read(Class<T> dataObjectClass);

    /**
     * Read the dataObject bound to the given alias from this bindable container. Its state is updated according to any 
     * value binding declaration to this dataObject   
     * @param dataObjectAlias dataObject alias
     * @return an updated dataObject
     */
    <T> T read(String dataObjectAlias);
    
    /**
     * Add a new binding configuration for a DataValue object
     * @param dataObjectBinder the binder class
     * @param dataObjectAlias the binder alias
     */
    void addDataObjectBinder(DataObjectBinder<?> dataObjectBinder, String dataObjectAlias);

    /**
     * Retrieve the configuration object for a DataValue object
     * @param dataObjectAlias the binder alias.
     * @return the configuration object for a DataValue object
     */
    <T> DataObjectBinder<T> getDataObjectBinder(String dataObjectAlias);
    
    /**
     * Retrieve the configuration object for a DataValue object
     * @param dataObjectClass the DataValue class.
     * @return the configuration object for a DataValue object
     */
    <T> DataObjectBinder<T> getDataObjectBinder(Class<T> dataObjectClass);
}
