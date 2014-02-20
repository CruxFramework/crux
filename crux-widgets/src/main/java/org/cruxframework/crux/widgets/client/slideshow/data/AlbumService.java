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
package org.cruxframework.crux.widgets.client.slideshow.data;

import org.cruxframework.crux.widgets.client.slideshow.Slideshow;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface AlbumService
{
	int getImagesSize();
	void setImagesSize(int imagesSize);
	int getThumbnailsSize();
	void setThumbnailsSize(int thumbnailsSize);
	void loadAlbum();
	void loadAlbum(Callback callback);
	void setSlideshow(Slideshow slideshow);
	Slideshow getSlideshow();
	
	public static interface Callback
	{
		void onLoaded(PhotoAlbum album);
		void onError(Throwable t);
	}
}
