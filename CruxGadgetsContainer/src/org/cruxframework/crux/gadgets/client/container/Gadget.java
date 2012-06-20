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
package org.cruxframework.crux.gadgets.client.container;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Gadget extends JavaScriptObject
{
	protected Gadget() {}
	
	public final native int getId()/*-{
		return this.id;
	}-*/;

	public final native String getUrl()/*-{
		return this.specUrl;
	}-*/;

	public final native String getTitle()/*-{
		return this.title;
	}-*/;

	public final native JavaScriptObject getUserPrefs()/*-{
		return this.userPrefs;
	}-*/;
	
	public final native int getHeight()/*-{
		return this.height;
	}-*/;
	
	public final native int getWidth()/*-{
		return this.width;
	}-*/;
	
	public final native void handleOpenUserPrefsDialog()/*-{
		this.handleOpenUserPrefsDialog();
	}-*/;
	
	public final native void handleToggle()/*-{
		this.handleToggle();
	}-*/;

	public final native void refresh()/*-{
		this.refresh();
	}-*/;
	
	public final native void clearContent()/*-{
		var iframeId = this.getIframeId();
		$doc.getElementById(iframeId).src = 'about:blank'; 
	}-*/;

	public final void deactivate()
	{
		if (isProfileView())
		{
			setActive(false);
			clearContent();
			collapse();
			setControlButtonsVisible(false);
		}
	}
	
	public final void activate()
	{
		if (isProfileView())
		{
			handleToggle();
			setControlButtonsVisible(true);
			setActive(true);
			refresh();
		}
	}

	public final void setControlButtonsVisible(final boolean visible)
	{
		int gadgetId = getId();
		Element menuButton = DOM.getElementById("gadgets_gadget_menu_"+gadgetId);
		Element toogleButton = DOM.getElementById("gadgets_gadget_toogle_"+gadgetId);
		Element fullScreenButton = DOM.getElementById("gadgets_gadget_full_screen_"+gadgetId);

		menuButton.getStyle().setDisplay(visible?Display.BLOCK:Display.NONE);
		toogleButton.getStyle().setDisplay(visible?Display.BLOCK:Display.NONE);
		fullScreenButton.setClassName(visible?getCssFullScreenButton():getCssRestoreScreenButton());
	}

	public final native String getCssFullScreenButton()/*-{
		return this.cssClassTitleButtonFullScreen
	}-*/;

	public final native String getCssRestoreScreenButton()/*-{
		return this.cssClassTitleButtonRestoreScreen
	}-*/;
	
	public final native String getCssOptionsMenu()/*-{
		return this.cssClassTitleOptionsMenu
	}-*/;
	
	public final native void collapse()/*-{
		var gadgetIframe = $doc.getElementById(this.getIframeId());
		if (gadgetIframe) {
			var gadgetContent = gadgetIframe.parentNode;
			gadgetContent.style.display = 'none';
		} 	
	}-*/;
	
	private native void setActive(boolean active)/*-{
		this.isActive_ = active;
	}-*/;
	
	public final native boolean isProfileView()/*-{
		return this.isProfileView();
	}-*/;
	
	public final native boolean hasViewablePrefs()/*-{
		var ret = this.hasViewablePrefs_();
		return (ret?true:false); // avoid error converting 'undefined' to boolean
	}-*/;
	
	
	public final void remove()
	{
		GadgetContainer.get().removeGadget(this.getId());
	}
}
