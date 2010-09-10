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
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
 */
public class BeforeRowSelectEvent extends GwtEvent<BeforeRowSelectHandler>{

	private static Type<BeforeRowSelectHandler> TYPE = new Type<BeforeRowSelectHandler>();
	private HasBeforeRowSelectHandlers source;
	private DataRow row;
	private boolean canceled;

	/**
	 * 
	 */
	public BeforeRowSelectEvent(HasBeforeRowSelectHandlers source, DataRow row)
	{
		this.source = source;
		this.row = row;
	}

	/**
	 * @return the source
	 */
	public HasBeforeRowSelectHandlers getSource()
	{
		return source;
	}
	
	/**
	 * @return
	 */
	public static Type<BeforeRowSelectHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeRowSelectHandler handler)
	{
		handler.onBeforeRowSelect(this);
	}

	@Override
	public Type<BeforeRowSelectHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static BeforeRowSelectEvent fire(HasBeforeRowSelectHandlers source, DataRow row)
	{
		BeforeRowSelectEvent event = new BeforeRowSelectEvent(source, row);
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
	
	/**
	 * @return the canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * Cancel the before selection event.
	 */
	public void cancel()
	{
		canceled = true;
	}
}