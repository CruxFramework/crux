/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.resources.client.ImageResource;

/**
 * PromoBanner implementation for small devices with touch
 * @author Thiago da Rosa de Bustamante
 *
 */
class BannerImplSmallTouch extends BannerImplTouch
{
	@Override
    public void setLargeBannersHeight(String height)
    {
		// Do nothing
    }

	@Override
    public String getLargeBannersHeight()
    {
		return null;
    }

	@Override
    public void setSmallBannersHeight(String height)
    {
		setBannersHeight(height);
    }

	@Override
    public String getSmallBannersHeight()
    {
        return getBannersHeight();
    }

	@Override
    public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
    }

	@Override
    public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		// Do nothing
    }

	@Override
    public void addSmallBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		addBanner(image, title, text, styleName, buttonLabel, onclick);
    }

	@Override
    public void addLargeBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		// Do nothing
    }
}