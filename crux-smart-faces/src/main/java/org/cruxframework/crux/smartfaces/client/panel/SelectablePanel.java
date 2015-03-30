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
package org.cruxframework.crux.smartfaces.client.panel;

import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.select.SelectableWidget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SelectablePanel extends SelectableWidget implements HasAllFocusHandlers, AcceptsOneWidget, HasEnabled
{
	private static final String DEFAULT_STYLE_NAME = "faces-SelectablePanel";
	private SimplePanel panel;
	private static FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

	public SelectablePanel()
	{
		this(new InternalPanel(focusImpl.createFocusable()));
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public SelectablePanel(SelectHandler buttonSelectHandler) 
	{
		super();
		addSelectHandler(buttonSelectHandler);
	}
	
	public SelectablePanel(Element element)
	{
		this(new InternalPanel(element));
	}
	
	protected SelectablePanel(SimplePanel panel)
	{
		this.panel = panel;
		makeFocusable(panel.getElement());
		initWidget(this.panel);
	}

	@Override
    public void setWidget(IsWidget w)
    {
		panel.setWidget(w);
    }

	public Widget getChildWidget()
	{
	    return panel.getWidget();
	}
	
	public void add(IsWidget w)
	{
		panel.add(w);
	}
	
	public boolean remove(IsWidget w)
	{
		return panel.remove(w);
	}
	
	public void select()
	{
		getSelectEventsHandler().select();
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
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
		if (enabled)
		{
			removeStyleDependentName("disabled");
		}
		else
		{
			addStyleDependentName("disabled");
		}
	}
	
	public int getTabIndex() 
	{
		return focusImpl.getTabIndex(getElement());
	}

	public void setFocus(boolean focused)
	{
		if (focused)
		{
			focusImpl.focus(getElement());
		}
		else
		{
			focusImpl.blur(getElement());
		}
	}
	
	public void setAccessKey(char key)
	{
		focusImpl.setAccessKey(getElement(), key);
	}
	
	public void setTabIndex(int index)
	{
		focusImpl.setTabIndex(getElement(), index);
	}
	
	protected void makeFocusable(Element e)
	{
	    e.setTabIndex(0);
	}
	
	private static class InternalPanel extends SimplePanel
	{
		public InternalPanel(Element element)
		{
			super(element);
		}
	}
}