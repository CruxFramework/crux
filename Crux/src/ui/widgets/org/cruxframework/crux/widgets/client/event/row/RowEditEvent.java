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


/**
 * Fired after a row has been edited 
 * @author Gesse Dafe
 */
public class RowEditEvent extends BaseRowEditEvent<RowEditHandler, HasRowEditHandlers>
{
	public RowEditEvent(HasRowEditHandlers source, DataRow row) 
	{
		super(source, row);
	}

	private static Type<RowEditHandler> TYPE = new Type<RowEditHandler>();

	/**
	 * @return
	 */
	public static Type<RowEditHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(RowEditHandler handler)
	{
		handler.onRowEdit(this);
	}

	@Override
	public Type<RowEditHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static RowEditEvent fire(HasRowEditHandlers source, DataRow row)
	{
		RowEditEvent event = new RowEditEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}