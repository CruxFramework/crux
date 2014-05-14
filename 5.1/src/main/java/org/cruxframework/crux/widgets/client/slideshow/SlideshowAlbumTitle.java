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

import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowAlbumTitle extends SlideshowComponent
{
	
	private Label title;

	public SlideshowAlbumTitle()
    {
		super();
		setStyleName("crux-SlideshowAlbumTitle");
    }

	@Override
    public void onAlbumLoaded()
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		PhotoAlbum album = getSlideshow().getAlbum();
		if (album != null)
		{
			title.setText(album.getTitle());
		}
    }

	@Override
    protected Widget createMainWidget()
    {
		title = new Label();
		title.setWidth("100%");
		
	    return title;
    }

}
