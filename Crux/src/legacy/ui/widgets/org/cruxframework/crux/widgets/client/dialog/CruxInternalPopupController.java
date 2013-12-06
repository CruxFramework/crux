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
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Global;
import org.cruxframework.crux.core.client.controller.crossdoc.RequiresCrossDocumentSupport;
import org.cruxframework.crux.core.client.controller.crossdoc.Target;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.event.openclose.OpenEvent;
import org.cruxframework.crux.widgets.client.util.FrameStateCallback;
import org.cruxframework.crux.widgets.client.util.FrameUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 */
@Global
@Controller(value="__popup", lazy=false)
@RequiresCrossDocumentSupport
@Legacy
@Deprecated
public class CruxInternalPopupController implements CruxInternalPopupControllerCrossDoc
{
	protected CruxInternalPopupControllerCrossDoc crossDoc = GWT.create(CruxInternalPopupControllerCrossDoc.class);
	protected DialogMessages messages = GWT.create(DialogMessages.class);	
	private FastList<CustomDialogBox> dialogBoxes = new FastList<CustomDialogBox>();
	private boolean waitingForOpenEvent = false;
	
	/**
	 * If there is an shown popup at this moment, it must be the one whose opening must be notified. 
	 */
	public CruxInternalPopupController()
	{
		Timer timer = new Timer()
		{
			@Override
			public void run()
			{
				JSWindow opener = Popup.getOpener();
				if(opener != null)
				{
					((TargetDocument) crossDoc).setTargetWindow(opener);
					crossDoc.onOpen();				
				}
			}
		};
		
		timer.schedule(10);
	}
	
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
	 * Return the window of the last shown popup
	 * @return
	 */
	public static native JSWindow getWindow() /*-{
		if($wnd.top._popup_wndws != null && $wnd.top._popup_wndws.length > 0)
		{
			return $wnd.top._popup_wndws[$wnd.top._popup_wndws.length - 1];
		}
		return null; 
	}-*/;

	/**
	 * Invoke hide on top. It is required to handle multi-frame pages.
	 */
	public static void hide()
	{
		hide(false);
	}

	/**
	 * Invoke hide on top. It is required to handle multi-frame pages.
	 * 
	 * @param fireCloseEvent
	 *            Inform if BeforeCloseEvent must be fired
	 */
	public static void hide(boolean fireCloseEvent)
	{
		CruxInternalPopupControllerCrossDoc crossDoc = GWT.create(CruxInternalPopupControllerCrossDoc.class);
		((TargetDocument) crossDoc).setTargetWindow(getOpener());
		if (fireCloseEvent)
		{
			crossDoc.onClose();
		}
		else
		{
			crossDoc.close();
		}
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeOnOpener(String call, Object param) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
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
	public static <T> T invokeOnOpener(String call, Object param, Class<T> resultType) throws org.cruxframework.crux.core.client.screen.ModuleComunicationException
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
	private static native boolean popPopupFromStack()/*-{
		if($wnd.top._popup_origin != null && $wnd.top._popup_wndws != null)
		{
			$wnd.top._popup_origin.pop();
			$wnd.top._popup_wndws.pop();
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
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalPopupControllerCrossDoc#onClose()
	 */
	public void onClose()
	{
		BeforeCloseEvent evt = BeforeCloseEvent.fire(Popup.getLastShownPopup());
		if(!evt.isCanceled())
		{
			close();
		}
	}
	
	/**
	 * Called from popup window and executed on popup opener
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalPopupControllerCrossDoc#close()
	 */
	public void close()
	{
		Popup.unregisterLastShownPopup();
		if (popPopupFromStack())
		{
			((TargetDocument)crossDoc).setTarget(Target.TOP);
			crossDoc.hidePopup();
		}
	}
	
	
	/**
	 * Called by top window
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalPopupControllerCrossDoc#prepareToOpen()
	 */
	public void prepareToOpen()
	{
		this.waitingForOpenEvent = true;
	}
	
	/**
	 * Called by top window
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalPopupControllerCrossDoc#isWaitingForOpen()
	 */
	public boolean isWaitingForOpenEvent()
	{
		return this.waitingForOpenEvent;
	}

	/**
	 * Called by top window
	 * @see org.cruxframework.crux.widgets.client.dialog.CruxInternalPopupControllerCrossDoc#onOpen()
	 */
	public void onOpen()
	{
		if(this.isWaitingForOpenEvent())
		{
			this.waitingForOpenEvent = false;
			OpenEvent.fire(Popup.getLastShownPopup());
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
			
			Frame frame = new Frame(Screen.rewriteUrl(data.getUrl()));
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
				final Button focusPanel = new Button(" ");
				focusPanel.setStyleName("closeButton");
				
				focusPanel.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						if (canClose(frameElement))
						{
							((TargetDocument) crossDoc).setTargetWindow(getOpener());
							crossDoc.onClose();
						}
					}
				});
				
