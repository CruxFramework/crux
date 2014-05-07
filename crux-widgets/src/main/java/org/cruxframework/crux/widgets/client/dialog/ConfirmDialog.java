/*
 * Copyright 2013 cruxframework.org.
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

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.CancelHandler;
import org.cruxframework.crux.widgets.client.event.HasCancelHandlers;
import org.cruxframework.crux.widgets.client.event.HasOkHandlers;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple confirm dialog box
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConfirmDialog  implements HasOkHandlers, HasCancelHandlers, HasAnimation, IsWidget, HasCloseHandlers<ConfirmDialog>, HasOpenHandlers<ConfirmDialog>
{
	private static final String DEFAULT_STYLE_NAME = "crux-ConfirmDialog";
	private DialogBox dialogBox;
	private DockPanel confirmPanel;
	private Label messageLabel;
	private Button okButton;
	private Button cancelButton;

	private static List<CloseHandler<ConfirmDialog>> defaultCloseHandlers = new ArrayList<CloseHandler<ConfirmDialog>>();
	private static List<OpenHandler<ConfirmDialog>> defaultOpenHandlers = new ArrayList<OpenHandler<ConfirmDialog>>();
	
	/**
	 * Constructor 
	 */
	public ConfirmDialog()
	{
		dialogBox = new DialogBox(false, true);

		confirmPanel = new DockPanel();
		messageLabel = createMessageLabel();
		confirmPanel.add(messageLabel, DockPanel.CENTER);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(10);
		okButton = createOkButton();
		horizontalPanel.add(okButton);
		cancelButton = createCancelButton();
		horizontalPanel.add(cancelButton);
		
		if(defaultCloseHandlers != null)
		{
			for(CloseHandler<ConfirmDialog> closeHandler : defaultCloseHandlers)
			{
				this.addCloseHandler(closeHandler);
			}
		}
		
		if(defaultOpenHandlers != null)
		{
			for(OpenHandler<ConfirmDialog> openHandler : defaultOpenHandlers)
			{
				this.addOpenHandler(openHandler);
			}
		}

		confirmPanel.add(horizontalPanel, DockPanel.SOUTH);
		confirmPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);

		dialogBox.add(confirmPanel);
		confirmPanel.getElement().getParentElement().setAttribute("align", "center");

		setStyleName(DEFAULT_STYLE_NAME);
    }

	/**
	 * Get the dialog box title
	 * @return
	 */
	public String getDialogTitle()
	{
		return dialogBox.getText();
	}

	/**
	 * Set the dialog box title
	 * @param title
	 */
	public void setDialogTitle(String title)
	{
		dialogBox.setText(title);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#isAnimationEnabled()
	 */
	public boolean isAnimationEnabled()
	{
		return dialogBox.isAnimationEnabled();
	}

	/**
	 * Gets the message to be displayed to the user
	 * @return the message
	 */
	public String getMessage()
	{
		return messageLabel.getText();
	}

	/**
	 * Sets the message to be displayed to the user
	 * @param message
	 */
	public void setMessage(String message)
	{
		messageLabel.setText(message);
	}

	
	@Override
    public Widget asWidget()
    {
	    return dialogBox;
    }

	/**
	 * 
	 * @param styleName
	 */
	public void setStyleName(String styleName)
	{
		dialogBox.setStyleName(styleName);
	}
	
	/**
	 * 
	 * @param width
	 */
	public void setWidth(String width)
	{
		dialogBox.setWidth(width);
	}

	/**
	 * 
	 * @param height
	 */
	public void setHeight(String height)
	{
		dialogBox.setHeight(height);
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		dialogBox.setTitle(title);
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle()
	{
		return dialogBox.getTitle();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasAnimation#setAnimationEnabled(boolean)
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		dialogBox.setAnimationEnabled(animationEnabled);
	}	

	/**
	 * Adds a handler for the OK button click event
	 */
	public HandlerRegistration addOkHandler(OkHandler handler)
	{
		return dialogBox.addHandler(handler, OkEvent.getType());
	}	

	/**
	 * Adds a handler for the Cancel button click event
	 */
	public HandlerRegistration addCancelHandler(CancelHandler handler)
	{
		return dialogBox.addHandler(handler, CancelEvent.getType());
	}	

	/**
	 * Show message dilaog. The dialog is centered and the screen is blocked for edition
	 */
	public void show()
	{
		Screen.blockToUser("crux-ConfirmDialogScreenBlocker");
		
		//if it's a touch device, then we should wait for virtual keyboard to get closed.
		//Otherwise the dialog message will not be properly centered in screen.  
		if(Screen.getCurrentDevice().getInput().equals(DeviceAdaptive.Input.touch))
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
			{
				@Override
				public boolean execute() 
				{
					doShow();
					return false;
				}
			}, 1000);
		} else 
		{
			doShow();
		}
	}
	
	/**
	 * Show message dilaog. The dialog is centered and the screen is blocked for edition
	 */
	private void doShow()
	{
		try
		{
			dialogBox.center();
			dialogBox.show();
			okButton.setFocus(true);
			OpenEvent.fire(ConfirmDialog.this, ConfirmDialog.this);
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * Hides the message dialog
	 */
	public void hide()
	{
		dialogBox.hide();
		Screen.unblockToUser();
		CloseEvent.fire(ConfirmDialog.this, ConfirmDialog.this);
	}
	
	/**
	 * 
	 * @param okLabel
	 */
	public void setOkLabel(String okLabel)
	{
		okButton.setText(okLabel);
	}
	
	/**
	 * 
	 * @param cancelLabel
	 */
	public void setCancelLabel(String cancelLabel)
	{
		cancelButton.setText(cancelLabel);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOkLabel()
	{
		return okButton.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCancelLabel()
	{
		return cancelButton.getText();
	}

	/**
	 * Shows a confirm dialog
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 * @param cancelHandler a handler for the Cancel button click event
	 */
	public static ConfirmDialog show(String title, String message, OkHandler okHandler, CancelHandler cancelHandler)
	{
		return show(title, message, WidgetMsgFactory.getMessages().okLabel(), WidgetMsgFactory.getMessages().cancelLabel(), okHandler, cancelHandler, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * Shows a confirm dialog
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okLabel the text to be displayed in the body of the message box
	 * @param cancelLabel the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 * @param cancelHandler a handler for the Cancel button click event
	 */
	public static ConfirmDialog show(String title, String message, String okLabel, String cancelLabel, OkHandler okHandler, CancelHandler cancelHandler)
	{
		return show(title, message, okLabel, cancelLabel, okHandler, cancelHandler, DEFAULT_STYLE_NAME, false);
	}

	/**
	 * Shows a confirm dialog
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okLabel the text to be displayed in the body of the message box
	 * @param cancelLabel the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 * @param cancelHandler a handler for the Cancel button click event
	 * @param styleName the name of the CSS class to be applied in the message box element 
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public static ConfirmDialog show(String dialogTitle, String message, String okLabel, String cancelLabel, OkHandler okHandler, CancelHandler cancelHandler, String styleName, boolean animationEnabled)
	{
		ConfirmDialog confirm = new ConfirmDialog(); 
		confirm.setDialogTitle(dialogTitle);
		confirm.setOkLabel(okLabel);
		confirm.setCancelLabel(cancelLabel);
		confirm.setMessage(message);
		confirm.setStyleName(styleName);
		confirm.setAnimationEnabled(animationEnabled);
		if (okHandler != null)
		{
			confirm.addOkHandler(okHandler);
		}
		if (cancelHandler != null)
		{
			confirm.addCancelHandler(cancelHandler);
		}
		confirm.show();
		return confirm;
	}
	
	/**
	 * Creates the OK button
	 * @return
	 */
	private Button createOkButton()
	{
		Button okButton = new Button();
		
		okButton.setText(WidgetMsgFactory.getMessages().okLabel());
		okButton.addStyleName("button");
		okButton.addStyleName("okButton");
		okButton.addSelectHandler(new SelectHandler()
		{
			public void onSelect(SelectEvent event)
			{
				hide();
				try
				{
					OkEvent.fire(ConfirmDialog.this);
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
	 * Creates the Cancel button
	 * @return
	 */
	private Button createCancelButton()
	{
		Button cancelButton = new Button();
		
		cancelButton.setText(WidgetMsgFactory.getMessages().cancelLabel());
		cancelButton.addStyleName("button");
		cancelButton.addStyleName("cancelButton");
		cancelButton.addSelectHandler(new SelectHandler()
		{
			public void onSelect(SelectEvent event)
			{
				hide();
				try
				{
					CancelEvent.fire(ConfirmDialog.this);
				}
				catch (Throwable e)
				{
					Crux.getErrorHandler().handleError(e);
				}
			}
		});
		return cancelButton;
	}
	
	/**
	 * Creates a label to display the message
	 * @param data
	 * @return
	 */
	private Label createMessageLabel()
	{
		Label label = new Label();
		label.setStyleName("message");
		return label;
	}

	@Override
    public void fireEvent(GwtEvent<?> event)
    {
		dialogBox.fireEvent(event);
    }
	
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<ConfirmDialog> handler)
	{
		return dialogBox.addHandler(handler, CloseEvent.getType());
	}
	
	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<ConfirmDialog> handler) 
	{
		return dialogBox.addHandler(handler, OpenEvent.getType());
	}
	
	/**
	 * Add a default open handler that will be appended to each created object
	 * @param defaultOpenHandler
	 */
	public static void addDefaultOpenHandler(OpenHandler<ConfirmDialog> defaultOpenHandler) 
	{
		if(defaultOpenHandlers == null)
		{
			defaultOpenHandlers = new ArrayList<OpenHandler<ConfirmDialog>>();
		}
		defaultOpenHandlers.add(defaultOpenHandler);
	}
	
	/**
	 * Add a default close handler that will be appended to each created object
	 * @param defaultCloseHandler
	 */
	public static void addDefaultCloseHandler(CloseHandler<ConfirmDialog> defaultCloseHandler) 
	{
		if(defaultCloseHandlers == null)
		{
			defaultCloseHandlers = new ArrayList<CloseHandler<ConfirmDialog>>();
		}
		defaultCloseHandlers.add(defaultCloseHandler);
	}
}
