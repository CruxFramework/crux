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

import com.google.gwt.core.client.JavaScriptObject;

public class MediaThumbnail extends JavaScriptObject
{
	protected MediaThumbnail() {}

	/**
	 * Retrieve the url of the thumbnail. It is a required attribute.
	 * @return
	 */
	public final native String getUrl()/*-{
		return this.url;
	}-*/;
	
	/**
	 * Retrieve is the height of the thumbnail. It is an optional attribute.
	 * @return
	 */
	public final native int getHeight()/*-{
		return (this.height?this.height:0);
	}-*/;

	/**
	 * Retrieve the width of the thumbnail. It is an optional attribute.
	 * @return
	 */
	public final native int getWidth()/*-{
		return (this.width?this.width:0);
	}-*/;
	
}
