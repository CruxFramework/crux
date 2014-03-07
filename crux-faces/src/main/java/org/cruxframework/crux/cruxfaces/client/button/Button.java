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
package org.cruxframework.crux.cruxfaces.client.button;

import org.cruxframework.crux.cruxfaces.client.event.SelectHandler;
import org.cruxframework.crux.cruxfaces.client.select.SelectableWidget;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * A cross device button, that use touch events on touch enabled devices to implement Google Fast Buttons
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Button extends SelectableWidget implements HasHTML, HasSafeHtml, HasAllFocusHandlers, HasEnabled
{
	private static final String DEFAULT_STYLE_NAME = "faces-Button";
	private com.google.gwt.user.client.ui.Button button;

	public Button()
	{
		this(new com.google.gwt.user.client.ui.Button());
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public Button(String text, SelectHandler buttonSelectHandler) 
	{
		this(new com.google.gwt.user.client.ui.Button());
		setText(text);
		addSelectHandler(buttonSelectHandler);
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	public Button(ButtonElement element)
	{
		this(new InternalButton(element));
	}
	
	protected Button(com.google.gwt.user.client.ui.Button button)
	{
		this.button = button;
		initWidget(this.button);
	}

	public void select()
	{
		getSelectEventsHandler().select();
	}

	@Override
	public String getText()
	{
		return button.getText();
	}

	@Override
	public void setText(String text)
	{
		button.setText(text);
	}

	@Override
	public String getHTML()
	{
		return button.getHTML();
	}

	@Override
	public void setHTML(String html)
	{
		button.setHTML(html);
	}

	@Override
	public void setHTML(SafeHtml html)
	{
		button.setHTML(html);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return button.addFocusHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return button.addBlurHandler(handler);
	}

	@Override
	public boolean isEnabled()
	{
		return getSelectEventsHandler().isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		getSelectEventsHandler().setEnabled(enabled);
		button.setEnabled(enabled);
	}
	
	public void setFocus(boolean focused)
	{
		button.setFocus(focused);
	}
	
	public void setAccessKey(char key)
	{
		button.setAccessKey(key);
	}
	
	public void setTabIndex(int index)
	{
		button.setTabIndex(index);
	}
	
	private static class InternalButton extends com.google.gwt.user.client.ui.Button
	{
		public InternalButton(ButtonElement element)
		{
			super(element);
		}
	}
}
