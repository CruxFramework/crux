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


/**
 * Provides access to the pubsub feature.
 * @author Thiago da Rosa de Bustamante
 */
public interface PubsubFeature 
{
	/**
	 * Handler for messages received from an observed channel
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	interface Callback
	{
		void onMessage(String message);
	}
	
	/**
	 * Publish a message on the specified channel. All subscribers of the channel will be notified, 
	 * through its Callback objects.
	 * @param channelName
	 * @param message
	 */
	void publish(String channelName, String message);
	
	/**
	 * Subscribes to the specified channel. Whenever any gadget publish some message to the channel, 
	 * the callback object will be informed (through onMessage() method).
	 * @param channelName
	 * @param callback
	 */
	void subscribe(String channelName, Callback callback);
  
	/**
	 * Unsubscribes from the specified channel. No more notifications will be received.
	 * @param channelName
	 */
	void unsubscribe(String channelName);
}