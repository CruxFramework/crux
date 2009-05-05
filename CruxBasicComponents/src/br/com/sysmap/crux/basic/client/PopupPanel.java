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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.UIObject;
import br.com.sysmap.crux.core.client.event.bind.CloseEvtBind;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;


/**
 * Represents a PopupPanel
 * @author Thiago Bustamante
 *
 */
public class PopupPanel extends SimplePanel
{
	protected com.google.gwt.user.client.ui.PopupPanel popupPanelWidget;

	public PopupPanel(String id)
	{
		this(id, new com.google.gwt.user.client.ui.PopupPanel());
	}

	public PopupPanel(String id, boolean autoHide, boolean modal)
	{
		this(id, new com.google.gwt.user.client.ui.PopupPanel(autoHide, modal));
	}

	public PopupPanel(String id, Element element)
	{
		this(id, createPopupWidget(element));
	}

	protected PopupPanel(String id, com.google.gwt.user.client.ui.PopupPanel widget) 
	{
		super(id, widget);
		this.popupPanelWidget = widget; 
	}

	protected static com.google.gwt.user.client.ui.PopupPanel createPopupWidget(Element element)
	{
		String autoHideStr = element.getAttribute("_autoHide");
		boolean autoHide = false;
		if (autoHideStr != null && autoHideStr.length() >0)
		{
			autoHide = Boolean.parseBoolean(autoHideStr);
		}
		String modalStr = element.getAttribute("_modal");
		boolean modal = false;
		if (modalStr != null && modalStr.length() >0)
		{
			modal = Boolean.parseBoolean(modalStr);
		}

		return new com.google.gwt.user.client.ui.PopupPanel(autoHide, modal);
	}

	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 * 
	 * @param partner the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner) 
	{
		popupPanelWidget.addAutoHidePartner(partner);
	}

	/**
	 * Centers the popup in the browser window and shows it. If the popup was
	 * already showing, then the popup is centered.
	 */
	public void center() 
	{
		popupPanelWidget.center();
	}	

	/**
	 * Gets the popup's left position relative to the browser's client area.
	 * 
	 * @return the popup's left position
	 */
	public int getPopupLeft() 
	{
		return popupPanelWidget.getPopupLeft();
	}

	/**
	 * Gets the popup's top position relative to the browser's client area.
	 * 
	 * @return the popup's top position
	 */
	public int getPopupTop() 
	{
		return popupPanelWidget.getPopupTop();
	}

	/**
	 * Hides the popup and detaches it from the page. This has no effect if it is
	 * not currently showing.
	 */
	public void hide() 
	{
		popupPanelWidget.hide();
	}

	/**
	 * Hides the popup and detaches it from the page. This has no effect if it is
	 * not currently showing.
	 * 
	 * @param autoClosed the value that will be passed to
	 *          {@link CloseHandler#onClose(CloseEvent)} when the popup is closed
	 */
	public void hide(boolean autoClosed) 
	{
		popupPanelWidget.hide(autoClosed);
	}

	public boolean isAnimationEnabled() 
	{
		return popupPanelWidget.isAnimationEnabled();
	}

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the user clicks outside of it.
	 * 
	 * @return true if autoHide is enabled, false if disabled
	 */
	public boolean isAutoHideEnabled() 
	{
		return popupPanelWidget.isAutoHideEnabled();
	}

	/**
	 * Returns <code>true</code> if keyboard or mouse events that do not target
	 * the PopupPanel or its children should be ignored.
	 * 
	 * @return true if popup is modal, false if not
	 */
	public boolean isModal() 
	{
		return popupPanelWidget.isModal();
	}

	/**
	 * Returns <code>true</code> if the popup should preview all native events,
	 * even if the event has already been consumed by another popup.
	 * 
	 * @return true if previewAllNativeEvents is enabled, false if disabled
	 */
	public boolean isPreviewingAllNativeEvents() 
	{
		return popupPanelWidget.isPreviewingAllNativeEvents();
	}

	/**
	 * Determines whether or not this popup is showing.
	 * 
	 * @return <code>true</code> if the popup is showing
	 * @see #show()
	 * @see #hide()
	 */
	public boolean isShowing() 
	{
		return popupPanelWidget.isShowing();
	}

	/**
	 * Remove an autoHide partner.
	 * 
	 * @param partner the auto hide partner to remove
	 */
	public void removeAutoHidePartner(Element partner) 
	{
		popupPanelWidget.removeAutoHidePartner(partner);
	}

	public void setAnimationEnabled(boolean enable) 
	{
		popupPanelWidget.setAnimationEnabled(enable);
	}

	/**
	 * Enable or disable the autoHide feature. When enabled, the popup will be
	 * automatically hidden when the user clicks outside of it.
	 * 
	 * @param autoHide true to enable autoHide, false to disable
	 */
	public void setAutoHideEnabled(boolean autoHide) 
	{
		popupPanelWidget.setAutoHideEnabled(autoHide);
	}

	/**
	 * When the popup is modal, keyboard or mouse events that do not target the
	 * PopupPanel or its children will be ignored.
	 * 
	 * @param modal true to make the popup modal
	 */
	public void setModal(boolean modal) 
	{
		popupPanelWidget.setModal(modal);
	}

	/**
	 * Sets the popup's position relative to the browser's client area. The
	 * popup's position may be set before calling {@link #show()}.
	 * 
	 * @param left the left position, in pixels
	 * @param top the top position, in pixels
	 */
	public void setPopupPosition(int left, int top) 
	{
		popupPanelWidget.setPopupPosition(left, top);
	}

	/**
	 * Sets the popup's position using a {@link PositionCallback}, and shows the
	 * popup. The callback allows positioning to be performed based on the
	 * offsetWidth and offsetHeight of the popup, which are normally not available
	 * until the popup is showing. By positioning the popup before it is shown,
	 * the the popup will not jump from its original position to the new position.
	 * 
	 * @param callback the callback to set the position of the popup
	 * @see PositionCallback#setPosition(int offsetWidth, int offsetHeight)
	 */
	public void setPopupPositionAndShow(PositionCallback callback) 
	{
		popupPanelWidget.setPopupPositionAndShow(callback);
	}

	/**
	 * <p>
	 * When enabled, the popup will preview all native events, even if another
	 * popup was opened after this one.
	 * </p>
	 * <p>
	 * If autoHide is enabled, enabling this feature will cause the popup to
	 * autoHide even if another non-modal popup was shown after it. If this
	 * feature is disabled, the popup will only autoHide if it was the last popup
	 * opened.
	 * </p>
	 * 
	 * @param previewAllNativeEvents true to enable, false to disable
	 */
	public void setPreviewingAllNativeEvents(boolean previewAllNativeEvents) 
	{
		popupPanelWidget.setPreviewingAllNativeEvents(previewAllNativeEvents);
	}

	/**
	 * Shows the popup and attach it to the page. It must have a child widget
	 * before this method is called.
	 */
	public void show() 
	{
		popupPanelWidget.show();
	}

	/**
	 * Normally, the popup is positioned directly below the relative target, with
	 * its left edge aligned with the left edge of the target. Depending on the
	 * width and height of the popup and the distance from the target to the
	 * bottom and right edges of the window, the popup may be displayed directly
	 * above the target, and/or its right edge may be aligned with the right edge
	 * of the target.
	 * 
	 * @param target the target to show the popup below
	 */
	public final void showRelativeTo(final UIObject target) 
	{
		popupPanelWidget.showRelativeTo(getWrappedUIObject(target));
	}

	@Override
	protected void renderAttributes(Element element) 
	{
		super.renderAttributes(element);
		
		String animationEnabled = element.getAttribute("_animationEnabled");
		if (animationEnabled != null && animationEnabled.length() > 0)
		{
			setAnimationEnabled(Boolean.parseBoolean(animationEnabled));
		}
		String previewAllNativeEvents = element.getAttribute("_previewAllNativeEvents");
		if (previewAllNativeEvents != null && previewAllNativeEvents.length() > 0)
		{
			setPreviewingAllNativeEvents(Boolean.parseBoolean(previewAllNativeEvents));
		}
		
	}
	
	
	@Override
	protected void attachEvents(Element element) 
	{
		super.attachEvents(element);
		
		CloseEvtBind.bindEvent(element, popupPanelWidget, getId());
	}
}
