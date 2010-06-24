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

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.controller.crossdoc.Target;
import br.com.sysmap.crux.core.client.controller.crossdoc.TargetDocument;
import br.com.sysmap.crux.core.client.screen.JSWindow;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.widgets.client.util.FrameStateCallback;
import br.com.sysmap.crux.widgets.client.util.FrameUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante
 */
@Global
@Controller("__popup")
public class CruxInternalPopupController implements CruxInternalPopupControllerCrossDoc
{
	protected CruxInternalPopupControllerCrossDoc crossDoc = GWT.create(CruxInternalPopupControllerCrossDoc.class);
	protected DialogMessages messages = GWT.create(DialogMessages.class);
	
	private List<CustomDialogBox> dialogBoxes = new ArrayList<CustomDialogBox>();
	
	/**
	 * @return the window object of the popup opener
	 */
	public static native JSWindow getOpener()/*-{
		try{
			return $wnd.top._popup_origin[$wnd.top._popup_origin.length - 1];
		}catch(e)
		{
			return null;
		}
	}-*/;

	/**
	 * Invoke hide on top. It is required to handle multi-frame pages.
	 * @param data
	 */
	public static void hide()
	{
		Popup.unregisterLastShownPopup();
		if (popPopupOnStack())
		{
			CruxInternalPopupControllerCrossDoc crossDoc = GWT.create(CruxInternalPopupControllerCrossDoc.class);
			((TargetDocument)crossDoc).setTarget(Target.TOP);
			crossDoc.hidePopup();
		}
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnOpener(String call, Object param) throws ModuleComunicationException
	{
		callOpenerControllerAccessor(call,  Screen.getCruxSerializer().serialize(param));
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T> T invokeOnOpener(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		String result = callOpenerControllerAccessor(call,  Screen.getCruxSerializer().serialize(param));
		return (T) Screen.getCruxSerializer().deserialize(result);
	}

	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callOpenerControllerAccessor(String call, String serializedData)/*-{
		var o = $wnd.top._popup_origin[$wnd.top._popup_origin.length - 1];
		return o._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * Closes the popup, removing its window from the stack 
	 */
	private static native boolean popPopupOnStack()/*-{
		if($wnd.top._popup_origin != null)
		{
			$wnd.top._popup_origin.pop();
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Handler method to be invoked on top. That method hides the popup dialog.
	 */
	public void hidePopup()
	{
		if (dialogBoxes.size() > 0)
		{
			CustomDialogBox dialogBox = dialogBoxes.remove(dialogBoxes.size() - 1);
			Screen.unblockToUser();
			dialogBox.hide();
			dialogBox = null;
		}
	}
	
	/**
	 * Called by top window
	 */
	public void onClose()
	{
		BeforeCloseEvent evt = BeforeCloseEvent.fire(Popup.getLastShownPopup());
		if(!evt.isCanceled())
		{
			Popup.unregisterLastShownPopup();
			if (popPopupOnStack())
			{
				((TargetDocument)crossDoc).setTarget(Target.TOP);
				crossDoc.hidePopup();
			}
		}
	}
	
	/**
	 * Handler method to be invoked on top. That method shows the popup dialog.
	 * @param controllerEvent
	 */
	public void openPopup(PopupData data)
	{
		Screen.blockToUser("crux-PopupScreenBlocker");
		
		try
		{
			CustomDialogBox dialogBox = new CustomDialogBox(false, true, true);
			dialogBox.setStyleName(data.getStyleName());
			dialogBox.setAnimationEnabled(data.isAnimationEnabled());
			dialogBox.setWidth(data.getWidth());
			dialogBox.setHeight(data.getHeight());
			
			Frame frame = new Frame(Screen.appendDebugParameters(data.getUrl()));
			frame.setStyleName("frame");
			frame.setHeight("100%");
			frame.setWidth("100%");
			
			final Element frameElement = frame.getElement();
			frameElement.setPropertyString("frameBorder", "no");
			frameElement.setPropertyInt("border", 0);
			frameElement.setPropertyInt("marginWidth", 0);
			frameElement.setPropertyInt("marginHeight", 0);
			frameElement.setPropertyInt("vspace", 0);
			frameElement.setPropertyInt("hspace", 0);
			
			if (data.isCloseable())
			{
				final FocusPanel focusPanel = new FocusPanel();
				focusPanel.setStyleName("closeButton");
				focusPanel.addStyleDependentName("disabled");
				
				focusPanel.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						if (canClose(frameElement))
						{
							((TargetDocument)crossDoc).setTargetWindow(getOpener());
							crossDoc.onClose();
						}
					}
				});
				frameElement.setAttribute("canClose", "false");
				FrameUtils.registerStateCallback(frameElement, new FrameStateCallback(){
					public void onComplete()
					{
						focusPanel.removeStyleDependentName("disabled");
						frameElement.setAttribute("canClose", "true");
					}
				}, 20000);

				Label label = new Label(" ");
				label.getElement().getStyle().setProperty("fontSize", "0px");
				label.getElement().getStyle().setProperty("fontFamily", "monospace");
				focusPanel.add(label);

				dialogBox.setTopRightWidget(focusPanel);
			}

			dialogBox.setText(data.getTitle());
			dialogBox.setWidget(frame);
			dialogBox.center();

			dialogBoxes.add(dialogBox);
			
			dialogBox.show();
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}		
	}
	
	/**
	 * Open popup dialog.
	 * @param data
	 */
	public void showPopup(PopupData data)
	{
		pushPopupOnStack();
		((TargetDocument)crossDoc).setTarget(Target.TOP);
		crossDoc.openPopup(data);
	}
	
	/**
	 * 
	 * @param frameElement
	 * @return
	 */
	protected boolean canClose(Element frameElement)
	{
		String canClose = frameElement.getAttribute("canClose");
		return canClose != null && Boolean.parseBoolean(canClose);
	}

	/**
	 * 
	 * @param call
	 * @param serializedData
	 */
	private native void pushPopupOnStack()/*-{
		if($wnd.top._popup_origin == null)
		{
			$wnd.top._popup_origin = new Array();
		}		
		$wnd.top._popup_origin.push($wnd);
	}-*/;
}