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

import java.io.Serializable;
import java.util.Iterator;

import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar.WizardCommand;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class Step<T extends Serializable>
{
	private boolean enabled = true;
	private String id;
	private String label;
	private Widget widget;
	private Wizard<T> wizard;
	
	/**
	 * @param id
	 * @param widget
	 */
	Step(Wizard<T> wizard, String id, String label, Widget widget)
    {
		this.wizard = wizard;
		this.id = id;
		this.label = label;
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
	public String getLabel()
	{
		return label;
	}
	
	/**
	 * @return
	 */
	public Wizard<T> getWizard()
    {
    	return wizard;
    }

	/**
	 * @return
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled)
    {
		this.enabled = enabled;
    }
	
	/**
	 * @return
	 */
	Widget getWidget()
    {
    	return widget;
    }
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
    Iterator<WizardCommand<T>> iterateCommands()
	{
		if (widget instanceof HasCommands)
		{
			return ((HasCommands<T>)widget).iterateCommands();
		}
		else if (widget instanceof PageStep)
		{
			return ((PageStep<T>) widget).iterateWizardCommands(wizard.getElement().getId());
		}
		return null;
	}

	/**
	 * @param id
	 */
	void setId(String id)
    {
    	this.id = id;
    }
	
	/**
	 * @param widget
	 */
	void setWidget(Widget widget)
    {
    	this.widget = widget;
    }
}
