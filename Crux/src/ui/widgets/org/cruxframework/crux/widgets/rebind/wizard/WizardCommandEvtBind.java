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
package org.cruxframework.crux.widgets.rebind.wizard;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.widgets.client.wizard.WizardCommandEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardCommandHandler;


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardCommandEvtBind extends EvtProcessor
{
	public WizardCommandEvtBind(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onCommand";

	/**
	 * @see org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}
	
	@Override
    public Class<?> getEventClass()
    {
	    return WizardCommandEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return WizardCommandHandler.class;
    }

	public void processEvent(SourcePrinter out, String eventValue, String widget, String id, String label, int order) 
	{
		out.println(widget+".addCommand("+EscapeUtils.quote(id)+","+EscapeUtils.quote(label)+",new "+getEventHandlerClass().getCanonicalName()+"(){");
		out.println("public void "+getEventName()+"("+getEventClass().getCanonicalName()+" event){");
		printEvtCall(out, eventValue, "event");
		out.println("}");
		out.println("}, "+order+");");
    }	
}
