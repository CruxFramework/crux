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
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Multi-frame aware controller for showing the progress dialog
 * @author Gesse S. F. Dafe
 */
@Global
@Controller(value="__progressDialog")
public class CruxInternalProgressDialogController implements CruxInternalProgressDialogControllerCrossDoc
{
	private DialogBox dialog = null;
	private Label messageLabel = null;
	private CruxInternalProgressDialogControllerCrossDoc crossDoc = GWT.create(CruxInternalProgressDialogControllerCrossDoc.class);
	
	
	private FastList<String> stack = new FastList<String>(); 
	private HandlerRegistration previewHandler = null; 
	private int numProgressDialogOnDocument = 0;
	
	/**
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalProgressDialogControllerCrossDoc#disableEventsOnOpener()
	 */
	public void disableEventsOnOpener()
	{
		numProgressDialogOnDocument++;
		if (numProgressDialogOnDocument == 1)
		{
			previewHandler = Event.addNativePreviewHandler(new NativePreviewHandler()
			{
				public void onPreviewNativeEvent(NativePreviewEvent event)
				{
					event.cancel();
					return;
				}
			});
		}
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalProgressDialogControllerCrossDoc#enableEventsOnOpener()
	 */
	public void enableEventsOnOpener()
	{
		numProgressDialogOnDocument--;
		if (numProgressDialogOnDocument == 0)
		{
			if (previewHandler != null)
			{
				previewHandler.removeHandler();
				previewHandler = null;
			}
		}
	}

	/**
	 * Invoke showProgressDialogOnTop on top window. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showProgressDialog(ProgressDialogData data)
	{
		pushProgressDialogOnStack();
		((TargetDocument)crossDoc).setTarget(Target.TOP);
		crossDoc.showProgressDialogBox(data);
		((TargetDocument)crossDoc).setTargetWindow(getOpener());
		crossDoc.disableEventsOnOpener();
	}
	
	/**
	 * Invoke hideProgressDialogOnTop on top window. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void hideProgressDialog()
	{
		JSWindow opener = getOpener();
		if (opener != null)
		{
			((TargetDocument)crossDoc).setTargetWindow(opener);
			crossDoc.enableEventsOnOpener();
			((TargetDocument)crossDoc).setTarget(Target.TOP);
			crossDoc.hideProgressDialogBox();
			popProgressDialogFromStack();
		}
	}
	
	/**
	 * Handler method to be invoked on top. This method shows the progress dialog.
	 * @param controllerEvent
	 */
	public void showProgressDialogBox(ProgressDialogData data)
	{
		try
		{
			String message = data.getMessage();
			this.stack.add(message);
			
			if(this.stack.size() == 1)
			{
				Screen.blockToUser("crux-ProgressDialogScreenBlocker");
				
				final DialogBox dialogBox = new DialogBox(false, true);
				dialogBox.setStyleName(data.getStyleName());
				dialogBox.setAnimationEnabled(data.isAnimationEnabled());
				
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setSpacing(0);
	
				FocusPanel iconPanel = createIconPanel();
				horizontalPanel.add(iconPanel);
				this.messageLabel = createMessageLabel(message);
				horizontalPanel.add(this.messageLabel);
							
				dialogBox.add(horizontalPanel);
				
				dialog = dialogBox;
				
				dialogBox.center();
				dialogBox.show();				
			}
			else
			{
				this.messageLabel.setText(message);
			}				
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * Handler method to be invoked on top. That method hides the progress dialog.
	 */
	public void hideProgressDialogBox()
	{
		if(this.stack.size() <= 1)
		{			
			if(dialog != null)
			{
				Screen.unblockToUser();
				dialog.hide();
				dialog = null;
			}
			
			this.stack.clear();
		}
		else
		{
			this.stack.remove(this.stack.size() - 1);
			String msg = this.stack.get(this.stack.size() - 1);
			this.messageLabel.setText(msg);
		}
	}
	
	/**
	 * Creates a panel to display a icon for the message 
	 * @return a panel
	 */
	private FocusPanel createIconPanel()
	{
		FocusPanel iconPanel = new FocusPanel();
		iconPanel.setStyleName("icon");
		return iconPanel;
	}

	/**
	 * Creates a label to display the message 
	 * @param data
	 * @return a label
	 */
	private Label createMessageLabel(String message)
	{
		Label label = new Label(message);
		label.setStyleName("message");
		return label;
	}
	
	
	/**
	 * Closes the popup, removing its window from the stack 
	 */
	private static native boolean popProgressDialogFromStack()/*-{
		if($wnd.top._progressDialog_origin != null)
		{
			$wnd.top._progressDialog_origin.pop();
			return true;
		}
		return false;
	}-*/;
	
	private native void pushProgressDialogOnStack()/*-{
		if($wnd.top._progressDialog_origin == null)
		{
			$wnd.top._progressDialog_origin = new Array();
		}		
		$wnd.top._progressDialog_origin.push($wnd);
	}-*/;
	
	public static native JSWindow getOpener()/*-{
	try{
		return $wnd.top._progressDialog_origin[$wnd.top._progressDialog_origin.length - 1];
	}catch(e)
	{
		return null;
	}
}-*/;
	
}
