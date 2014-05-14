package org.cruxframework.crux.widgets.client.promobanner;

import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;

/**
 * A cross device widget to show a banner carrousel of images. This widget accept different images for small 
 * and for large devices and can be controlled by mouses, touches or arrows, according to the running device.
 *    
 * @author Thiago da Rosa e Bustamante
 */
public class PromoBanner extends Composite
{
	private BannerImpl impl;
	
	/**
	 * Constructor
	 */
	public PromoBanner()
	{
		impl = GWT.create(BannerImpl.class);
		initWidget(impl);
		setStyleName("crux-PromoBanner");
	}

	public void setLargeBannersHeight(String height)
	{
		impl.setLargeBannersHeight(height);
	}
	
	public String getLargeBannersHeight()
	{
		return impl.getLargeBannersHeight();
	}

	public void setSmallBannersHeight(String height)
	{
		impl.setSmallBannersHeight(height);
	}
	
	public String getSmallBannersHeight()
	{
		return impl.getSmallBannersHeight();
	}

	public void setTransitionDuration(int transitionDuration)
	{
		impl.setTransitionDuration(transitionDuration);
	}
	
	public int getTransitionDuration()
	{
		return impl.getTransitionDuration();
	}

	public void setAutoTransitionInterval(int autoTransitionInterval)
	{
		impl.setAutoTransitionInterval(autoTransitionInterval);
	}
	
	public int getAutoTransitionInterval()
	{
		return impl.getAutoTransitionInterval();
	}

	public void addSmallBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addSmallBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addLargeBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addLargeBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addDefaultBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addDefaultBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}

	public void addSmallBanner(ImageResource image, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addSmallBanner(image, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addLargeBanner(ImageResource image, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addLargeBanner(image, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addDefaultBanner(ImageResource image, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addDefaultBanner(image, title, text, styleName, buttonLabel, onclick);
	}
}
