package br.com.sysmap.crux.advanced.client.grid.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for implementing grid widgets. All subclasses of this class must invoke the method <code>render()</code> in their constructors.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractGrid<C extends ColumnDefinition, R extends Row> extends Composite {	
	
	private SimplePanel panel;
	private GridHtmlTable table;
	private ColumnDefinitions<C> definitions;
	private String generatedId =  "cruxGrid_" + new Date().getTime();
	private GridLayout gridLayout = GWT.create(GridLayout.class);
	private List<R> rows = new ArrayList<R>();
	private RowSelectionModel rowSelection;
	private ScrollPanel scrollingArea;
	
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
		
		scrollingArea = new ScrollPanel();
		scrollingArea.setHeight("1");
		scrollingArea.setWidth("1");
		
		panel.add(scrollingArea);
				
		initWidget(panel);
		
		table = new GridHtmlTable();
		table.setCellSpacing(cellSpacing);
		table.setCellPadding(0);
		gridLayout.setTableLayout(table);
		
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{	
				resize();			
				scrollingArea.add(table);
			}
		});
	}
	
	private void resize()
	{
		Element elem = scrollingArea.getElement().getParentElement();
		if(elem != null)
		{
			int width = elem.getClientWidth();
			int height = elem.getClientHeight();
			scrollingArea.setWidth("" + width);
			scrollingArea.setHeight("" + height);
		}
		else
		{
			scrollingArea.setWidth("100%");
			scrollingArea.setHeight("100%");
		}
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
			rows.add(row);
		}
		
		renderHeaders();
		renderRows();
	}

	private void clearRendering()
	{
		int rowCount = getRowsToBeRendered() + 1;
		table.resize(rowCount, definitions.getDefinitions().size() + 1);
		this.rows = new ArrayList<R>();
		onClearRendering();
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
			radio.addValueChangeHandler(new RowSelectionHandler<R>(this, row));
			w = radio;
		}
		
		return createCell("", w);
	}

	private Iterator<R> getRowIterator()
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
		Cell cell = createHeaderCell(columnDefinition.getWidth(), columnHeader);
		return cell;
	}
	
	protected Cell createCell(String width, Widget widget)
	{
		Cell cell = createBaseCell(width, widget);
		cell.addStyleName("cell");
		return cell;
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
		
		Cell cell = createHeaderCell("", w);
		
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
	protected Cell createHeaderCell(String width, Widget widget)
	{
		Cell cell = createBaseCell(width, widget);
		cell.addStyleName("columnHeader");
		return cell;
	}
	
	/**
	 * Creates a cell with an widget to be inserted in table
	 * @param width
	 * @param widget
	 * @return
	 */
	private Cell createBaseCell(String width, Widget widget)
	{
		Cell cell = null;
		
		if(widget != null)
		{
			cell = new Cell(widget);
		}
		else
		{
			cell = new Cell();
		}
		
		cell.setWidth(width);
			
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
	static class RowSelectionHandler<R extends Row> implements ClickHandler, ValueChangeHandler<Boolean>
	{
		private AbstractGrid<?, R> grid;
		private R row;
		
		public RowSelectionHandler(AbstractGrid<?, R> grid, R row)
		{
			this.grid = grid;
		}
		
		public void onClick(ClickEvent event)
		{
			boolean selected = ((HasValue<Boolean>) event.getSource()).getValue();
			grid.onSelectRow(selected, row);
			
		}
		
		public void onValueChange(ValueChangeEvent<Boolean> event)
		{
			boolean selected = ((HasValue<Boolean>) event.getSource()).getValue();
			grid.onSelectRow(selected, row);
		}		
	}
}