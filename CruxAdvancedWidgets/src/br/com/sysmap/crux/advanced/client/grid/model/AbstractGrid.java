package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.advanced.client.event.row.HasRowClickHandlers;
import br.com.sysmap.crux.advanced.client.event.row.HasRowDoubleClickHandlers;
import br.com.sysmap.crux.advanced.client.event.row.HasRowRenderHandlers;
import br.com.sysmap.crux.advanced.client.event.row.RowClickEvent;
import br.com.sysmap.crux.advanced.client.event.row.RowClickHandler;
import br.com.sysmap.crux.advanced.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.advanced.client.event.row.RowDoubleClickHandler;
import br.com.sysmap.crux.advanced.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.advanced.client.event.row.RowRenderHandler;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grid widgets. All subclasses of this class must invoke the method <code>render()</code> in their constructors.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractGrid<C extends ColumnDefinition, R extends Row> extends Composite implements HasRowClickHandlers, HasRowDoubleClickHandlers, HasRowRenderHandlers {	
	
	private SimplePanel panel;
	private GridHtmlTable table;
	private ColumnDefinitions<C> definitions;
	private String generatedId =  "cruxGrid_" + new Date().getTime();
	private GridLayout gridLayout = GWT.create(GridLayout.class);
	private List<R> rows = new ArrayList<R>();
	private RowSelectionModel rowSelection;
	private ScrollPanel scrollingArea;
	private int visibleColumnCount = -1;
	
	/**
	 * Constructor
	 * @param columnDefinitions
	 */
	public AbstractGrid(ColumnDefinitions<C> columnDefinitions, RowSelectionModel rowSelection, int cellSpacing)
	{
		this.definitions = columnDefinitions;
		this.rowSelection = rowSelection;
		
		panel = new SimplePanel();
		panel.setStyleName("crux-Grid");
		panel.setWidth("100%");
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setHeight("1");
		scrollingArea.setWidth("1");
		panel.add(scrollingArea);
				
		initWidget(panel);
		
		table = new GridHtmlTable();
		table.setCellSpacing(cellSpacing);
		table.setCellPadding(0);
		gridLayout.adjustToBrowser(scrollingArea, table);
		
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{	
				resizeToFit(scrollingArea, panel);
				scrollingArea.add(table);
			}
		});
		
		Screen.addResizeHandler(new ResizeHandler(){
			public void onResize(ResizeEvent event)
			{
				resizeToFit(scrollingArea, panel);				
			}
		});
	}
	
	/**
	 * Resizes a widget so that it fills all available space from its parent
	 * @param widget
	 */
	private void resizeToFit(final Widget widget, final Panel parent)
	{
		final Element elem = parent.getElement();
		
		String borderLeft = elem.getStyle().getProperty("borderLeft");
		elem.getStyle().setProperty("borderLeft", "solid 1px #FFFFFF");
		
		parent.clear();
		
		int width = 0;
		int height = 0;
		
		if(elem != null)
		{
			width = elem.getClientWidth();
			height = elem.getClientHeight();
		}
		
		final int finalWidth = width;
		final int finalHeight = height;
		
		elem.getStyle().setProperty("borderLeft", borderLeft);
		
		DeferredCommand.addCommand(new Command(){

			public void execute()
			{
				if(finalWidth > 0)
				{
					widget.setWidth("" + finalWidth);
				}
				else
				{
					widget.setWidth("100%");
				}
				
				if(finalHeight > 0)
				{
					widget.setHeight("" + finalHeight);
				}
				else
				{
					widget.setHeight("100%");
				}
				
				parent.clear();
				parent.add(widget);			
			}
		});		
	}
	
	protected final void clearAndRender()
	{
		clear();		
		render();
	}

	protected void render()
	{
		int rowCount = getRowsToBeRendered() + 1;
		
		clearRendering();
			
		for (int i = 0; i < rowCount; i++)
		{
			R row = createRow(i, table.getRowElement(i));
			row.setStyle("row");
			rows.add(row);
		}
		
		renderHeaders();
		renderRows();
	}

	private void clearRendering()
	{
		int rowCount = getRowsToBeRendered() + 1;
		table.resize(rowCount, getVisibleColumnCount() + (hasSelectionColumn() ? 1 : 0));
		this.rows = new ArrayList<R>();
		onClearRendering();
	}

	private int getVisibleColumnCount()
	{
		if(this.visibleColumnCount == -1)
		{
			this.visibleColumnCount = 0;
			
			for (ColumnDefinition def : definitions.getDefinitions())
			{
				if(def.isVisible())
				{
					this.visibleColumnCount++;
				}
			}
		}
		
		return this.visibleColumnCount ;		
	}

	private void renderRows()
	{
		Iterator<R> it = getRowIterator();
		
		if(it.hasNext())
		{
			onBeforeRenderRows();
		}
		
		while(it.hasNext())
		{
			R row = it.next();
			
			if(hasSelectionColumn())
			{
				row.setCell(createSelectionCell(row), 0);
			}
			
			renderRow(row);
			
			fireRowRenderEvent(row);
		}
	}

	private Cell createSelectionCell(R row)
	{
		Widget w = null;
		
		if(RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelection))
		{
			CheckBox checkBox = new CheckBox();
			checkBox.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = checkBox;
		}
		else if(RowSelectionModel.SINGLE_WITH_RADIO.equals(rowSelection))
		{
			RadioButton radio = new RadioButton(generatedId + "_selector");
			radio.addClickHandler(new RowSelectionHandler<R>(this, row));
			w = radio;
		}
		
		Cell cell = createCell(w);
		cell.addStyleName("rowSelector");
		
		return cell;
	}

	protected Iterator<R> getRowIterator()
	{
		Iterator<R> it = new Iterator<R>(){

			int position = 1;
			
			public boolean hasNext()
			{
				return position < rows.size();
			}

			public R next()
			{
				if(hasNext())
				{
					return rows.get(position++);
				}
				else
				{
					return null;
				}
			}

			public void remove()
			{
				throw new RuntimeException(); // TODO - set message here				
			}			
		};
		
		return it;
	}

	private void renderHeaders()
	{
		R row = rows.get(0);
		
		row.setStyle("columnHeadersRow row");
		row.setCell(getHeaderFristColumnCell(), 0);
				
		List<C> columns = definitions.getDefinitions();
		for (C columnDefinition : columns)
		{
			if(columnDefinition.isVisible())
			{
				Cell cell = createColumnHeaderCell(columnDefinition);
				row.setCell(cell, columnDefinition.getKey());
			}
		}
	}

	protected Cell createColumnHeaderCell(C columnDefinition)
	{
		String label = columnDefinition.getLabel();
		Label columnHeader = new Label(label);
		Cell cell = createHeaderCell(columnHeader);
		return cell;
	}
	
	protected Cell createCell(Widget widget)
	{
		Cell cell = createBaseCell(widget, true, selectRowOnClickCell());
		cell.addStyleName("cell");
		return cell;
	}
	
	/**
	 * @return
	 */
	private boolean selectRowOnClickCell()
	{
		return RowSelectionModel.SINGLE.equals(rowSelection) || RowSelectionModel.MULTIPLE.equals(rowSelection);
	}

	private Cell getHeaderFristColumnCell()
	{
		Widget w = null;
		
		if(hasSelectionColumn())
		{
			if(RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelection))
			{
				CheckBox checkBox = new CheckBox();
				checkBox.addClickHandler(createSelectAllRowsClickHandler());
				w = checkBox;
			}	
		}
		
		if(w == null)
		{
			w = new Label(" ");
		}
		
		Cell cell = createHeaderCell(w);
		cell.setHeight("100%");
		return cell;
	}
	
	/**
	 * @return
	 */
	private ClickHandler createSelectAllRowsClickHandler()
	{
		return new ClickHandler()
		{
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event)
			{
				HasValue<Boolean> source = (HasValue<Boolean>) event.getSource();
				boolean select = source.getValue();
				selectAllRows(select);
			}
		};
	}

	private void selectAllRows(boolean select) 
	{
		Iterator<R> it = getRowIterator();
		
		while(it.hasNext())
		{
			R row = it.next();
			
			if(RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelection))
			{
				((CheckBox) row.getCell(0).getCellWidget()).setValue(select);
			}
			
			if(!RowSelectionModel.UNSELECTABLE.equals(rowSelection))
			{
				onSelectRow(select, row);
			}
		}
	}

	/**
	 * Creates a header styled cell
	 * @param width
	 * @param widget
	 * @return
	 */
	protected Cell createHeaderCell(Widget widget)
	{
		Cell cell = createBaseCell(widget, false, false);
		cell.addStyleName("columnHeader");
		return cell;
	}
	
	/**
	 * Creates a cell with an widget to be inserted in table
	 * @param widget
	 * @return
	 */
	protected Cell createBaseCell(Widget widget, boolean fireEvents, boolean selectRowOnclick)
	{
		Cell cell = null;
		
		if(widget != null)
		{
			cell = new Cell(widget, fireEvents, selectRowOnclick);
		}
		else
		{
			cell = new Cell(fireEvents, selectRowOnclick);
		}
		
		cell.setWidth("100%");
			
		return cell;
	}

	/**
	 * Empties the grid
	 */
	public void clear()
	{
		table.resizeRows(0);
		rows = new ArrayList<R>();
		onClear();
	}
	
	protected boolean hasSelectionColumn()
	{
		return RowSelectionModel.MULTIPLE_WITH_CHECKBOX.equals(rowSelection) || RowSelectionModel.SINGLE_WITH_RADIO.equals(rowSelection);
	}
	
	GridHtmlTable getTable()
	{
		return table;
	}

	protected abstract void onClear();
	
	protected abstract int getRowsToBeRendered();
	
	protected abstract void onSelectRow(boolean select, R row);
	
	protected abstract void renderRow(R row);
	
	protected abstract R createRow(int index, Element element);
	
	protected abstract void onBeforeRenderRows();
	
	protected abstract void onClearRendering();
	
	protected ColumnDefinitions<C> getColumnDefinitions()
	{
		return definitions;
	}
	
	@SuppressWarnings("unchecked")
	static class RowSelectionHandler<R extends Row> implements ClickHandler
	{
		private AbstractGrid<?, R> grid;
		private R row;
		
		public RowSelectionHandler(AbstractGrid<?, R> grid, R row)
		{
			this.grid = grid;
			this.row = row;
		}
		
		public void onClick(ClickEvent event)
		{
			boolean selected = ((HasValue<Boolean>) event.getSource()).getValue();
			row.markAsSelected(selected);
			grid.onSelectRow(selected, row);
			event.stopPropagation();
		}	
	}
	
	public HandlerRegistration addRowClickHandler(RowClickHandler handler)
	{
		return addHandler(handler, RowClickEvent.getType());		
	}

	public HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler handler)
	{
		return addHandler(handler, RowDoubleClickEvent.getType());
	}
	
	public HandlerRegistration addRowRenderHandler(RowRenderHandler handler)
	{
		return addHandler(handler, RowRenderEvent.getType());
	}
	
	void fireRowDoubleClickEvent(R row)
	{
		RowDoubleClickEvent.fire(this, row);
	}
	
	void fireRowClickEvent(R row)
	{
		RowClickEvent.fire(this, row);
	}

	void fireRowRenderEvent(R row)
	{
		RowRenderEvent.fire(this, row);
	}
	
	public abstract List<R> getSelectedRows(); 
}