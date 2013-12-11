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
package org.cruxframework.crux.gadget.client.features.impl;

import org.cruxframework.crux.gadget.client.features.Tabs;
import org.cruxframework.crux.gadget.client.features.TabsFeature;

import com.google.gwt.user.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TabsFeatureImpl implements TabsFeature
{
	private TabsFeatureImpl() {}

	public native Tabs getTabs()/*-{
	    return new $wnd.gadgets.TabSet();
    }-*/;

	public native Tabs getTabs(String moduleId)/*-{
	    return new $wnd.gadgets.TabSet(moduleId);
    }-*/;

	public native Tabs getTabs(String moduleId, String selectedTab)/*-{
	    return new $wnd.gadgets.TabSet(moduleId, selectedTab);
    }-*/;

	public native Tabs getTabs(String moduleId, String selectedTab, Element htmlContainer)/*-{
	    return new $wnd.gadgets.TabSet(moduleId, selectedTab, htmlContainer);
    }-*/;

}
