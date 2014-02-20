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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;

/**
 * Helper Class for widget load events binding
 * @author Thiago Bustamante
 *
 */
public class DettachEvtBind extends EvtProcessor
{
	public DettachEvtBind(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onDettach";

	public String getEventName()
	{
		return EVENT_NAME;
	}
	
	@Override
    public void processEvent(SourcePrinter out, String eventValue, String widget, String widgetId)
    {
		
		out.println(widget+".addAttachHandler(new "+getEventHandlerClass().getCanonicalName()+"(){");
		out.println("public void onAttachOrDetach("+getEventClass().getCanonicalName()+" event){");
		out.println("if (!event.isAttached()){");
		printEvtCall(out, eventValue, "event");
		out.println("}");
		out.println("}");
		out.println("});");
    }

	@Override
    public Class<?> getEventClass()
    {
	    return AttachEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return Handler.class;
    }		
}
