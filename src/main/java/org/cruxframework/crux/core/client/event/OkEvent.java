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
package org.cruxframework.crux.core.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OkEvent extends GwtEvent<OkHandler>
{
	private static Type<OkHandler> TYPE = new Type<OkHandler>();
	
	protected OkEvent()
	{
	}

	@Override
	protected void dispatch(OkHandler handler)
	{
		handler.onOk(this);
	}

	@Override
	public Type<OkHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Type<OkHandler> getType()
	{
		return TYPE;
	}
	
	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static OkEvent fire(HasOkHandlers source)
	{
		OkEvent event = new OkEvent();
		source.fireEvent(event);
		return event;
	}	
}
