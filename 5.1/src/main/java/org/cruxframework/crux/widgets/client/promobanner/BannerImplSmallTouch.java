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