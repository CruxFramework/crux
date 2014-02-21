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
package org.cruxframework.crux.gadget.client.features;

import org.cruxframework.crux.gadget.client.features.MiniMessageFeature.Callback;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MiniMessageFactory extends JavaScriptObject
{
	protected MiniMessageFactory(){}

	/**
	 * Create a message with a close [x] button, that allow user to dismiss the message.
	 * @param message
	 * @param callback
	 * @return
	 */
	public final native MiniMessage createDismissibleMessage(String message, Callback callback)/*-{
	    var miniMessage =[];
	    if (callback != null)
	    {
	    	miniMessage.element = this.createDismissibleMessage(message, function(){
	    		callback.@org.cruxframework.crux.gadget.client.features.MiniMessageFeature.Callback::onMessageDismissed()();
	    	});
	    }
	    else
	    {
	    	miniMessage.element = this.createDismissibleMessage(message);
	    }
	    
	    return miniMessage;
	}-*/;
	
	/**
	 * Creates a static message that can only be dismissed programmatically (by calling dismissMessage()).
	 * @param message
	 * @return
	 */
	public final native MiniMessage createStaticMessage(String message)/*-{
	    var miniMessage =[];
	    miniMessage.element = this.createStaticMessage(message);
	    return miniMessage;
	}-*/;

	/**
	 * Creates a message that displays for the specified number of seconds. When the timer expires, 
	 * the message is dismissed and the optional callback function is executed.
	 * 
	 * @param message
	 * @param seconds
	 * @param callback
	 * @return
	 */
	public final native MiniMessage createTimerMessage(String message, int seconds, Callback callback)/*-{
	    var miniMessage =[];
	    if (callback != null)
	    {
	    	miniMessage.element = this.createTimerMessage(message, seconds, function(){
	    		callback.@org.cruxframework.crux.gadget.client.features.MiniMessageFeature.Callback::onMessageDismissed()();
	    	});
	    }
	    else
	    {
	    	miniMessage.element = this.createTimerMessage(message, seconds);
	    }
	    
	    return miniMessage;
	}-*/;
	
	/**
	 * Dismiss the message received as parameter
	 * @param message
	 */
	public final native void dismissMessage(MiniMessage message)/*-{
		this.dismissMessage(message.element);
		message.element = null;
	}-*/;
}
