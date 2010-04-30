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

import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractWizardNavigationBar extends Composite implements WizardStepListener
{
	private boolean vertical;
	
	protected CellPanel cellPanel;
	protected Wizard wizard;

	/**
	 * @param vertical
	 */
	public AbstractWizardNavigationBar(boolean vertical, String styleName)
	{
		this.vertical = vertical;
		
		if (vertical)
		{
			this.cellPanel = new VerticalPanel();
		}
		else
		{
			this.cellPanel = new HorizontalPanel();
		}
		this.cellPanel.setStyleName(styleName);
		
		initWidget(cellPanel);
		setSpacing(5);
    }
	
	/**
	 * @return
	 */
	public boolean isVertical()
	{
		return vertical;
	}
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		cellPanel.setSpacing(spacing);
	}
	
	/**
	 * @return
	 */
	public int getSpacing()
	{
		return cellPanel.getSpacing();
	}	
	
	/**
	 * @return
	 */
	public Wizard getWizard()
    {
    	return wizard;
    }
	
	/**
	 * @param wizard
	 */
	protected void setWizard(Wizard wizard)
    {
    	this.wizard = wizard;
    }	
	
	/**
	 * 
	 */
	protected void checkWizard()
	{
		if (this.wizard == null)
		{
			throw new NullPointerException(WidgetMsgFactory.getMessages().wizardControlBarOrphan());
		}
	}	
}
