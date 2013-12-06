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
package org.cruxframework.crux.widgets.client.slideshow;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SlideshowComponent extends Composite
{
	private Slideshow slideshow;

	protected SlideshowComponent()
	{
		initWidget(createMainWidget());
		setStyleName("crux-SlideshowComponent");
	}

	/**
	 * Override this method to listen for AlbumLoaded events
	 */
	protected void onAlbumLoaded()
	{
		
	}
	
	/**
	 * Override this method to listen for PhotoLoaded events
	 */
	protected void onPhotoLoaded(int previousIndex, int nextIndex)
	{
		
	}
	
	/**
	 * Override this method to listen for StartPlaying events
	 */
	protected void onStartPlaying()
	{
		
	}
	
	/**
	 * Override this method to listen for StopPlaying events
	 */
	protected void onStopPlaying()
	{
		
	}
	
	public void setSlideShow(Slideshow slideshow)
	{
		this.slideshow = slideshow;
	}
	
	public Slideshow getSlideshow()
	{
		return slideshow;
	}

	protected abstract Widget createMainWidget();
}
