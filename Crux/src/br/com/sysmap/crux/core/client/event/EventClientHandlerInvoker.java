/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.event;

import com.google.gwt.event.shared.GwtEvent;

public interface EventClientHandlerInvoker 
{
	void invoke(String metodo, GwtEvent<?> sourceEvent, EventProcessor eventProcessor) throws Exception;

	void invoke(String method, CruxEvent<?> sourceEvent, EventProcessor eventProcessor) throws Exception;
	
	void updateScreenWidgets();
}
