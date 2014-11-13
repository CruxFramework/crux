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
package org.cruxframework.crux.widgets.client.event.row;

import org.cruxframework.crux.widgets.client.grid.DataRow;

import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * @author alexandre
 *
 */
public class RowEditingEvent extends BaseRowEditEvent<RowEditingHandler, HasRowEditingHandlers>
{
	public RowEditingEvent(HasRowEditingHandlers source, DataRow row) 
	{
		super(source, row);
	}

	private static Type<RowEditingHandler> TYPE = new Type<RowEditingHandler>();

	/**
	 * @return
	 */
	public static Type<RowEditingHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(RowEditingHandler handler)
	{
		handler.onRowEditing(this);
	}

	@Override
	public Type<RowEditingHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static RowEditingEvent fire(HasRowEditingHandlers source, DataRow row)
	{
		RowEditingEvent event = new RowEditingEvent(source, row);
		source.fireEvent(event);
		return event;
	}

}
