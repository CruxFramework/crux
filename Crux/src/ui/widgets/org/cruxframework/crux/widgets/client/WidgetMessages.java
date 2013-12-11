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
package org.cruxframework.crux.widgets.client;

import com.google.gwt.i18n.client.Messages;

public interface WidgetMessages extends Messages
{
	@DefaultMessage("Filterable widget ''{0}'' not found during Filter instantiation. Please, check the given filterable id.")
	String filterableNotFoundWhenInstantiantingFilter(String filterable);
	
	@DefaultMessage("Could not create widget for grid column ''{0}''")
	String errorCreatingWidgetForColumn(String key);
	
	@DefaultMessage("It is not possible unregister a column definition.")
	String removingColumnDefinitionByIterator();
	
	@DefaultMessage("There is no sibling tab with the id ''{0}''")
	String tabsControllerNoSiblingTabFound(String tabId);
	
	@DefaultMessage("There is no tab with the id ''{0}''")
	String tabsControllerNoTabFound(String tabId);
	
	@DefaultMessage("Random paging is only supported in Grids whose DataSources are instances of MeasurablePagedDataSource.")
	String gridRandomPagingNotSupported();

	@DefaultMessage("< Back")
	String wizardBackCommand();	

	@DefaultMessage("Next >")
	String wizardNextCommand();	

	@DefaultMessage("Cancel")
	String wizardCancelCommand();	

	@DefaultMessage("Finish")
	String wizardFinishCommand();

	@DefaultMessage("WizardControlBar is not associated with any Wizard.")
	String wizardControlBarOrphan();
	
	@DefaultMessage("A started timer can not be modified.")
	String startedTimerCannotBeModified();

	@DefaultMessage("TextArea widget only accepts maxLength greater or equals zero.")
	String textAreaInvalidMaxLengthParameter();

	@DefaultMessage("Only one WizardPage widget can be used on a single screen.")
	String wizardPageDuplicatedWidgetOnPage();

	@DefaultMessage("This wizard already contains a step with id {0}.")
	String wizardDuplicatedStep(String id);

	@DefaultMessage("Step {0} not found.")
	String wizardStepNotFound(String stepId);

	@DefaultMessage("Wizard does not have a step with index {0}.")
	String wizardStepNotFound(int stepOrder);

	@DefaultMessage("The wizard step with index {0} can not be selected because it is not enabled.")
	String wizardInvalidStepSelected(int step);

	@DefaultMessage("Error runnig the wizard command {0}. Message: {1}.")
	String wizardCommandError(String commandId, String message);

	@DefaultMessage("It is not possible to remove a row from a Grid.")
	String errorItsNotPossibleToRemoveARowmFromAGrid();

	@DefaultMessage("Error sorting the Grid. No DataColumn with the key ''{0}'' was found.")
	String errorGridNoDataColumnFound(String column);

	@DefaultMessage("Wizard does not have any WizardData assigned.")
	String wizardNoSerializerAssigned();

	@DefaultMessage("No pageable widget set for this pager.")
	String pagerNoPageableSet();

	@DefaultMessage("Select Color")
	String colorPickerDialogSelectColor();

	@DefaultMessage("Ok")
	String okLabel();

	@DefaultMessage("Cancel")
	String cancelLabel();
}