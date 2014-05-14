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
package org.cruxframework.crux.widgets.client.deviceadaptivegrid;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cruxframework.crux.core.client.datasource.HasDataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.event.row.RowRenderHandler;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinitions;
import org.cruxframework.crux.widgets.client.grid.DataColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.DataRow;
import org.cruxframework.crux.widgets.client.grid.Grid;
import org.cruxframework.crux.widgets.client.grid.Grid.SortingType;
import org.cruxframework.crux.widgets.client.grid.RowSelectionModel;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition.WidgetColumnCreator;
import org.cruxframework.crux.widgets.client.paging.Pageable;
import org.cruxframework.crux.widgets.client.paging.Pager;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox.Caption;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author wesley.diniz
 *
 */
public class DeviceAdaptiveGrid extends Composite implements Pageable, HasDataSource<PagedDataSource<?>>
{
	private GridImpl gridImpl;

	/**
	 *
	 */
	public DeviceAdaptiveGrid()
	{
		this.gridImpl = GWT.create(GridImpl.class);
		initWidget(this.gridImpl);
	}

	/**
	 * @param dataSource The datasource used to fill the grid
	 */
	public void setDataSource(PagedDataSource<?> dataSource)
	{
		gridImpl.setDataSource(dataSource);
	}

	public void setDataSource(PagedDataSource<?> dataSource, boolean autoLoadData)
	{
		gridImpl.setDataSource(dataSource, autoLoadData);
	}
	
	/**
	 *  @see org.cruxframework.crux.widgets.client.grid.Grid#loadData()
	 */
	public void loadData()
	{
		gridImpl.loadData();
	}


	/**
	 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#clear()
	 **/
	public void clear()
	{
		gridImpl.clear();
	}

	/**
	 * @return
	 */
	public ColumnDefinitions getGridColumnDefinitionsByDevice()
	{
		return gridImpl.getGridColumnDefinitionsByDevice();
	}

	/**
	 * @param handler Event handler
	 * @return HandlerRegistration
	 * @see Add a rowrender event handler
	 */
	public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
	{
		return gridImpl.addRowRenderHandler(handler);
	}

	/**
	 * @return page number
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		return gridImpl.getCurrentPageSize();
	}


	/**
	 * @return PagedDataSource object
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getDataSource()
	 */
	public PagedDataSource<?> getDataSource()
	{
		return gridImpl.getDataSource();
	}


	/**
	 * @return List of the rows in current page
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageRows()
	 */
	public List<DataRow> getCurrentPageRows()
	{
		return gridImpl.getCurrentPageRows();
	}


	/**
	 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#refresh()
	 */
	public void refresh()
	{
		gridImpl.refresh();
	}


	/**
	 * @return A list with selected rows
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getSelectedRows()
	 */
	public List<DataRow> getSelectedRows()
	{
		return gridImpl.getSelectedRows();
	}


	/**
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#nextPage()
	 */
	public void nextPage()
	{
		gridImpl.nextPage();
	}


	/**
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#previousPage()
	 */
	public void previousPage()
	{
		gridImpl.previousPage();
	}


	/**
	 * @param pager
	 *  @see org.cruxframework.crux.widgets.client.grid.Grid#setPager(org.cruxframework.crux.widgets.client.paging.Pager)
	 */
	public void setPager(Pager pager)
	{
		gridImpl.setPager(pager);
	}


	/**
	 * @param page The page of the requested index
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#goToPage(int)
	 */
	public void goToPage(int page)
	{
		gridImpl.goToPage(page);
	}


	/**
	 * @param w
	 * @return The datarow that contains the informed widget
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getRow(com.google.gwt.user.client.ui.Widget)
	 */
	public DataRow getRow(Widget w)
	{
		return gridImpl.getRow(w);
	}



	/**
	 * @return Page count
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#getPageCount()
	 */
	public int getPageCount()
	{
		return gridImpl.getPageCount();
	}


	/**
	 * @return Verify if there is any data loaded
	 * @see org.cruxframework.crux.widgets.client.grid.Grid#isDataLoaded()
	 */
	public boolean isDataLoaded()
	{
		return gridImpl.isDataLoaded();
	}


	/**
	 * Retrieve the widget associated to the grid component
	 * @param id
	 * @return Widget
	 */
	public Widget getActionWidget(String key)
	{
		return gridImpl.getActionWidget(key);
	}

