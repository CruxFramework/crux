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
package br.com.sysmap.crux.widgets.client.event.timeout;

import com.google.gwt.event.shared.GwtEvent;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class TimeoutEvent extends GwtEvent<TimeoutHandler>
{
	private static Type<TimeoutHandler> TYPE = new Type<TimeoutHandler>();
	private HasTimeoutHandlers source;

	/**
	 * 
	 */
	public TimeoutEvent(HasTimeoutHandlers source)
	{
		this.source = source;
	}

	/**
	 * @return
	 */
	public static Type<TimeoutHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(TimeoutHandler handler)
	{
		handler.onTimeout(this);
	}

	@Override
	public Type<TimeoutHandler> getAssociatedType()
	{
		return TYPE;
	}

	/**
	 * @return the source
	 */
	public HasTimeoutHandlers getSource()
	{
		return source;
	}
}