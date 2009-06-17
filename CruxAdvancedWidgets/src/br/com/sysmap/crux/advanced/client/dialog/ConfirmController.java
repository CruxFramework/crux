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

import br.com.sysmap.crux.advanced.client.event.dialog.CancelEvent;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvent;
import br.com.sysmap.crux.core.client.component.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.component.ModuleComunicationException;
import br.com.sysmap.crux.core.client.component.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.component.Screen;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Controller("confirmController")
public class ConfirmController 
{
	private ModuleComunicationSerializer serializer;
	
	public ConfirmController()
	{
		this.serializer = Screen.getModuleShareableSerializer();
		this.serializer.registerModuleShareable(ConfirmData.class.getName(), new ConfirmData());
	}

	/**
	 * Called by top window
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void onOk()
	{
		OkEvent.fire(Confirm.confirm);
	}

	/**
	 * Called by top window
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void onCancel()
	{
		CancelEvent.fire(Confirm.confirm);
	}

	/**
	 * Invoke showConfirm on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showConfirm(ConfirmData data)
	{
		try
		{
			showConfirmOnTop("confirmController.showConfirmHandler", serializer.serialize(data));
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the confirm dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showConfirmHandler(InvokeControllerEvent controllerEvent)
	{
		final ConfirmData data = (ConfirmData) controllerEvent.getData();
		
		final DialogBox dialogBox = new DialogBox(false, true);
		dialogBox.setStyleName(data.getStyleName());
		
		DockPanel dockPanel = new DockPanel();
		dockPanel.add(new Label(data.getMessage()), DockPanel.CENTER);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		Button okButton = new Button();
		okButton.setText("Ok");//TODO: internacionalizar
		okButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				try
				{
					okClick();
				}
				catch (Throwable e)
				{
					GWT.log(e.getMessage(), e);
				}
				dialogBox.hide();
			}
		});

		Button cancelButton = new Button();
		cancelButton.setText("Cancel");//TODO: internacionalizar
		cancelButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				try
				{
					cancelClick();
				}
				catch (Throwable e)
				{
					GWT.log(e.getMessage(), e);
				}
				dialogBox.hide();
			}
		});

		horizontalPanel.add(okButton);
		horizontalPanel.add(cancelButton);
		
		dockPanel.add(horizontalPanel, DockPanel.SOUTH);
		
		dialogBox.add(dockPanel);
		dialogBox.center();
		dialogBox.show();
	}

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void showConfirmOnTop(String call, String serializedData)/*-{
		$wnd.top._confirm_origin = $wnd;
		$wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void okClick()/*-{
		var o = $wnd.top._confirm_origin;
		$wnd.top._confirm_origin = null;
		o._cruxScreenControllerAccessor("confirmController.onOk", null);
	}-*/;

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void cancelClick()/*-{
		var o = $wnd.top._confirm_origin;
		$wnd.top._confirm_origin = null;
		o._cruxScreenControllerAccessor("confirmController.onCancel", null);
	}-*/;
}