	/**
	 * Initialize grid component
	 * @param columnDefinitions The definition of grid`s columns
	 * @param pageSize The maximum size of a page
	 * @param rowSelection Row selection mode
	 * @param cellSpacing Space between cells
	 * @param autoLoadData Load data automatically
	 * @param stretchColumns Stretch columns to content, if needed
	 * @param highlightRowOnMouseOver Highlight row when the mouse cursor is over it
	 * @param emptyDataFilling
	 * @param fixedCellSize Fix the size of all cells
	 * @param defaultSortingColumn The default column used to sort data
	 * @param defaultSortingType Sorting type
	 */
	public void initGrid(DeviceAdaptiveGridColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection,
			int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling,
			boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
	{
		this.gridImpl.initGrid(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType);
	}

	static abstract class GridImpl extends SimplePanel
	{
		protected Grid grid;
		protected DeviceAdaptiveGridColumnDefinitions columnDefinitions;

		/**
		 * Initialize grid component
		 * @param columnDefinitions The definition of grid`s columns
		 * @param pageSize The maximum size of a page
		 * @param rowSelection Row selection mode
		 * @param cellSpacing Space between cells
		 * @param autoLoadData Load data automatically
		 * @param stretchColumns Stretch columns to content, if needed
		 * @param highlightRowOnMouseOver Highlight row when the mouse cursor is over it
		 * @param emptyDataFilling
		 * @param fixedCellSize Fix the size of all cells
		 * @param defaultSortingColumn The default column used to sort data
		 * @param defaultSortingType Sorting type
		 */
		public void initGrid(DeviceAdaptiveGridColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection,
				int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling,
				boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
		{
			this.columnDefinitions = columnDefinitions;
			this.grid = new Grid(getGridColumnDefinitionsByDevice(), pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns,
					highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType);

			setWidget(this.grid);
		}

		/**
		 * @param dataSource
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#setDataSource(org.cruxframework.crux.core.client.datasource.PagedDataSource)
		 */
		public void setDataSource(PagedDataSource<?> dataSource)
		{
			grid.setDataSource(dataSource);
		}

		public void setDataSource(PagedDataSource<?> dataSource, boolean autoLoadData)
		{
			grid.setDataSource(dataSource, autoLoadData);
		}

		/**
		 *
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#loadData()
		 */
		public void loadData()
		{
			grid.loadData();
		}

		/**
		 *
		 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#clear()
		 */
		public void clear()
		{
			grid.clear();
		}

		/**
		 *
		 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#refresh()
		 */
		public void refresh()
		{
			grid.refresh();
		}

		/**
		 * @return List of selected rows
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getSelectedRows()
		 */
		public List<DataRow> getSelectedRows()
		{
			return grid.getSelectedRows();
		}


		/**
		 * @return The current page size
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageSize()
		 */
		public int getCurrentPageSize()
		{
			return grid.getCurrentPageSize();
		}

		/**
		 * @return PagedDataSource
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getDataSource()
		 */
		public PagedDataSource<?> getDataSource()
		{
			return grid.getDataSource();
		}

		/**
		 * @return List of the rows in current page
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageRows()
		 */
		public List<DataRow> getCurrentPageRows()
		{
			return grid.getCurrentPageRows();
		}


		/**
		 * @param w
		 * @return The datarow that contains the informed widget
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getRow(com.google.gwt.user.client.ui.Widget)
		 */
		public DataRow getRow(Widget w)
		{
			return grid.getRow(w);
		}


		/**
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#nextPage()
		 */
		public void nextPage()
		{
			grid.nextPage();
		}

		/**
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#previousPage()
		 */
		public void previousPage()
		{
			grid.previousPage();
		}

		/**
		 * @param pager
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#setPager(org.cruxframework.crux.widgets.client.paging.Pager)
		 */
		public void setPager(Pager pager)
		{
			grid.setPager(pager);
		}

		/**
		 * @param page
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#goToPage(int)
		 */
		public void goToPage(int page)
		{
			grid.goToPage(page);
		}


		/**
		 * @return Page count
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getPageCount()
		 */
		public int getPageCount()
		{
			return grid.getPageCount();
		}

		/**
		 * @return Verify if there is any data loaded
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#isDataLoaded()
		 */
		public boolean isDataLoaded()
		{
			return grid.isDataLoaded();
		}

		/**
		 * @param handler Event handler
		 * @return HandlerRegistration
		 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#addRowRenderHandler(org.cruxframework.crux.widgets.client.event.row.RowRenderHandler)
		 */
		public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
		{
			return grid.addRowRenderHandler(handler);
		}

		/**
		 * Retrieve the columns defined to a Large device
		 * @return ColumnDefinitions to a large device
		 */
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			return columnDefinitions.getLargeColumnDefinitions();
		}


		/**
		 * Retrieve the widget associated to the grid component
		 * @param id
		 * @return Widget
		 */
		public abstract Widget getActionWidget(String key);
	}

	static class GridSmallImpl extends GridImpl
	{
		HashMap<String, Button> actionsWidgets = new HashMap<String, Button>();

		
		/**
		 * @see org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGrid.GridImpl#initGrid(org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGridColumnDefinitions, int, org.cruxframework.crux.widgets.client.grid.RowSelectionModel, int, boolean, boolean, boolean, java.lang.String, boolean, java.lang.String, org.cruxframework.crux.widgets.client.grid.Grid.SortingType)
		 */
		@Override
		public void initGrid(DeviceAdaptiveGridColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling,
				boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
		{
			super.initGrid(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType);

			initActionColumns();
		}

		/**
		 * initialize the actions available on grid detail when in mobile visualization
		 */
		private void initActionColumns()
		{
			Iterator<ColumnDefinition> it = columnDefinitions.getSmallColumnDefinitions().getIterator();

			while(it.hasNext())
			{
				ColumnDefinition colDef = it.next();

				if (colDef instanceof ActionColumnDefinition)
				{
					ActionColumnDefinition actionColumnDefinition = (ActionColumnDefinition) colDef;
					Widget widget = actionColumnDefinition.getWidgetColumnCreator().createWidgetForColumn();

					if (!(widget instanceof Button))
					{
						throw new RuntimeException("Action columns only supports org.cruxframework.crux.widgets.client.button.Button");
					}

					Button button = (Button) widget;
					actionsWidgets.put(colDef.getKey(), button);
				}
			}
		}

		
		/**
		 * @see org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGrid.GridImpl#getGridColumnDefinitionsByDevice()
		 */
		@Override
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			ColumnDefinitions smallColumnDefinitions = this.columnDefinitions.getSmallColumnDefinitions();

			smallColumnDefinitions.add("detail", new WidgetColumnDefinition("", "24px", new WidgetColumnCreator()
			{
				public Widget createWidgetForColumn()
				{
					final Button detailButton =  new Button();
					detailButton.setHeight("24px");
					detailButton.setWidth("24px");
					detailButton.setStylePrimaryName("detail-icon");

					detailButton.addSelectHandler(new SelectHandler()
					{

						@Override
						public void onSelect(SelectEvent event)
						{
							DataRow row = getRow(detailButton);

							new DetailDialogBox(actionsWidgets, columnDefinitions, row).show();

						}
					});
					return detailButton;
				}
			}, true, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE));

			return smallColumnDefinitions;
		}

		/**
		 * @param w
		 * @return The data row that contains the informed widget
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getRow(com.google.gwt.user.client.ui.Widget)
		 */
		@Override
		public DataRow getRow(Widget w)
		{
		    DataRow ret = super.getRow(w);
		    if (ret == null)
		    {
		    	Widget parent = w.getParent();
		    	while (parent != null)
		    	{
		    		if (parent instanceof DetailDialogBox)
		    		{
		    			ret = ((DetailDialogBox)parent).getRow();
		    			break;
		    		}
		    		parent = parent.getParent();
		    	}
		    }
		    return ret;
		}

		/**
		 * Retrieve the widget associated to the grid component
		 * @param id
		 * @return Widget
		 */
		@Override
		public Widget getActionWidget(String key)
		{
			return actionsWidgets.get(key);
		}

	static class DetailDialogBox extends com.google.gwt.user.client.ui.DialogBox
	{
		private final DataRow row;

		/**
		 * @param actionWidgets Button that will be showed on mobile visualization
		 * @param columnDefinitions ColumnDefinitions
		 * @param row DataRow
		 */
		public DetailDialogBox(HashMap<String, Button> actionWidgets, DeviceAdaptiveGridColumnDefinitions columnDefinitions, DataRow row)
		{

			super(false, true, new CloseButtonCaption(""));
			this.row = row;

			setStyleName("grid-detail-dialogbox");

		    CloseButtonCaption ref = (CloseButtonCaption) this.getCaption();
		    PushButton closeButton = ref.getCloseButton();
		    closeButton.setHeight("24px");
		    closeButton.setWidth("24px");
		    closeButton.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent arg0)
				{
					hide();
				}
			});

		    buildDialogContents(actionWidgets, columnDefinitions);
		}

		
		/**
		 * @see com.google.gwt.user.client.ui.DialogBox#show()
		 */
		@Override
		public void show()
		{
			super.show();
			super.center();
		}

