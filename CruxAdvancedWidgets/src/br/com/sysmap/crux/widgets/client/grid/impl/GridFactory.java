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
package br.com.sysmap.crux.widgets.client.grid.impl;

import java.util.List;

import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;
import br.com.sysmap.crux.widgets.client.event.row.RowEventsBind;
import br.com.sysmap.crux.widgets.client.grid.model.ColumnDefinition;
import br.com.sysmap.crux.widgets.client.grid.model.ColumnDefinitions;
import br.com.sysmap.crux.widgets.client.grid.model.RowSelectionModel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="grid", library="widgets")
public class GridFactory extends WidgetFactory<Grid>
{
	/**
	 * @param cellSpacing 
	 * @param autoLoad 
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public Grid instantiateWidget(Element gridElem, String widgetId) throws InterfaceConfigException
	{
		Grid grid = new Grid(getColumnDefinitions(gridElem), getPageSize(gridElem), 
				                               getRowSelectionModel(gridElem), getCellSpacing(gridElem), 
				                               getAutoLoad(gridElem), getStretchColumns(gridElem), getHighlightRowOnMouseOver(gridElem));
		return grid;
	}
	
	private boolean getHighlightRowOnMouseOver(Element gridElem)
	{
		String highlight = gridElem.getAttribute("_highlightRowOnMouseOver");
		
		if(highlight != null && highlight.trim().length() > 0)
		{
			return Boolean.parseBoolean(highlight);
		}
		
		return false;
	}

	private boolean getAutoLoad(Element gridElem)
	{
		String autoLoad = gridElem.getAttribute("_autoLoadData");
		
		if(autoLoad != null && autoLoad.trim().length() > 0)
		{
			return Boolean.parseBoolean(autoLoad);
		}
		
		return false;
	}
	
	private boolean getStretchColumns(Element gridElem)
	{
		String stretchColumns = gridElem.getAttribute("_stretchColumns");
		
		if(stretchColumns != null && stretchColumns.trim().length() > 0)
		{
			return Boolean.parseBoolean(stretchColumns);
		}
		
		return false;
	}

	private int getCellSpacing(Element gridElem)
	{
		String spacing = gridElem.getAttribute("_cellSpacing");
		
		if(spacing != null && spacing.trim().length() > 0)
		{
			return Integer.parseInt(spacing);
		}
		
		return 1;
	}

	/**
	 * @param grid
	 * @param gridElem
	 */
	private void bindDataSource(WidgetFactoryContext<Grid> context)
	{
		Element element = context.getElement();
		final Grid widget = context.getWidget();

		final String dataSourceName = element.getAttribute("_dataSource");
		
		if(dataSourceName != null && dataSourceName.length() > 0)
		{
			DeferredCommand.addCommand(new Command()
			{
				public void execute()
				{	
					EditablePagedDataSource dataSource = (EditablePagedDataSource) Screen.getDataSource(dataSourceName);
					widget.setDataSource(dataSource);
				}
			});
		}
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private RowSelectionModel getRowSelectionModel(Element gridElem)
	{
		String rowSelection = gridElem.getAttribute("_rowSelection");
		
		if(rowSelection != null && rowSelection.length() > 0)
		{
			if("unselectable".equals(rowSelection))
			{
				return RowSelectionModel.unselectable;
			}
			else if("single".equals(rowSelection))
			{
				return RowSelectionModel.single;
			}
			else if("multiple".equals(rowSelection))
			{
				return RowSelectionModel.multiple;
			}
			else if("singleWithRadioButton".equals(rowSelection))
			{
				return RowSelectionModel.singleWithRadioButton;
			}
			else if("multipleWithCheckBox".equals(rowSelection))
			{
				return RowSelectionModel.multipleWithCheckBox;
			}
		}
		
		return RowSelectionModel.unselectable;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private int getPageSize(Element gridElem)
	{
		String pageSize = gridElem.getAttribute("_pageSize");
		
		if(pageSize != null && pageSize.length() > 0)
		{
			return Integer.parseInt(pageSize);
		}
		
		return Integer.MAX_VALUE;
	}

	/**
	 * @param gridElem
	 * @return
	 * @throws InterfaceConfigException
	 */
	private ColumnDefinitions getColumnDefinitions(Element gridElem) throws InterfaceConfigException
	{
		ColumnDefinitions defs = new ColumnDefinitions();
		
		List<Element> colElems = ensureChildrenSpans(gridElem, false);
		if(colElems != null && colElems.size() > 0)
		{
			for (Element colElem : colElems)
			{
				String columnType = colElem.getAttribute("_columnType");
				String width = colElem.getAttribute("_width");
				String strVisible = colElem.getAttribute("_visible");
				String label = colElem.getAttribute("_label");
				String key = colElem.getAttribute("_key");
				String strFormatter = colElem.getAttribute("_formatter");
				String hAlign = colElem.getAttribute("_horizontalAlignment");
				String vAlign = colElem.getAttribute("_verticalAlignment");
				
				boolean visible = (strVisible != null && strVisible.length() > 0) ? Boolean.parseBoolean(strVisible) : true;
				String formatter = (strFormatter != null && strFormatter.length() > 0) ? strFormatter : null;
				label = (label != null && label.length() > 0) ? ScreenFactory.getInstance().getDeclaredMessage(label) : "";
				
				ColumnDefinition def = null;
				
				if("dataColumn".equals(columnType))
				{
					def = new DataColumnDefinition(
							label, 
							width, 
							formatter, 
							visible, 
							AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.ALIGN_CENTER),
							AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.ALIGN_MIDDLE));
				}
				else if("widgetColumn".equals(columnType))
				{
					def = new WidgetColumnDefinition(
							label, 
							width, 
							ensureFirstChildSpan(colElem, false),
							visible, 
							AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.ALIGN_CENTER),
							AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.ALIGN_MIDDLE));
				}
					
				defs.add(key, def);
			}
		}
		else
		{
			// TODO - msg de erro, não há colunas
			throw new InterfaceConfigException("");
		}
				
		return defs;
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="pageSize", type=Integer.class, defaultValue="8"),
		@TagAttributeDeclaration(value="rowSelection", type=RowSelectionModel.class, defaultValue="unselectable"),
		@TagAttributeDeclaration("dataSource"),
		@TagAttributeDeclaration(value="cellSpacing", type=Integer.class, defaultValue="1"),
		@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="stretchColumns", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="highlightRowOnMouseOver", type=Boolean.class, defaultValue="false")
	})
	public void processAttributes(WidgetFactoryContext<Grid> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		bindDataSource(context);
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onRowClick"),
		@TagEventDeclaration("onRowDoubleClick"),
		@TagEventDeclaration("onRowRender"),
		@TagEventDeclaration("onBeforeRowSelect")
	})
	public void processEvents(WidgetFactoryContext<Grid> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		Grid widget = context.getWidget();

		RowEventsBind.bindClickRowEvent(element, widget);
		RowEventsBind.bindDoubleClickRowEvent(element, widget);
		RowEventsBind.bindRenderRowEvent(element, widget);
		RowEventsBind.bindBeforeSelectRowEvent(element, widget);
		
		super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(value=ColumnProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetFactoryContext<Grid> context) throws InterfaceConfigException {}
	
	
	@TagChildAttributes(maxOccurs="unbounded")
	public static class ColumnProcessor extends ChoiceChildProcessor<Grid>
	{
		@Override
		@TagChildren({
			@TagChild(DataColumnProcessor.class),
			@TagChild(WidgetColumnProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException {}
	}

	
	@TagChildAttributes(tagName="dataColumn", minOccurs="0", maxOccurs="unbounded")
	public static class DataColumnProcessor extends WidgetChildProcessor<Grid>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration("key"),
			@TagAttributeDeclaration("formatter"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(tagName="widgetColumn", minOccurs="0", maxOccurs="unbounded")
	public static class WidgetColumnProcessor extends WidgetChildProcessor<Grid>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration("key"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<Grid> context) throws InterfaceConfigException {}
	}
	
	public static class WidgetProcessor extends AnyWidgetChildProcessor<Grid>{}
}