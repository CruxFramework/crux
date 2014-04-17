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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

/**
 * Flap class for gadgets. You create tabs using the Tabs addTab() method. 
 * To get Flap objects, use the Tabs getSelectedTab() or getTabs() methods.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Tab extends JavaScriptObject
{
	protected Tab(){}
	
	/**
	 * Returns the HTML element where the tab content is rendered.
	 * @return
	 */
	public final native Element getContentContainer()/*-{
		return this.getContentContainer();
	}-*/;
	
	/**
	 * Returns the tab's index.
	 * @return
	 */
	public final native int getIndex()/*-{
		return this.getIndex();
	}-*/;

	/**
	 * Returns the label of the tab as a string (may contain HTML).
	 * @return
	 */
	public final native String getName()/*-{
		return this.getName();
	}-*/;

	/**
	 * Returns the HTML element that contains the tab's label.
	 * @return
	 */
	public final native Element getNameContainer()/*-{
		return this.getNameContainer();
	}-*/;

}
