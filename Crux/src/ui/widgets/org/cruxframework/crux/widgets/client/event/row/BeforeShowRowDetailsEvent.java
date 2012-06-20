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
package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event fired right before the details of some grid's row gets shown
 * @author Gesse S. F. Dafe
 */
public class BeforeShowRowDetailsEvent extends GwtEvent<BeforeShowRowDetailsHandler>{

	private static Type<BeforeShowRowDetailsHandler> TYPE = new Type<BeforeShowRowDetailsHandler>();
	private HasBeforeShowDetailsHandlers source;
	private DataRow row;
	private boolean canceled;

	public BeforeShowRowDetailsEvent(HasBeforeShowDetailsHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	public HasBeforeShowDetailsHandlers getSource()
	{
		return source;
	}
	
	public static Type<BeforeShowRowDetailsHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeShowRowDetailsHandler handler)
	{
		handler.onBeforeShowRowDetails(this);
	}

	@Override
	public Type<BeforeShowRowDetailsHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static BeforeShowRowDetailsEvent fire(HasBeforeShowDetailsHandlers source, DataRow row)
	{
		BeforeShowRowDetailsEvent event = new BeforeShowRowDetailsEvent(source, row);
		source.fireEvent(event);
		return event;
	}

	/**
	 * @return The grid's row which holds the details
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