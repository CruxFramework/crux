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
package org.cruxframework.crux.widgets.client.slideshow;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.rollingpanel.RollingPanel;
import org.cruxframework.crux.widgets.client.slideshow.data.Photo;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowThumbnails extends SlideshowComponent
{
	private SlideshowComponent impl;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class SlideshowThumbnailsImpl extends SlideshowComponent
	{
		protected RollingPanel thumbnails;
		
		@Override
	    public void onAlbumLoaded()
	    {
			assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
			thumbnails.clear();
			PhotoAlbum album = getSlideshow().getAlbum();
			if (album != null)
			{
				FastList<Photo> images = album.getImages();
				if (images != null)
				{
					for (int i=0; i< images.size(); i++)
					{
						Widget image = createThumbnail(images.get(i), i);
						thumbnails.add(image);
					}
				}
			}
	    }
		
		@Override
	    public void onPhotoLoaded(int previousIndex, int nextIndex)
	    {
			assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
			if (nextIndex >= 0)
			{
				Widget nextWidget = thumbnails.getWidget(nextIndex);
				nextWidget.addStyleDependentName("selected");
				thumbnails.scrollToWidget(nextWidget);
			}
			else
			{
				thumbnails.setScrollPosition(0);
			}
			if (previousIndex >= 0)
			{
				Widget previousWidget = thumbnails.getWidget(previousIndex);
				previousWidget.removeStyleDependentName("selected");
			}
	    }

		protected SimplePanel createThumbnail(Photo photo, final int index)
	    {
			Image image = new Image(photo.getThumbnail());
			image.setWidth(photo.getThumbnailWidth()+"px");
			image.setHeight(photo.getThumbnailHeight()+"px");
			String description = photo.getDescription();
			if (!StringUtils.isEmpty(description))
			{
				if (description.length() > 50)
				{
					description = description.substring(0, 50)+" ...";
				}
				image.setTitle(description);
			}
			SimplePanel imageWrapper = new SimplePanel();
			imageWrapper.add(image);
			imageWrapper.getElement().getStyle().setMargin(8, Unit.PX);
			imageWrapper.setStyleName("thumbnail");
		    return imageWrapper;
	    }

		@Override
	    protected Widget createMainWidget()
	    {
			thumbnails = new RollingPanel();
			thumbnails.setWidth("100%");
		    return thumbnails;
	    }
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class ThumbnailsNoTouchImpl extends SlideshowThumbnailsImpl
	{
		@Override
		protected SimplePanel createThumbnail(Photo photo, final int index)
	    {
			SimplePanel thumbnail = super.createThumbnail(photo, index);
			Image image = (Image) thumbnail.getWidget();
			image.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					getSlideshow().showPhoto(index);
				}
			});
		    return thumbnail;
	    }
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class ThumbnailsTouchImpl extends SlideshowThumbnailsImpl
	{
		@Override
		protected SimplePanel createThumbnail(Photo photo, final int index)
	    {
			SimplePanel thumbnail = super.createThumbnail(photo, index);
			Image image = (Image) thumbnail.getWidget();
			image.addTouchEndHandler(new TouchEndHandler()
			{
				@Override
				public void onTouchEnd(TouchEndEvent event)
				{
					getSlideshow().showPhoto(index);
					event.preventDefault();
					event.stopPropagation();
				}
			});
		    return thumbnail;
	    }
	}

	@Override
    protected Widget createMainWidget()
    {
		impl = GWT.create(SlideshowThumbnailsImpl.class);
		impl.setStyleName("crux-SlideshowThumbnails");
	    return impl;
    }
	
	@Override
	protected void onAlbumLoaded()
	{
		impl.onAlbumLoaded();
	}
	
	@Override
	protected void onPhotoLoaded(int previousIndex, int nextIndex)
	{
		impl.onPhotoLoaded(previousIndex, nextIndex);
	}
	
	@Override
	public void setSlideShow(Slideshow slideshow)
	{
	    super.setSlideShow(slideshow);
	    impl.setSlideShow(slideshow);
	}
}
