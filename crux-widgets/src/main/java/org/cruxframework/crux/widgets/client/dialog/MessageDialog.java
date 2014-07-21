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
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.widgets.client.WidgetMessages;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.HasOkHandlers;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple message dialog box
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MessageDialog implements HasOkHandlers, HasAnimation, IsWidget, OrientationChangeHandler, HasCloseHandlers<MessageDialog>, HasOpenHandlers<MessageDialog>
{
	private static final String DEFAULT_STYLE_NAME = "crux-MessageDialog";
	private DialogBox dialogBox;
	private DockPanel messagePanel;
	private HTML messageLabel;
	private Button okButton;
	protected WidgetMessages messages = WidgetMsgFactory.getMessages();
	private FastList<DialogBox> openedDialogBoxes = new FastList<DialogBox>(); 

	private static List<CloseHandler<MessageDialog>> defaultCloseHandlers = new ArrayList<CloseHandler<MessageDialog>>();
	private static List<OpenHandler<MessageDialog>> defaultOpenHandlers = new ArrayList<OpenHandler<MessageDialog>>();
	
	/**
	 * Constructor 
	 */
	public MessageDialog()
	{
		dialogBox = new DialogBox(false, true);

		messagePanel = new DockPanel();
		messageLabel = createMessageLabel();
		messagePanel.add(messageLabel, DockPanel.CENTER);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(10);
		okButton = createOkButton();
		horizontalPanel.add(okButton);
		
		if(defaultCloseHandlers != null)
		{
			for(CloseHandler<MessageDialog> closeHandler : defaultCloseHandlers)
			{
				this.addCloseHandler(closeHandler);
			}
		}
		
		if(defaultOpenHandlers != null)
		{
			for(OpenHandler<MessageDialog> openHandler : defaultOpenHandlers)
			{
				this.addOpenHandler(openHandler);
			}
		}

		messagePanel.add(horizontalPanel, DockPanel.SOUTH);
		messagePanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);

		dialogBox.add(messagePanel);
		messagePanel.getElement().getParentElement().setAttribute("align", "center");

		setStyleName(DEFAULT_STYLE_NAME);
		handleOrientationChangeHandlers();
    }

	private void handleOrientationChangeHandlers() {
		dialogBox.addAttachHandler(new Handler()
		{
			private HandlerRegistration orientationHandlerRegistration;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					try
					{
						orientationHandlerRegistration = Screen.addOrientationChangeHandler(MessageDialog.this);	
					} catch (Exception e)
					{
						orientationHandlerRegistration = null;
					}
				}
				else if (orientationHandlerRegistration != null)
				{
					orientationHandlerRegistration.removeHandler();
					orientationHandlerRegistration = null;
				}
			}
		});
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
		dialogBox.setHTML(title);
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
	public void setMessage(SafeHtml message)
	{
		messageLabel.setHTML(message);
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
	public void setTitle(SafeHtml title)
	{
		dialogBox.setHTML(title);
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
			openedDialogBoxes.add(dialogBox);
			dialogBox.center();
			dialogBox.show();
			okButton.setFocus(true);
			OpenEvent.fire(MessageDialog.this, MessageDialog.this);
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
		openedDialogBoxes.remove(openedDialogBoxes.indexOf(dialogBox));
		Screen.unblockToUser();
		CloseEvent.fire(MessageDialog.this, MessageDialog.this);
	}
	
	
	/**
	 * Shows a message dialog
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 */
	public static MessageDialog show(String title, String message, OkHandler okHandler)
	{
		return show(title, message, okHandler, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * Shows a message dialog
	 * @param title the text to be displayed as the caption of the message box 
	 * @param message the text to be displayed in the body of the message box
	 * @param okHandler a handler for the OK button click event
	 * @param styleName the name of the CSS class to be applied in the message box element 
	 * @param animationEnabled true to enable animations while showing or hiding the message box
	 */
	public static MessageDialog show(String title, String message, OkHandler okHandler, String styleName, boolean animationEnabled)
	{
		MessageDialog messageBox = new MessageDialog(); 
		messageBox.setDialogTitle(title);
		messageBox.setMessage(message);
		messageBox.setStyleName(styleName);
		messageBox.setAnimationEnabled(animationEnabled);
		if (okHandler != null)
		{
			messageBox.addOkHandler(okHandler);
		}
		messageBox.show();
		return messageBox;
	}
	
	/**
	 * Creates the OK button
	 * @return
	 */
	private Button createOkButton()
	{
		Button okButton = new Button();
		
		okButton.setText(messages.okLabel());
		okButton.addStyleName("button");
		okButton.addStyleName("okButton");
		okButton.addSelectHandler(new SelectHandler()
		{
			public void onSelect(SelectEvent event)
			{
				hide();
				try
				{
					OkEvent.fire(MessageDialog.this);
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
	 * Creates a label to display the message
	 * @param data
	 * @return
	 */
	private HTML createMessageLabel()
	{
		HTML label = new HTML();
		label.setStyleName("message");
		return label;
	}

	@Override
    public void fireEvent(GwtEvent<?> event)
    {
		dialogBox.fireEvent(event);
    }

	@Override
	public void onOrientationChange() 
	{
		if(openedDialogBoxes == null)
		{
			return;
		}
		
		for(int i=0; i<openedDialogBoxes.size(); i++)
		{
			final int index = i;
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() 
			{
				@Override
				public boolean execute() {
					openedDialogBoxes.get(index).center();
					return false;
				}
			}, 1000);
		}
	}
	
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<MessageDialog> handler)
	{
		return dialogBox.addHandler(handler, CloseEvent.getType());
	}
	
	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<MessageDialog> handler) 
	{
		return dialogBox.addHandler(handler, OpenEvent.getType());
	}
	
	/**
	 * Add a default open handler that will be appended to each created object
	 * @param defaultOpenHandler
	 */
	public static void addDefaultOpenHandler(OpenHandler<MessageDialog> defaultOpenHandler) 
	{
		if(defaultOpenHandlers == null)
		{
			defaultOpenHandlers = new ArrayList<OpenHandler<MessageDialog>>();
		}
		defaultOpenHandlers.add(defaultOpenHandler);
	}
	
	/**
	 * Add a default close handler that will be appended to each created object
	 * @param defaultCloseHandler
	 */
	public static void addDefaultCloseHandler(CloseHandler<MessageDialog> defaultCloseHandler) 
	{
		if(defaultCloseHandlers == null)
		{
			defaultCloseHandlers = new ArrayList<CloseHandler<MessageDialog>>();
		}
		defaultCloseHandlers.add(defaultCloseHandler);
	}
}
