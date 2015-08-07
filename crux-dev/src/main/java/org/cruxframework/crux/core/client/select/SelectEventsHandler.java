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

import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SelectEventsHandler implements HasEnabled
{
	protected SelectableWidget selectableWidget;
	protected boolean preventDefaultTouchEvents = false;
	protected boolean stopPropagationTouchEvents = false;
	protected boolean allowPropagationToNonSelectableWidgets = false;

	private boolean enabled = true;

	@Override
	public boolean isEnabled()
	{
	    return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setSelectableWidget(SelectableWidget selectableWidget)
	{
		this.selectableWidget = selectableWidget;
	}
	
	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		this.preventDefaultTouchEvents = preventDefaultTouchEvents;
	}

	public void setStopPropagationTouchEvents(boolean stopPropagationTouchEvents) 
	{
		this.stopPropagationTouchEvents = stopPropagationTouchEvents;
	}

	public void setAllowPropagationToNonSelectableWidgets(boolean allowPropagationToNonSelectableWidgets) 
	{
		this.allowPropagationToNonSelectableWidgets = allowPropagationToNonSelectableWidgets;
	}
	
	public abstract void handleWidget();
}
