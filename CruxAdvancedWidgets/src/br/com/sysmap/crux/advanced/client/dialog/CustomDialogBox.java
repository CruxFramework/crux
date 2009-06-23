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
package br.com.sysmap.crux.advanced.client.dialog;

import br.com.sysmap.crux.core.client.component.CruxWidgetPanel;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CustomDialogBox extends DialogBox
{
	private CruxWidgetPanel topRightPanel;
	private Widget topRightWidget;
	
	/**
	 * 
	 */
	public CustomDialogBox()
	{
		super();
	}

	/**
	 * @param autoHide
	 */
	public CustomDialogBox(boolean autoHide)
	{
		super(autoHide);
	}

	/**
	 * @param autoHide
	 * @param modal
	 */
	public CustomDialogBox(boolean autoHide, boolean modal)
	{
		super(autoHide, modal);
	}
	
	/**
	 * @param widget
	 */
	public void setTopRightWidget(Widget widget) 
	{
		if(topRightPanel == null)
		{
			topRightPanel = new CruxWidgetPanel(getCellElement(0, 2));
		}
		
		if(topRightWidget != null)
		{
			remove(topRightWidget);
		}
		
		topRightWidget = widget;

		topRightPanel.add(widget);
	}

}
