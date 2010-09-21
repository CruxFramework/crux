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

import br.com.sysmap.crux.widgets.client.grid.DataRow;

import com.google.gwt.event.shared.GwtEvent;


/**
 * TODO - Gess� - Comment this
 * @author Gessé S. F. Dafé
 */
public class RowRenderEvent extends GwtEvent<RowRenderHandler>{

	private static Type<RowRenderHandler> TYPE = new Type<RowRenderHandler>();
	private HasRowRenderHandlers source;
	private DataRow row;

	/**
	 * 
	 */
	public RowRenderEvent(HasRowRenderHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	/**
	 * @return the source
	 */
	public HasRowRenderHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<RowRenderHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(RowRenderHandler handler)
	{
		handler.onRowRender(this);
	}

	@Override
	public Type<RowRenderHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static RowRenderEvent fire(HasRowRenderHandlers source, DataRow row)
	{
		RowRenderEvent event = new RowRenderEvent(source, row);
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