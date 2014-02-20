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
package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;


/**
 * Base class to implement a row event
 * @author Gesse Dafe
 */
public abstract class BaseRowEditEvent<H extends EventHandler, S extends HasHandlers> extends GwtEvent<H> {
	
	public static enum Operation
	{
		CREATE, REMOVE, EDIT
	}
	
	private S source;
	private DataRow row;
	private boolean canceled;
	private Operation operation;

	public BaseRowEditEvent(S source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}
	
	/**
	 * @return the source
	 */
	public S getSource()
	{
		return source;
	}
	
	/**
	 * @return the row
	 */
	public DataRow getRow()
	{
		return row;
	}
	
	/**
	 * @return the canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * Cancel the before Edit event.
	 */
	public void cancel()
	{
		canceled = true;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() 
	{
		return operation;
	}
}