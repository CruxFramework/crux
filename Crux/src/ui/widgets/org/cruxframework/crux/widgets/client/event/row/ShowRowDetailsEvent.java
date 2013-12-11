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

public class ShowRowDetailsEvent extends GwtEvent<ShowRowDetailsHandler>{

	private static Type<ShowRowDetailsHandler> TYPE = new Type<ShowRowDetailsHandler>();
	private HasShowRowDetailsHandlers source;
	private DataRow row;
	private boolean canceled;

	public ShowRowDetailsEvent(HasShowRowDetailsHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	public HasShowRowDetailsHandlers getSource()
	{
		return source;
	}

	public static Type<ShowRowDetailsHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(ShowRowDetailsHandler handler)
	{
		handler.onShowRowDetails(this);
	}

	@Override
	public Type<ShowRowDetailsHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static ShowRowDetailsEvent fire(HasShowRowDetailsHandlers source, DataRow row)
	{
		ShowRowDetailsEvent event = new ShowRowDetailsEvent(source, row);
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