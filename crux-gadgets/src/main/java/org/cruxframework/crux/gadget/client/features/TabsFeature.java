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

import com.google.gwt.user.client.Element;

/**
 * Provides access to the tabs feature.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public interface TabsFeature
{
	/**
	 * Handler called when a tab is selected
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	interface Callback
	{
		void onTabSelected(String tabId);
	}
	
	/**
	 * Create a new TabSet
	 * @return
	 */
	Tabs getTabs();

	/**
	 * Create a new TabSet
	 * @param moduleId
	 * @return
	 */
	Tabs getTabs(String moduleId);

	/**
	 * Create a new TabSet
	 * @param moduleId
	 * @param selectedTab
	 * @return
	 */
	Tabs getTabs(String moduleId, String selectedTab);

	/**
	 * Create a new TabSet
	 * @param moduleId
	 * @param selectedTab
	 * @param htmlContainer
	 * @return
	 */
	Tabs getTabs(String moduleId, String selectedTab, Element htmlContainer);
}
