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

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Photo
{
	private int width;
	private int height;
	private String thumbnail;
	private int thumbnailWidth;
	private int thumbnailHeight;
	private String url;
	private String title;
	private String description;
	
	public String getThumbnail()
    {
    	return thumbnail;
    }
	public void setThumbnail(String thumbnail)
    {
    	this.thumbnail = thumbnail;
    }
	public String getUrl()
    {
    	return url;
    }
	public void setUrl(String url)
    {
    	this.url = url;
    }
	public String getTitle()
    {
    	return title;
    }
	public void setTitle(String title)
    {
    	this.title = title;
    }
	public int getWidth()
    {
    	return width;
    }
	public void setWidth(int width)
    {
    	this.width = width;
    }
	public int getHeight()
    {
    	return height;
    }
	public void setHeight(int height)
    {
    	this.height = height;
    }
	public String getDescription()
    {
    	return description;
    }
	public void setDescription(String description)
    {
    	this.description = description;
    }
	public int getThumbnailWidth()
    {
    	return thumbnailWidth;
    }
	public void setThumbnailWidth(int thumbnailWidth)
    {
    	this.thumbnailWidth = thumbnailWidth;
    }
	public int getThumbnailHeight()
    {
    	return thumbnailHeight;
    }
	public void setThumbnailHeight(int thumbnailHeight)
    {
    	this.thumbnailHeight = thumbnailHeight;
    }
}
