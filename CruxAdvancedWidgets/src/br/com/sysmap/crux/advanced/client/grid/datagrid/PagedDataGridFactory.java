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

import br.com.sysmap.crux.advanced.client.grid.model.RowSelectionModel;
import br.com.sysmap.crux.core.client.datasource.EditableDataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

/**
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class PagedDataGridFactory extends WidgetFactory<PagedDataGrid>
{
	/**
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	protected PagedDataGrid instantiateWidget(Element gridElem, String widgetId) throws InterfaceConfigException
	{
		PagedDataGrid grid = new PagedDataGrid(getColumnDefinitions(gridElem), getPageSize(gridElem), getRowSelectionModel(gridElem));
		bindDataSource(grid, gridElem);
		return grid;
	}

	/**
	 * @param grid
	 * @param gridElem
	 */
	private void bindDataSource(final PagedDataGrid grid, final Element gridElem)
	{
		final String dataSourceName = gridElem.getAttribute("_dataSource");
		
		if(dataSourceName != null && dataSourceName.length() > 0)
		{
			DeferredCommand.addCommand(new Command()
			{
				@SuppressWarnings("unchecked")
				public void execute()
				{	
					PagedDataSource<EditableDataSourceRecord> dataSource = (PagedDataSource<EditableDataSourceRecord>) Screen.getDataSource(dataSourceName);
					grid.setDataSource(dataSource);
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
				
				boolean visible = (strVisible != null && strVisible.length() > 0) ? Boolean.parseBoolean(strVisible) : true;
				String formatter = (strFormatter != null && strFormatter.length() > 0) ? strFormatter : null;
				label = (label != null && label.length() > 0) ? ScreenFactory.getInstance().getDeclaredMessage(label) : "";
				
				DataColumnDefinition def = new DataColumnDefinition(label, width, formatter, visible);
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
}