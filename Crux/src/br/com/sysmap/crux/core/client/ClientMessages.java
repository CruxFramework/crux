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
	//  eventFactory
	String eventProcessorFactoryInvalidEventType(String evtId, String evtCall, String evtType);
	
	//  event
	String eventProcessorRPCFailure(String errMsg);
	String eventProcessorClientHandlerNotFound(String handler);
	String eventProcessorRPCCallbackNotFound(String handler);
	String eventProcessorRPCCallbackError(String callback);
	String eventProcessorClientError(String call);
	String eventProcessorServerError();
	String eventProcessorRPCError();
	String eventProcessorServerResponseParserError();
	String eventProcessorServerJsonError(String errorCode, String errorMessage);
	String eventProcessorServerJsonInvalidResponse();
	
	//  screenFactory
	String screenFactoryGenericErrorCreateComponent(String errMsg);
	
	// screenSerialization
	String screenSerializationParserError(String errMsg);
	String screenSerializationServerError(String errMsg);
	String screenSerializationInvalidComponent(String componentId);
	
	//  component
	String screenFactoryComponentIdRequired();
	String screenFactoryInvalidComponentParent(String componentId);
	String screenFactoryErrorCreateComponent(String componentId);
	
	String componentFormatterNotFound(String formatterId);
}
