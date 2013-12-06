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
package org.cruxframework.crux.widgets.client.storyboard;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="storyboardLarge", device=Device.largeDisplayArrows),
	@Template(name="storyboardLarge", device=Device.largeDisplayTouch),
	@Template(name="storyboardLargeMouse", device=Device.largeDisplayMouse),
	@Template(name="storyboardSmall", device=Device.smallDisplayArrows),
	@Template(name="storyboardSmall", device=Device.smallDisplayTouch)
})
public interface Storyboard extends DeviceAdaptive, IndexedPanel, HasSelectionHandlers<Integer>, HasWidgets
{
	String getLargeDeviceItemWidth();
	void setLargeDeviceItemWidth(String width);
	String getSmallDeviceItemHeight();
	void setSmallDeviceItemHeight(String height);
	String getLargeDeviceItemHeight();
	void setLargeDeviceItemHeight(String height);
	void setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant value);
	void setVerticalAlignment(HasVerticalAlignment.VerticalAlignmentConstant value);
	void setLargeDeviceItemHeight(IsWidget child, String height);
	void setSmallDeviceItemHeight(IsWidget child, String height);
	void setLargeDeviceItemWidth(IsWidget child, String width);
	void setHorizontalAlignment(IsWidget child, HasHorizontalAlignment.HorizontalAlignmentConstant value);
	void setVerticalAlignment(IsWidget child, HasVerticalAlignment.VerticalAlignmentConstant value);
	void setFixedHeight(boolean fixedHeight);
	void setFixedWidth(boolean fixedWidth);
}
