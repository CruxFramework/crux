/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.tools.quickstart.client.dto;

import java.io.Serializable;

import org.cruxframework.crux.core.client.controller.ValueObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@ValueObject
public class GadgetInfo implements Serializable
{
    private static final long serialVersionUID = -3484145503979665071L;

    private boolean useLongManifestName;
    private String author;
    private String authorAboutMe;
    private String authorAffiliation;
    private String authorEmail;
    private String authorLink;
    private String authorLocation;
    private String authorPhoto;
    private String authorQuote;
    private String description;
    private String directoryTitle;
    private Integer height = 200;
    private Integer width = 320;
    private String screenshot;
    private String thumbnail;
    private String title;
    private String titleUrl;
    private boolean scrolling;
    private boolean singleton;
    private boolean scaling;
    private String locales;
    private String features;
    private String userPreferences;
    
	public boolean isUseLongManifestName()
    {
    	return useLongManifestName;
    }
	public void setUseLongManifestName(boolean useLongManifestName)
    {
    	this.useLongManifestName = useLongManifestName;
    }
	public String getAuthor()
    {
    	return author;
    }
	public void setAuthor(String author)
    {
    	this.author = author;
    }
	public String getAuthorAboutMe()
    {
    	return authorAboutMe;
    }
	public void setAuthorAboutMe(String authorAboutMe)
    {
    	this.authorAboutMe = authorAboutMe;
    }
	public String getAuthorAffiliation()
    {
    	return authorAffiliation;
    }
	public void setAuthorAffiliation(String authorAffiliation)
    {
    	this.authorAffiliation = authorAffiliation;
    }
	public String getAuthorEmail()
    {
    	return authorEmail;
    }
	public void setAuthorEmail(String authorEmail)
    {
    	this.authorEmail = authorEmail;
    }
	public String getAuthorLink()
    {
    	return authorLink;
    }
	public void setAuthorLink(String authorLink)
    {
    	this.authorLink = authorLink;
    }
	public String getAuthorLocation()
    {
    	return authorLocation;
    }
	public void setAuthorLocation(String authorLocation)
    {
    	this.authorLocation = authorLocation;
    }
	public String getAuthorPhoto()
    {
    	return authorPhoto;
    }
	public void setAuthorPhoto(String authorPhoto)
    {
    	this.authorPhoto = authorPhoto;
    }
	public String getAuthorQuote()
    {
    	return authorQuote;
    }
	public void setAuthorQuote(String authorQuote)
    {
    	this.authorQuote = authorQuote;
    }
	public String getDescription()
    {
    	return description;
    }
	public void setDescription(String description)
    {
    	this.description = description;
    }
	public String getDirectoryTitle()
    {
    	return directoryTitle;
    }
	public void setDirectoryTitle(String directoryTitle)
    {
    	this.directoryTitle = directoryTitle;
    }
	public Integer getHeight()
    {
    	return height;
    }
	public void setHeight(Integer height)
    {
    	this.height = height;
    }
	public Integer getWidth()
    {
    	return width;
    }
	public void setWidth(Integer width)
    {
    	this.width = width;
    }
	public String getScreenshot()
    {
    	return screenshot;
    }
	public void setScreenshot(String screenshot)
    {
    	this.screenshot = screenshot;
    }
	public String getThumbnail()
    {
    	return thumbnail;
    }
	public void setThumbnail(String thumbnail)
    {
    	this.thumbnail = thumbnail;
    }
	public String getTitle()
    {
    	return title;
    }
	public void setTitle(String title)
    {
    	this.title = title;
    }
	public String getTitleUrl()
    {
    	return titleUrl;
    }
	public void setTitleUrl(String titleUrl)
    {
    	this.titleUrl = titleUrl;
    }
	public boolean isScrolling()
    {
    	return scrolling;
    }
	public void setScrolling(boolean scrolling)
    {
    	this.scrolling = scrolling;
    }
	public boolean isSingleton()
    {
    	return singleton;
    }
	public void setSingleton(boolean singleton)
    {
    	this.singleton = singleton;
    }
	public boolean isScaling()
    {
    	return scaling;
    }
	public void setScaling(boolean scaling)
    {
    	this.scaling = scaling;
    }
	public String getLocales()
    {
    	return locales;
    }
	public void setLocales(String locales)
    {
    	this.locales = locales;
    }
	public String getFeatures()
    {
    	return features;
    }
	public void setFeatures(String features)
    {
    	this.features = features;
    }
	public String getUserPreferences()
    {
    	return userPreferences;
    }
	public void setUserPreferences(String userPreferences)
    {
    	this.userPreferences = userPreferences;
    }
}
