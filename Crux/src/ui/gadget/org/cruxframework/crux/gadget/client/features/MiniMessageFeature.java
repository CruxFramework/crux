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

import com.google.gwt.user.client.Element;


/**
 * Provides access to the minimessage feature.
 */
public interface MiniMessageFeature 
{
	/**
	 * Handler called whe a minimessage is dismessed 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	interface Callback
	{
		void onMessageDismissed();
	}
	
	/**
	 * Create a new factory for miniMessages
	 * @return
	 */
	MiniMessageFactory getMessageFactory();

	/**
	 * Create a new factory for miniMessages
	 * @param moduleId
	 * @return
	 */
	MiniMessageFactory getMessageFactory(String moduleId);

	/**
	 * Create a new factory for miniMessages
	 * @param moduleId
	 * @param htmlContainer
	 * @return
	 */
	MiniMessageFactory getMessageFactory(String moduleId, Element htmlContainer);
}