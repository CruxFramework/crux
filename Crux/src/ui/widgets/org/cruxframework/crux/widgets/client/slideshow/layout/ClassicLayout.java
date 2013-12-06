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
package org.cruxframework.crux.widgets.client.slideshow.layout;

import org.cruxframework.crux.widgets.client.slideshow.Slideshow;
import org.cruxframework.crux.widgets.client.slideshow.Slideshow.Layout;
import org.cruxframework.crux.widgets.client.slideshow.Slideshow.Name;
import org.cruxframework.crux.widgets.client.slideshow.SlideshowAlbumTitle;
import org.cruxframework.crux.widgets.client.slideshow.SlideshowNavigator;
import org.cruxframework.crux.widgets.client.slideshow.SlideshowPhotoDescription;
import org.cruxframework.crux.widgets.client.slideshow.SlideshowPlayPanel;
import org.cruxframework.crux.widgets.client.slideshow.SlideshowThumbnails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Name("classic")
public class ClassicLayout  implements Layout
{
	private Impl impl = GWT.create(Impl.class);
	
	static class Impl
	{
		void createComponents(Slideshow slideshow)
		{
			SlideshowAlbumTitle title = new SlideshowAlbumTitle();
			slideshow.addComponent(title, Slideshow.Position.north);
			slideshow.setHorizontalAlignment(title, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(title, "40px");

			SlideshowThumbnails thumbnails = new SlideshowThumbnails();
			slideshow.addComponent(thumbnails, Slideshow.Position.north);
			slideshow.setHorizontalAlignment(thumbnails, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(thumbnails, "90px");

			SlideshowNavigator navigator = new SlideshowNavigator();
			slideshow.addComponent(navigator, Slideshow.Position.north);
			slideshow.setHorizontalAlignment(navigator, HasHorizontalAlignment.ALIGN_RIGHT);
			slideshow.setHeight(navigator, "20px");

			SlideshowPhotoDescription description = new SlideshowPhotoDescription();
			slideshow.addComponent(description, Slideshow.Position.south);
			slideshow.setVerticalAlignment(description, HasVerticalAlignment.ALIGN_MIDDLE);
			slideshow.setHorizontalAlignment(description, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(description, "90px");

			SlideshowPlayPanel play = new SlideshowPlayPanel();
			slideshow.addComponent(play, Slideshow.Position.south);
			slideshow.setVerticalAlignment(play, HasVerticalAlignment.ALIGN_MIDDLE);
			slideshow.setHorizontalAlignment(play, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(play, "30px");
		}
	}
	
	static class MobileImpl extends Impl
	{
		void createComponents(Slideshow slideshow)
		{
			SlideshowThumbnails thumbnails = new SlideshowThumbnails();
			slideshow.addComponent(thumbnails, Slideshow.Position.north);
			slideshow.setHorizontalAlignment(thumbnails, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(thumbnails, "90px");

			SlideshowPhotoDescription description = new SlideshowPhotoDescription();
			slideshow.addComponent(description, Slideshow.Position.south);
			slideshow.setVerticalAlignment(description, HasVerticalAlignment.ALIGN_MIDDLE);
			slideshow.setHorizontalAlignment(description, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(description, "70px");

			SlideshowPlayPanel play = new SlideshowPlayPanel();
			slideshow.addComponent(play, Slideshow.Position.south);
			slideshow.setVerticalAlignment(play, HasVerticalAlignment.ALIGN_MIDDLE);
			slideshow.setHorizontalAlignment(play, HasHorizontalAlignment.ALIGN_CENTER);
			slideshow.setHeight(play, "20px");
		}
	}

	@Override
    public void createComponents(Slideshow slideshow)
    {
		impl.createComponents(slideshow);
    }
}
