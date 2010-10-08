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
package br.com.sysmap.crux.gadget.client.widget;

import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.gadget.client.GadgetException;
import br.com.sysmap.crux.gadget.client.GadgetMsgFactory;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is used to limit the area, inside a .crux.xml page, that will
 * be added to a specific gadget view
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetView extends HTMLPanel
{
	public static enum View{canvas, home, noViews, profile}

	public static final String GADGET_VIEW_ID = "__Crux_Gadget_View";
	
	private View view = View.noViews;
	
	/**
	 * @param html
	 */
	public GadgetView(String html)
	{
		super(html);
		checkUniquess();
	}

	/**
	 * @param tag
	 * @param html
	 */
	public GadgetView(String tag, String html)
	{
		super(tag, html);
		checkUniquess();
	}

	/**
	 * @return
	 */
	public View getView()
	{
		return view;
	}

	/**
	 * @param view
	 */
	public void setView(View view)
	{
		this.view = view;
	}

	/**
	 * 
	 */
	private void checkUniquess()
	{
		Widget unique = Screen.get(GADGET_VIEW_ID);
		if (unique != null)
		{
			throw new GadgetException(GadgetMsgFactory.getMessages().gadgetViewDuplicatedWidget());
		}
		Screen.add(GADGET_VIEW_ID, this);
	}
}
