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
package br.com.sysmap.crux.widgets.client.event.row;

import br.com.sysmap.crux.widgets.client.grid.impl.DataRow;

import com.google.gwt.event.shared.GwtEvent;


/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class RowDoubleClickEvent extends GwtEvent<RowDoubleClickHandler>{

	private static Type<RowDoubleClickHandler> TYPE = new Type<RowDoubleClickHandler>();
	private HasRowDoubleClickHandlers source;
	private DataRow row;

	/**
	 * 
	 */
	public RowDoubleClickEvent(HasRowDoubleClickHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	/**
	 * @return the source
	 */
	public HasRowDoubleClickHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<RowDoubleClickHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(RowDoubleClickHandler handler)
	{
		handler.onRowDoubleClick(this);
	}

	@Override
	public Type<RowDoubleClickHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static RowDoubleClickEvent fire(HasRowDoubleClickHandlers source, DataRow row)
	{
		RowDoubleClickEvent event = new RowDoubleClickEvent(source, row);
		source.fireEvent(event);
		return event;
	}

	/**
	 * @return the row
	 */
	public DataRow getRow()
	{
		return row;
	}
}