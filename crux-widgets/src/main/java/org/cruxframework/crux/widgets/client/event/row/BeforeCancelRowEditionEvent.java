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

public class BeforeCancelRowEditionEvent extends BaseRowEditEvent<BeforeCancelRowEditionHandler, HasBeforeCancelRowEditionHandler>
{

	public BeforeCancelRowEditionEvent(HasBeforeCancelRowEditionHandler source, DataRow row)
	{
		super(source, row);
	}

	private static Type<BeforeCancelRowEditionHandler> TYPE = new Type<BeforeCancelRowEditionHandler>();

	/**
	 * @return
	 */
	public static Type<BeforeCancelRowEditionHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(BeforeCancelRowEditionHandler handler)
	{
		handler.onBeforeCancelRowEdition(this);
	}

	@Override
	public Type<BeforeCancelRowEditionHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static BeforeCancelRowEditionEvent fire(HasBeforeCancelRowEditionHandler source, DataRow row)
	{
		BeforeCancelRowEditionEvent event = new BeforeCancelRowEditionEvent(source, row);
		source.fireEvent(event);
		return event;
	}
}
