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
	
	public static final String EVENT_CLICK = "_onclick";
	public static final String EVENT_CHANGE = "_onchange";
	public static final String EVENT_FOCUS = "_onfocus";
	public static final String EVENT_BLUR = "_onblur";
	public static final String EVENT_LOAD = "_onload";
	public static final String EVENT_KEY_DOWN = "_onkeydown";
	public static final String EVENT_KEY_PRESS = "_onkeypress";
	public static final String EVENT_KEY_UP = "_onkeyup";
	public static final String EVENT_MOUSE_DOWN = "_onmousedown";
	public static final String EVENT_MOUSE_MOVE = "_onmousemove";
	public static final String EVENT_MOUSE_UP = "_onmouseup";
	public static final String EVENT_MOUSE_OUT = "_onmouseout";
	public static final String EVENT_MOUSE_OVER = "_onmouseover";
	public static final String EVENT_MOUSE_WHEEL = "_onmousewheel";
	public static final String EVENT_CLOSE = "_onclose";
	public static final String EVENT_CLOSING = "_onclosing";
	public static final String EVENT_RESIZED = "_onresized";
	public static final String EVENT_ERROR = "_onerror";
	public static final String EVENT_LOAD_IMAGES = "_onloadimage";
	public static final String EVENT_LOAD_ORACLE = "_onloadoracle";
	public static final String EVENT_LOAD_FORMAT = "_onloadformat";
	public static final String EVENT_SCROLL = "_onscroll";
	public static final String EVENT_BEFORE_SELECTION = "_onbeforeselection";
	public static final String EVENT_SELECTION = "_onselection";
	public static final String EVENT_LOAD_WIDGET = "_onloadwidget";
	public static final String EVENT_EXECUTE_EVENT = "_onexecute";
	public static final String EVENT_SUBMIT_COMPLETE = "_onsubmitcomplete";
	public static final String EVENT_SUBMIT = "_onsubmit";
	public static final String EVENT_HISTORY_CHANGED = "_onhistorychanged";
	
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
			Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientError(event.getController()+"."+event.getMethod()), 
					processor.exception());
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
				final EventClientHandlerInvoker handler = (EventClientHandlerInvoker)getRegisteredClientEventHandlers().getEventHandler(event.getController());
				if (handler == null)
				{
					Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(event.getController()));
					return;
				}
				try
				{
					handler.invoke(event.getMethod(), sourceEvent, this);
				}
				catch (Exception e) 
				{
					_exception = e;
				}
			}

			public void processEvent(CruxEvent<?> sourceEvent, boolean fromOutOfModule)
			{
				final EventClientHandlerInvoker handler = (EventClientHandlerInvoker)getRegisteredClientEventHandlers().getEventHandler(event.getController());
				if (handler == null)
				{
					Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(event.getController()));
					return;
				}
				try
				{
					handler.invoke(event.getMethod(), sourceEvent, fromOutOfModule, this);
				}
				catch (Exception e) 
				{
					_exception = e;
				}
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
