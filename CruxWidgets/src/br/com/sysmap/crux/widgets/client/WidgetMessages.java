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
package br.com.sysmap.crux.widgets.client;

import com.google.gwt.i18n.client.Messages;

public interface WidgetMessages extends Messages
{
	@DefaultMessage("[crux-widgets - 001] - Filterable widget ''{0}'' not found during Filter instantiation. Please, check the given filterable id.")
	String filterableNotFoundWhenInstantiantingFilter(String filterable);

	@DefaultMessage("[crux-widgets - 002] - formattable widget must use setUnformattedValue value instead of setValue.")
	String formattableWidgetMustUseSetUnformattedValue();

	@DefaultMessage("[crux-widgets - 003] - The attribute formatter is required for MaskedTextBox.")
	String maskedTextBoxFormatterRequired();

	@DefaultMessage("[crux-widgets - 004] - The formatter {0} was not found on this screen.")
	String maskedTextBoxFormatterNotFound(String formatter);
	
	@DefaultMessage("[crux-widgets - 005] - The operation <code>getBindedObject()</code> is only supported in grids whose datasources implement <code>BindableDataSource</code>.")
	String getBindedObjectNotSupported();

	@DefaultMessage("[crux-widgets - 006] - Could not create widget for grid column ''{0}''")
	String errorCreatingWidgetForColumn(String key);
	
	@DefaultMessage("[crux-widgets - 007] - Found a null element reference when trying to modify it''s styleName property.")
	String nullElementAtSetStyleName();
	
	@DefaultMessage("[crux-widgets - 008] - Empty strings can not be used as a styleName property value.")
	String emptyStringAsStyleNameValue();
	
	@DefaultMessage("[crux-widgets - 009] - It is not possible unregister a column definition.")
	String removingColumnDefinitionByIterator();
	
	@DefaultMessage("[crux-widgets - 010] - There is no sibling tab with the id ''{0}''")
	String tabsControllerNoSiblingTabFound(String tabId);
	
	@DefaultMessage("[crux-widgets - 011] - There is no tab with the id ''{0}''")
	String tabsControllerNoTabFound(String tabId);
	
	@DefaultMessage("[crux-widgets - 012] - It is not possible remove a grid row.")
	String gridDoesNotAllowRemoveRow();

	@DefaultMessage("[crux-widgets - 013] - The formatter {0} was not found on this screen.")
	String maskedLabelFormatterNotFound(String formatter);

	@DefaultMessage("[crux-widgets - 014] - The attribute formatter is required for MaskedLabel.")
	String maskedLabelFormatterRequired();	
	
}