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
package br.com.sysmap.crux.widgets.client.wizard;

import java.util.List;

import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class Step
{
	private Wizard wizard;
	private String id;
	private Widget widget;
	
	/**
	 * @param id
	 * @param widget
	 */
	Step(Wizard wizard, String id, Widget widget)
    {
		this.id = id;
		this.widget = widget;
    }
	
	/**
	 * @return
	 */
	public String getId()
    {
    	return id;
    }
	
	/**
	 * @return
	 */
	public Wizard getWizard()
    {
    	return wizard;
    }

	/**
	 * @param id
	 */
	void setId(String id)
    {
    	this.id = id;
    }
	
	/**
	 * @return
	 */
	Widget getWidget()
    {
    	return widget;
    }
	
	/**
	 * @param widget
	 */
	void setWidget(Widget widget)
    {
    	this.widget = widget;
    }
	
	/**
	 * @return
	 */
	List<WizardCommand> getCommands()
	{
		if (widget instanceof HasCommands)
		{
			return ((HasCommands)widget).getCommands();
		}
		return null;
	}
}