		/**
		 * @param w
		 * @return The data row that contains the informed widget
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getRow(com.google.gwt.user.client.ui.Widget)
		 */
		public DataRow getRow()
		{
			return row;
		}


		private void buildDialogContents(HashMap<String, Button> actionWidgets, DeviceAdaptiveGridColumnDefinitions columnDefinitions)
		{
			VerticalPanel vPanel = new VerticalPanel();
			vPanel.setWidth("100%");
			vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			vPanel.setSpacing(5);

			SimplePanel lineSep = new SimplePanel();
			lineSep.setStyleName("line");
			vPanel.add(lineSep);

			Iterator<ColumnDefinition> it = columnDefinitions.getLargeColumnDefinitions().getIterator();

			while(it.hasNext())
			{
				ColumnDefinition colDef = it.next();

				if (colDef instanceof DataColumnDefinition)
				{
					String colValue = (row.getValue(colDef.getKey()) != null ? row.getValue(colDef.getKey()).toString() : "");
					vPanel.add(buildDetailField(colDef.getLabel(), colValue));
				}
			}

			it = columnDefinitions.getSmallColumnDefinitions().getIterator();

			for (java.util.Map.Entry<String, Button> entry : actionWidgets.entrySet())
			{
				Button button = entry.getValue();

				button.addSelectHandler(new SelectHandler()
				{
					@Override
					public void onSelect(SelectEvent event)
					{
						DetailDialogBox.this.hide();
					}
				});

				vPanel.add(button);
			}

			vPanel.setHeight("100%");
			setWidget(vPanel);
		}

