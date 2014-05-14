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
package org.cruxframework.crux.widgets.client.toptoolbar;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.widgets.client.event.openclose.HasBeforeOpenAndBeforeCloseHandlers;

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="topToolBarLarge", device=Device.all),
	@Template(name="topToolBarSmallArrows", device=Device.smallDisplayArrows),
	@Template(name="topToolBarSmallTouch", device=Device.smallDisplayTouch)
})
public interface TopToolBar extends DeviceAdaptive, IndexedPanel, HasWidgets,
								    HasBeforeOpenAndBeforeCloseHandlers, 
								    HasOpenHandlers<TopToolBar>, HasCloseHandlers<TopToolBar>
{
	void setGripWidget(Widget widget);
	Widget getGripWidget();
	void close();
	void open();
	void toggle();
	void setGripHeight(int height);
	int getGripHeight();
}
