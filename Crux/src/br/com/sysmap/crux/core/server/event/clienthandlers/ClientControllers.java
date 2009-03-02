package br.com.sysmap.crux.core.server.event.clienthandlers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.event.annotation.ClientCallback;
import br.com.sysmap.crux.core.client.event.annotation.ClientController;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;

public class ClientControllers 
{
	private static final Log logger = LogFactory.getLog(ClientControllers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Class<?>> clientHandlers;
	private static Map<String, Class<?>> clientCallbacks;
	
	public static void initialize(URL[] urls)
	{
		if (clientHandlers != null && clientCallbacks != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (clientHandlers != null && clientCallbacks != null)
			{
				return;
			}
			
			initializeClientHandlers(urls);
			initializeClientCallbacks(urls);
		}
		finally
		{
			lock.unlock();
		}
	}

	protected static void initializeClientHandlers(URL[] urls)
	{
		clientHandlers = new HashMap<String, Class<?>>();
		Set<String> controllerNames =  ClassScanner.getInstance(urls).searchClassesByAnnotation(ClientController.class);
		if (controllerNames != null)
		{
			for (String controller : controllerNames) 
			{
				try 
				{
					Class<?> controllerClass = Class.forName(controller);
					ClientController annot = controllerClass.getAnnotation(ClientController.class);
					clientHandlers.put(annot.value(), controllerClass);
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.clientHandlersHandlerInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}
	
	protected static void initializeClientCallbacks(URL[] urls)
	{
		clientCallbacks = new HashMap<String, Class<?>>();
		Set<String> callbackNames =  ClassScanner.getInstance(urls).searchClassesByAnnotation(ClientCallback.class);
		if (callbackNames != null)
		{
			for (String callback : callbackNames) 
			{
				try 
				{
					Class<?> callbackClass = Class.forName(callback);
					ClientCallback annot = callbackClass.getAnnotation(ClientCallback.class);
					clientCallbacks.put(annot.value(), callbackClass);
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.clientHandlersCallbackInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}
	
	public static Class<?> getClientHandler(String name)
	{
		if (clientHandlers == null)
		{
			initialize(ScannerURLS.getURLsForSearch(null));
		}
		return clientHandlers.get(name);
	}

	public static Class<?> getClientCallback(String name)
	{
		if (clientCallbacks == null)
		{
			initialize(ScannerURLS.getURLsForSearch(null));
		}
		return clientCallbacks.get(name);
	}
}
