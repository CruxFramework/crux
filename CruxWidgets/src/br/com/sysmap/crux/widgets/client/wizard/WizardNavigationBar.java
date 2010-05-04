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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;


/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WizardNavigationBar extends AbstractWizardNavigationBar
{
	public static final String DEFAULT_LABEL_STYLE_NAME = "crux-WizardNavigationLabel";
	public static final String DEFAULT_SEPARATOR_HORIZONTAL_STYLE_NAME = "crux-WizardNavigationHorizontalSeparator";
	public static final String DEFAULT_SEPARATOR_VERTICAL_STYLE_NAME = "crux-WizardNavigationVerticalSeparator";
	public static final String DEFAULT_STYLE_NAME = "crux-WizardNavigationBar";
	
	
	private boolean allowSelectStep = true;
	private String horizontalSeparatorStyleName;
	private String labelStyleName;
	private final boolean showAllSteps;
	private String verticalSeparatorStyleName;
	
	/**
	 * @param vertical
	 */
	public WizardNavigationBar(boolean vertical, boolean showAllSteps)
    {
	    super(vertical, DEFAULT_STYLE_NAME);
	    setLabelStyleName(DEFAULT_LABEL_STYLE_NAME);
	    setHorizontalSeparatorStyleName(DEFAULT_SEPARATOR_HORIZONTAL_STYLE_NAME);
	    setVerticalSeparatorStyleName(DEFAULT_SEPARATOR_VERTICAL_STYLE_NAME);
	    this.showAllSteps = showAllSteps;
    }

	/**
	 * @return
	 */
	public String getHorizontalSeparatorStyleName()
    {
    	return horizontalSeparatorStyleName;
    }

	/**
	 * @return
	 */
	public String getLabelStyleName()
    {
    	return labelStyleName;
    }

	/**
	 * @return
	 */
	public String getVerticalSeparatorStyleName()
    {
    	return verticalSeparatorStyleName;
    }

	/**
	 * @return
	 */
	public boolean isAllowSelectStep()
    {
    	return allowSelectStep;
    }

	/**
	 * @param allowSelectStep
	 */
	public void setAllowSelectStep(boolean allowSelectStep)
    {
    	this.allowSelectStep = allowSelectStep;
    }

	/**
	 * @param horizontalSeparatorStyleName
	 */
	public void setHorizontalSeparatorStyleName(String horizontalSeparatorStyleName)
    {
    	this.horizontalSeparatorStyleName = horizontalSeparatorStyleName;
    }

	/**
	 * @param labelStyleName
	 */
	public void setLabelStyleName(String labelStyleName)
    {
    	this.labelStyleName = labelStyleName;
    }

	/**
	 * @param verticalSeparatorStyleName
	 */
	public void setVerticalSeparatorStyleName(String verticalSeparatorStyleName)
    {
    	this.verticalSeparatorStyleName = verticalSeparatorStyleName;
    }

	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.WizardStepListener#stepChanged(br.com.sysmap.crux.widgets.client.wizard.Step, br.com.sysmap.crux.widgets.client.wizard.Step)
	 */
	public void stepChanged(Step currentStep, Step previousStep)
    {
		updateNavigationBar(wizard.getStepOrder(currentStep.getId()));
    }
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.wizard.AbstractWizardNavigationBar#setWizard(br.com.sysmap.crux.widgets.client.wizard.Wizard)
	 */
	@Override
	protected void setWizard(Wizard wizard)
	{
	    super.setWizard(wizard);
	    updateNavigationBar(-1);
	}

	/**
	 * 
	 */
	private void updateNavigationBar(int currentStep)
    {
		rollingPanel.clear();
		int originalScrollPosition = (rollingPanel.isVertical()?rollingPanel.getVerticalScrollPosition():rollingPanel.getHorizontalScrollPosition());
		boolean needsSeparator = false;
		for (int i=0; i<wizard.getStepCount() && (showAllSteps || i<= currentStep); i++)
		{
			Step step = wizard.getStep(i);
			if (step.isEnabled())
			{
				if (needsSeparator)
				{
					Label separator = new Label();
					separator.setStyleName(isVertical()?verticalSeparatorStyleName:horizontalSeparatorStyleName);
					rollingPanel.add(separator);
				}
				needsSeparator = true;
				Label label = new Label(step.getLabel());
				label.setStyleName(labelStyleName);

				if (i == wizard.getCurrentStepIndex())
				{
					label.addStyleDependentName("selected");
				}
				else if (allowSelectStep)
				{
					final int stepIndex = i;
					label.addClickHandler(new ClickHandler()
					{
						public void onClick(ClickEvent event)
						{
							wizard.selectStep(stepIndex);
						}
					});
				}

				rollingPanel.add(label);
			}
		}
		updateScrollPosition(originalScrollPosition);
    }
}
