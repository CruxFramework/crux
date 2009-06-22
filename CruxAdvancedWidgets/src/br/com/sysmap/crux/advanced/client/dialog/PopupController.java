/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.advanced.client.dialog;

import br.com.sysmap.crux.advanced.client.titlepanel.TitlePanel;
import br.com.sysmap.crux.core.client.component.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.component.ModuleComunicationException;
import br.com.sysmap.crux.core.client.component.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.component.Screen;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Global
@Controller("__popup")
public class PopupController 
{
	private ModuleComunicationSerializer serializer;
	private DialogBox dialogBox;
	
	@Create
	protected DialogMessages messages;
	
	public PopupController()
	{
		this.serializer = Screen.getModuleShareableSerializer();
		this.serializer.registerModuleShareable(PopupData.class.getName(), new PopupData());
	}

	/**
	 * Called by top window
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void onClose()
	{
		CloseEvent.fire(Popup.popup, Popup.popup);
	}

	/**
	 * Invoke showPopup on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showPopup(PopupData data)
	{
		try
		{
			showPopupOnTop(serializer.serialize(data));
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the popup dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showPopupHandler(InvokeControllerEvent controllerEvent)
	{
		final PopupData data = (PopupData) controllerEvent.getData();
		
		dialogBox = new DialogBox(false, true);
		dialogBox.setStyleName(data.getStyleName());
		dialogBox.setAnimationEnabled(data.isAnimationEnabled());

		dialogBox.addCloseHandler(new CloseHandler<PopupPanel>()
		{
			public void onClose(CloseEvent<PopupPanel> event)
			{
				closePopup();
			}
		});
		
		TitlePanel titlePanel = new TitlePanel("100%", "100%", null);
		Frame frame = new Frame(data.getUrl());

		titlePanel.setContentWidget(frame);
		titlePanel.setTitleText(data.getTitle());

		FocusPanel focusPanel = new FocusPanel();
		focusPanel.setStyleName("closeButton");
		focusPanel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				hide();
			}
		});
		Label label = new Label(" ");
		label.getElement().getStyle().setProperty("fontSize", "0px");
		label.getElement().getStyle().setProperty("fontFamily", "monospace");
		focusPanel.add(label);
		
		titlePanel.setControlWidget(focusPanel);
		
		dialogBox.setWidget(titlePanel);
		
		dialogBox.center();
		dialogBox.show();
	}

	/**
	 * Invoke hide on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void hide()
	{
		hidePopupOnTop();
	}
	
	/**
	 * Handler method to be invoked on top. That method hides the popup dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void hidePopupHandler(InvokeControllerEvent controllerEvent)
	{
		if (dialogBox != null)
		{
			dialogBox.hide();
			dialogBox = null;
		}
	}
	
	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void showPopupOnTop(String serializedData)/*-{
		$wnd.top._popup_origin = $wnd;
		$wnd.top._cruxScreenControllerAccessor("__popup.showPopupHandler", serializedData);
	}-*/;

	/**
	 * 
	 */
	private native void hidePopupOnTop()/*-{
		$wnd.top._cruxScreenControllerAccessor("__popup.hidePopupHandler", null);
	}-*/;

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	protected native void closePopup()/*-{
		var o = $wnd.top._popup_origin;
		$wnd.top._popup_origin = null;
		o._cruxScreenControllerAccessor("__popup.onClose", null);
	}-*/;
}
