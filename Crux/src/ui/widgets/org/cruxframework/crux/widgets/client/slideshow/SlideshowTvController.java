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

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ArrowsDevice;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("slideshowTvController")
public class SlideshowTvController extends SlideshowBaseController
{
	@Expose
	public void onFocusPanelKeyDown(KeyDownEvent event)
	{
		switch(event.getNativeKeyCode())
		{
			case ArrowsDevice.Keys.KEY_UP_ARROW: 
			case ArrowsDevice.Keys.KEY_LEFT_ARROW:
			case ArrowsDevice.Keys.KEY_SKIP_BACK:
				stop();
				previous();
			break;
			case ArrowsDevice.Keys.KEY_DOWN_ARROW: 
			case ArrowsDevice.Keys.KEY_RIGHT_ARROW:
			case ArrowsDevice.Keys.KEY_SKIP_FORWARD:
				stop();
				next();
			break;
			case ArrowsDevice.Keys.KEY_STOP: 
				stop();
			break;
			case ArrowsDevice.Keys.KEY_PLAY_PAUSE: 
				if (isPlaying())
				{
					stop();
				}
				else
				{
					play();
				}
			break;
		}
	}
	
	@Override
    public void configurePhotoPanel()
    {
		table.add(photoPanel, DockPanel.CENTER);
		setVerticalAlignment(photoPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		setHorizontalAlignment(photoPanel, HasHorizontalAlignment.ALIGN_CENTER);
    }
}
