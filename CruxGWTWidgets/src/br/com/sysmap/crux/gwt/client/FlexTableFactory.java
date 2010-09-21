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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Factory for FlexTable widget
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="flexTable", library="gwt")
public class FlexTableFactory extends HTMLTableFactory<FlexTable>
{

	@Override
	public FlexTable instantiateWidget(Element element, String widgetId)
	{
		return new FlexTable();
	}
	
	/**
	 * Populate the panel with declared items
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	@Override
	@TagChildren({
		@TagChild(GridRowProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<FlexTable> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class GridRowProcessor extends TableRowProcessor<FlexTable>
	{
		@Override
		@TagChildren({
			@TagChild(GridCellProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException
		{
			int r = context.getRootWidget().getRowCount();
			context.getRootWidget().insertRow(r);
			super.processChildren(context);
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static class GridCellProcessor extends TableCellProcessor<FlexTable>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="colSpan", type=Integer.class),
			@TagAttributeDeclaration(value="rowSpan", type=Integer.class)
		})
		@TagChildren({
			@TagChild(GridChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			context.getRootWidget().addCell(indexRow);
			
			super.processChildren(context);

			String colspan = context.readChildProperty("colSpan");
			if(colspan != null && colspan.length() > 0)
			{
				Integer indexCol = (Integer) context.getAttribute("colIndex");
				context.getRootWidget().getFlexCellFormatter().setColSpan(indexRow, indexCol, Integer.parseInt(colspan));
			}
			
			String rowSpan = context.readChildProperty("rowSpan");
			if(rowSpan != null && rowSpan.length() > 0)
			{
				Integer indexCol = (Integer) context.getAttribute("colIndex");
				context.getRootWidget().getFlexCellFormatter().setRowSpan(indexRow, indexCol, Integer.parseInt(rowSpan));
			}
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class GridChildrenProcessor extends ChoiceChildProcessor<FlexTable> 
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);

		@Override
		@TagChildren({
			@TagChild(FlexCellTextProcessor.class),
			@TagChild(FlexCellHTMLProcessor.class),
			@TagChild(FlexCellWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException	{}
	}
	
	public static class FlexCellTextProcessor extends CellTextProcessor<FlexTable>{}
	public static class FlexCellHTMLProcessor extends CellHTMLProcessor<FlexTable>{}
	public static class FlexCellWidgetProcessor extends CellWidgetProcessor<FlexTable>
	{
		@Override
		@TagChildren({
			@TagChild(FlexWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException {}
		
	}
	public static class FlexWidgetProcessor extends WidgetProcessor<FlexTable>{} 
		
}
