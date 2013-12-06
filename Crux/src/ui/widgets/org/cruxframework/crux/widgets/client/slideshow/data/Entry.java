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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Entry extends JavaScriptObject
{
	protected Entry() {}
	
	public final native String getTitle() /*-{
	    return this.media$group.media$title.$t;
	}-*/;
	
	public final native String getUrl() /*-{
	    return this.media$group.media$content[0].url;
	}-*/;
	
	public final native String getMedium() /*-{
	    return this.media$group.media$content[0].medium;
	}-*/;
	
	public final native int getHeight() /*-{
	    return this.media$group.media$content[0].height;
	}-*/;

	public final native int getWidth() /*-{
	    return this.media$group.media$content[0].width;
	}-*/;

	public final native String getThumbnail() /*-{
	    return this.media$group.media$thumbnail[0].url;
	}-*/;

	public final native int getThumbnailWidth() /*-{
	    return this.media$group.media$thumbnail[0].width;
	}-*/;

	public final native int getThumbnailHeight() /*-{
	    return this.media$group.media$thumbnail[0].height;
	}-*/;

	public final native String getDescription() /*-{
	    return this.media$group.media$description.$t;
	}-*/;
}