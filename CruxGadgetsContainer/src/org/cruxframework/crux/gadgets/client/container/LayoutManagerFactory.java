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

import org.cruxframework.crux.gadgets.client.GadgetContainerMsg;
import org.cruxframework.crux.gadgets.client.layout.GridLayoutManager;
import org.cruxframework.crux.gadgets.client.layout.LayoutManager;
import org.cruxframework.crux.gadgets.client.layout.LayoutManagerException;
import org.cruxframework.crux.gadgets.client.layout.TabLayoutManager;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class LayoutManagerFactory
{
	private static LayoutManager layoutManager = null;
	
	/**
	 * 
	 * @return
	 */
	static LayoutManager getLayoutManager()
	{
		if (layoutManager == null)
		{
			String layoutManagerDesc = getLayoutManagerDescriptor();
			if (layoutManagerDesc.equals("gridLayoutManager"))
			{
				layoutManager = new GridLayoutManager();
			}
			else if (layoutManagerDesc.equals("tabLayoutManager"))
			{
				layoutManager = new TabLayoutManager();
			}
			else
			{
				GadgetContainerMsg messages = GWT.create(GadgetContainerMsg.class);
				throw new LayoutManagerException(messages.layoutManagerNotFound());
			}
		}
		return layoutManager;
	}
	
	/**
	 * 
	 * @return
	 */
	private static native String getLayoutManagerDescriptor()/*-{
		return ($wnd._layoutManager?$wnd._layoutManager:'');
	}-*/;
}
