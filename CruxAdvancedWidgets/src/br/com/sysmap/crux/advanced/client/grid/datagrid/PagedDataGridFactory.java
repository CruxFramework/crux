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
package br.com.sysmap.crux.advanced.client.grid.datagrid;

import java.util.List;

import br.com.sysmap.crux.advanced.client.event.row.RowEventsBind;
import br.com.sysmap.crux.advanced.client.grid.model.RowSelectionModel;
import br.com.sysmap.crux.advanced.client.util.AlignmentUtil;
import br.com.sysmap.crux.core.client.datasource.EditablePagedDataSource;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="pagedDataGrid", library="adv")
public class PagedDataGridFactory extends WidgetFactory<PagedDataGrid>
{
	/**
	 * @param cellSpacing 
	 * @param autoLoad 
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public PagedDataGrid instantiateWidget(Element gridElem, String widgetId) throws InterfaceConfigException
	{
		PagedDataGrid grid = new PagedDataGrid(getColumnDefinitions(gridElem), getPageSize(gridElem), 
				                               getRowSelectionModel(gridElem), getCellSpacing(gridElem), 
				                               getAutoLoad(gridElem));
		return grid;
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
	private void bindDataSource(WidgetFactoryContext<PagedDataGrid> context)
	{
		Element element = context.getElement();
		final PagedDataGrid widget = context.getWidget();

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
				return RowSelectionModel.UNSELECTABLE;
			}
			else if("single".equals(rowSelection))
			{
				return RowSelectionModel.SINGLE;
			}
			else if("multiple".equals(rowSelection))
			{
				return RowSelectionModel.MULTIPLE;
			}
			else if("singleWithRadioButton".equals(rowSelection))
			{
				return RowSelectionModel.SINGLE_WITH_RADIO;
			}
			else if("multipleWithCheckBox".equals(rowSelection))
			{
				return RowSelectionModel.MULTIPLE_WITH_CHECKBOX;
			}
		}
		
		return RowSelectionModel.UNSELECTABLE;
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
	private DataColumnDefinitions getColumnDefinitions(Element gridElem) throws InterfaceConfigException
	{
		DataColumnDefinitions defs = new DataColumnDefinitions();
		
		List<Element> colElems = ensureChildrenSpans(gridElem, false);
		if(colElems != null && colElems.size() > 0)
		{
			for (Element colElem : colElems)
			{
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
				
				DataColumnDefinition def = new DataColumnDefinition(
						label, 
						width, 
						formatter, 
						visible, 
						AlignmentUtil.getHorizontalAlignment(hAlign, HasHorizontalAlignment.ALIGN_CENTER),
						AlignmentUtil.getVerticalAlignment(vAlign, HasVerticalAlignment.ALIGN_MIDDLE));
				
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
	@TagAttributes({
		@TagAttribute(value="pageSize", type=Integer.class, defaultValue="0x7fffffff", autoProcess=false),
		@TagAttribute(value="rowSelection", type=RowSelectionModel.class, defaultValue="unselectable", autoProcess=false),
		@TagAttribute(value="dataSource", autoProcess=false),
		@TagAttribute(value="cellSpacing", type=Integer.class, defaultValue="1", autoProcess=false),
		@TagAttribute(value="autoLoadData", type=Boolean.class, defaultValue="false", autoProcess=false)
	})
	public void processAttributes(WidgetFactoryContext<PagedDataGrid> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		bindDataSource(context);
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onRowClick"),
		@TagEventDeclaration("onRowDoubleClick"),
		@TagEventDeclaration("onRowRender")
	})
	public void processEvents(WidgetFactoryContext<PagedDataGrid> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		PagedDataGrid widget = context.getWidget();

		RowEventsBind.bindClickRowEvent(element, widget);
		RowEventsBind.bindDoubleClickRowEvent(element, widget);
		RowEventsBind.bindRenderRowEvent(element, widget);
		super.processEvents(context);
	}
}