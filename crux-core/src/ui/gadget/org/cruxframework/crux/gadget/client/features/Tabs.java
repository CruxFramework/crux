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
package org.cruxframework.crux.gadget.client.features;

import org.cruxframework.crux.gadget.client.features.TabsFeature.Callback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Element;

/**
 * Represents a TabSet open social API
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Tabs extends JavaScriptObject
{
	protected Tabs(){}
	
	/**
	 * Add a new tab into the tabSet
	 * @param tabName
	 * @return
	 */
	public final String addTab(String tabName)
	{
		return addTab(tabName, -1, null, null, null);
	}

	/**
	 * Add a new tab into the tabSet
	 * 
	 * @param tabName
	 * @param index
	 * @return
	 */
	public final String addTab(String tabName, int index)
	{
		return addTab(tabName, index, null, null, null);
	}

	/**
	 * Add a new tab into the tabSet
	 * 
	 * @param tabName
	 * @param index
	 * @param tooltip
	 * @return
	 */
	public final String addTab(String tabName, int index, String tooltip)
	{
		return addTab(tabName, index, tooltip, null, null);
	}

	/**
	 * Add a new tab into the tabSet
	 * 
	 * @param tabName
	 * @param index
	 * @param tooltip
	 * @param callback
	 * @return
	 */
	public final String addTab(String tabName, int index, String tooltip, Callback callback)
	{
		return addTab(tabName, index, tooltip, callback, null);
	}

	/**
	 * Add a new tab into the tabSet
	 * 
	 * @param tabName
	 * @param index
	 * @param tooltip
	 * @param callback
	 * @param contentContainer
	 * @return
	 */
	public final native String addTab(String tabName, int index, String tooltip, Callback callback, Element contentContainer)/*-{
		var params = {};
		if (index >= 0){
			params.index = index;
		}
		if (contentContainer != null){
			params.contentContainer = contentContainer;
		}
		if (tooltip != null){
			params.tooltip = tooltip;
		}
		if (callback != null){
			params.callback = function(tabId){
				callback.@org.cruxframework.crux.gadget.client.features.TabsFeature.Callback::onTabSelected(Ljava/lang/String;)(tabId);
			};
		}
		
		return this.addTab(tabName, params);
	}-*/;
	
	/**
	 * Sets the alignment of tabs
	 * 
	 * @param align
	 */
	public final void alignTabs(Align align)
	{
		alignTabs(align, "3px");
	}

	/**
	 * Sets the alignment of tabs
	 * 
	 * @param align
	 * @param offset
	 */
	public final void alignTabs(Align align, String offset)
	{
		alignTabs(align.toString(), offset);
	}

	/**
	 * Sets the alignment of tabs
	 * 
	 * @param align
	 * @param offset
	 */
	public final native void alignTabs(String align, String offset)/*-{
		this.alignTabs(align,offset);
	}-*/;

	/**
	 * Hide or show the tabs
	 * @param display
	 */
	public final native void displayTabs(boolean display)/*-{
		this.displayTabs(display);
	}-*/;
	
	/**
	 * Returns the tab headers container element.
	 * @return
	 */
	public final native Element getHeaderContainer()/*-{
		return this.getHeaderContainer();
	}-*/;
	
	/**
	 * Returns the currently selected tab object.
	 * @return
	 */
	public final native Tab getSelectedTab()/*-{
		return this.getSelectedTab();
	}-*/;
	
	/**
	 * Returns an array of all existing tab objects.
	 * @return
	 */
	public final native JsArray<Tab> getTabs()/*-{
		return this.getTabs();
	}-*/;
	
	/**
	 * Removes a tab at tabIndex and all of its associated content.
	 * @param index
	 */
	public final native void removeTab(int index)/*-{
		this.removeTab(index);
	}-*/;
	
	/**
	 * Selects the tab at tabIndex and fires the tab's callback function if it exists. 
	 * If the tab is already selected, the callback is not fired.
	 * @param index
	 */
	public final native void setSelectedTab(int index)/*-{
		this.setSelectedTab(index);
	}-*/;
	
	/**
	 * Swaps the positions of tabs at tabIndex1 and tabIndex2. 
	 * The selected tab does not change, and no callback functions are called.
	 * @param index1
	 * @param index2
	 */
	public final native void swapTabs(int index1, int index2)/*-{
		this.swapTabs(index);
	}-*/;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum Align{left, center, right};
}
