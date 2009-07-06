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

import br.com.sysmap.crux.core.client.component.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.component.ModuleComunicationException;
import br.com.sysmap.crux.core.client.component.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.component.Screen;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@Global
@Controller("__progressDialog")
public class ProgressDialogController 
{
	private ModuleComunicationSerializer serializer;
	private DialogBox dialog;
	
	public ProgressDialogController()
	{
		this.serializer = Screen.getCruxSerializer();
		this.serializer.registerCruxSerializable(ProgressDialogData.class.getName(), new ProgressDialogData());
	}

	/**
	 * Invoke showProgressDialogOnTop on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showProgressDialog(ProgressDialogData data)
	{
		try
		{
			showProgressDialogOnTop(serializer.serialize(data));
		}
		catch (ModuleComunicationException e)
		{
			GWT.log(e.getMessage(), e);
		}	
	}
	
	/**
	 * Invoke hideProgressDialogOnTop on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void hideProgressDialog()
	{
		hideProgressDialogOnTop();
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the progress dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showProgressDialogHandler(InvokeControllerEvent controllerEvent)
	{
		if(dialog == null)
		{
			Screen.blockToUser("crux-ProgressDialogScreenBlocker");
			
			try
			{
				final ProgressDialogData data = (ProgressDialogData) controllerEvent.getData();
				
				final DialogBox dialogBox = new DialogBox(false, true);
				dialogBox.setStyleName(data.getStyleName());
				dialogBox.setAnimationEnabled(data.isAnimationEnabled());
				
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setSpacing(0);
	
				horizontalPanel.add(createIconPanel());
				horizontalPanel.add(createMessageLabel(data));
							
				dialogBox.add(horizontalPanel);
				
				dialog = dialogBox;
				
				dialogBox.center();
				dialogBox.show();
				
				
			}
			catch (Exception e)
			{
				GWT.log(e.getMessage(), e);
				Screen.unblockToUser();
			}
		}
	}

	/**
	 * Handler method to be invoked on top. That method hides the progress dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void hideProgressDialogHandler(InvokeControllerEvent controllerEvent)
	{
		if(dialog != null)
		{
			Screen.unblockToUser();
			dialog.hide();
			dialog = null;
		}
	}
	
	/**
	 * @return
	 */
	private Widget createIconPanel()
	{
		SimplePanel iconPanel = new SimplePanel();
		iconPanel.setStyleName("icon");
		return iconPanel;
	}

	/**
	 * @param data
	 * @return
	 */
	private Label createMessageLabel(final ProgressDialogData data)
	{
		Label label = new Label(data.getMessage());
		label.setStyleName("message");
		return label;
	}

	/**
	 * @param serializedData
	 */
	private native void showProgressDialogOnTop(String serializedData)/*-{
		$wnd.top._cruxScreenControllerAccessor("__progressDialog.showProgressDialogHandler", serializedData);
	}-*/;
	
	/**
	 * 
	 */
	private native void hideProgressDialogOnTop()/*-{
		$wnd.top._cruxScreenControllerAccessor("__progressDialog.hideProgressDialogHandler");
	}-*/;
}
