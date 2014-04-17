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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.widgets.client.WidgetMsgFactory;
import org.cruxframework.crux.widgets.client.rollingpanel.CustomRollingPanel;

import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Legacy
@Deprecated
public abstract class AbstractWizardNavigationBar<T extends Serializable> extends Composite implements WizardStepListener<T>
{
	protected Wizard<T> wizard;
	protected CustomRollingPanel rollingPanel;

	/**
	 * @param vertical
	 */
	public AbstractWizardNavigationBar(String styleName)
	{
		this.rollingPanel = new CustomRollingPanel();
		this.rollingPanel.setStyleName(styleName);
		initWidget(this.rollingPanel);
		this.getElement().getStyle().setTableLayout(TableLayout.AUTO);
    }
	
	/**
	 * @return
	 */
	public String getNextButtonStyleName()
    {
    	return this.rollingPanel.getNextButtonStyleName();
    }

	/**
	 * @return
	 */
	public String getPreviousButtonStyleName()
    {
    	return this.rollingPanel.getPreviousButtonStyleName();
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
	public Wizard<T> getWizard()
    {
    	return wizard;
    }

	/**
	 * @param nextButtonStyleName
	 */
	public void setNextButtonStyleName(String nextButtonStyleName)
    {
		this.rollingPanel.setNextButtonStyleName(nextButtonStyleName);
    }

	/**
	 * @param previousButtonStyleName
	 */
	public void setPreviousButtonStyleName(String previousButtonStyleName)
    {
		this.rollingPanel.setPreviousButtonStyleName(previousButtonStyleName);
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		this.rollingPanel.setSpacing(spacing);
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
	protected void setWizard(Wizard<T> wizard)
    {
    	this.wizard = wizard;
    }
	
	/**
	 * @param originalScrollPosition
	 */
	protected void updateScrollPosition(int originalScrollPosition)
    {
		rollingPanel.setScrollPosition(originalScrollPosition);
    }
}
