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
package org.cruxframework.crux.core.client.event.paste;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event fired when a value is pasted on associated element
 * @author Gesse S. F. Dafe
 */
public class PasteEvent extends GwtEvent<PasteHandler>
{
	private static Type<PasteHandler> TYPE = new Type<PasteHandler>();

	/**
	 * Protected constructor 
	 */
	protected PasteEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<PasteHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<PasteHandler> getAssociatedType()
	{
		return TYPE;
	}
	
	public static PasteEvent fire(HasPasteHandlers source)
	{
		PasteEvent event = new PasteEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(PasteHandler handler)
	{
		handler.onPaste(this);
	}
}