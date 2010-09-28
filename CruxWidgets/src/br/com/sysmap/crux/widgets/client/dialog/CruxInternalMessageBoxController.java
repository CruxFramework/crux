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
package br.com.sysmap.crux.widgets.client.dialog;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.controller.crossdoc.Target;
import br.com.sysmap.crux.core.client.controller.crossdoc.TargetDocument;
import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;
import br.com.sysmap.crux.widgets.client.event.OkEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A cross frame controller for showing message boxes
 * @author Gesse S. F. Dafe
 */
@Global
@Controller(value="__messageBox", lazy=false)
public class CruxInternalMessageBoxController implements CruxInternalMessageBoxControllerCrossDoc
{
	protected DialogMessages messages = GWT.create(DialogMessages.class);
	protected CruxInternalMessageBoxControllerCrossDoc crossDoc  = GWT.create(CruxInternalMessageBoxControllerCrossDoc.class);

	/**
	 * Invoke showMessageBox on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showMessageBox(MessageBoxData data)
	{
		setMessageBoxOrigin();
		((TargetDocument)crossDoc).setTarget(Target.TOP);
		crossDoc.showMessageBoxDialog(data);
	}
	
	/**
	 * Fires the OK button click event 
	 */
	public void onOk()
	{
		OkEvent.fire(MessageBox.messageBox);
	}
	
	/**
	 * Handler method to be invoked on top. This method does show the message box.
	 * @param controllerEvent
	 */
	public void showMessageBoxDialog(MessageBoxData data)
	{
		Screen.blockToUser("crux-MessageBoxScreenBlocker");
		
		try
		{
			final DialogBox dialogBox = new DialogBox(false, true);
			dialogBox.setStyleName(data.getStyleName());
			dialogBox.setText(data.getTitle());
			dialogBox.setAnimationEnabled(data.isAnimationEnabled());
			
			DockPanel dockPanel = new DockPanel();
			dockPanel.add(createMessageLabel(data), DockPanel.CENTER);
			
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSpacing(10);
			DecoratedButton okButton = createOkButton(dialogBox, data);
			horizontalPanel.add(okButton);
			
			dockPanel.add(horizontalPanel, DockPanel.SOUTH);
			dockPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
			
			dialogBox.add(dockPanel);
			
			dockPanel.getElement().getParentElement().setAttribute("align", "center");
			
			dialogBox.center();
			dialogBox.show();
			
			okButton.setFocus(true);
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * Creates a label to display the message
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
	 * Creates the OK button
	 * @param dialogBox
	 * @param data 
	 * @return
	 */
	private DecoratedButton createOkButton(final DialogBox dialogBox, MessageBoxData data)
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
					JSWindow origin = getAndClearMessageBoxOrigin();
					((TargetDocument)crossDoc).setTargetWindow(origin);
					crossDoc.onOk();
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}
			}
		});
		
		Screen.ensureDebugId(okButton, "_crux_msgBox_ok_" + data.getMessage());
		
		return okButton;
	}

	/**
	 * Calls the method <code>showMessageBoxHandler</code> in the top window
	 * @param serializedData
	 */
	private native void setMessageBoxOrigin()/*-{
		$wnd.top._messageBox_origin = $wnd;
	}-*/;

	/**
	 * Calls the method <code>onOk</code> in the top window that has invoked the message box
	 */
	public static native JSWindow getAndClearMessageBoxOrigin()/*-{
		var o = $wnd.top._messageBox_origin;
		$wnd.top._messageBox_origin = null;
		return o;
	}-*/;
}