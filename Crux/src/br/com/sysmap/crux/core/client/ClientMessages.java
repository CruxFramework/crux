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
	String eventProcessorServerAutoError();
	String eventProcessorServerRPCError();
	String eventProcessorServerAutoResponseParserError();
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
