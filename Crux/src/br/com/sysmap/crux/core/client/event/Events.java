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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class Events 
{
	public static final String SYNC_TYPE_SYNCHRONOUS = "synchronous";
	public static final String SYNC_TYPE_ASSYNCHRONOUS = "assynchronous";
	
	public static final String EVENT_LOAD = "onload";
	public static final String EVENT_CLOSE = "onclose";
	public static final String EVENT_CLOSING = "onclosing";
	public static final String EVENT_RESIZED = "onresized";
	public static final String EVENT_LOAD_ORACLE = "onloadoracle";
	public static final String EVENT_LOAD_FORMAT = "onloadformat";
	public static final String EVENT_LOAD_WIDGET = "onloadwidget";
	public static final String EVENT_EXECUTE_EVENT = "onexecute";
	public static final String EVENT_SUBMIT_COMPLETE = "onsubmitcomplete";
	public static final String EVENT_SUBMIT = "onsubmit";
	public static final String EVENT_HISTORY_CHANGED = "onhistorychanged";
	
	private static RegisteredClientEventHandlers registeredClientEventHandlers;
	
	
	/**
	 * Create an event object.
	 * @param evtId
	 * @param evt
	 * @return
	 */
	public static Event getEvent(String evtId, String evt)
	{
		try
		{
			if (evtId != null && evtId.trim().length() > 0 && evt != null && evt.trim().length() > 0)
			{
				int dotPos = evt.indexOf('.');
				if (dotPos > 0 && dotPos < evt.length()-1)
				{
					String evtHandler = evt.substring(0, dotPos);
					final String method = evt.substring(dotPos+1);				
					return new Event(evtId, evtHandler, method);
				}
				else
				{
					throw new EventException(Crux.getMessages().eventFactoryInvalidHandlerMethodDeclaration(evt));
				}
			}
		}
		catch (Throwable e)
		{
			Crux.getErrorHandler().handleError(e);
		}
		return null;
	}

	/**
	 * Dispatch an event call.
	 * @param event
	 * @param sourceEvent
	 * @return
	 */
	public static Object callEvent(Event event, CruxEvent<?> sourceEvent)
	{
		return callEvent(event, sourceEvent, false);
	}
	
	/**
	 * Dispatch an event call.
	 * @param event
	 * @param sourceEvent
	 * @param fromOutOfModule
	 * @return
	 */
	public static Object callEvent(Event event, CruxEvent<?> sourceEvent, boolean fromOutOfModule)
	{
		try 
		{
			EventProcessor processor = createEventProcessor(event);
			processor.processEvent(sourceEvent, fromOutOfModule);
			return processEventResult(event, processor);
		}
		catch (InterfaceConfigException e) 
		{
			Crux.getErrorHandler().handleError(e);
		}
		return null;
	}
	
	/**
	 * Dispatch an event call.
	 * @param event
	 * @param sourceEvent
	 * @return
	 */
	public static Object callEvent(Event event, GwtEvent<?> sourceEvent)
	{
		try 
		{
			EventProcessor processor = createEventProcessor(event);
			processor.processEvent(sourceEvent);
			return processEventResult(event, processor);
		}
		catch (InterfaceConfigException e) 
		{
			Crux.getErrorHandler().handleError(e);
		}
		return null;
	}

	/**
	 * 
	 * @param event
	 * @param processor
	 * @return
	 */
	protected static Object processEventResult(Event event, EventProcessor processor)
	{
		if (processor.hasException())
		{
			Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientError(event.getController()+"."+event.getMethod(), 
					processor.exception().getLocalizedMessage()), processor.exception());
		}
		else if (processor.validationMessage() != null)
		{
			Crux.getValidationErrorHandler().handleValidationError(processor.validationMessage());
		}
		else if (processor.hasReturn())
		{
			return processor.returnValue();
		}
		return null;
	}
	
	/**
	 * Create a EventProcessor for the event.
	 * @param event
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static EventProcessor createEventProcessor(final Event event) throws InterfaceConfigException
	{
		getRegisteredClientEventHandlers();

		return new EventProcessor()
		{
			public void processEvent(GwtEvent<?> sourceEvent)
			{
				getRegisteredClientEventHandlers().invokeEventHandler(event.getController(), event.getMethod(), false, sourceEvent, this);
			}

			public void processEvent(CruxEvent<?> sourceEvent, boolean fromOutOfModule)
			{
				getRegisteredClientEventHandlers().invokeEventHandler(event.getController(), event.getMethod(), fromOutOfModule, sourceEvent, this);
			}
		};
	}

	protected static RegisteredClientEventHandlers getRegisteredClientEventHandlers()
	{
		if (registeredClientEventHandlers == null)
		{
			registeredClientEventHandlers = (RegisteredClientEventHandlers)GWT.create(RegisteredClientEventHandlers.class);
		}
		return registeredClientEventHandlers;
	}
}
