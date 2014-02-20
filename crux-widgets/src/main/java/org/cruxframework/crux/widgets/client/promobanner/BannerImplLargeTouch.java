package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.resources.client.ImageResource;

/**
 * PromoBanner implementation for large devices with touch
 * @author Thiago da Rosa de Bustamante
 *
 */
class BannerImplLargeTouch extends BannerImplTouch
{
	@Override
    public void setLargeBannersHeight(String height)
    {
		setBannersHeight(height);
    }

	@Override
    public String getLargeBannersHeight()
    {
        return getBannersHeight();
    }

	@Override
    public void setSmallBannersHeight(String height)
    {
        // Do nothing
    }

	@Override
    public String getSmallBannersHeight()
    {
        return null;
    }

	@Override
    public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
        // Do nothing
    }

	@Override
    public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
    }

	@Override
    public void addSmallBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
        // Do nothing
    }

	@Override
    public void addLargeBanner(ImageResource image, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		addBanner(image, title, text, styleName, buttonLabel, onclick);
    }
}

