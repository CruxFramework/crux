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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SelectableFlowPanel extends SelectableWidget implements HasAllFocusHandlers, 
								IndexedPanel.ForIsWidget, InsertPanel.ForIsWidget, HasEnabled
{
	private static final String DEFAULT_STYLE_NAME = "faces-SelectablePanel";
	private static FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();
	private FlowPanel panel;

	public SelectableFlowPanel()
	{
		this(new FlowPanel());
	}

	public SelectableFlowPanel(SelectHandler buttonSelectHandler) 
	{
		this();
		addSelectHandler(buttonSelectHandler);
	}
		
	protected SelectableFlowPanel(FlowPanel panel)
	{
		this.panel = panel;
		makeFocusable(panel.getElement());
		initWidget(this.panel);
		setStyleName(DEFAULT_STYLE_NAME);
	}

	public void add(IsWidget w)
	{
		panel.add(w);
	}

	@Override
    public void add(Widget w)
    {
		panel.add(w);
    }

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addDomHandler(handler, BlurEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addDomHandler(handler, FocusEvent.getType());
	}

	public int getTabIndex() 
	{
		return focusImpl.getTabIndex(getElement());
	}

	@Override
    public Widget getWidget(int index)
    {
	    return panel.getWidget(index);
    }

	@Override
    public int getWidgetCount()
    {
	    return panel.getWidgetCount();
    }

	@Override
    public int getWidgetIndex(IsWidget child)
    {
	    return panel.getWidgetIndex(child);
    }	
	
	@Override
    public int getWidgetIndex(Widget child)
    {
	    return panel.getWidgetIndex(child);
    }
	
	@Override
    public void insert(IsWidget w, int beforeIndex)
    {
		panel.insert(w, beforeIndex);
    }
	
	@Override
    public void insert(Widget w, int beforeIndex)
    {
		panel.insert(w, beforeIndex);
    }

	@Override
	public boolean isEnabled()
	{
		return getSelectEventsHandler().isEnabled();
	}

	@Override
    public boolean remove(int index)
    {
	    return panel.remove(index);
    }

	public boolean remove(IsWidget w)
	{
		return panel.remove(w);
	}

	public void select()
	{
		getSelectEventsHandler().select();
	}
	
	public void setAccessKey(char key)
	{
		focusImpl.setAccessKey(getElement(), key);
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
	
	public void setTabIndex(int index)
	{
		focusImpl.setTabIndex(getElement(), index);
	}
	
	protected void makeFocusable(Element e)
	{
	    e.setTabIndex(0);
	}
}