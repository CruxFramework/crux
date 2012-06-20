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
package org.cruxframework.crux.core.rebind.screen.widget.creator.event;

import org.cruxframework.crux.core.client.screen.WidgetLoadEvent;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

/**
 * Helper Class for widget load events binding
 * @author Thiago Bustamante
 *
 */
public class LoadWidgetEvtProcessor extends EvtProcessor
{
	public LoadWidgetEvtProcessor(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onLoadWidget";

	/**
	 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}

	@Override
    public void processEvent(SourcePrinter out, String eventValue, String widget, String widgetId)
    {
		printPostProcessingEvtCall(eventValue, "new WidgetLoadEvent("+widget+","+EscapeUtils.quote(widgetId)+")");
    }	
	
	@Override
    public Class<?> getEventClass()
    {
	    return WidgetLoadEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return null;
    }		
}
