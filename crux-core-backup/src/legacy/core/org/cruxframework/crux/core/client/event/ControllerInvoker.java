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
package org.cruxframework.crux.core.client.event;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.ScreenBindableObject;

/**
 * Defines a contract for event handling. All generated controllers implement this interface
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public interface ControllerInvoker extends ScreenBindableObject
{
	/**
	 * Called by Crux Events engine to dispatch an event.
	 * 
	 * @param method
	 * @param sourceEvent
	 * @param fromOutOfModule
	 * @param eventProcessor
	 * @throws Exception
	 */
	void invoke(String method, Object sourceEvent, boolean fromOutOfModule, EventProcessor eventProcessor) throws Exception;
}
