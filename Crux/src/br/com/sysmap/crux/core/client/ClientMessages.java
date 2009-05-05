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
package br.com.sysmap.crux.core.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Contains all client messages used by Crux Framework
 * @author Thiago
 *
 */
public interface ClientMessages extends Messages
{
	//  event
	@DefaultMessage("[event 001] - Client Handler Named ''{0}'' not found.")
	String eventProcessorClientHandlerNotFound(String handler);
	@DefaultMessage("[event 002] - Error running client method ''{0}''.")
	String eventProcessorClientError(String call);
	
	//  screenFactory
	@DefaultMessage("[screenFactory 001] - Error Creating component: ''{0}''. See Log for more detail.")
	String screenFactoryGenericErrorCreateComponent(String errMsg);
	
	//  component
	@DefaultMessage("[component - 001] - The id attribute is required for CRUX Components.")
	String screenFactoryComponentIdRequired();
	@DefaultMessage("[component - 002] - The component ''{0}'' only can be attached in a container or directly in the screen.")
	String screenFactoryInvalidComponentParent(String componentId);
	@DefaultMessage("[component - 003] - Can not create component ''{0}''. Verify the component type.")
	String screenFactoryErrorCreateComponent(String componentId);
	@DefaultMessage("[component - 004] - Formatter ''{0}'' not found.")
	String componentFormatterNotFound(String formatterId);
}
