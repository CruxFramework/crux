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

import java.util.List;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Grid;

/**
 * Factory for Grid widget
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="grid", library="gwt")
public class GridFactory extends HTMLTableFactory<Grid>
{

	@Override
	public Grid instantiateWidget(Element element, String widgetId)
	{
		return new Grid();
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
	public void processChildren(WidgetFactoryContext<Grid> context) throws InterfaceConfigException	
	{
		List<Element> childrenSpans = ensureChildrenSpans(context.getElement(), true);
		context.getWidget().resizeRows(childrenSpans.size());
	}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class GridRowProcessor extends TableRowProcessor<Grid>
	{
		@Override
		@TagChildren({
			@TagChild(GridCellProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException
		{
			Boolean cellsInitialized = (Boolean) context.getAttribute("cellsInitialized");
			if (cellsInitialized == null || !cellsInitialized)
			{
				List<Element> childrenSpans = ensureChildrenSpans(context.getChildElement(), true);
				context.getRootWidget().resizeColumns(childrenSpans.size());
				context.setAttribute("cellsInitialized", new Boolean(true));
			}
			
			super.processChildren(context);
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static class GridCellProcessor extends TableCellProcessor<Grid>
	{
		@Override
		@TagChildren({
			@TagChild(GridChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException
		{
			
			super.processChildren(context);
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class GridChildrenProcessor extends ChoiceChildProcessor<Grid> 
	{
		protected BasicMessages messages = GWT.create(BasicMessages.class);
		
		@Override
		@TagChildren({
			@TagChild(GridCellTextProcessor.class),
			@TagChild(GridCellHTMLProcessor.class),
			@TagChild(GridCellWidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException {}
	}
	
	public static class GridCellTextProcessor extends CellTextProcessor<Grid>{}
	public static class GridCellHTMLProcessor extends CellHTMLProcessor<Grid>{}
	public static class GridCellWidgetProcessor extends CellWidgetProcessor<Grid>
	{
		@Override
		@TagChildren({
			@TagChild(GridWidgetProcessor.class)
		})	
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException {}
		
	}
	public static class GridWidgetProcessor extends WidgetProcessor<Grid>{} 
	
}
