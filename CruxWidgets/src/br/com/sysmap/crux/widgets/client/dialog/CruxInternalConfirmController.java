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
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
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
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Global
@Controller("__confirm")
public class CruxInternalConfirmController implements CruxInternalConfirmControllerCrossDoc
{
	protected DialogMessages messages = GWT.create(DialogMessages.class);
	protected CruxInternalConfirmControllerCrossDoc crossDoc = GWT.create(CruxInternalConfirmControllerCrossDoc.class);

	/**
	 * Called by top window
	 */
	public void onCancel()
	{
		CancelEvent.fire(Confirm.confirm);
	}

	/**
	 * Called by top window
	 */
	public void onOk()
	{
		OkEvent.fire(Confirm.confirm);
	}

	/**
	 * Handler method to be invoked on top. That method shows the popup dialog.
	 * @param data all data needed to show the popup
	 */
	public void showConfirm(ConfirmData data)
	{
		Screen.blockToUser("crux-ConfirmScreenBlocker");
		
		try
		{
			final DialogBox dialogBox = new DialogBox(false, true);
			
			DecoratedButton okBtn = createOkButton(dialogBox, data);
			
			dialogBox.setStyleName(data.getStyleName());
			dialogBox.setAnimationEnabled(data.isAnimationEnabled());
			dialogBox.setText(data.getTitle());
			
			DockPanel dockPanel = new DockPanel();
			dockPanel.add(createMessageLabel(data), DockPanel.CENTER);
			
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSpacing(10);
			horizontalPanel.add(okBtn);
			horizontalPanel.add(createCancelButton(dialogBox, data));
			
			dockPanel.add(horizontalPanel, DockPanel.SOUTH);
			dockPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
			
			dialogBox.add(dockPanel);
			dialogBox.center();
			dialogBox.show();
			
			okBtn.setFocus(true);
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void cancelClick()/*-{
		var o = $wnd.top._confirm_origin;
		$wnd.top._confirm_origin = null;
		o._cruxCrossDocumentAccessor("__confirm|onCancel()|");
	}-*/;

	/**
	 * @param dialogBox
	 * @param data 
	 * @return
	 */
	private DecoratedButton createCancelButton(final DialogBox dialogBox, ConfirmData data)
	{
		DecoratedButton cancelButton = new DecoratedButton();
		String text = data.getCancelButtonText();
		if(text == null || text.length() == 0)
		{
			text = messages.confirmCancelLabel();
		}
		cancelButton.setText(text);
		cancelButton.addStyleName("button");
		cancelButton.addStyleName("cancelButton");
		cancelButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Screen.unblockToUser();
				
				dialogBox.hide();
								
				try
				{
					cancelClick();
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}
			}
		});
		
		Screen.ensureDebugId(cancelButton, "_crux_confirm_cancel_" + data.getMessage());
		
		return cancelButton;
	}

	/**
	 * @param data
	 * @return
	 */
	private Label createMessageLabel(final ConfirmData data)
	{
		Label label = new Label(data.getMessage());
		label.setStyleName("message");
		return label;
	}

	/**
	 * @param dialogBox
	 * @param data 
	 * @return
	 */
	private DecoratedButton createOkButton(final DialogBox dialogBox, ConfirmData data)
	{
		DecoratedButton okButton = new DecoratedButton();
		String text = data.getOkButtonText();
		if(text == null || text.length() == 0)
		{
			text = messages.confirmOkLabel();
		}
		okButton.setText(text);
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
		
		Screen.ensureDebugId(okButton, "_crux_confirm_ok_" + data.getMessage());
		
		return okButton;
	}

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void okClick()/*-{
		var o = $wnd.top._confirm_origin;
		$wnd.top._confirm_origin = null;
		o._cruxCrossDocumentAccessor("__confirm|onOk()|");
	}-*/;
}
