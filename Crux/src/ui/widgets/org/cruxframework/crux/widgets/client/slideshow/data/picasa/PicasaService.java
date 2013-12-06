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
package org.cruxframework.crux.widgets.client.slideshow.data.picasa;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.slideshow.Slideshow.Name;
import org.cruxframework.crux.widgets.client.slideshow.data.Entry;
import org.cruxframework.crux.widgets.client.slideshow.data.Feed;
import org.cruxframework.crux.widgets.client.slideshow.data.Photo;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbumService;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Name("picasa")
public class PicasaService extends PhotoAlbumService
{		
	private String albumID;
	private String userID;

	public PicasaService()
    {
		
    }
	
	public PicasaService(String userID, String albumID, int imagesSize, int thumbnailsSize)
    {
		super(imagesSize, thumbnailsSize);
		setUserID(userID);
		setAlbumID(albumID);
    }
	
	public String getAlbumID()
    {
    	return albumID;
    }
	
	public void setAlbumID(String albumID)
    {
    	this.albumID = albumID;
    }
	
	public String getUserID()
    {
    	return userID;
    }
	
	public void setUserID(String userID)
    {
    	this.userID = userID;
    }
	
	@Override
    protected void doLoad()
    {
		JsonpRequestBuilder request = new JsonpRequestBuilder();
		String url = getRequestURL();
		request.requestObject(url, new AsyncCallback<Feed>()
		{
			@Override
            public void onFailure(Throwable t)
            {
				errorLoading(t);        
            }

			@Override
            public void onSuccess(Feed feed)
            {
				PhotoAlbum album = extractAlbum(feed, userID, albumID);
				completeLoading(album);
            }
		});
	}
	
	private PhotoAlbum extractAlbum(Feed feed, String userID, String albumID)
    {
		PhotoAlbum album = new PhotoAlbum();
		
		album.setTitle(feed.getTitle());
		album.setIconURL(feed.getIcon());
		album.setUserID(userID);
		album.setAlbumID(albumID);

		JsArray<Entry> entries = feed.getEntries();
		for (int i=0; i<entries.length(); i++)
		{
			Entry entry = entries.get(i);
			Photo photo = new Photo();
			photo.setHeight(entry.getHeight());
			photo.setWidth(entry.getWidth());
			photo.setThumbnail(entry.getThumbnail());
			photo.setThumbnailWidth(entry.getThumbnailWidth());
			photo.setThumbnailHeight(entry.getThumbnailHeight());
			photo.setTitle(entry.getTitle());
			photo.setDescription(entry.getDescription());
			photo.setUrl(entry.getUrl());
			
			album.addImage(photo);
		}
		
        return album;
    }

	private String getRequestURL()
	{
		assert(!StringUtils.isEmpty(userID)):"UserID can not be empty";
		assert(!StringUtils.isEmpty(albumID)):"albumID can not be empty";
		int imagesSize = getImagesSize();
		int thumbnailsSize = getThumbnailsSize();
		if (thumbnailsSize <= 0)
		{
			thumbnailsSize = 72;
		}
		
		String url = "http://picasaweb.google.com/data/feed/api/user/"+userID+"/albumid/"+albumID+
		"?kind=photo&alt=json&fields=entry/media:group/media:description,entry/media:group/media:title,entry/media:group/media:thumbnail," +
		"entry/media:group/media:content,title,icon&kind=photo&thumbsize="+thumbnailsSize+"c";
		if (imagesSize > 0)
		{
			url = url+"&imgmax="+imagesSize;
		}
		
		return url;
	}
}
