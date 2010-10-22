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
 * @author Thiago da Rosa de Bustamante
 */
public class Events 
{
	private static RegisteredControllers registeredControllers;
	
	
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
			if (evtId != null && evtId.length() > 0 && evt != null && evt.length() > 0)
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
		getRegisteredControllers();

		return new EventProcessor()
		{
			public void processEvent(GwtEvent<?> sourceEvent)
			{
				getRegisteredControllers().invokeController(event.getController(), event.getMethod(), false, sourceEvent, this);
			}

			public void processEvent(CruxEvent<?> sourceEvent, boolean fromOutOfModule)
			{
				getRegisteredControllers().invokeController(event.getController(), event.getMethod(), fromOutOfModule, sourceEvent, this);
			}
		};
	}

	public static RegisteredControllers getRegisteredControllers()
	{
		if (registeredControllers == null)
		{
			registeredControllers = (RegisteredControllers)GWT.create(RegisteredControllers.class);
		}
		return registeredControllers;
	}
}
