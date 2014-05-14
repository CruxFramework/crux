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

import org.cruxframework.crux.core.client.collection.FastList;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class PhotoAlbum
{
	private String userID;
	private String albumID;
	private String title;
	private String iconURL;
	private FastList<Photo> images = new FastList<Photo>();
	public String getUserID()
    {
    	return userID;
    }
	public void setUserID(String userID)
    {
    	this.userID = userID;
    }
	public String getAlbumID()
    {
    	return albumID;
    }
	public void setAlbumID(String albumID)
    {
    	this.albumID = albumID;
    }
	public String getTitle()
    {
    	return title;
    }
	public void setTitle(String title)
    {
    	this.title = title;
    }
	public String getIconURL()
    {
    	return iconURL;
    }
	public void setIconURL(String iconURL)
    {
    	this.iconURL = iconURL;
    }
	public FastList<Photo> getImages()
    {
    	return images;
    }
	public void addImage(Photo image)
    {
    	this.images.add(image);
    }
	
}
