/*
 * Copyright 2014 cruxframework.org.
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

package org.cruxframework.crux.widgets.client.datepicker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.datepicker.client.MonthSelector;

/**
 * @author samuel.cardoso
 *
 */
public final class CruxMonthSelector extends MonthSelector 
{
	private PushButton prevMonth;
	private PushButton nextMonth;
	private PushButton prevYear;
	private PushButton nextYear;
	private Grid grid;

	/**
	 * Constructor.
	 */
	public CruxMonthSelector() {
	}

	/**
	 * Returns the button for moving to the previous month.
	 */
	public Element getBackwardButtonElement() {
		return prevMonth.getElement();
	}

	/**
	 * Returns the button for moving to the next month.
	 */
	public Element getForwardButtonElement() {
		return nextMonth.getElement();
	}

	@Override
	protected void refresh() {
		String formattedMonth = getModel().formatCurrentMonth();
		grid.setText(0, 2, formattedMonth);
	}

	@Override
	protected void setup() 
	{
		prevMonth = getPrevMonthButton();
		nextMonth = getNextMonthButton();
		prevYear = getPrevYearButton();
		nextYear = getNextYearButton();
		
		// Set up grid.
		grid = new Grid(1, 5);
		grid.setWidget(0, 0, prevYear);
		grid.setWidget(0, 1, prevMonth);
		grid.setWidget(0, 3, nextMonth);
		grid.setWidget(0, 4, nextYear);
				
		CellFormatter formatter = grid.getCellFormatter();
		//formatter.setStyleName(0, 1, css().month());
		formatter.setWidth(0, 0, "1");
		formatter.setWidth(0, 1, "1");
		formatter.setWidth(0, 2, "100%");
		formatter.setWidth(0, 3, "1");
		formatter.setWidth(0, 4, "1");
		//grid.setStyleName(css().monthSelector());
		initWidget(grid);
	}

	private PushButton getNextYearButton() 
	{
		PushButton button = new PushButton();
		button.getUpFace().setHTML("&#9656;");
		//forwards.setStyleName(css().nextButton());
		button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				increaseYear();
			}
		});
		return button;
	}
	
	private PushButton getPrevYearButton() 
	{
		PushButton button = new PushButton();
		button.getUpFace().setHTML("&#9666;");
		//forwards.setStyleName(css().nextButton());
		button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				decreaseYear();
			}
		});
		return button;
	}
	
	private PushButton getNextMonthButton() 
	{
		PushButton button = new PushButton();
		button.getUpFace().setHTML("&raquo;");
		//forwards.setStyleName(css().nextButton());
		button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				increaseMonth();
			}
		});
		return button;
	}

	private void increaseMonth() 
	{
		addMonths(+1);
	}
	
	private void decreaseMonth() 
	{
		addMonths(-1);
	}
	
	private void increaseYear() 
	{
		addMonths(+12);
	}
	
	private void decreaseYear() 
	{
		addMonths(-12);
	}
	
	private PushButton getPrevMonthButton() 
	{
		// Set up backwards.
		PushButton button = new PushButton();
		button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				decreaseMonth();
			}
		});

		button.getUpFace().setHTML("&laquo;");
		//backwards.setStyleName(css().previousButton());
		return button;
	}
}
