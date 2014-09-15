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

public class BeforeSaveRowEditionEvent extends BaseRowEditEvent<BeforeSaveRowEditionHandler, HasBeforeSaveRowEditionHandler>{

	public BeforeSaveRowEditionEvent(HasBeforeSaveRowEditionHandler source, DataRow row) 
	{
		super(source, row);
	}

	private static Type<BeforeSaveRowEditionHandler> TYPE = new Type<BeforeSaveRowEditionHandler>();

	/**
	 * @return
	 */
	public static Type<BeforeSaveRowEditionHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeSaveRowEditionHandler handler)
	{
		handler.onBeforeSaveRowEdition(this);
	}

	@Override
	public Type<BeforeSaveRowEditionHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static BeforeSaveRowEditionEvent fire(HasBeforeSaveRowEditionHandler source, DataRow row)
	{
		BeforeSaveRowEditionEvent event = new BeforeSaveRowEditionEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}
