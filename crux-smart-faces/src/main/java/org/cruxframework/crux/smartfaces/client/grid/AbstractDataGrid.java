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
package org.cruxframework.crux.smartfaces.client.grid;

import java.util.Comparator;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T>
 */
//CHECKSTYLE:OFF
public abstract class AbstractDataGrid<T> extends AbstractPageable<T> implements Pageable<PagedDataProvider<T>>, HasAllFocusHandlers, HasEnabled, HasSelectHandlers, HasAllMouseHandlers
{
	public static final String STYLE_FACES_DATAGRID = "faces-Datagrid";

	final FlexTable table = new FlexTable();
	FastList<Column<?>> columns = new FastList<AbstractDataGrid<T>.Column<?>>();
	
	public AbstractDataGrid()
	{
		table.setStyleName(STYLE_FACES_DATAGRID);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		initWidget(table);
	}
	
	public void addColumn(Column<?> widgetColumn)
    {
		columns.add(widgetColumn);
    }
	
	@Override
	protected org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable.Renderer<T> getRenderer()
	{
	    return new Renderer<T>()
		{
			@Override
			public void render(T value)
			{
				for(int i=0; i<columns.size();i++)
				{
					AbstractDataGrid<T>.Column<?> dataGridColumn = columns.get(i);
					
					for(int j=0;j<dataGridColumn.cells.size();j++)
					{
						AbstractDataGrid<T>.Column<?>.Cell cell = dataGridColumn.cells.get(j);
						//Thiago! HELP!
//						dataGridColumn.getRenderer().onCellRender(cell, value);
						table.setWidget(i, j, cell.getWidget());
					}
				}
			}
		};
	}
	
	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 *
	 * @param <T>
	 */
	public class Column<V>
	{
		Label label;
		String header;
		FastList<Cell> cells;
		Comparator<T> comparator;
		AbstractDataGrid<T>.Column<V>.Renderer renderer;

		public abstract class Renderer
		{
			public abstract void onCellRender(Cell cell, T value);
		}
		
		public class Cell implements HasValue<V>
		{
			String tooltip;
			V value;
			IsWidget widget;
		
			public void setTooltip(String tooltip)
		    {
			    this.tooltip = tooltip;
		    }

			@Override
	        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler)
	        {
		        // TODO Auto-generated method stub
		        return null;
	        }

			@Override
	        public void fireEvent(GwtEvent<?> event)
	        {
		        // TODO Auto-generated method stub
	        }

			@Override
	        public V getValue()
	        {
				return value;
	        }
			
			public IsWidget getWidget()
	        {
				return widget;
	        }

			@Override
	        public void setValue(V value)
	        {
				this.value = value;
				
				if(value instanceof IsWidget)
				{
					widget = (IsWidget) value;
				} else
				{
					widget = new Label(value.toString());
				}
	        }

			@Override
	        public void setValue(V value, boolean fireEvents)
	        {
				setValue(value);
				if(fireEvents)
				{
					
				}
	        }
		}
		
		public Column<V> setHeader(String header)
	    {
			this.header = header;
		    return this;
	    }

		public Column<V> setComparator(Comparator<T> comparator)
	    {
		    this.comparator = comparator;
		    return this;
	    }

		public Column<V> setRenderer(Renderer renderer)
        {
	        this.renderer = renderer;
	        return this;
        }

		public AbstractDataGrid<T>.Column<V>.Renderer getRenderer()
		{
			return renderer;
		}
	}	
	
	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 *
	 * @param <T>
	 */
	public class DataGridColumnGroup
	{
		FastList<Column<?>> columns;
		
		public void addColumn(Column<?> widgetColumn)
	    {
			columns.add(widgetColumn);
	    }
	}
}
