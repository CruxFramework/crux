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
package org.cruxframework.crux.widgets.rebind.event;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.widgets.client.event.timeout.TimeoutEvent;
import org.cruxframework.crux.widgets.client.event.timeout.TimeoutHandler;


/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class TimeoutEvtBind
{
	private static final String EVENT_NAME = "onTimeout";

	/**
	 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
	 */
	public static String getEventName()
	{
		return EVENT_NAME;
	}


    public static void processEvent(SourcePrinter out, String eventValue, String time, String widget, String widgetId, WidgetCreator<?> creator)
    {
		if(time != null && eventValue != null)
		{

			out.println(widget+".addTimeoutHandler(new "+TimeoutHandler.class.getCanonicalName()+"(){");
			out.println("public void onTimeout("+TimeoutEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(out, eventValue, "onTimeout", TimeoutEvent.class, "event", creator);
			
			out.println("}");
			out.println("public long getScheduledTime(){");
			out.println("return "+Long.parseLong(time)+";");
			out.println("}");

			out.println("});");
		}
    }	
}
