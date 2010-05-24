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
	@DefaultMessage("[crux-widgets 001] - Filterable widget ''{0}'' not found during Filter instantiation. Please, check the given filterable id.")
	String filterableNotFoundWhenInstantiantingFilter(String filterable);

	@DefaultMessage("[crux-widgets 002] - formattable widget must use setUnformattedValue value instead of setValue.")
	String formattableWidgetMustUseSetUnformattedValue();

	@DefaultMessage("[crux-widgets 003] - The attribute formatter is required for MaskedTextBox.")
	String maskedTextBoxFormatterRequired();

	@DefaultMessage("[crux-widgets 004] - The formatter {0} was not found on this screen.")
	String maskedTextBoxFormatterNotFound(String formatter);
	
	@DefaultMessage("[crux-widgets 005] - The operation <code>getBindedObject()</code> is only supported in grids whose datasources implement <code>BindableDataSource</code>.")
	String getBindedObjectNotSupported();

	@DefaultMessage("[crux-widgets 006] - Could not create widget for grid column ''{0}''")
	String errorCreatingWidgetForColumn(String key);
	
	@DefaultMessage("[crux-widgets 009] - It is not possible unregister a column definition.")
	String removingColumnDefinitionByIterator();
	
	@DefaultMessage("[crux-widgets 010] - There is no sibling tab with the id ''{0}''")
	String tabsControllerNoSiblingTabFound(String tabId);
	
	@DefaultMessage("[crux-widgets 011] - There is no tab with the id ''{0}''")
	String tabsControllerNoTabFound(String tabId);
	
	@DefaultMessage("[crux-widgets 012] - It is not possible remove a grid row.")
	String gridDoesNotAllowRemoveRow();

	@DefaultMessage("[crux-widgets 013] - The formatter {0} was not found on this screen.")
	String maskedLabelFormatterNotFound(String formatter);

	@DefaultMessage("[crux-widgets 014] - The attribute formatter is required for MaskedLabel.")
	String maskedLabelFormatterRequired();	
	
	@DefaultMessage("[crux-widgets 015] - Random paging is only supported in Grids whose DataSources are instances of MeasurablePagedDataSource.")
	String gridRandomPagingNotSupported();
	
	@DefaultMessage("[crux-widgets 016] - No pageable widget set for this pager.")
	String pagerNoPageableSet();

	@DefaultMessage("[crux-widgets 017] - Grid {0} has no column.")
	String gridDoesNotHaveColumns(String gridId);

	@DefaultMessage("< Back")
	String wizardBackCommand();	

	@DefaultMessage("Next >")
	String wizardNextCommand();	

	@DefaultMessage("Cancel")
	String wizardCancelCommand();	

	@DefaultMessage("Finish")
	String wizardFinishCommand();

	@DefaultMessage("[crux-widgets 018] - WizardControlBar is not associated with any Wizard.")
	String wizardControlBarOrphan();
	
	@DefaultMessage("[crux-widgets 019] - A started timer can not be modified.")
	String startedTimerCannotBeModified();

	@DefaultMessage("[crux-widgets 020] - TextArea widget only accepts maxLength greater or equals zero.")
	String textAreaInvalidMaxLengthParameter();

	@DefaultMessage("[crux-widgets 021] - Error while communicating with inner page step.")
	String wizardPageStepErrorInvokingEventOnInnerPage();

	@DefaultMessage("[crux-widgets 022] - Error while communicating with wizard widget.")
	String wizardPageStepErrorInvokingEventOuterPage();

	@DefaultMessage("[crux-widgets 023] - Only one WizardPage widget can be used on a single screen.")
	String wizardPageDuplicatedWidgetOnPage();

	@DefaultMessage("[crux-widgets 024] - This wizard already contains a step with id {0}.")
	String wizardDuplicatedStep(String id);

	@DefaultMessage("[crux-widgets 025] - Step {0} not found.")
	String wizardStepNotFound(String stepId);

	@DefaultMessage("[crux-widgets 026] - Wizard does not have a step with index {0}.")
	String wizardStepNotFound(int stepOrder);

	@DefaultMessage("[crux-widgets 027] - The wizard step with index {0} can not be selected because it is not enabled.")
	String wizardInvalidStepSelected(int step);

	@DefaultMessage("[crux-widgets 028] - Error runnig the wizard command {0}. Message: {1}.")
	String wizardCommandError(String commandId, String message);

	@DefaultMessage("[crux-widgets 029] - Error for generating invoker wrapper: Invalid Method signature: {0}. A valid signature must have the form [methodName][OnTab][tabId]")
	String tabsControllerInvalidSignature(String signature);

	@DefaultMessage("[crux-widgets 030] - It is not possible to remove a row from a Grid.")
	String errorItsNotPossibleToRemoveARowmFromAGrid();
}