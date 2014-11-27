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

import java.util.ArrayList;
import java.util.Comparator;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.grid.Type.SelectStrategy;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T>
 * 
 * Sample:
 * 
        DataGrid<Person> grid = new DataGrid<Person>();
		EagerPagedDataProvider<Person> dataProvider = new EagerPagedDataProvider<Person>();
		grid.setDataProvider(dataProvider, true);
		
		grid.newColumn(new DataFactory<Label, Person>()
		{
			@Override
			public Label createData(Person value)
			{
				return new Label(value.getName());
			}
		});

		grid.newColumn(new DataFactory<DatePicker, Person>()
		{
			@Override
            public DatePicker createData(Person value)
            {
				DatePicker datePicker = new DatePicker();
		        datePicker.setValue(new Date());
				return datePicker;
            }
		}).setCellEditor(new CellEditor<Person, Date>()
		{
			@Override
            public HasValue<Date> getWidget(Person value)
            {
				DatePicker datePicker = new DatePicker();
	            return datePicker;
            }
			
			@Override
			public boolean isEditable(Person value)
			{
			    return true;
			}

			@Override
			public void setProperty(Person value, Date newValue)
			{
				Window.alert(newValue.toString());
				value.setName(newValue.toString());
			}
		});
		
		dataProvider.setData(mockPersonData());
		
		screen.panel().add(grid);
 */
//CHECKSTYLE:OFF
public class PageableDataGrid<T> extends AbstractPageable<T> implements Pageable<PagedDataProvider<T>>
{
	public static final String STYLE_FACES_DATAGRID = "faces-Datagrid";

	final FlexTable table = new FlexTable();
	
	private SelectStrategy selectStrategy = SelectStrategy.SINGLE;
	
	FastList<Column<?>> columns = new FastList<PageableDataGrid<T>.Column<?>>();
	
	public PageableDataGrid()
	{
		table.setStyleName(STYLE_FACES_DATAGRID);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		initWidget(table);
	}
	
	@Override
    protected void clear()
    {
	    table.clear();
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
					PageableDataGrid<T>.Column<?> dataGridColumn = columns.get(i);
					dataGridColumn.renderCell(i, value, dataGridColumn);
				}
			}
		};
	}
	
	public static abstract class CellEditor<T, K>
	{
		public abstract HasValue<K> getWidget(T value);
		
		public abstract void setProperty(T value, K newValue);
		
		public void setEditableWidget(FlexTable table, int rowNumber, final T value, final PagedDataProvider<T> dataProvider, final int rowIndex)
		{
			HasValue<K> widget = getWidget(value);
			assert(widget != null): "widget cannot be null";
			table.setWidget(rowNumber, rowIndex, (Widget) widget);
			
			widget.addValueChangeHandler(new ValueChangeHandler<K>()
			{
				@Override
                public void onValueChange(ValueChangeEvent<K> event)
                {
					setProperty(value, event.getValue());
					dataProvider.set(rowIndex, value);
                }
			});
		}
		
		public boolean isEditable(T value)
		{
			return false;
		}
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
		DataFactory<V, T> dataFactory;
		CellEditor<T, ?> cellEditor;
		
		boolean editable = false;
		boolean freezeHeader = false;
		boolean selected = false;
		ArrayList<Device> visibleDevices = new ArrayList<Device>();
		ArrayList<Device> notVisibleDevices = new ArrayList<Device>();

		public Column()
        {
        }
		
		public Column(DataFactory<V, T> dataFactory)
        {
			assert(dataFactory != null): "dataFactory must not be null";
	        this.dataFactory = dataFactory;
        }
		
		public void renderCell(int rowIndex, T value, PageableDataGrid<T>.Column<?> dataGridColumn)
		{
			Cell cell = new Cell();
			V data = getDataFactory().createData(value);
			cell.setValue(data);
			
			boolean isEditable = dataGridColumn.getCellEditor() != null ? dataGridColumn.getCellEditor().isEditable(value) : false;
			
			if(cells == null)
			{
				cells = new FastList<PageableDataGrid<T>.Column<V>.Cell>();
			}
			
			if (isEditable)
			{
				cellEditor.setEditableWidget(table, cells.size(), value, getDataProvider(), rowIndex);
			} else
			{
				table.setWidget(cells.size(), rowIndex, cell.getWidget());				
			}
			
			cells.add(cell);
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
				
				if(value == null)
				{
					return;
				}
				
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

		public Column<V> setDataFactory(DataFactory<V, T> dataFactory)
        {
	        this.dataFactory = dataFactory;
	        return this;
        }

		public DataFactory<V, T> getDataFactory()
		{
			return dataFactory;
		}

		public CellEditor<T, ?> getCellEditor()
		{
			return cellEditor;
		}

		public Column<V> setCellEditor(CellEditor<T, ?> cellEditor)
		{
			this.cellEditor = cellEditor;
			return this;
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

	public SelectStrategy getSelectStrategy()
	{
		return selectStrategy;
	}

	public void setSelectStrategy(SelectStrategy selectStrategy)
	{
		this.selectStrategy = selectStrategy;
	}
}
