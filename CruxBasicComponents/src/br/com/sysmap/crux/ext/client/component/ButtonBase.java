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
package br.com.sysmap.crux.ext.client.component;


/**
 * Base Class for button components
 * @author Thiago Bustamante
 */
public abstract class ButtonBase extends FocusComponent 
{
	protected com.google.gwt.user.client.ui.ButtonBase buttonBase;
	
	protected ButtonBase(String id, com.google.gwt.user.client.ui.ButtonBase widget) 
	{
		super(id, widget);
		buttonBase = widget;
	}

	public String getHTML() 
	{
		return buttonBase.getHTML();
	}

	public String getText() 
	{
		return buttonBase.getText();
	}

	public void setHTML(String html) 
	{
		buttonBase.setHTML(html);
	}

	public void setText(String text) 
	{
		buttonBase.setText(text);
	}

}
