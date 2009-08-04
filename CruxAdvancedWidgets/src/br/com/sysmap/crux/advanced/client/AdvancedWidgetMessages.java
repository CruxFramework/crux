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
package br.com.sysmap.crux.advanced.client;

import com.google.gwt.i18n.client.Messages;

public interface AdvancedWidgetMessages extends Messages
{
	@DefaultMessage("[advanced-widgets - 001] - Filterable widget ''{0}'' not found during Filter instantiation. Please, check the given filterable id.")
	String filterableNotFoundWhenInstantiantingFilter(String filterable);

	@DefaultMessage("[advanced-widgets - 002] - formattable widget must use setUnformattedValue value instead of setValue.")
	String formattableWidgetMustUseSetUnformattedValue();

	@DefaultMessage("[advanced-widgets - 003] - The attribute formatter is required for MaskedTextBox.")
	String maskedTextBoxFormatterRequired();

	@DefaultMessage("[advanced-widgets - 003] - The formatter {0} was not found on this screen.")
	String maskedTextBoxFormatterNotFound(String formatter);
}
