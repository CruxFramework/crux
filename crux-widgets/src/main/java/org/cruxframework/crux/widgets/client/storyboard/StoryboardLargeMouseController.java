/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.storyboard;

import org.cruxframework.crux.core.client.controller.Controller;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardLargeMouseController")
public class StoryboardLargeMouseController extends StoryboardLargeController
{
	@Override
	protected Widget createClickablePanelForCell(Widget widget)
	{
		final FocusPanel panel = new FocusPanel();
		panel.add(widget);
		panel.setStyleName("item");
		configHeightWidth(panel);

		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardLargeMouseController.this, index);
			}
		});
		panel.getElement().getStyle().setProperty("display", "inline-table");
		panel.getElement().getStyle().setProperty("verticalAlign", "bottom");
		panel.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
				{
					int index = storyboard.getWidgetIndex(panel);
					SelectionEvent.fire(StoryboardLargeMouseController.this, index);
				}
			}
		});
	    
		return panel;
	}
}
