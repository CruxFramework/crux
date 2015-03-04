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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.grid.Type.SelectStrategy;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample code:
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
		EagerPagedDataProvider<Person> dataProvider = new EagerPagedDataProvider<Person>();
		DataGrid<Person> grid = new DataGrid<Person>(dataProvider, true);
		
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
			public void setProperty(Person value, String newValue)
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
				public void setProperty(Person value, Date newValue)
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

/**
 * @author samuel.cardoso
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class PageableDataGrid<T> extends AbstractPageable<T> implements Pageable<PagedDataProvider<T>>
{
	/**
	 * Style class.
	 */
	public static final String STYLE_FACES_DATAGRID = "faces-Datagrid";
	private FlexTable table = new FlexTable();
	private SelectStrategy selectStrategy = SelectStrategy.SINGLE;
	private Array<Column<?>> columns = CollectionFactory.createArray();
	
	/**
	 * @param dataProvider the dataprovider.
	 * @param autoLoadData if true, the data must be loaded after the constructor has been invoked.
	 */
	public PageableDataGrid(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		table.setStyleName(STYLE_FACES_DATAGRID);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		initWidget(table);
		setDataProvider(dataProvider, autoLoadData);
	}
	
	@Override
    protected void clear()
    {
	    table.clear();
    }
	
	@Override
	protected void clearRange(int pageStart)
	{
		while (table.getRowCount() > pageStart)
		{
			table.removeRow(pageStart);
		}
	}
	
	/**
	 * @param column the column to be added.
	 */
	public void addColumn(Column<?> column)
    {
		columns.add(column);
    }
	
	@Override
	protected org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable.Renderer<T> getRenderer()
	{
	    return new Renderer<T>()
		{
			@Override
			public void render(T dataObject)
			{
				for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
				{
					PageableDataGrid<T>.Column<?> dataGridColumn = columns.get(columnIndex);
					
					dataGridColumn.renderCell(
						columnIndex, 
						dataGridColumn.getCells().size(), 
						dataObject,
						dataGridColumn.getCellEditor() != null ? dataGridColumn.getCellEditor().isEditable(dataObject) : false,
						true);
				}
			}
		};
	}
	
	/**
	 * @author samuel.cardoso
	 *
	 * @param <T> the DataObject type.
	 * @param <K> the property type inside of DataObject.
	 */
	public abstract static class CellEditor<T, K>
	{
		private boolean autoRefreshRow = false;

		/**
		 * The default constructor.
		 */
		public CellEditor()
        {
        }
		
		/**
		 * @param autoRefreshRow if true, the row will be refreshed when changed.
		 */
		public CellEditor(boolean autoRefreshRow)
        {
	        this.autoRefreshRow = autoRefreshRow;
        }

		/**
		 * @param dataObject the dataObject.
		 * @return the widget to be rendered when in edition mode.
		 */
		public abstract HasValue<K> getWidget(T dataObject);
		
		/**
		 * @param dataObject the dataObject. 
		 * @param newValue the new value to be inserted in the dataObject. 
		 */
		public abstract void setProperty(T dataObject, K newValue);
		
		/**
		 * @param grid
		 * @param rowIndex
		 * @param columnIndex
		 * @param dataObject
		 */
		public void setEditableWidget(
			final PageableDataGrid<T> grid, 
			final int rowIndex, 
			final int columnIndex, 
			final T dataObject 
		)
		{
			HasValue<K> widget = getWidget(dataObject);
			assert(widget != null): "widget cannot be null";
			grid.getTable().setWidget(rowIndex, columnIndex, (Widget) widget);
			
			widget.addValueChangeHandler(new ValueChangeHandler<K>()
			{
				@Override
                public void onValueChange(ValueChangeEvent<K> event)
                {
					CellEditor.this.setProperty(dataObject, event.getValue());
					grid.getDataProvider().set(columnIndex, dataObject);
					
					if(autoRefreshRow)
					{
						for(int columnIndex=0; columnIndex<grid.getColumns().size();columnIndex++)
						{
							PageableDataGrid<T>.Column<?> dataGridColumn = grid.getColumns().get(columnIndex);
							
							dataGridColumn.renderCell(
								columnIndex, 
								rowIndex, 
								dataObject,
								dataGridColumn.getCellEditor() != null ? dataGridColumn.getCellEditor().isEditable(dataObject) : false,
								false);
						}
					}
					
                }
			});
		}
		
		public boolean isEditable(T dataObject)
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
		Array<Cell> cells = CollectionFactory.createArray();
		Comparator<T> comparator;
		DataFactory<V, T> dataFactory;
		CellEditor<T, ?> cellEditor;
		
		public Column(DataFactory<V, T> dataFactory)
        {
			assert(dataFactory != null): "dataFactory must not be null";
	        this.dataFactory = dataFactory;
        }
		
		public void addCell(Cell cell)
		{
			if(cells == null)
			{
				cells = CollectionFactory.createArray();
			}
			cell.setColumn(this);
			cells.add(cell);
		}
		
		public Column<V> getColumn()
		{
			return this; 
		}
		
		private void renderCell(int columnIndex, int rowIndex, T dataObject, boolean isEditable, boolean addCell)
		{
			Cell cell = new Cell(); 
			V data = getDataFactory().createData(dataObject);
			cell.setValue(data);
			
			if (isEditable)
			{
				cellEditor.setEditableWidget(PageableDataGrid.this, rowIndex, columnIndex, dataObject);
			} else
			{
				table.setWidget(rowIndex, columnIndex, cell.getWidget());	
			}
			
			if(addCell)
			{
				addCell(cell);
			}
		}
		
		public class Cell
		{
			String tooltip;
			V value;
			IsWidget widget;
			Column<V> column;
		
			public void setTooltip(String tooltip)
		    {
			    this.tooltip = tooltip;
		    }
			
	        public V getValue()
	        {
				return value;
	        }
			
			public IsWidget getWidget()
	        {
				return widget;
	        }

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
				} 
				else
				{
					widget = new Label(value.toString());
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

		public Array<Cell> getCells()
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
		private Array<Column<?>> columns;
		
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

	public Array<Column<?>> getColumns()
	{
		return columns;
	}

	public FlexTable getTable()
	{
		return table;
	}
}
