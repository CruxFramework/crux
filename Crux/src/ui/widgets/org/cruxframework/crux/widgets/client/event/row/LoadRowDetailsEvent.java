/*
 * Copyright 2009 cruxframework.org
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

import com.google.gwt.event.shared.GwtEvent;

public class LoadRowDetailsEvent extends GwtEvent<LoadRowDetailsHandler>{

	private static Type<LoadRowDetailsHandler> TYPE = new Type<LoadRowDetailsHandler>();
	private HasLoadRowDetailsHandlers source;
	private DataRow row;
	private boolean canceled;

	public LoadRowDetailsEvent(HasLoadRowDetailsHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	public HasLoadRowDetailsHandlers getSource()
	{
		return source;
	}
	
	public static Type<LoadRowDetailsHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LoadRowDetailsHandler handler)
	{
		handler.onLoadRowDetails(this);
	}

	@Override
	public Type<LoadRowDetailsHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static LoadRowDetailsEvent fire(HasLoadRowDetailsHandlers source, DataRow row)
	{
		LoadRowDetailsEvent event = new LoadRowDetailsEvent(source, row);
		source.fireEvent(event);
		return event;
	}

	/**
	 * @return The grid's row which owns the details
	 */
	public DataRow getRow()
	{
		return row;
	}
	
	/**
	 * @return <code>true</code> if the event was canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * Cancels the event
	 */
	public void cancel()
	{
		canceled = true;
	}
}