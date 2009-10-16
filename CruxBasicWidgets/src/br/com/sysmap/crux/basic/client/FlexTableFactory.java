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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Factory for FlexTable widget
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="flexTable", library="bas")
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
			super.processChildren(context);
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class GridCellProcessor extends TableCellProcessor<FlexTable>
	{
		@Override
		@TagChildren({
			@TagChild(GridChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException
		{
			super.processChildren(context);
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class GridChildrenProcessor extends CellChildrenProcessor<FlexTable> 
	{
		protected BasicMessages messages = GWT.create(BasicMessages.class);

		@Override
		@TagChildren({
			@TagChild(FlexCellTextProcessor.class),
			@TagChild(FlexCellHTMLProcessor.class),
			@TagChild(FlexCellWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<FlexTable> context) throws InterfaceConfigException	
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");

			if (indexRow < 0 || indexCol < 0)
			{
				throw new IndexOutOfBoundsException(messages.flexTableInvalidRowColIndexes(context.getRootWidgetId()));
			}
			int r = context.getRootWidget().getRowCount();
			while (context.getRootWidget().getRowCount() < indexRow+1)
			{
				context.getRootWidget().insertRow(r++);
			}
			
			while (context.getRootWidget().getCellCount(indexRow) < indexCol+1)
			{
				context.getRootWidget().addCell(indexRow);
			}
		}
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
