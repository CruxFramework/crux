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
        package org.cruxframework.crossdeviceshowcase.client.controller.samples.simplegrid;

import java.util.ArrayList;
import java.util.Date;

import org.cruxframework.crossdeviceshowcase.client.controller.samples.grid.GridMessages;
import org.cruxframework.crossdeviceshowcase.client.controller.samples.slider.Person;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.dataprovider.EagerPagedDataProvider;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindView;
import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.smartfaces.client.grid.DataGrid;
import org.cruxframework.crux.smartfaces.client.grid.PageableDataGrid.CellEditor;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.widgets.client.datepicker.DatePicker;
import org.cruxframework.crux.widgets.client.styledpanel.StyledPanel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

@Controller("simpleGridController")
public class SimpleGridController
{
	@Inject
	private MyWidgetAccessor screen;

	@Inject
	private GridMessages messages;

	@Expose
	public void onLoad()
	{
		screen.htmlDescText().setHTML(messages.htmlDescText());

		loadData();
	}

	private <T> void loadData()
	{
		final DataGrid<Person> grid = new DataGrid<Person>();
		final EagerPagedDataProvider<Person> dataProvider = new EagerPagedDataProvider<Person>();
		grid.setDataProvider(dataProvider, true);
		
		grid.newColumn(new DataFactory<Label, Person>()
		{
			@Override
            public Label createData(Person value)
            {
	            return new Label(value.getName());
            }
		});
		
		grid.newColumn(new DataFactory<Label, Person>()
		{
			@Override
            public Label createData(Person value)
            {
				return new Label(value.getName());
            }
		}).setCellEditor(new CellEditor<Person, String>(true)
		{
			@Override
            public HasValue<String> getWidget(Person value)
            {
				TextBox textBox = new TextBox();
				textBox.setText(value.getName());
	            return textBox;
            }
			
			@Override
			public boolean isEditable(Person value)
			{
			    return true;
			}

			@Override
			public void onValueChange(Person value, String newValue)
			{
				value.setName(newValue);
			}
		});
		
		grid.newColumn(new DataFactory<Label, Person>()
			{
				@Override
	            public Label createData(Person value)
	            {
					return new Label(value.getName());
	            }
			}).setCellEditor(new CellEditor<Person, Date>(true)
			{
				@Override
	            public HasValue<Date> getWidget(Person value)
	            {
		            return new DatePicker();
	            }
				
				@Override
				public boolean isEditable(Person value)
				{
				    return value.getAge() > 3;
				}

				@Override
				public void onValueChange(Person value, Date newValue)
				{
					value.setName(newValue.toString());
				}
			});
		
		dataProvider.setData(mockPersonData());
		
		screen.panel().add(grid);
	}

	private ArrayList<Person> mockPersonData()
	{
		ArrayList<Person> people = new ArrayList<Person>();
		
		people.add(mockPerson(1));
		people.add(mockPerson(2));
		people.add(mockPerson(3));
		people.add(mockPerson(4));
		people.add(mockPerson(5));
		
		return people;
	}
	
	private Person mockPerson(int seed)
	{
		Person person = new Person();
		person.setAge(seed);
		person.setExperienceTime(seed);
		person.setName("name_" + seed);
		person.setPhone(seed);
		person.setProfession("profession_" + seed);
		return person;
	}
	
	
	@BindView("simpleGrid")
	public static interface MyWidgetAccessor extends WidgetAccessor
	{
		HTML htmlDescText();
		StyledPanel panel();
	}

	public void setMessages(GridMessages messages) {
		this.messages = messages;
	}

	public MyWidgetAccessor getScreen()
	{
		return screen;
	}

	public void setScreen(MyWidgetAccessor screen)
	{
		this.screen = screen;
	}
}

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
				for(int columnIndex=0; columnIndex<columns.size();columnIndex++)
				{
					PageableDataGrid<T>.Column<?> dataGridColumn = columns.get(columnIndex);
					
					dataGridColumn.renderCell(columnIndex, dataGridColumn.getCells().size(), value,
						dataGridColumn.getCellEditor() != null ? dataGridColumn.getCellEditor().isEditable(value) : false);
				}
			}
		};
	}
	
	public static abstract class CellEditor<T, K>
	{
		final private boolean autoRefreshRows;

		public CellEditor(boolean autoRefreshRows)
        {
	        this.autoRefreshRows = autoRefreshRows;
        }

		public abstract HasValue<K> getWidget(T value);
		
		public abstract void onValueChange(T value, K newValue);
		
		public void setEditableWidget(FlexTable table, final PageableDataGrid<T> grid, final PageableDataGrid<T>.Column<?>.Cell cell, final int rowIndex, final int columnIndex, final T value, final PagedDataProvider<T> dataProvider)
		{
			HasValue<K> widget = getWidget(value);
			assert(widget != null): "widget cannot be null";
			table.setWidget(rowIndex, columnIndex, (Widget) widget);
			
			widget.addValueChangeHandler(new ValueChangeHandler<K>()
			{
				@Override
                public void onValueChange(ValueChangeEvent<K> event)
                {
					CellEditor.this.onValueChange(value, event.getValue());
					dataProvider.set(columnIndex, value);
					
					if(autoRefreshRows)
					{
						for(int columnIndex=0; columnIndex<grid.getColumns().size();columnIndex++)
						{
							PageableDataGrid<T>.Column<?> dataGridColumn = grid.getColumns().get(columnIndex);
							
							dataGridColumn.renderCell(columnIndex, rowIndex, value,
								dataGridColumn.getCellEditor() != null ? dataGridColumn.getCellEditor().isEditable(value) : false);
						}
					}
					
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
		FastList<Cell> cells = new FastList<PageableDataGrid<T>.Column<V>.Cell>();
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
		
		public void addCell(Cell cell)
		{
			if(cells == null)
			{
				cells = new FastList<PageableDataGrid<T>.Column<V>.Cell>();
			}
			cell.setColumn(this);
			cells.add(cell);
		}
		
		public Column(DataFactory<V, T> dataFactory)
        {
			assert(dataFactory != null): "dataFactory must not be null";
	        this.dataFactory = dataFactory;
        }
		
		public Column<V> getColumn()
		{
			return this; 
		}
		
		private void renderCell(int columnIndex, int rowIndex, T value, boolean isEditable)
		{
			Cell cell = new Cell(); 
			V data = getDataFactory().createData(value);
			cell.setValue(data);
			
			if (isEditable)
			{
				cellEditor.setEditableWidget(table, PageableDataGrid.this, cell, rowIndex, columnIndex, value, getDataProvider());
			} else
			{
				table.setWidget(rowIndex, columnIndex, cell.getWidget());	
			}
			addCell(cell);
		}
		
		public class Cell implements HasValue<V>
		{
			String tooltip;
			V value;
			IsWidget widget;
			Column<V> column;
		
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

			public Column<V> getColumn()
			{
				return column;
			}

			private void setColumn(Column<V> column)
			{
				this.column = column;
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

		public FastList<Cell> getCells()
		{
			return cells;
		}
	}	
	
	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 *
	 * @param <T>
	 */
	public class DataGridColumnGroup
	{
		private FastList<Column<?>> columns;
		
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

	public FastList<Column<?>> getColumns()
	{
		return columns;
	}
}
