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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.ScreenFactory;


import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
@Deprecated
public class Events 
{
	private static Logger logger = Logger.getLogger(Events.class.getName());
	private static RegisteredControllers registeredControllers;
	//TODO ver quais metodos dessa classe podemos remover
	
	/**
	 * Create an event object.
	 * @param evtId
	 * @param evt
	 * @return
	 */
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	protected static EventProcessor createEventProcessor(final Event event) throws InterfaceConfigException
	{
		getRegisteredControllers();

		return new EventProcessor()
		{
			public void processEvent(GwtEvent<?> sourceEvent)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Invoking method ["+event.getMethod()+"] on controller ["+event.getController()+"]...");
				}
				getRegisteredControllers().invokeController(event.getController(), event.getMethod(), false, sourceEvent, this);
			}

			public void processEvent(CruxEvent<?> sourceEvent, boolean fromOutOfModule)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Invoking method ["+event.getMethod()+"] on controller ["+event.getController()+"]...");
				}
				getRegisteredControllers().invokeController(event.getController(), event.getMethod(), fromOutOfModule, sourceEvent, this);
			}
		};
	}

	@Deprecated
	private static RegisteredControllers getRegisteredControllers()
	{
		if (registeredControllers == null)
		{
			registeredControllers = ScreenFactory.getInstance().getRegisteredControllers();
		}
		return registeredControllers;
	}
}
