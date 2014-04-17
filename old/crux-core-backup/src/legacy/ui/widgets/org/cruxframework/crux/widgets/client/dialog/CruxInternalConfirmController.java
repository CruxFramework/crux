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
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.crossdoc.RequiresCrossDocumentSupport;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.decoratedbutton.DecoratedButton;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
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
 *
 * @author Thiago da Rosa de Bustamante
 *
 */
@Global
@Controller("__confirm")
@RequiresCrossDocumentSupport
@Legacy
@Deprecated
public class CruxInternalConfirmController implements CruxInternalConfirmControllerCrossDoc
{
	protected DialogMessages messages = GWT.create(DialogMessages.class);
	protected CruxInternalConfirmControllerCrossDoc crossDoc = GWT.create(CruxInternalConfirmControllerCrossDoc.class);

	/**
	 * Called by top window
	 */
	public void onCancel()
	{
		try
		{
			CancelEvent.fire(Confirm.confirm);
		}
		catch (Throwable e)
		{
			Crux.getErrorHandler().handleError(e);
		}
	}

	/**
	 * Called by top window
	 */
	public void onOk()
	{
		try
		{
			OkEvent.fire(Confirm.confirm);
		}
		catch (Throwable e)
		{
			Crux.getErrorHandler().handleError(e);
		}
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
					JSWindow origin = getOpener();
					if (origin != null)
					{
						cancelClick(origin);
					}
				}
				catch (Throwable e)
				{
					// IE 7 BUG: When the reference window no longer exists.
				}

				popConfirmFromStack();
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
					JSWindow origin = getOpener();
					if (origin != null)
					{
						okClick(origin);
					}
				}
				catch (Throwable e)
				{
					// IE 7 BUG: When the reference window no longer exists.
				}

				popConfirmFromStack();
			}
		});

		Screen.ensureDebugId(okButton, "_crux_confirm_ok_" + data.getMessage());

		return okButton;
	}

	/**
	 * Execute a ok click event on a origin window
	 * @param origin
	 */
	private native void okClick(JSWindow origin)/*-{
		if (origin && origin._cruxCrossDocumentAccessor)
		{
			origin._cruxCrossDocumentAccessor("__confirm|onOk()|");
		}
	}-*/;

	/**
	 * Execute a cancel click event on a origin window
	 * @param origin
	 */
	private native void cancelClick(JSWindow origin)/*-{
		if (origin && origin._cruxCrossDocumentAccessor)
		{
			origin._cruxCrossDocumentAccessor("__confirm|onCancel()|");
		}
	}-*/;

	/**
	 * Closes the confirm, removing its window from the stack
	 */
	private static native boolean popConfirmFromStack()/*-{
		if($wnd.top._confirm_origin != null)
		{
			$wnd.top._confirm_origin.pop();
			if ($wnd.top._confirm_origin.length == 0)
			{
				$wnd.top._messageBox_origin = null;
			}
			return true;
		}
		return false;
	}-*/;

	/**
	 * Gets the window that has invoked the confirm
	 * @return
	 */
	private static native JSWindow getOpener()/*-{
		try
		{
			var o = $wnd.top._confirm_origin[$wnd.top._confirm_origin.length - 1];

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
