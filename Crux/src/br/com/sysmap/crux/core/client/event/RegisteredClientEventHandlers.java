package br.com.sysmap.crux.core.client.event;


/**
 * Used to retrieve client controllers.
 * @author Thiago
 */
public interface RegisteredClientEventHandlers 
{
	EventClientHandlerInvoker getEventHandler(String id);
	EventClientCallbackInvoker getEventCallback(String id);
}
