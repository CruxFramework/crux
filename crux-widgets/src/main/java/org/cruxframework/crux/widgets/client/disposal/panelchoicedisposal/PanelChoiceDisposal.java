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
package org.cruxframework.crux.widgets.client.disposal.panelchoicedisposal;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;

/**
 * @author Gesse Dafe
 *
 */
@Templates({
	@Template(name="panelChoiceDisposalLarge", device=Device.all),
	@Template(name="panelChoiceDisposalSmall", device=Device.smallDisplayArrows),
	@Template(name="panelChoiceDisposalSmall", device=Device.smallDisplayTouch)
})
public interface PanelChoiceDisposal extends DeviceAdaptive
{
	void addChoice(String viewId, String viewLabel, String targetView, ViewActivateHandler handler);
	void choose(String targetView, String viewId);
}
