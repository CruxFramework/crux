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
import br.com.sysmap.crux.widgets.client.rollingpanel.RollingPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class AbstractWizardNavigationBar extends Composite implements WizardStepListener
{
	protected Wizard wizard;
	protected RollingPanel rollingPanel;

	/**
	 * @param vertical
	 */
	public AbstractWizardNavigationBar(boolean vertical, String styleName)
	{
		this.rollingPanel = new RollingPanel(vertical);
		this.rollingPanel.setStyleName(styleName);
		initWidget(this.rollingPanel);
    }
	
	/**
	 * @return
	 */
	public String getHorizontalNextButtonStyleName()
    {
    	return this.rollingPanel.getHorizontalNextButtonStyleName();
    }

	/**
	 * @return
	 */
	public String getHorizontalPreviousButtonStyleName()
    {
    	return this.rollingPanel.getHorizontalPreviousButtonStyleName();
    }

	/**
	 * @return
	 */
	public int getSpacing()
	{
		return this.rollingPanel.getSpacing();
	}

	/**
	 * @return
	 */
	public String getVerticalNextButtonStyleName()
    {
    	return this.rollingPanel.getVerticalNextButtonStyleName();
    }

	/**
	 * @return
	 */
	public String getVerticalPreviousButtonStyleName()
    {
    	return this.rollingPanel.getVerticalPreviousButtonStyleName();
    }

	/**
	 * @return
	 */
	public Wizard getWizard()
    {
    	return wizard;
    }

	/**
	 * @return
	 */
	public boolean isVertical()
	{
		return this.rollingPanel.isVertical();
	}

	/**
	 * @param horizontalNextButtonStyleName
	 */
	public void setHorizontalNextButtonStyleName(String horizontalNextButtonStyleName)
    {
		this.rollingPanel.setHorizontalNextButtonStyleName(horizontalNextButtonStyleName);
    }

	/**
	 * @param horizontalPreviousButtonStyleName
	 */
	public void setHorizontalPreviousButtonStyleName(String horizontalPreviousButtonStyleName)
    {
		this.rollingPanel.setHorizontalPreviousButtonStyleName(horizontalPreviousButtonStyleName);
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		this.rollingPanel.setSpacing(spacing);
	}

	/**
	 * @param verticalNextButtonStyleName
	 */
	public void setVerticalNextButtonStyleName(String verticalNextButtonStyleName)
    {
		this.rollingPanel.setVerticalNextButtonStyleName(verticalNextButtonStyleName);
    }
	
	/**
	 * @param verticalPreviousButtonStyleName
	 */
	public void setVerticalPreviousButtonStyleName(String verticalPreviousButtonStyleName)
    {
		this.rollingPanel.setVerticalPreviousButtonStyleName(verticalPreviousButtonStyleName);
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

	/**
	 * @param align
	 */
	protected void setCellHorizontalAlignment(HorizontalAlignmentConstant align)
    {
		this.rollingPanel.setHorizontalAlignment(align);
    }

	/**
	 * @param verticalAlign
	 */
	protected void setCellVerticalAlignment(VerticalAlignmentConstant verticalAlign)
    {
		this.rollingPanel.setVerticalAlignment(verticalAlign);
    }
	
	/**
	 * @param wizard
	 */
	protected void setWizard(Wizard wizard)
    {
    	this.wizard = wizard;
    }
	
	/**
	 * @param originalScrollPosition
	 */
	protected void updateScrollPosition(int originalScrollPosition)
    {
	    if (rollingPanel.isVertical())
		{
			rollingPanel.setVerticalScrollPosition(originalScrollPosition);
		}
		else
		{
			rollingPanel.setHorizontalScrollPosition(originalScrollPosition);
		}
    }
}
