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
package org.cruxframework.crux.core.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Contains all client messages used by Crux Framework
 * @author Thiago da Rosa de Bustamante -
 */
public interface ClientMessages extends Messages
{
	//  event
	@DefaultMessage("Client Controller Named ''{0}'' not found.")
	String eventProcessorClientControllerNotFound(String controller);
	@DefaultMessage("Error running client method ''{0}''. Message: ''{1}''")
	String eventProcessorClientError(String call, String errMsg);
	@DefaultMessage("Invalid declaration for handler method ''{0}''. Correct syntaxe is <controller.method>.")
	String eventFactoryInvalidHandlerMethodDeclaration(String evt);
	@DefaultMessage("Error loading screen fragment {0}.")
	String viewFactoryCanNotBeLoaded(String controller);
	@DefaultMessage("Please wait. Your request is still being processed.")
	String methodIsAlreadyBeingProcessed();
	@DefaultMessage("Client Controller Named ''{0}'' does not implement CrossDocument interface and can not be called outside document.")
	String crossDocumentInvalidCrossDocumentController(String controller);
	@DefaultMessage("Error on cross document call. No responde received from method.")
	String crossDocumentInvocationError();
	@DefaultMessage("Error on cross document call. Invalid Target.")
	String crossDocumentInvalidTarget();
	@DefaultMessage("Error on cross document call: Screen [{0}]. Error Message: [{1}].")
	String crossDocumentInvocationGeneralError(String screenId, String errorMessage);
	@DefaultMessage("Can not identify the method to be called.")
	String crossDocumentCanNotIdentifyMethod();
	@DefaultMessage("Can not find the method to be called.")
	String crossDocumentMethodNotFound();
	@DefaultMessage("WriterStream is not open.")
	String crossDocumentSerializationErrorStreamClosed();
	
	//  screenFactory
	@DefaultMessage("The widget ''{0}'' is a layout panel that does not have its dimensions defined. Explicity define it, or append it directly on body element.")
	String screenFactoryLayoutPanelWithoutSize(String widgetId);
	
	// screen
	@DefaultMessage("The id attribute is required for CRUX Screens.")
	String screenFactoryScreenIdRequired();
	
	@DefaultMessage("Error creating view [{0}]. ")
	String viewContainerErrorCreatingView(String id);
	
	@DefaultMessage("An Invalid controller was passed to AsyncCallbackAdapter.")
	String asyncCallbackInvalidHandlerError();

	@DefaultMessage("An Invalid object was passed to update screen or DTOs.")
	String screenInvalidObjectError();
	@DefaultMessage("Creating the view for screen {0}.")
	String viewContainerCreatingView(String identifier);
	@DefaultMessage("View {0} created.")
	String viewContainerViewCreated(String identifier);
	@DefaultMessage("View {0} rendered.")
	String viewContainerViewRendered(String identifier);
	@DefaultMessage("This application contains components that are not fully supported by your brownser.")
	String viewContainerUnsupportedWidget();
	@DefaultMessage("To use this feature you must enabled compatibility with Crux 2 old interfaces.")
	String screenFactoryCrux2OldInterfacesCompatibilityDisabled();
	
	
	@DefaultMessage("Type ''{0}'' can not be shared between modules. Only primitives (and its wrappers), Strings, Dates, Arrays (not multidimesional) and classes implementing CruxSerializable can be used.")
	String moduleComunicationInvalidParamType(String name);

	@DefaultMessage("Error loading dataSource data: {0}")
	String localDataSourceErrorLoadingData(String message);
	@DefaultMessage("Error loading dataSource remote data: {0}")
	String remoteDataSourceErrorLoadingData(String message);
	@DefaultMessage("Error processing requested operation. DataSource is not loaded yet.")
	String dataSourceNotLoaded();
	@DefaultMessage("DataSource has changes on page. You must save or discard them before perform this operation.")
	String remoteDataSourcePageDirty();
	@DefaultMessage("The column {0} can not be sorted.")
	String dataSourceErrorColumnNotComparable(String columnName);
	
	@DefaultMessage("Found a null element reference when trying to modify it''s styleName property.")
	String nullElementAtSetStyleName();	
	@DefaultMessage("Empty strings can not be used as a styleName property value.")
	String emptyStringAsStyleNameValue();

	@DefaultMessage("Crux Engine is already loaded.")
	String cruxAlreadyInitializedError();
	
	@DefaultMessage("Calling a cross document method. Screen[{0}], Controller[{1}], Method[{2}], Target[{3}]")
	String screenAccessorCallingCrossDocument(String screenId, String controller, String method, String target);
	@DefaultMessage("Cross document method executed. Screen[{0}], Controller[{1}], Method[{2}], Target[{3}]")
	String screenAccessorCrossDocumentExecuted(String screenId, String controller, String method, String target);

	@DefaultMessage("Invalid value for style property: [{0}]:[{1}]")
	String styleErrorInvalidProperty(String camelizedName, String value);
	@DefaultMessage("Can not retrieve the widget [{1}] from view [{0}]. View is not loaded. Load this view into a ViewContainer first.")
	String viewNotInitialized(String viewId, String widgetId);

	@DefaultMessage("Informed object is not aware of current view. This method must be used to discover current view for controllers, datasouces or other ViewAware objects.")
	String viewOjectIsNotAwareOfView();

	@DefaultMessage("Resources [{0}] initialized.")
	String resourcesInitialized(String resourceId);
	
	@DefaultMessage("Css Resource [{0}] injected.")
	String resourceCsssInjected(String cssClassName);

	@DefaultMessage("Unexpected error calling rest service. Error [{0}].")
	String restServiceUnexpectedError(String errorMesg);
	@DefaultMessage("Can not invoke write operation for uri[{0}] without previously loading it.")
	String restServiceMissingStateEtag(String uri);
}
