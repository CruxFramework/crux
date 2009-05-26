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
	@DefaultMessage("[screenFactory 001] - Error Creating widget: ''{0}''. See Log for more detail.")
	String screenFactoryGenericErrorCreateWidget(String errMsg);
	@DefaultMessage("[screenFactory 002] - Can not found widgetFactory for type: ''{0}''.")
	String screenFactoryWidgetFactoryNotFound(String type);

	//  widget
	@DefaultMessage("[widget - 001] - The id attribute is required for CRUX widgets.")
	String screenFactoryWidgetIdRequired();
	@DefaultMessage("[widget - 002] - The widget ''{0}'' does not implement HasWidgets and can not contains any other widget.")
	String screenFactoryInvalidWidgetParent(String widgetId);
	@DefaultMessage("[widget - 003] - Can not create widget ''{0}''. Verify the widget type.")
	String screenFactoryErrorCreateWidget(String widgetId);
	@DefaultMessage("[widget - 004] - Formatter ''{0}'' not found.")
	String widgetFormatterNotFound(String formatterId);
	@DefaultMessage("[widget - 005] - The span element must contains children.")
	String widgetFactoryEnsureChildrenSpansEmpty();
	@DefaultMessage("[widget - 006] - The span element must contains at least one child.")
	String widgetFactoryEnsureFirstChildSpanOrphanElement();
	@DefaultMessage("[widget - 007] - The span element does not represents a widget.")
	String widgetFactoryEnsureWidgetFail();
	@DefaultMessage("[widget - 008] - The element is not a span.")
	String widgetFactoryEnsureSpanFail();
	@DefaultMessage("[widget - 009] - The widget ''{0}'' was added to a pure HTML node where already exists other children. In this case, crux can not ensure an order for this widget. To solve this, If you must use panels as widget parent.")
	String screenFactoryNonDeterministicWidgetPositionInParent(String widgetId);
	
	@DefaultMessage("[callback - 001] - An Invalid controller was passed to AsyncCallbackAdapter.")
	String asyncCallbackInvalidHandlerError();
	
}