				PopupLoadListener listener = GWT.create(PopupLoadListener.class);
				listener.setup(frameElement, focusPanel);
				FrameUtils.registerStateCallback(frameElement, listener);
				
				dialogBox.setTopRightWidget(focusPanel);
			}			
			
			 ((TargetDocument) crossDoc).setTargetWindow(getOpener());
			 crossDoc.prepareToOpen();

			dialogBox.setText(data.getTitle());
			dialogBox.setWidget(frame);
			dialogBox.center();

			dialogBoxes.add(dialogBox);
			
			dialogBox.show();
			
			pushPopupWindowOnStack(FrameUtils.getFrameWindow((IFrameElement) frameElement));
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}		
	}
	
	/**
	 * CallBack for Popup frame loading 
	 * @author Gesse Dafe
	 */
	public static abstract class PopupLoadListener implements FrameStateCallback
	{
		private Widget closeBtn;
		private Element popupFrame;

		/**
		 * @param popupFrame
		 * @param closeBtn
		 */
		public void setup(Element popupFrame, Widget closeBtn)
		{
			this.popupFrame = popupFrame;
			this.closeBtn = closeBtn;
			blockPopupClosing(popupFrame, closeBtn);		
		}

		/**
		 * @param popupFrame 
		 */
		protected abstract void blockPopupClosing(Element popupFrame, Widget closeBtn);
		
		/**
		 * @param popupFrame 
		 * @param closeBtn 
		 */
		protected abstract void unblockPopupClosing(Element popupFrame, Widget closeBtn);

		/**
		 * @see org.cruxframework.crux.widgets.client.util.FrameStateCallback#onComplete()
		 */
		public void onComplete()
		{
			if(this.closeBtn != null)
			{
				unblockPopupClosing(this.popupFrame, this.closeBtn);
			}
		}
	}
	
	/**
	 * CallBack for Popup frame loading 
	 * @author Gesse Dafe
	 */
	public static class PopupLoadListenerImpl extends PopupLoadListener
	{
		@Override
		protected void blockPopupClosing(Element popupFrame, Widget closeBtn)
		{
			popupFrame.setAttribute("canClose", "false");
			closeBtn.addStyleDependentName("disabled");
		}

		@Override
		protected void unblockPopupClosing(Element popupFrame, Widget closeBtn)
		{
			closeBtn.removeStyleDependentName("disabled");
			popupFrame.setAttribute("canClose", "true");			
		}		
	}
	
	/**
	 * CallBack for Popup frame loading 
	 * @author Gesse Dafe
	 */
	public static class PopupLoadListenerSafariImpl extends PopupLoadListener
	{
		@Override
		protected void blockPopupClosing(Element popupFrame, Widget closeBtn)
		{
			popupFrame.setAttribute("canClose", "true");
		}

		@Override
		protected void unblockPopupClosing(Element popupFrame, Widget closeBtn)
		{
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
	 * Registers the freshly open popup window on the stack
	 * @param frameWindow
	 */
	private native void pushPopupWindowOnStack(JSWindow frameWindow)/*-{
		$wnd.top._popup_wndws.push(frameWindow);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 */
	private native void pushPopupOnStack()/*-{
		if($wnd.top._popup_origin == null && $wnd.top._popup_wndws == null)
		{
			$wnd.top._popup_origin = new Array();
			$wnd.top._popup_wndws = new Array();
		}		
		$wnd.top._popup_origin.push($wnd);
	}-*/;
}