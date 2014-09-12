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
public class SelectEvent extends GwtEvent<SelectHandler>
{
	private static Type<SelectHandler> TYPE = new Type<SelectHandler>();
	private boolean canceled = false;
	private boolean stopped;
	
	/**
	 * 
	 */
	public SelectEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<SelectHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SelectHandler handler)
	{
		handler.onSelect(this);
	}

	@Override
	public Type<SelectHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> SelectEvent fire(HasSelectHandlers source) 
	{
		if (TYPE != null) 
		{
			SelectEvent event = new SelectEvent();
			source.fireEvent(event);
			return event;
		}
		return null;
	}

	public void stopPropagation()
	{
		this.stopped = true;
	}
	
	public boolean isStopped()
	{
		return this.stopped;
	}
	
	public boolean isCanceled()
    {
    	return canceled;
    }

	public void setCanceled(boolean canceled)
    {
    	this.canceled = canceled;
    }
}