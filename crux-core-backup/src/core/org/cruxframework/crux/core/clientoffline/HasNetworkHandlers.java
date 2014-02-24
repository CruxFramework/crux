/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.clientoffline;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HasNetworkHandlers
{
  /**
   * Adds an {@link NetworkEvent} handler.
   * 
   * @param handler the handler
   * @return the handler registration
   */
  HandlerRegistration addNetworkHandler(NetworkEvent.Handler handler);

  /**
   * Returns whether or not the network is onLine
   * @return true if onLine, false otherwise
   */
  boolean isOnLine();
}
