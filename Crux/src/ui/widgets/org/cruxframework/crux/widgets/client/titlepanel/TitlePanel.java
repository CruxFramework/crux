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
package org.cruxframework.crux.widgets.client.titlepanel;

import org.cruxframework.crux.widgets.client.decoratedpanel.DecoratedPanel;

import com.google.gwt.user.client.ui.Widget;

/**
 * A decorated panel, with a title bar.
 * @author Gesse S. F. Dafe
 */
public class TitlePanel extends DecoratedPanel
{
	public static final String DEFAULT_STYLE_NAME = "crux-TitlePanel" ;
	
	/**
	 * 
	 */
	public TitlePanel()
	{
		this(null, null, null);
	}
	
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 */
	public TitlePanel(String width, String height, String styleName)
	{
		super(width, height, styleName != null && styleName.length() > 0 ? styleName : DEFAULT_STYLE_NAME);
	}
	
	/**
	 * @param text
	 */
	public void setTitleText(String text)
	{
		getTopCenterCell().setInnerText(text);
	}
	
	/**
	 * @param text
	 */
	public void setTitleHtml(String html)
	{
		getTopCenterCell().setInnerHTML(html);
	}
	
	/**
	 * @param text
	 */
	public void setTitleWidget(Widget widget)
	{
		getTopCenterCell().setInnerHTML("");
		add(widget, getTopCenterCell());
	}
}