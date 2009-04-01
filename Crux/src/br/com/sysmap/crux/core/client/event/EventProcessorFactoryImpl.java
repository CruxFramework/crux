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
import br.com.sysmap.crux.core.client.component.ScreenSerialization;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Default factory for event handlers. Can be overwrote in the client module of 
 * target application to produce new kinds of event handlers.
 * @author Thiago
 *
 */
public class EventProcessorFactoryImpl implements IEventProcessorFactory{
	private RegisteredClientEventHandlers registeredClientEventHandlers;
	private ScreenSerialization screenSerialization;
	
	public EventProcessorFactoryImpl() 
	{
		this.registeredClientEventHandlers = (RegisteredClientEventHandlers)GWT.create(RegisteredClientEventHandlers.class);
		this.screenSerialization = (ScreenSerialization)GWT.create(ScreenSerialization.class);
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
						if (event.isSync())
						{
							try
							{
								handler.invoke(method, screen, idSender);
							}
							catch (Exception e) 
							{
								GWT.log(e.getLocalizedMessage(), e);
								Window.alert(JSEngine.messages.eventProcessorClientError(evtCall));
							}
						}
						else
						{
							new Timer()
							{
								public void run() 
								{
									try
									{
										handler.invoke(method, screen, idSender);
									}
									catch (Exception e) 
									{
										GWT.log(e.getLocalizedMessage(), e);
										Window.alert(JSEngine.messages.eventProcessorClientError(evtCall));
									}
								}
							}.schedule(1);
						}
				}
			}
		};
	}
	
	/**
	 * Create a eventProcessor for a RPC event.
	 * @param event
	 * @return
	 */
	protected EventProcessor createRPCEventProcessor(final Event event)
	{
		return new EventProcessor()
		{
			public void processEvent(final Screen screen, final String idSender)
			{
				if (event.isSync()) screen.blockToUser();
				String moduleRelativeURL = GWT.getModuleBaseURL() + "rpc";
				StringBuffer postData = new StringBuffer();
				postData.append("idSender="+URL.encodeComponent(idSender!=null?idSender:"")+
								"&screenId="+URL.encodeComponent(screen.getId())+
						        "&evtCall="+URL.encodeComponent(event.getEvtCall())+
								"&"+screenSerialization.getPostData(screen));

				RequestCallback callback = new RequestCallback()
				{
					public void onError(Request request, Throwable exception) 
					{
						if (event.isSync()) screen.unblockToUser();
						Window.alert(JSEngine.messages.eventProcessorRPCError());
					}

					public void onResponseReceived(Request request, Response response) 
					{
						if (response.getStatusCode() != 200) 
						{
							if (event.isSync()) screen.unblockToUser();
							Window.alert(JSEngine.messages.eventProcessorRPCError());
							return;
						}
						try
						{
							JSONValue jsonValue = JSONParser.parse(response.getText());
							JSONObject object = jsonValue.isObject();
							if (object != null)
							{
								if ((jsonValue = object.get("error")) != null)
								{
									object = jsonValue.isObject();
									String errorCode = object.get("code").toString();
									String errorMsg = object.get("msg").toString();
									if ("595".equals(errorCode))
									{
										Window.alert(errorMsg);
									}
									else
									{
										Window.alert(JSEngine.messages.eventProcessorServerJsonError(errorCode, errorMsg));
									}
								}
								else
								{
									if ((jsonValue = object.get("dtoChanges")) != null)
									{
										screenSerialization.updateScreen(screen, jsonValue.isArray());
									}
									if ((jsonValue = object.get("result")) != null)
									{
										invokeCallbackMethod(event, screen, idSender, jsonValue);
									}
									else
									{
										Window.alert(JSEngine.messages.eventProcessorServerJsonInvalidResponse());
									}
								}
							}
							if (event.isSync()) screen.unblockToUser();
						}
						catch (Exception e)
						{
							GWT.log(e.getLocalizedMessage(), e);
							if (event.isSync()) screen.unblockToUser();
							Window.alert(JSEngine.messages.eventProcessorRPCResultProcessingError());
						}
					}
				};

				try
				{
					RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, moduleRelativeURL);
					builder.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
					builder.sendRequest(postData.toString(), callback);
				}
				catch (Exception e)
				{
					if (event.isSync()) screen.unblockToUser();
					Window.alert(JSEngine.messages.eventProcessorRPCError());
				}
			}
		};
	}
	
	private void invokeCallbackMethod(Event event, Screen screen, String idSender, JSONValue jsonValue)
	{
		String evtCallback = event.getEvtCallback();
		try
		{
			if (evtCallback == null || evtCallback.trim().length() == 0)
			{
				if (event.isSync()) screen.unblockToUser();
				return;
			}
			int dotPos = evtCallback.indexOf('.');
			if (dotPos > 0)
			{
				String evtHandler = evtCallback.substring(0, dotPos);
				String method = evtCallback.substring(dotPos+1);
				EventClientCallbackInvoker callback = (EventClientCallbackInvoker)registeredClientEventHandlers.getEventCallback(evtHandler);
				if (callback == null)
				{
					if (event.isSync()) screen.unblockToUser();
					Window.alert(JSEngine.messages.eventProcessorRPCCallbackNotFound(evtHandler));
					return;
				}
				callback.invoke(method, screen, idSender, jsonValue);
			}
		}
		catch (Exception e)
		{
			GWT.log(e.getLocalizedMessage(), e);
			if (event.isSync()) screen.unblockToUser();
			Window.alert(JSEngine.messages.eventProcessorRPCCallbackError(evtCallback));
		}
	}

	/**
	 * Create a eventProcessor for a SUBMIT event.
	 * @param event
	 * @return
	 */
	protected EventProcessor createSubmitEventProcessor(final Event event)
	{
		return new EventProcessor()
		{
			public void processEvent(final Screen screen, String idSender)
			{
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
		if (EventFactory.TYPE_CLIENT.equals(event.getType()))
		{
			return createClientEventProcessor(event);
		}
		else if (EventFactory.TYPE_SUBMIT.equals(event.getType()))
		{
			return createSubmitEventProcessor(event);
		}
		else if (EventFactory.TYPE_RPC.equals(event.getType()))
		{
			return createRPCEventProcessor(event);
		}
		
		throw new InterfaceConfigException(JSEngine.messages.eventProcessorFactoryInvalidEventType(event.getId(), event.getEvtCall(), event.getType()));
	}
}
