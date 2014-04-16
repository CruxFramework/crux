/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.themes.widgets.xstandard.client.resource.small;

import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

/**
 * @author Gesse Dafe
 *
 */
@Resource(value="xStandardResources", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch})
public interface XStandardResourcesSmall extends ClientBundle
{
	@Source("cssXStandardSmall.css")
	CssXStandardSmall css();
	
	@Source("top-menu-disposal-menu-btn.png")
	DataResource topMenuDisposalShowMenuButton();
	
	@Source("svg-icon-close.svg")
	DataResource svgIconClose();
	
	@Source("svg-icon-danger.svg")
	DataResource svgIconDanger();
	
	@Source("svg-icon-warning.svg")
	DataResource svgIconWarning();
	
	@Source("svg-icon-success.svg")
	DataResource svgIconSuccess();
	
	@Source("detail-icon.png")
	DataResource detailIcon();

}
