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
package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.JSEngine;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * Default factory for event handlers. Can be overwrote in the client module of 
 * target application to produce new kinds of event handlers.
 * @author Thiago
 *
 */
public class EventProcessorFactoryImpl implements IEventProcessorFactory{
	private RegisteredClientEventHandlers registeredClientEventHandlers;
	
	public EventProcessorFactoryImpl() 
	{
		this.registeredClientEventHandlers = (RegisteredClientEventHandlers)GWT.create(RegisteredClientEventHandlers.class);
	}
	
	/**
	 * Create a eventProcessor for a CLIENT event.
	 * @param event
	 * @return
	 */
	protected EventProcessor createClientEventProcessor(final Event event)
	{
		return new EventProcessor()
		{
			public void processEvent(final Screen screen, final String idSender)
			{
				final String evtCall = event.getEvtCall();
				int dotPos = evtCall.indexOf('.');
				if (dotPos > 0 && dotPos < evtCall.length()-1)
				{
					String evtHandler = evtCall.substring(0, dotPos);
					final String method = evtCall.substring(dotPos+1);
					final EventClientHandlerInvoker handler = (EventClientHandlerInvoker)registeredClientEventHandlers.getEventHandler(evtHandler);
					if (handler == null)
					{
						Window.alert(JSEngine.messages.eventProcessorClientHandlerNotFound(evtHandler));
						return;
					}
					try
					{
						handler.invoke(method, screen, idSender, this);
					}
					catch (Exception e) 
					{
						_exception = e;
						GWT.log(e.getLocalizedMessage(), e);
						Window.alert(JSEngine.messages.eventProcessorClientError(evtCall));
					}
				}
			}
		};
	}
	
	/**
	 * Create a EventProcessor for the event.
	 * @param event
	 * @return
	 * @throws InterfaceConfigException
	 */
	public EventProcessor createEventProcessor(final Event event) throws InterfaceConfigException
	{
			return createClientEventProcessor(event);
	}
}
