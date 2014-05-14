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
public abstract class PhotoAlbumService implements AlbumService
{
	private Callback callback;
	private Slideshow slideshow;
	private int imagesSize;
	private int thumbnailsSize;
	
	/**
	 * Constructor
	 */
	public PhotoAlbumService()
    {
    }
	
	/**
	 * Constructor
	 * @param imagesSize maximum size for album photos
	 * @param thumbnailsSize size for photo thumbnails
	 */
	public PhotoAlbumService(int imagesSize, int thumbnailsSize)
    {
		setImagesSize(imagesSize);
		setThumbnailsSize(thumbnailsSize);
    }

	/**
	 * 
	 * @return
	 */
	public int getImagesSize()
    {
    	return imagesSize;
    }

	/**
	 * 
	 * @param imagesSize
	 */
	public void setImagesSize(int imagesSize)
    {
    	this.imagesSize = imagesSize;
    }
	
	/**
	 * 
	 * @return
	 */
	public int getThumbnailsSize()
    {
    	return thumbnailsSize;
    }

	/**
	 * 
	 * @param thumbnailsSize
	 */
	public void setThumbnailsSize(int thumbnailsSize)
    {
    	this.thumbnailsSize = thumbnailsSize;
    }

	/**
	 * 
	 */
	public void loadAlbum()
	{
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set service's slideshow property first.";
		doLoad();
	}

	/**
	 * 
	 * @param callback
	 */
	public void loadAlbum(Callback callback)
	{
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set service's slideshow property first.";
		this.callback = callback;
		loadAlbum();
	}
	
	/**
	 * 
	 * @param slideshow
	 */
	public void setSlideshow(Slideshow slideshow)
	{
		this.slideshow = slideshow;
	}
	
	/**
	 * 
	 * @return
	 */
	public Slideshow getSlideshow()
	{
		return slideshow;
	}
	
	/**
	 * 
	 * @param album
	 */
	protected void completeLoading(PhotoAlbum album)
	{
		slideshow.setAlbum(album);
		if (this.callback != null)
		{
			callback.onLoaded(album);
			this.callback = null;
		}
	}

	/**
	 * 
	 * @param album
	 */
	protected void errorLoading(Throwable t)
	{
		if (this.callback != null)
		{
			callback.onError(t);
			this.callback = null;
		}
	}
	
	/**
	 * 
	 */
	protected abstract void doLoad();

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
}
