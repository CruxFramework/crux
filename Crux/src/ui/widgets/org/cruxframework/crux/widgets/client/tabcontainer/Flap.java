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
package org.cruxframework.crux.widgets.client.tabcontainer;

import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;
import org.cruxframework.crux.widgets.client.rollingtabs.SimpleDecoratedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
class Flap extends Composite implements HasBeforeFocusAndBeforeBlurHandlers 
{
	private SimpleDecoratedPanel panel;
	private FlapController flapController;	
	
	/**
	 * @param tabs
	 * @param view
	 * @param closeable
	 */
	Flap(final TabContainer tabs, View view, boolean closeable)
	{
		panel = new SimpleDecoratedPanel();
		flapController = new FlapController(tabs, view.getId(), view.getTitle(), false, closeable);
		panel.setContentWidget(flapController);
		initWidget(panel);
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeBlurHandlers#addBeforeBlurHandler(org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurHandler)
	 */
	public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
	{
		return addHandler(handler, BeforeBlurEvent.getType());
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeFocusHandlers#addBeforeFocusHandler(org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler)
	 */
	public HandlerRegistration addBeforeFocusHandler(BeforeFocusHandler handler)
	{
		return addHandler(handler, BeforeFocusEvent.getType());
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addHandler(handler, ClickEvent.getType());
	}
	
	void setLabel(String label)
	{
		flapController.setTabTitle(label);
	}
	
	String getLabel()
	{
		return flapController.getTabTitle();
	}

	boolean isCloseable()
    {
	    return flapController.isCloseable();
    }
}