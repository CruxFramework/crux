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

import org.cruxframework.crux.core.client.file.Blob;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * Implementation for HTML5 WebSocket API (http://www.w3.org/TR/2011/WD-websockets-20110419/)
 * @author Thiago da Rosa de Bustamante
 *
 */
class WebSocketJS extends JavaScriptObject
{
	protected WebSocketJS(){}

	public final native short getReadyState()/*-{
		return this.readyState;
	}-*/;
	
	public final native double getBufferedAmount()/*-{
		return this.bufferedAmount;
	}-*/;
	
	public final native String getUrl()/*-{
		return this.url;
	}-*/;

	public final native String getProtocol()/*-{
		return this.protocol;
	}-*/;

	public final native String getExtensions()/*-{
		return this.extensions;
	}-*/;

	public final native void send(String data)/*-{
		this.send(data);
	}-*/;

	public final native void send(Blob data)/*-{
		this.send(data);
	}-*/;

	public final native void send(ArrayBuffer data)/*-{
		this.send(data);
	}-*/;

	public final native void send(ArrayBufferView data)/*-{
		this.send(data);
	}-*/;

	public final native void close()/*-{
		this.close();
	}-*/;

	public final native void close(short code, String reason)/*-{
		this.close(code, reason);
	}-*/;
	

	static native WebSocketJS createSocket(String url)/*-{
		return new $wnd.WebSocket(url);
	}-*/;
	
	static native WebSocketJS createSocket(String url, String protocol)/*-{
		return new $wnd.WebSocket(url, protocol);
	}-*/;

	static native WebSocketJS createSocket(String url, JsArrayString protocols)/*-{
		return new $wnd.WebSocket(url, protocols);
	}-*/;
}
