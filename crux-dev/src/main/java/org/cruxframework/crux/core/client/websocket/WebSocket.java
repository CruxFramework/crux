/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.client.websocket;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.file.Blob;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * Implementation for HTML5 WebSocket API (http://www.w3.org/TR/2011/WD-websockets-20110419/)
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WebSocket
{
	private HandlerManager handlerManager;
	private FastList<SocketMessageHandler> messageHandlers = new FastList<SocketMessageHandler>();
	private FastList<SocketOpenHandler> openHandlers = new FastList<SocketOpenHandler>();
	private FastList<SocketCloseHandler> closeHandlers = new FastList<SocketCloseHandler>();
	private FastList<SocketErrorHandler> errorHandlers = new FastList<SocketErrorHandler>();
	private final WebSocketJS socketJS;

	protected WebSocket(WebSocketJS socketJS)
	{
		this.socketJS = socketJS;
	}

	public static final short CONNECTING = 0;
	public static final short OPEN = 1;
	public static final short CLOSING = 2;
	public static final short CLOSED = 3;
	
	public short getReadyState()
	{
		return socketJS.getReadyState();
	}
	
	public double getBufferedAmount()
	{
		return socketJS.getBufferedAmount();
	}
	
	public String getUrl()
	{
		return socketJS.getUrl();
	}

	public String getProtocol()
	{
		return socketJS.getProtocol();
	}

	public String getExtensions()
	{
		return socketJS.getExtensions();
	}

	public void send(String data)
	{
		socketJS.send(data);
	}

	public void send(Blob data)
	{
		socketJS.send(data);
	}

	public void send(ArrayBuffer data)
	{
		socketJS.send(data);
	}

	public void send(ArrayBufferView data)
	{
		socketJS.send(data);
	}

	public void close()
	{
		socketJS.close();
	}

	public void close(short code, String reason)
	{
		socketJS.close(code, reason);
	}
	
	public HandlerRegistration addMessageHandler(final SocketMessageHandler handler)
	{
		messageHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = messageHandlers.indexOf(handler);
				if (index >= 0)
				{
					messageHandlers.remove(index);
				}
			}
		};
	}
	
	public HandlerRegistration addOpenHandler(final SocketOpenHandler handler)
	{
		openHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = openHandlers.indexOf(handler);
				if (index >= 0)
				{
					openHandlers.remove(index);
				}
			}
		};
	}

	public HandlerRegistration addCloseHandler(final SocketCloseHandler handler)
	{
		closeHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = closeHandlers.indexOf(handler);
				if (index >= 0)
				{
					closeHandlers.remove(index);
				}
			}
		};
	}

	public HandlerRegistration addErrorHandler(final SocketErrorHandler handler)
	{
		errorHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = errorHandlers.indexOf(handler);
				if (index >= 0)
				{
					errorHandlers.remove(index);
				}
			}
		};
	}

	void fireMessageEvent(String message)
	{
		SocketMessageEvent event = new SocketMessageEvent(this, message);
		for (int i = 0; i < messageHandlers.size(); i++)
		{
			SocketMessageHandler handler = messageHandlers.get(i);
			handler.onMessage(event);
		}
	}
	
	void fireErrorEvent()
	{
		SocketErrorEvent event = new SocketErrorEvent(this);
		for (int i = 0; i < errorHandlers.size(); i++)
		{
			SocketErrorHandler handler = errorHandlers.get(i);
			handler.onError(event);
		}
	}
	
	void fireOpenEvent()
	{
		SocketOpenEvent event = new SocketOpenEvent(this);
		for (int i = 0; i < openHandlers.size(); i++)
		{
			SocketOpenHandler handler = openHandlers.get(i);
			handler.onOpen(event);
		}
	}
	
	void fireCloseEvent(boolean wasClean, short code, String reason)
	{
		SocketCloseEvent event = new SocketCloseEvent(this, wasClean, code, reason);
		for (int i = 0; i < closeHandlers.size(); i++)
		{
			SocketCloseHandler handler = closeHandlers.get(i);
			handler.onClose(event);
		}
	}
	
	public static native final boolean isSupported()/*-{
		if ($wnd.WebSocket)
		{
			return true;
		}
		return false;
	}-*/;
	
	public static final WebSocket createIfSupported(String url)
	{
		if (isSupported())
		{
			WebSocketJS webSocketJS = WebSocketJS.createSocket(url);
			WebSocket socket = new WebSocket(webSocketJS);
			initializeSocketHandlers(socket, webSocketJS);
			return socket;
		}
		return null;
	}
	
	public static final WebSocket createIfSupported(String url, String protocol)
	{
		if (isSupported())
		{
			WebSocketJS webSocketJS = WebSocketJS.createSocket(url, protocol);
			WebSocket socket = new WebSocket(webSocketJS);
			initializeSocketHandlers(socket, webSocketJS);
			return socket;
		}
		return null;
	}

	public static final WebSocket createIfSupported(String url, JsArrayString protocols)
	{
		if (isSupported())
		{
			WebSocketJS webSocketJS = WebSocketJS.createSocket(url, protocols);
			WebSocket socket = new WebSocket(webSocketJS);
			initializeSocketHandlers(socket, webSocketJS);
			return socket;
		}
		return null;
	}

	private static native void initializeSocketHandlers(WebSocket socket, WebSocketJS socketJS)/*-{
		socketJS.onerror = function() {
			socket.@org.cruxframework.crux.core.client.websocket.WebSocket::fireErrorEvent()();
		};
		socketJS.onopen = function() {
			socket.@org.cruxframework.crux.core.client.websocket.WebSocket::fireOpenEvent()();
		};
		socketJS.onmessage = function(event) {
			socket.@org.cruxframework.crux.core.client.websocket.WebSocket::fireMessageEvent(Ljava/lang/String;)(event.data);
		};
		socketJS.onclose = function(event) {
			socket.@org.cruxframework.crux.core.client.websocket.WebSocket::fireCloseEvent(ZSLjava/lang/String;)(event.wasClean, event.code, event.reason);
		};
	}-*/;
}
