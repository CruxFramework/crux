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
import com.google.gwt.core.client.JsArray;

public class MediaContent extends JavaScriptObject
{
	protected MediaContent() {}
	
	/**
	 * Retrieve the direct url to the media object. 
	 * If not included, a <media:player> element must be specified.
	 * @return
	 */
	public final native String getUrl()/*-{
		return this.url;
	}-*/;
	
	/**
	 * Retrieve the number of bytes of the media object. It is an optional attribute.
	 * @return
	 */
	public final native int getFileSize()/*-{
		return (this.fileSize?this.fileSize:-1);
	}-*/;

	/**
	 * Retrieve the standard MIME type of the object. It is an optional attribute.
	 * @return
	 */
	public final native String getType()/*-{
		return this.type;
	}-*/;

	/**
	 * Retrieve the type of object (image | audio | video | document | executable). 
	 * While this attribute can at times seem redundant if type is supplied, 
	 * it is included because it simplifies decision making on the reader side, 
	 * as well as flushes out any ambiguities between MIME type and object type. 
	 * It is an optional attribute.
	 * @return
	 */
	public final native String getMedium()/*-{
		return this.medium;
	}-*/;

	/**
	 * Determines if this is the default object that should be used for the <media:group>. 
	 * There should only be one default object per <media:group>. It is an optional attribute.
	 * @return
	 */
	public final native boolean isDefault()/*-{
		return (this.isDefault?true:false);
	}-*/;

	/**
	 * Determines if the object is a sample or the full version of the object, or even 
	 * if it is a continuous stream (sample | full | nonstop). Default value 
	 * is 'full'. It is an optional attribute.
	 * @return
	 */
	public final native String getExpression()/*-{
		return (this.expression?this.expression:'full');
	}-*/;

	/**
	 * Retrieve the kilobits per second rate of media. It is an optional attribute.
	 * @return
	 */
	public final native int getBitRate()/*-{
		return (this.bitrate?this.bitrate:0);
	}-*/;

	/**
	 * Retrieve the number of frames per second for the media object. It is an optional attribute.
	 * @return
	 */
	public final native int getFrameRate()/*-{
		return (this.framerate?this.framerate:0);
	}-*/;

	/**
	 * Retrieve the number of samples per second taken to create the media object. 
	 * It is expressed in thousands of samples per second (kHz). It is an optional attribute.
	 * @return
	 */
	public final native float getSamplingRate()/*-{
		return (this.samplingrate?this.samplingrate:0);
	}-*/;

	/**
	 * Retrieve the number of audio channels in the media object. It is an optional attribute.
	 * @return
	 */
	public final native int getChannels()/*-{
		return (this.channels?this.channels:0);
	}-*/;

	/**
	 * Retrieve the number of seconds the media object plays. It is an optional attribute.
	 * @return
	 */
	public final native int getDuration()/*-{
		return (this.duration?this.duration:0);
	}-*/;

	/**
	 * Retrieve is the height of the media object. It is an optional attribute.
	 * @return
	 */
	public final native int getHeight()/*-{
		return (this.height?this.height:0);
	}-*/;

	/**
	 * Retrieve the width of the media object. It is an optional attribute.
	 * @return
	 */
	public final native int getWidth()/*-{
		return (this.width?this.width:0);
	}-*/;

	/**
	 *  Retrieves the primary language encapsulated in the media object. Language codes possible are 
	 *  detailed in RFC 3066. This attribute is used similar to the xml:lang attribute 
	 *  detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.
	 * @return
	 */
	public final native String getLang()/*-{
		return this.lang;
	}-*/;
	
	/**
	 * Allows particular images to be used as representative images for the media object. 
	 * If multiple thumbnails are included, and time coding is not at play, it is assumed that 
	 * the images are in order of importance.
	 * @return
	 */
	public final native JsArray<MediaThumbnail> getThumbnails()/*-{
		return this.thumbnails;
	}-*/;
	
	/**
	 * Allows the media object to be accessed through a web browser media player console. 
	 * This element is required only if a direct media url attribute is not specified in the 
	 * <media:content> element.
	 * @return
	 */
	public final native MediaPlayer getPlayer()/*-{
		return this.player;
	}-*/;
	
}
