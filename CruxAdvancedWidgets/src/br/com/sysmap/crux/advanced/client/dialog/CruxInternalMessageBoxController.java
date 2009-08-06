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

import br.com.sysmap.crux.advanced.client.decoratedbutton.DecoratedButton;
import br.com.sysmap.crux.advanced.client.event.dialog.OkEvent;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@Global
@Controller("__messageBox")
public class CruxInternalMessageBoxController 
{
	private ModuleComunicationSerializer serializer;
	
	@Create
	protected DialogMessages messages;
	
	/**
	 * 
	 */
	public CruxInternalMessageBoxController()
	{
		this.serializer = Screen.getCruxSerializer();
		this.serializer.registerCruxSerializable(MessageBoxData.class.getName(), new MessageBoxData());
	}

	/**
	 * Invoke showMessageBox on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showMessageBox(MessageBoxData data)
	{
		try
		{
			showMessageBoxOnTop(serializer.serialize(data));
		}
		catch (ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e);
		}	
	}
	
	/**
	 * Called by top window
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void onOk()
	{
		OkEvent.fire(MessageBox.messageBox);
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the message box.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showMessageBoxHandler(InvokeControllerEvent controllerEvent)
	{
		Screen.blockToUser("crux-MessageBoxScreenBlocker");
		
		try
		{
			final MessageBoxData data = (MessageBoxData) controllerEvent.getParameter();
			
			final DialogBox dialogBox = new DialogBox(false, true);
			dialogBox.setStyleName(data.getStyleName());
			dialogBox.setText(data.getTitle());
			dialogBox.setAnimationEnabled(data.isAnimationEnabled());
			
			DockPanel dockPanel = new DockPanel();
			dockPanel.add(createMessageLabel(data), DockPanel.CENTER);
			
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSpacing(10);
			horizontalPanel.add(createOkButton(dialogBox));
			
			dockPanel.add(horizontalPanel, DockPanel.SOUTH);
			dockPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
			
			dialogBox.add(dockPanel);
			dialogBox.center();
			dialogBox.show();
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * @param data
	 * @return
	 */
	private Label createMessageLabel(final MessageBoxData data)
	{
		Label label = new Label(data.getMessage());
		label.setStyleName("message");
		return label;
	}

	/**
	 * @param dialogBox
	 * @return
	 */
	private DecoratedButton createOkButton(final DialogBox dialogBox)
	{
		DecoratedButton okButton = new DecoratedButton();
		okButton.setText(messages.messageBoxOkLabel());
		okButton.addStyleName("button");
		okButton.addStyleName("okButton");
		okButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Screen.unblockToUser();
				
				dialogBox.hide();
				
				try
				{
					okClick();
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}
			}
		});
		return okButton;
	}

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void showMessageBoxOnTop(String serializedData)/*-{
		$wnd.top._messageBox_origin = $wnd;
		$wnd.top._cruxScreenControllerAccessor("__messageBox.showMessageBoxHandler", serializedData);
	}-*/;

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void okClick()/*-{
		var o = $wnd.top._messageBox_origin;
		$wnd.top._messageBox_origin = null;
		o._cruxScreenControllerAccessor("__messageBox.onOk", null);
	}-*/;
}
