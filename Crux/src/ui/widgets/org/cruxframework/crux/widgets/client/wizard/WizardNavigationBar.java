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
package org.cruxframework.crux.widgets.client.wizard;

import java.io.Serializable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardNavigationBar<T extends Serializable> extends AbstractWizardNavigationBar<T>
{
	public static final String DEFAULT_LABEL_STYLE_NAME = "crux-WizardNavigationLabel";
	public static final String DEFAULT_SEPARATOR_STYLE_NAME = "crux-WizardNavigationSeparator";
	public static final String DEFAULT_STYLE_NAME = "crux-WizardNavigationBar";
	
	
	private boolean allowSelectStep = true;
	private String horizontalSeparatorStyleName;
	private String labelStyleName;
	private final boolean showAllSteps;
	
	/**
	 * @param vertical
	 */
	public WizardNavigationBar(boolean showAllSteps)
    {
	    super(DEFAULT_STYLE_NAME);
	    setLabelStyleName(DEFAULT_LABEL_STYLE_NAME);
	    setSeparatorStyleName(DEFAULT_SEPARATOR_STYLE_NAME);
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
	public void setSeparatorStyleName(String horizontalSeparatorStyleName)
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
	 * @see org.cruxframework.crux.widgets.client.wizard.WizardStepListener#stepChanged(org.cruxframework.crux.widgets.client.wizard.Step, org.cruxframework.crux.widgets.client.wizard.Step)
	 */
	public void stepChanged(Step<T> currentStep, Step<T> previousStep)
    {
		updateNavigationBar(wizard.getStepOrder(currentStep.getId()));
    }
	
	/**
	 * @see org.cruxframework.crux.widgets.client.wizard.AbstractWizardNavigationBar#setWizard(org.cruxframework.crux.widgets.client.wizard.Wizard)
	 */
	@Override
	protected void setWizard(Wizard<T> wizard)
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
		int originalScrollPosition = (rollingPanel.getScrollPosition());
		boolean needsSeparator = false;
		for (int i=0; i<wizard.getStepCount() && (showAllSteps || i<= currentStep); i++)
		{
			Step<T> step = wizard.getStep(i);
			if (step.isEnabled())
			{
				if (needsSeparator)
				{
					Label separator = new Label();
					separator.setStyleName(horizontalSeparatorStyleName);
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
