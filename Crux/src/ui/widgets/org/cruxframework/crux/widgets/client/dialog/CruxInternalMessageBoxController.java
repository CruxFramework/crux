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
package org.cruxframework.crux.widgets.client.dialog;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.decoratedbutton.DecoratedButton;
import org.cruxframework.crux.widgets.client.event.OkEvent;

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
@Controller(value="__messageBox")
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
		pushMessageBoxOnStack();
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
					JSWindow origin = getOpener();
					if (origin != null)
					{	
						((TargetDocument)crossDoc).setTargetWindow(origin);
						crossDoc.onOk();
					}
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}

				popMessageBoxFromStack();
			}
		});
		
		Screen.ensureDebugId(okButton, "_crux_msgBox_ok_" + data.getMessage());
		
		return okButton;
	}

	/**
	 * Closes the message box, removing its window from the stack 
	 */
	private static native boolean popMessageBoxFromStack()/*-{
		if($wnd.top._messageBox_origin != null)
		{
			$wnd.top._messageBox_origin.pop();
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Push the window that has invoked the message box
	 */
	private native void pushMessageBoxOnStack()/*-{
		if($wnd.top._messageBox_origin == null)
		{
			$wnd.top._messageBox_origin = new Array();
		}		
		$wnd.top._messageBox_origin.push($wnd);
	}-*/;
	
	/**
	 * Gets the window that has invoked the message box
	 * @return
	 */
	public static native JSWindow getOpener()/*-{
		try
		{
			var o = $wnd.top._messageBox_origin[$wnd.top._messageBox_origin.length - 1];
			
			if (o && o._cruxCrossDocumentAccessor)
			{
				return o;
			}	
			else 
			{
				return null;
			}	
		}
		catch(e)
		{
			return null;
		}
	}-*/;
}