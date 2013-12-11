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
package org.cruxframework.crux.widgets.client.rss.feed;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class Entry extends JavaScriptObject
{
	protected Entry() {}

	public final native JsArray<MediaGroup> getMediaGroups()/*-{
		return this.mediaGroups;
	}-*/;		

	public final native String getTitle()/*-{
		return this.title;
	}-*/;		

	public final native String getLink()/*-{
		return this.link;
	}-*/;		

	public final native String getContent()/*-{
		return this.content;
	}-*/;		

	public final native String getContentSnippet()/*-{
		return this.contentSnippet;
	}-*/;		

    @SuppressWarnings("deprecation")
    public final Date getPublishedDate()
	{
		String time = getPublishedDateString();
		return (time != null && time.length()>0?new Date(time):null);
	}
	
	public final native String getPublishedDateString()/*-{
		return this.publishedDate;
	}-*/;		

	public final native JsArrayString getCategories()/*-{
		return this.categories;
	}-*/;		
}
