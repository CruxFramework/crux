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
package org.cruxframework.cruxsite.client.resource.small;

import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

/**
 * @author Gesse Dafe
 *
 */
@Resource(value="siteDeviceResources", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch})
public interface SiteResourcesSmall extends ClientBundle
{
	@Source("cssSiteSmall.css")
	CssSiteSmall css();
	
	@Source("top-menu-disposal-header-logo.jpg")
	DataResource topMenuDisposalHeaderLogo();
	
	@Source("noise.png")
	DataResource noiseBg();
	
	@Source("banner-cross-device@2x.jpg")
	ImageResource bannerCrossDevice();
	
	@Source("banner-fast@2x.jpg")
	ImageResource bannerFast();
	
	@Source("banner-productivity@2x.jpg")
	ImageResource bannerProductivity();
	
	@Source("banner-right-arrow.png")
	ImageResource bannerRightArrow();
	
	@Source("banner-left-arrow.png")
	ImageResource bannerLeftArrow();
	
	@Source("view-text-panel-bg.png")
	DataResource viewTextPanelBg();
	
	@Source("mountain-bg.png")
	DataResource mountainBg();
	
	@Source("crux-header-logo@4x.png")
	DataResource cruxHeaderLogo();
	
	@Source("br-flag.png")
	DataResource brFlag();
	
	@Source("uk-flag.png")
	DataResource ukFlag();
	
	@Source("showcase-tip.png")
	DataResource showcaseTip();
	
	@Source("logo-vivo.png")
	ImageResource logoVivo();
	
	@Source("logo-tim.png")
	ImageResource logoTim();
	
	@Source("logo-unimed.png")
	ImageResource logoUnimed();
	
	@Source("logo-natura.png")
	ImageResource logoNatura();
	
	@Source("icon-arrow-btn.png")
	DataResource iconArrowBtn();
	
	@Source("crux-footer-logo@4x.png")
	ImageResource cruxFooterLogo();

	@Source("triggo-footer-logo.png")
	ImageResource triggoFooterLogo();
	
	@Source("logo-sysmap.png")
	ImageResource logoSysmap();
	
	@Source("user-img.png")
	DataResource userBg();
	
	@Source("icon-3stars@4x.png")
	DataResource icon3stars();
	
	@Source("icon-2stars@4x.png")
	DataResource icon2stars();
	
	@Source("icon-1star@4x.png")
	DataResource icon1star();
	
	@Source("icon-mobile-menu@4x.png")
	DataResource mobileMenuButton();
	
	@Source("bg-helloworld.png")
	ImageResource bgHelloWorld();
	
	@Source("bg-showcase.png")
	ImageResource bgShowcase();
}
