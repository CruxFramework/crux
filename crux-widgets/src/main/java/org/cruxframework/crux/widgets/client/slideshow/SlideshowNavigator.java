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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowNavigator extends SlideshowComponent
{
	private FlowPanel navigatorPanel;
	private Label currentPhoto;
	private Label photoCount;
	private Label separator;
	
	public SlideshowNavigator()
    {
		super();
		setStyleName("crux-SlideshowNavigator");
    }

	@Override
    public void onAlbumLoaded()
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		photoCount.setText(Integer.toString(getSlideshow().getPhotoCount()));
		currentPhoto.setText("0");
    }

	@Override
    public void onPhotoLoaded(int previousIndex, int nextIndex)
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		currentPhoto.setText(Integer.toString(nextIndex+1));
    }

	@Override
    protected Widget createMainWidget()
    {
		navigatorPanel = new FlowPanel();
		navigatorPanel.setWidth("100%");
		
		
		currentPhoto = new Label("0");
		currentPhoto.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		separator = new Label(" / ");
		separator.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		photoCount = new Label("0");
		photoCount.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		navigatorPanel.add(currentPhoto);
		navigatorPanel.add(separator);
		navigatorPanel.add(photoCount);
		
	    return navigatorPanel;
    }

}
