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
package org.cruxframework.crux.widgets.client.formdisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Gesse S. F. Dafe
 */
//TODO rever estrutura de componentes. Evitar tables para isso
public class FormDisplay extends Composite
{
	private static final String DEFAULT_STYLE_NAME = "crux-FormDisplay";
	
	private FlexTable panel = new FlexTable();
	
	public FormDisplay()
	{
		initWidget(panel);
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	public void addEntry(String label, IsWidget widget, HorizontalAlignmentConstant align)
	{
		FormEntry entry = GWT.create(FormEntry.class);
		entry.setLabel(label);
		entry.setWidget(widget);
		entry.setHorizontalAlignment(align);
		entry.attachTo(panel);
	}
	
	/**
	 * A single and optionally labeled data entry in a form.
	 * @author Gesse Dafe
	 */
	public static class FormEntry
	{
		protected IsWidget widget;
		protected String label;
		protected HorizontalAlignmentConstant align = HasHorizontalAlignment.ALIGN_LEFT;
		
		/**
		 * @param panel
		 */
		protected void attachTo(FlexTable panel)
		{
			FlexCellFormatter cellFormatter = (FlexCellFormatter) panel.getCellFormatter();
			int numRows = panel.getRowCount();
			int widgetColumnIndex = 0;
			
			if(label != null)
			{
				panel.setWidget(numRows, 0, new Label(label));
				cellFormatter.setStyleName(numRows, 0, "formEntryLabel");
				cellFormatter.getElement(numRows, 0).getStyle().setProperty("display", "table-cell");
				cellFormatter.getElement(numRows, widgetColumnIndex).getStyle().setProperty("textAlign", align.getTextAlignString());
				widgetColumnIndex = 1;
			}
			
			panel.setWidget(numRows, widgetColumnIndex, widget);
			cellFormatter.setStyleName(numRows, widgetColumnIndex, "formEntryData");
			cellFormatter.getElement(numRows, widgetColumnIndex).getStyle().setProperty("display", "table-cell");
			cellFormatter.getElement(numRows, widgetColumnIndex).getStyle().setProperty("textAlign", align.getTextAlignString());
			
			if(label == null)
			{
				cellFormatter.setColSpan(numRows, widgetColumnIndex, 2);
			}
		}
				
		public void setHorizontalAlignment(HorizontalAlignmentConstant align)
		{
			this.align = align;
		}

		public void setWidget(IsWidget widget)
		{
			this.widget = widget;
		}

		public void setLabel(String label)
		{
			this.label = label;
		}
	}
	
	/**
	 * A form entry for small display devices
	 * @author Gesse Dafe
	 */
	public static class FormEntrySmall extends FormEntry
	{
		@Override
		protected void attachTo(FlexTable panel)
		{
			FlexCellFormatter cellFormatter = (FlexCellFormatter) panel.getCellFormatter();

			int numRows = panel.getRowCount();
			
			if(label != null)
			{
				panel.setWidget(numRows, 0, new Label(label));
				cellFormatter.setStyleName(numRows, 0, "formEntryLabel");
				cellFormatter.getElement(numRows, 0).getStyle().setProperty("display", "table-cell");
				numRows++;
			}
			
			panel.setWidget(numRows, 0, widget);
			cellFormatter.setStyleName(numRows, 0, "formEntryData");
			cellFormatter.getElement(numRows, 0).getStyle().setProperty("display", "table-cell");
			cellFormatter.getElement(numRows, 0).getStyle().setProperty("textAlign", align.getTextAlignString());
		}
	}
}