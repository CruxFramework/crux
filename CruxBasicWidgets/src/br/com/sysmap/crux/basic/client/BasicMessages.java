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
package br.com.sysmap.crux.basic.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Messages for basic widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface BasicMessages extends Messages
{
	@DefaultMessage("[basic-widgets - 001] - The DisclosurePanel ''{0}'' must contains a required child element to describe the widget and an optional first element to describe the header.")
	String disclosurePanelInvalidChildrenElements(String widgetId);
	@DefaultMessage("[basic-widgets - 002] - The items of MenuBar ''{0}'' must contains the required attribute _itemType (with one of the values: ''text'', ''html'', ''separator'').")
	String menuBarItemTypeEmpty(String widgetId);
	@DefaultMessage("[basic-widgets - 003] - The items of Tree ''{0}'' must contains the attribute _text or be a valid widget declaration.")
	String treeInvalidTreeItem(String id);
	@DefaultMessage("[basic-widgets - 004] - The items of HorizontalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''left'', ''right''.")
	String horizontalSplitPanelInvalidPosition(String id);
	@DefaultMessage("[basic-widgets - 005] - The items of VerticalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''top'', ''bottom''.")
	String verticalSplitPanelInvalidPosition(String id);
	@DefaultMessage("[basic-widgets - 006] - Error preparing the cells for FlexTable ''{0}'': Row and Col indexes must be greater or equals 0.")
	String flexTableInvalidRowColIndexes(String widgetId);
	@DefaultMessage("[basic-widgets - 007] - Error preparing the cells for Grid ''{0}'': Row and Col indexes must be greater or equals 0.")
	String gridInvalidRowColIndexes(String widgetId);
	@DefaultMessage("[basic-widgets - 008] - Error in ScrollPanel ''{0}''.Error ensuring visibility for component ''{1}''.")
	String scrollPanelWidgetNotFound(String widgetId, String visibleWidget);
	@DefaultMessage("[basic-widgets - 009] - Error adding widget ''{0}'' on parent Stackpanel ''{1}''.")
	String stackPanelIvalidChild(String idChild, String idParent);
	@DefaultMessage("[basic-widgets - 009] - Error adding widget ''{0}'' on parent Dockpanel ''{1}''. Invalid direction.")
	String dockPanelInvalidDirection(String id, String id2);
	@DefaultMessage("[basic-widgets - 010] - The menu item ''{0}'' has no child items and has no command. Ignoring item...")
	String menubarItemWithoutChildrenOrCommand(String itemCaption);

}
