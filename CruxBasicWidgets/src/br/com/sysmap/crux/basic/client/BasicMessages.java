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
 * Messages for basic components
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface BasicMessages extends Messages
{
	@DefaultMessage("[basic-components - 001] - The DisclosurePanel ''{0}'' must contains a required child element to describe the widget and an optional first element to describe the header.")
	String disclosurePanelInvalidChildrenElements(String widgetId);
	@DefaultMessage("[basic-components - 002] - The items of MenuBar ''{0}'' must contains the required attribute _itemType (with one of the values: ''text'', ''html'', ''separator'').")
	String menuBarItemTypeEmpty(String widgetId);
	@DefaultMessage("[basic-components - 003] - The items of Tree ''{0}'' must contains the attribute _text or be a valid widget declaration.")
	String treeInvalidTreeItem(String id);
	@DefaultMessage("[basic-components - 004] - The items of HorizontalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''left'', ''right''.")
	String horizontalSplitPanelInvalidPosition(String id);
	@DefaultMessage("[basic-components - 005] - The items of VerticalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''top'', ''bottom''.")
	String verticalSplitPanelInvalidPosition(String id);
	@DefaultMessage("[basic-components - 006] - Error preparing the cells for FlexTable ''{0}'': Row and Col indexes must be greater or equals 0.")
	String flexTableInvalidRowColIndexes(String widgetId);
	@DefaultMessage("[basic-components - 007] - Error preparing the cells for Grid ''{0}'': Row and Col indexes must be greater or equals 0.")
	String gridInvalidRowColIndexes(String widgetId);
	@DefaultMessage("[basic-components - 008] - Error in ScrollPanel ''{0}''.Error ensuring visibility for component ''{1}''.")
	String scrollPanelWidgetNotFound(String widgetId, String visibleWidget);
	@DefaultMessage("[basic-components - 009] - Error adding widget ''{0}'' on parent Stackpanel ''{1}''.")
	String stackPanelIvalidChild(String idChild, String idParent);
	@DefaultMessage("[basic-components - 009] - Error adding widget ''{0}'' on parent Dockpanel ''{1}''. Invalid direction.")
	String dockPanelInvalidDirection(String id, String id2);

}
