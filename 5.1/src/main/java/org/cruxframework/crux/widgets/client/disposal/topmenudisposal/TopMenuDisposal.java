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
package org.cruxframework.crux.widgets.client.disposal.topmenudisposal;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="topMenuDisposalLarge", device=Device.all),
	@Template(name="topMenuDisposalSmall", device=Device.smallDisplayArrows),
	@Template(name="topMenuDisposalSmall", device=Device.smallDisplayTouch)
})
public interface TopMenuDisposal extends DeviceAdaptive
{
	public static final String HASH = "#";
	
	void showMenu();
	void addMenuEntry(String label, String targetView);
	void showView(String viewName, boolean saveHistory);
	void setDefaultView(String viewName);
}
