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
 * @author breno.lages
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
	 * @param dataSource
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#setDataSource(org.cruxframework.crux.core.client.datasource.PagedDataSource)
	 */
	public void setDataSource(PagedDataSource<?> dataSource)
	{
		gridImpl.setDataSource(dataSource);
	}


	/**
	 *
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#loadData()
	 */
	public void loadData()
	{
		gridImpl.loadData();
	}


	/**
	 *
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#clear()
	 */
	public void clear()
	{
		gridImpl.clear();
	}



	/**
	 * @param handler
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#addRowRenderHandler(org.cruxframework.crux.widgets.client.event.row.RowRenderHandler)
	 */
	public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
	{
		return gridImpl.addRowRenderHandler(handler);
	}

	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		return gridImpl.getCurrentPageSize();
	}


	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getDataSource()
	 */
	public PagedDataSource<?> getDataSource()
	{
		return gridImpl.getDataSource();
	}


	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getCurrentPageRows()
	 */
	public List<DataRow> getCurrentPageRows()
	{
		return gridImpl.getCurrentPageRows();
	}


	/**
	 *
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#refresh()
	 */
	public void refresh()
	{
		gridImpl.refresh();
	}



	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getSelectedRows()
	 */
	public List<DataRow> getSelectedRows()
	{
		return gridImpl.getSelectedRows();
	}




	/**
	 *
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#nextPage()
	 */
	public void nextPage()
	{
		gridImpl.nextPage();
	}


	/**
	 *
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#previousPage()
	 */
	public void previousPage()
	{
		gridImpl.previousPage();
	}


	/**
	 * @param pager
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#setPager(org.cruxframework.crux.widgets.client.paging.Pager)
	 */
	public void setPager(Pager pager)
	{
		gridImpl.setPager(pager);
	}


	/**
	 * @param page
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#goToPage(int)
	 */
	public void goToPage(int page)
	{
		gridImpl.goToPage(page);
	}


	/**
	 * @param w
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getRow(com.google.gwt.user.client.ui.Widget)
	 */
	public DataRow getRow(Widget w)
	{
		return gridImpl.getRow(w);
	}



	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getPageCount()
	 */
	public int getPageCount()
	{
		return gridImpl.getPageCount();
	}


	/**
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#isDataLoaded()
	 */
	public boolean isDataLoaded()
	{
		return gridImpl.isDataLoaded();
	}


	/**
	 * @param key
	 * @return
	 * @see br.com.mca.comissionamento.core.client.widget.grid.DeviceAdaptiveGrid.GridImpl#getActionWidget(java.lang.String)
	 */
	public Widget getActionWidget(String key)
	{
		return gridImpl.getActionWidget(key);
	}

	/**
	 * Inicializa o componente de Grid
	 * @param columnDefinitions
	 * @param pageSize
	 * @param rowSelection
	 * @param cellSpacing
	 * @param autoLoadData
	 * @param stretchColumns
	 * @param highlightRowOnMouseOver
	 * @param emptyDataFilling
	 * @param fixedCellSize
	 * @param defaultSortingColumn
	 * @param defaultSortingType
	 */
	//CHECKSTYLE:OFF
	public void initGrid(DeviceAdaptiveGridColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection,
			int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling,
			boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
	{
		this.gridImpl.initGrid(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType);
	}
	//CHECKSTYLE:ON

	static abstract class GridImpl extends SimplePanel
	{
		protected Grid grid;
		protected DeviceAdaptiveGridColumnDefinitions columnDefinitions;

		/**
		 * Inicializa o componente de Grid
		 * @param columnDefinitions
		 * @param pageSize
		 * @param rowSelection
		 * @param cellSpacing
		 * @param autoLoadData
		 * @param stretchColumns
		 * @param highlightRowOnMouseOver
		 * @param emptyDataFilling
		 * @param fixedCellSize
		 * @param defaultSortingColumn
		 * @param defaultSortingType
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
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getSelectedRows()
		 */
		public List<DataRow> getSelectedRows()
		{
			return grid.getSelectedRows();
		}


		/**
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageSize()
		 */
		public int getCurrentPageSize()
		{
			return grid.getCurrentPageSize();
		}

		/**
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getDataSource()
		 */
		public PagedDataSource<?> getDataSource()
		{
			return grid.getDataSource();
		}

		/**
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getCurrentPageRows()
		 */
		public List<DataRow> getCurrentPageRows()
		{
			return grid.getCurrentPageRows();
		}


		/**
		 * @param w
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getRow(com.google.gwt.user.client.ui.Widget)
		 */
		public DataRow getRow(Widget w)
		{
			return grid.getRow(w);
		}


		/**
		 *
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#nextPage()
		 */
		public void nextPage()
		{
			grid.nextPage();
		}

		/**
		 *
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
			//FIXME grid.setPager(pager);
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
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#getPageCount()
		 */
		public int getPageCount()
		{
			return grid.getPageCount();
		}

		/**
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.Grid#isDataLoaded()
		 */
		public boolean isDataLoaded()
		{
			return grid.isDataLoaded();
		}



		/**
		 * @param handler
		 * @return
		 * @see org.cruxframework.crux.widgets.client.grid.AbstractGrid#addRowRenderHandler(org.cruxframework.crux.widgets.client.event.row.RowRenderHandler)
		 */
		public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
		{
			return grid.addRowRenderHandler(handler);
		}

		/**
		 * Recupera colunas definidas para o dispositivo Large (DEFAULT)
		 * @return
		 */
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			return columnDefinitions.getLargeColumnDefinitions();
		}


		/**
		 * Recupera widget associada ao grid
		 * @param id
		 * @return Widget
		 */
		public abstract Widget getActionWidget(String key);
	}

	static class GridSmallImpl extends GridImpl
	{
		HashMap<String, Button> actionsWidgets = new HashMap<String, Button>();

		@Override
		//CHECKSTYLE:OFF
		public void initGrid(DeviceAdaptiveGridColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling,
				boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
		{
			super.initGrid(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType);

			initActionColumns();
		}
		//CHECKSTYLE:ON

		/**
		 * Pré inicializa as ações disponibilidas no detalhe do grid, visão mobile
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
						throw new RuntimeException("Action columns only supports org.cruxframework.crux.smartfaces.client.button.Button");
					}

					Button button = (Button) widget;
					actionsWidgets.put(colDef.getKey(), button);
				}
			}
		}

		@Override
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			ColumnDefinitions smallColumnDefinitions = this.columnDefinitions.getSmallColumnDefinitions();

			smallColumnDefinitions.add("detail", new WidgetColumnDefinition("", "5%", new WidgetColumnCreator()
			{
				public Widget createWidgetForColumn()
				{
					final Button detailButton =  new Button();
					detailButton.setStyleName("detailImage");

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

		@Override
		public Widget getActionWidget(String key)
		{
			return actionsWidgets.get(key);
		}

	static class DetailDialogBox extends com.google.gwt.user.client.ui.DialogBox
	{
		private final DataRow row;

		public DetailDialogBox(HashMap<String, Button> actionWidgets, DeviceAdaptiveGridColumnDefinitions columnDefinitions, DataRow row)
		{

			super(false, true, new CloseButtonCaption(""));
			this.row = row;

			setStyleName("grid-detail-dialogbox");

		    CloseButtonCaption ref = (CloseButtonCaption) this.getCaption();
		    PushButton closeButton = ref.getCloseButton();
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

		@Override
		public void show()
		{
			super.show();
			super.center();
		}

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
		@Override
		protected ColumnDefinitions getGridColumnDefinitionsByDevice()
		{
			return this.columnDefinitions.getLargeColumnDefinitions();
		}

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
	   * @return the button at caption
	   */
	  public PushButton getCloseButton()
	  {
	    return closeDialog;
	  }
	  public CloseButtonCaption( String label )
	  {
	    super();
	    setWidth( "100%" );
	    setStyleName("caption");
	    closeDialog = new PushButton("X");
	    closeDialog.setStyleName("close");
	    text = new InlineLabel(label);
	    add(text);
	    add(closeDialog);
	    setCellWidth( closeDialog, "1px" );
	  }
	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.dom.client.MouseDownHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseDownHandler( MouseDownHandler handler )
	  {
	    return addMouseDownHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.client.MouseUpHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseUpHandler( MouseUpHandler handler )
	  {
	    return addMouseUpHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseOutHandler( MouseOutHandler handler )
	  {
	    return addMouseOutHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseOverHandler( MouseOverHandler handler )
	  {
	    return addMouseOverHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseMoveHandler( MouseMoveHandler handler )
	  {
	    return addMouseMoveHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
	   */
	  @Override
	  public HandlerRegistration addMouseWheelHandler( MouseWheelHandler handler )
	  {
	    return addMouseWheelHandler( handler );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.user.client.ui.HasHTML#getHTML()
	   */
	  @Override
	  public String getHTML()
	  {
	    return getElement().getInnerHTML();
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.user.client.ui.HasHTML#setHTML(java.lang.String)
	   */
	  @Override
	  public void setHTML( String html )
	  {
	    remove( text );
	    insert( text, 1 );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.user.client.ui.HasText#getText()
	   */
	  @Override
	  public String getText()
	  {
	    return text.getText();
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	   */
	  @Override
	  public void setText( String text )
	  {
	    this.text.setText( text );
	  }

	  /* (non-Javadoc)
	   * @see com.google.gwt.safehtml.client.HasSafeHtml#setHTML(com.google.gwt.safehtml.shared.SafeHtml)
	   */
	  @Override
	  public void setHTML( SafeHtml html )
	  {
	    setHTML( html.asString() );
	  }
	}
}
