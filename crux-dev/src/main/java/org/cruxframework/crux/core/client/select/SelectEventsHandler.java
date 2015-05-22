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
package org.cruxframework.crux.core.client.select;

import org.cruxframework.crux.core.client.event.SelectEvent;

import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SelectEventsHandler implements HasEnabled
{
	protected boolean allowPropagationToNonSelectableWidgets = false;
	protected boolean preventDefaultTouchEvents = false;
	protected SelectableWidget selectableWidget;
	protected boolean stopPropagationTouchEvents = false;

	private boolean enabled = true;

	public abstract void handleWidget();
	
	@Override
	public boolean isEnabled()
	{
	    return enabled;
	}

	public void select()
	{
		SelectEvent.fire(selectableWidget);
	}
	
	public void setAllowPropagationToNonSelectableWidgets(boolean allowPropagationToNonSelectableWidgets) 
	{
		this.allowPropagationToNonSelectableWidgets = allowPropagationToNonSelectableWidgets;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		this.preventDefaultTouchEvents = preventDefaultTouchEvents;
	}

	public void setSelectableWidget(SelectableWidget selectableWidget)
	{
		this.selectableWidget = selectableWidget;
	}
	
	public void setStopPropagationTouchEvents(boolean stopPropagationTouchEvents) 
	{
		this.stopPropagationTouchEvents = stopPropagationTouchEvents;
	}
}
