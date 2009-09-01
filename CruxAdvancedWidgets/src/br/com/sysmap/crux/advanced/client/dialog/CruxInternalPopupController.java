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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
@Global
@Controller("__popup")
public class CruxInternalPopupController 
{
	private ModuleComunicationSerializer serializer;
	private List<CustomDialogBox> dialogBoxes = new ArrayList<CustomDialogBox>();
	
	@Create
	protected DialogMessages messages;
	
	public CruxInternalPopupController()
	{
		this.serializer = Screen.getCruxSerializer();
		this.serializer.registerCruxSerializable(PopupData.class.getName(), new PopupData());
	}

	/**
	 * Called by top window
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void onClose()
	{
		BeforeCloseEvent evt = BeforeCloseEvent.fire(Popup.popup);
		if(!evt.isCanceled())
		{
			hidePopupOnTop();
		}
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
			Crux.getErrorHandler().handleError(e);
		}
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the popup dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showPopupHandler(InvokeControllerEvent controllerEvent)
	{
		Screen.blockToUser("crux-PopupScreenBlocker");
		
		try
		{
			final PopupData data = (PopupData) controllerEvent.getParameter();

			CustomDialogBox dialogBox = new CustomDialogBox(false, true, true);
			dialogBox.setStyleName(data.getStyleName());
			dialogBox.setAnimationEnabled(data.isAnimationEnabled());
			dialogBox.setWidth(data.getWidth());
			dialogBox.setHeight(data.getHeight());
			
			Frame frame = new Frame(data.getUrl());
			frame.setStyleName("frame");
			frame.setHeight("100%");
			frame.setWidth("100%");
			frame.getElement().setPropertyString("frameBorder", "no");
			frame.getElement().setPropertyInt("border", 0);
			frame.getElement().setPropertyInt("marginWidth", 0);
			frame.getElement().setPropertyInt("marginHeight", 0);
			frame.getElement().setPropertyInt("vspace", 0);
			frame.getElement().setPropertyInt("hspace", 0);

			if (data.isCloseable())
			{
				FocusPanel focusPanel = new FocusPanel();
				focusPanel.setStyleName("closeButton");
				focusPanel.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						closePopup();
					}
				});
				Label label = new Label(" ");
				label.getElement().getStyle().setProperty("fontSize", "0px");
				label.getElement().getStyle().setProperty("fontFamily", "monospace");
				focusPanel.add(label);

				dialogBox.setTopRightWidget(focusPanel);
			}

			dialogBox.setText(data.getTitle());
			dialogBox.setWidget(frame);
			dialogBox.center();

			dialogBoxes.add(dialogBox);
			
			dialogBox.show();
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}		
	}

	/**
	 * Invoke hide on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public static void hide()
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
		if (dialogBoxes.size() > 0)
		{
			CustomDialogBox dialogBox = dialogBoxes.remove(dialogBoxes.size() - 1);
			Screen.unblockToUser();
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
		if($wnd.top._popup_origin == null)
		{
			$wnd.top._popup_origin = new Array();
		}		
		$wnd.top._popup_origin.push($wnd);
		$wnd.top._cruxScreenControllerAccessor("__popup.showPopupHandler", serializedData);
	}-*/;

	/**
	 * Closes the popup, removing its window from the stack 
	 */
	private static native void hidePopupOnTop()/*-{
		if($wnd.top._popup_origin != null)
		{
			$wnd.top._cruxScreenControllerAccessor("__popup.hidePopupHandler", null);
			$wnd.top._popup_origin.pop();
		}
	}-*/;

	/**
	 * Invoked when the user clicks the close button. Depending on the beforeClose event handling, may not close the popup. 
	 */
	protected native void closePopup()/*-{
		var o = $wnd.top._popup_origin[$wnd.top._popup_origin.length - 1];
		o._cruxScreenControllerAccessor("__popup.onClose", null);
	}-*/;
}