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
package org.cruxframework.crux.gadget.client.widget;

import org.cruxframework.crux.gadget.client.Gadget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * This widget is used to limit the area, inside a .crux.xml page, that will
 * be added to a specific gadget view
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetView extends HTMLPanel
{
	private String view;
	
	private static Gadget gadget = null;
	
	/**
	 * @return
	 */
	public static Gadget getGadget()
	{
		if (gadget == null)
		{
			gadget = GWT.create(Gadget.class);
		}
		return gadget;
	}
	
	/**
	 * @param html
	 */
	public GadgetView(String html)
	{
		super(html);
		if (gadget == null)
		{
			gadget = GWT.create(Gadget.class);
		}
	}

	/**
	 * @param tag
	 * @param html
	 */
	public GadgetView(String tag, String html)
	{
		super(tag, html);
	}

	/**
	 * @return
	 */
	public String getView()
	{
		return view;
	}

	/**
	 * @param view
	 */
	public void setView(String view)
	{
		this.view = view;
	}
}
