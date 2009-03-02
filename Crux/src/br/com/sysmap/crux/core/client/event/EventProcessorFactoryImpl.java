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
	 * Create a eventProcessor for a SERVER AUTO event.
	 * @param event
	 * @return
	 */
	protected EventProcessor createServerAutoEventProcessor(final Event event)
	{
		return new EventProcessor()
		{
			public void processEvent(final Screen screen, String idSender)
			{
				if (event.isSync()) screen.blockToUser();
				String moduleRelativeURL = GWT.getModuleBaseURL() + "auto";
				String postData = "idSender="+URL.encodeComponent(idSender!=null?idSender:"")+
				                  "&evtCall="+URL.encodeComponent(event.getEvtCall())+
				                  "&screenId="+URL.encodeComponent(screen.getId())+
				                  "&"+screenSerialization.getPostData(screen);
				
				RequestCallback callback = new RequestCallback()
				{
					public void onError(Request request, Throwable exception) 
					{
						if (event.isSync()) screen.unblockToUser();
						Window.alert(JSEngine.messages.eventProcessorServerAutoError());
					}

					public void onResponseReceived(Request request, Response response) 
					{
						if (response.getStatusCode() != 200) 
						{
							if (event.isSync()) screen.unblockToUser();
							Window.alert(JSEngine.messages.eventProcessorServerAutoError());
							return;
						}
						screenSerialization.confirmSerialization(screen);
						screenSerialization.updateScreen(screen, response.getText());
						if (event.isSync()) screen.unblockToUser();
					}
				};
				try
				{
					RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, moduleRelativeURL);
					builder.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
					builder.sendRequest(postData, callback);
				}
				catch (Exception e) 
				{
					if (event.isSync()) screen.unblockToUser();
					Window.alert(JSEngine.messages.eventProcessorServerAutoError());
				}
			}
		};
	}
	
	/**
	 * Create a eventProcessor for a SERVER RPC event.
	 * @param event
	 * @return
	 */
	protected EventProcessor createSeverRPCEventProcessor(final Event event)
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
								"&"+screenSerialization.getDTOPostData(screen));

				RequestCallback callback = new RequestCallback()
				{
					public void onError(Request request, Throwable exception) 
					{
						if (event.isSync()) screen.unblockToUser();
						Window.alert(JSEngine.messages.eventProcessorServerRPCError());
					}

					public void onResponseReceived(Request request, Response response) 
					{
						if (response.getStatusCode() != 200) 
						{
							if (event.isSync()) screen.unblockToUser();
							Window.alert(JSEngine.messages.eventProcessorServerRPCError());
							return;
						}
						String evtCallback = event.getEvtCallback();
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
											screenSerialization.updateScreen(screen, jsonValue.isString().stringValue());
										}
										if ((jsonValue = object.get("result")) != null)
										{
											callback.invoke(method, screen, idSender, jsonValue);
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
								Window.alert(JSEngine.messages.eventProcessorRPCCallbackError(evtCallback));
							}
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
					Window.alert(JSEngine.messages.eventProcessorServerRPCError());
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
		if (EventFactory.TYPE_CLIENT.equals(event.getType()))
		{
			return createClientEventProcessor(event);
		}
		else if (EventFactory.TYPE_SERVER_AUTO.equals(event.getType()))
		{
			return createServerAutoEventProcessor(event);
		}
		else if (EventFactory.TYPE_SERVER_RPC.equals(event.getType()))
		{
			return createSeverRPCEventProcessor(event);
		}
		
		throw new InterfaceConfigException(JSEngine.messages.eventProcessorFactoryInvalidEventType(event.getId(), event.getEvtCall(), event.getType()));
	}
}
