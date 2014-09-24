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
package org.cruxframework.crux.smartfaces.client.tabviewcontainer;

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.core.client.event.focusblur.BeforeBlurHandler;
import org.cruxframework.crux.core.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.core.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.core.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Bruno Medeiros (bruno@triggolabs.com)
 */
class Flap extends Composite implements HasBeforeFocusAndBeforeBlurHandlers  
{	
	private Label title;
	private Button closeButton;
	private final boolean closeable;
	
	/**
	 * @param tabs
	 * @param view 
	 * @param closeable
	 */
	Flap(final TabContainer tabs, View view, boolean closeable)
	{
		String tabLabel = view.getTitle();
		final String tabId = view.getId();
		
		this.closeable = closeable;
		FlowPanel flap = new FlowPanel();
		
		//TODO - Criar o recurso Css para esse componente
		flap.setStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
		
		initWidget(flap);
		
		title = new Label(tabLabel);
		
		title.setStyleName("flapLabel");
		flap.add(title);

		if (closeable)
		{
			closeButton = new Button();
			closeButton.setStyleName("faces-FlapCloseButton");
			closeButton.addSelectHandler(new SelectHandler()
			{
				
				@Override
				public void onSelect(SelectEvent event)
				{
					event.stopPropagation();
					tabs.closeView(tabId, false);
				}
			});

			closeButton.setVisible(closeable);

			Screen.ensureDebugId(closeButton, tabs.getElement().getId() + "_" + tabId + "_close_btn");
			
			flap.add(closeButton);
		}
	}
	
	/**
	 * @param tabs
	 * @param view 
	 * @param closeable
	 */
	Flap(final TabCrawlableViewContainer tabs, View view, boolean closeable)
	{
		String tabLabel = view.getTitle();
		final String tabId = view.getId();
		
		this.closeable = closeable;
		FlowPanel flap = new FlowPanel();
		
		//TODO - Criar o recurso Css para esse componente
		flap.setStyleName(FacesResources.INSTANCE.css().flexBoxHorizontalContainer());
		
		initWidget(flap);
		
		title = new Label(tabLabel);
		
		title.setStyleName("flapLabel");
		flap.add(title);

		if (closeable)
		{
			closeButton = new Button();
			closeButton.setStyleName("faces-FlapCloseButton");
			closeButton.addSelectHandler(new SelectHandler()
			{
				
				@Override
				public void onSelect(SelectEvent event)
				{
					event.stopPropagation();
					tabs.closeView(tabId, false);
				}
			});

			closeButton.setVisible(closeable);

			Screen.ensureDebugId(closeButton, tabs.getElement().getId() + "_" + tabId + "_close_btn");
			
			flap.add(closeButton);
		}
	}

	@Override
    public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
    {
		return addHandler(handler, BeforeBlurEvent.getType());
    }

	@Override
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
		this.title.setText(label);
	}
	
	String getLabel()
	{
		return this.title.getText();
	}

	void setCloseButtonEnabled(boolean enabled)
	{
		if (enabled)
		{
			closeButton.removeStyleDependentName("disable");
		}
		else
		{
			closeButton.addStyleDependentName("disable");
		}
	}

	public boolean isCloseable()
    {
	    return this.closeable;
    }

}