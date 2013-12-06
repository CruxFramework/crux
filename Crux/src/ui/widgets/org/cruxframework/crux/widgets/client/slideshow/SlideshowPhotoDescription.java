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

import org.cruxframework.crux.widgets.client.slideshow.data.Photo;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowPhotoDescription extends SlideshowComponent
{
	
	private Label description;

	public SlideshowPhotoDescription()
    {
		super();
		setStyleName("crux-SlideshowPhotoDescription");
    }

	@Override
    public void onAlbumLoaded()
    {
		description.setText("");
    }

	@Override
    public void onPhotoLoaded(int previousIndex, int nextIndex)
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		Photo photo = getSlideshow().getPhoto(nextIndex);
		if (photo != null)
		{
			description.setText(photo.getDescription());
		}
    }

	@Override
    protected Widget createMainWidget()
    {
		description = new Label();
		description.setWidth("100%");
		
	    return description;
    }

}