		private HorizontalPanel buildDetailField(String label, String value)
		{
			HorizontalPanel panel = new HorizontalPanel();
			panel.setWidth("100%");
			panel.setSpacing(5);
			Label labelKey = new Label(label + ": ");
			labelKey.setStyleName("detail-key");
			panel.add(labelKey);

			Label labelValue = new Label((value != null ? value.toString() : ""));
			labelValue.setStyleName("detail-value");
			panel.add(labelValue);

			panel.setCellWidth(labelKey, "20%");
			panel.setCellWidth(labelValue, "80%");
			panel.setCellHorizontalAlignment(labelKey, HasHorizontalAlignment.ALIGN_LEFT);
			panel.setCellHorizontalAlignment(labelValue, HasHorizontalAlignment.ALIGN_LEFT);

			return panel;
		}
	  }



	}

	static class GridLargeImpl extends GridImpl
	{
		
		/**
		 * @see org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGrid.GridImpl#getGridColumnDefinitionsByDevice()
		 */
		@Override
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			return this.columnDefinitions.getLargeColumnDefinitions();
		}

		
		/**
		 * @see org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGrid.GridImpl#getActionWidget(java.lang.String)
		 */
		@Override
		public Widget getActionWidget(String key)
		{
			throw new RuntimeException("Only Small Mode supports action widgets.");
		}
	}

	static class CloseButtonCaption extends HorizontalPanel implements Caption
	{
	  protected InlineLabel text;
	  protected PushButton closeDialog;

	  /**
	   * @return the close button
	   */
	  public PushButton getCloseButton()
	  {
	    return closeDialog;
	  }
	 
	  /**
	 * @param label
	 */
	public CloseButtonCaption( String label )
	  {
	    super();
	    setWidth( "100%" );
	    setStyleName("caption");
	    closeDialog = new PushButton("  X  ");
	    closeDialog.setStyleName("close");
	    text = new InlineLabel(label);
	    add(text);
	    add(closeDialog);
	    setCellWidth( closeDialog, "1px" );
	  }
	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.dom.client.MouseDownHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseDownHandler( MouseDownHandler handler )
	  {
	    return addMouseDownHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.client.MouseUpHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseUpHandler( MouseUpHandler handler )
	  {
	    return addMouseUpHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseOutHandler( MouseOutHandler handler )
	  {
	    return addMouseOutHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseOverHandler( MouseOverHandler handler )
	  {
	    return addMouseOverHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseMoveHandler( MouseMoveHandler handler )
	  {
	    return addMouseMoveHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseWheelHandler( MouseWheelHandler handler )
	  {
	    return addMouseWheelHandler( handler );
	  }

	  /**
	   * @see com.google.gwt.user.client.ui.HasHTML#getHTML()
	   */
	  @Override
	  public String getHTML()
	  {
	    return getElement().getInnerHTML();
	  }

	  /**
	   * @see com.google.gwt.user.client.ui.HasHTML#setHTML(java.lang.String)
	   */
	  @Override
	  public void setHTML( String html )
	  {
	    remove( text );
	    insert( text, 1 );
	  }

	  /**
	   * @see com.google.gwt.user.client.ui.HasText#getText()
	   */
	  @Override
	  public String getText()
	  {
	    return text.getText();
	  }

	  /**
	   * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	   */
	  @Override
	  public void setText( String text )
	  {
	    this.text.setText( text );
	  }

	  /**
	   * @see com.google.gwt.safehtml.client.HasSafeHtml#setHTML(com.google.gwt.safehtml.shared.SafeHtml)
	   */
	  @Override
	  public void setHTML( SafeHtml html )
	  {
	    setHTML( html.asString() );
	  }
	}
}
