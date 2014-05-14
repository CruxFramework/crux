/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.grid;

import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.button.Button;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class Row
{
	private AbstractGrid<?> grid;
	private int index;
	private Element elem;
	private boolean hasSelectionCell;
	private boolean selected;
	private boolean enabled;
	private boolean hasRowDetails;
	private boolean showRowDetailsIcon;
	private boolean detailsShown;
		
	protected Row(int index, Element elem, AbstractGrid<?> grid, boolean hasSelectionCell, boolean hasRowDetails, boolean showRowDetailsIcon)
	{
		this.index = index;
		this.elem = elem;
		this.hasSelectionCell = hasSelectionCell;
		this.hasRowDetails = hasRowDetails;
		this.grid = grid;
		this.showRowDetailsIcon = this.hasRowDetails && showRowDetailsIcon;
	}
	
	protected Row(int index, Element elem, AbstractGrid<?> grid, boolean hasSelectionCell)
	{
		this(index, elem, grid, hasSelectionCell, false, false);
	}
	
	protected void setCell(Cell cell, String column)
	{
		ColumnDefinition def = grid.getColumnDefinitions().getDefinition(column);
		int colIndex = getColumnIndex(column, false);
		
		if(def.getWidth() != null)
		{
			grid.getTable().setCellWidth(index, colIndex, def.getWidth());
		}	
		
		setCell(cell, colIndex);	
		grid.getTable().setCellAlignment(index, colIndex, def.getHorizontalAlign(), def.getVerticalAlign());
	}

	void setCell(Cell cell, int column)
	{
		grid.getTable().setWidget(index, column, cell);
		StyleUtils.addStyleName(grid.getTable().getCellElement(index, column), "cellWrapper");
		cell.setRow(this);
		cell.setGrid(grid);
		
		Widget cellWidget = cell.getCellWidget();
		if(cellWidget != null)
		{
			grid.registerWidget(cellWidget, this);
		}
	}
	
	Cell getCell(int column)
	{
		return (Cell) grid.getTable().getWidget(index, column);
	}
	
	protected Cell getCell(String column)
	{
		int colIndex = getColumnIndex(column, false);
		return (Cell) grid.getTable().getWidget(index, colIndex);
	}
	
	/**
	 * Adds a style name to the row
	 * @param rowIndex
	 */
	public void addStyleDependentName(String styleSuffix)
	{
		StyleUtils.addStyleDependentName(elem, styleSuffix);
	}
	
	/**
	 * Gets the cell internal widget  
	 * @param column
	 * @return
	 */
	public Widget getWidget(String column)
	{
		int colIndex = getColumnIndex(column, false);
		Cell cell = (Cell) grid.getTable().getWidget(index, colIndex);
		return cell.getCellWidget();
	}
	
	/**
	 * Adds a style name to the row
	 * @param rowIndex
	 */
	public void removeStyleDependentName(String styleSuffix)
	{
		StyleUtils.removeStyleDependentName(elem, styleSuffix);
	}
	
	/**
	 * Sets the style name of the row
	 * @param rowIndex
	 */
	public void setStyle(String styleName)
	{
		elem.setClassName(styleName);
	}
	
	@SuppressWarnings("unchecked")
	public void setSelected(boolean selected)
	{
		if(hasSelectionCell)
		{
			HasValue<Boolean> selector = (HasValue<Boolean>) getCell(0).getCellWidget();
			selector.setValue(selected);
		}
		
		if(selected)
		{
			addStyleDependentName("selected");
		}
		else
		{
			removeStyleDependentName("selected");
		}
		
		this.selected = selected;
	}
	
	private int getColumnIndex(String column, boolean considerInvisibleColumns)
	{
		int colIndex = grid.getColumnDefinitions().getColumnIndex(column, considerInvisibleColumns);
		
		if(hasSelectionCell)
		{
			colIndex++;
		}
		
		if(showRowDetailsIcon)
		{
			colIndex++;
		}
		
		return colIndex;
	}

	/**
	 * @return the selected
	 */
	protected boolean isSelected()
	{
		return selected;
	}

	/**
	 * @return the grid
	 */
	protected AbstractGrid<?> getGrid()
	{
		return grid;
	}
	
	public void setVisible(boolean visible)
	{
		elem.getStyle().setProperty("display", visible ? "" : "none");
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	
	/**
	 * Shows or hides the TR element below the current row, 
	 *   where the details will be attached
	 *   
	 * @param show
	 */
	void showDetailsArea(boolean show)
	{
		if(hasRowDetails)
		{
			if(show)
			{
				grid.getTable().getRowElement(index + 1).getStyle().setOverflow(Overflow.VISIBLE);
				grid.getTable().getRowElement(index + 1).getStyle().setProperty("height", "auto");
				grid.getTable().getRowElement(index + 1).getStyle().setOpacity(1);
			}
			else
			{
				grid.getTable().getRowElement(index + 1).getStyle().setOverflow(Overflow.HIDDEN);
				grid.getTable().getRowElement(index + 1).getStyle().setHeight(1, Unit.PX);
				grid.getTable().getRowElement(index + 1).getStyle().setOpacity(0.01);
			}
			
			this.detailsShown = show;
		}
	}
	
	/**
	 * Shows the details of this row.
	 * 
	 * @param show
	 * @param fireEvents
	 */
	public void showDetails(boolean show, boolean fireEvents)
	{
		grid.onShowDetails(show, this, fireEvents);
	}
	
	/**
	 * @return <code>true</code> if the details of the row are shown
	 */
	public boolean isDetailsShown()
	{
		return this.detailsShown;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		if(hasSelectionCell)
		{
			CheckBox selector = (CheckBox) getCell(0).getCellWidget();
			selector.setEnabled(enabled);
		}
		
		if(showRowDetailsIcon)
		{
			Button button = (Button) getCell(hasSelectionCell ? 1 : 0).getCellWidget();
			button.setEnabled(enabled);
		}
		
		if(!enabled)
		{
			addStyleDependentName("disabled");
		}
		else
		{
			removeStyleDependentName("disabled");
		}
		
		this.enabled = enabled;
	}
	
	/**
	 * @return The panel on which the row's details are attached 
	 */
	public RowDetailsPanel getDetailsPanel() 
	{
		return (RowDetailsPanel) grid.getTable().getWidget(index + 1, 0);
	}
	
	/**
	 * @return The icon which collapses or expands the row's details
	 */
	public Button getRowDetailsIcon()
	{
		Button btn = null; 
		
		if(showRowDetailsIcon)
		{
			btn = (Button) getCell(hasSelectionCell ? 1 : 0).getCellWidget();
		}
		
		return btn;
	}
	
	/**
	 * Attaches the details to the TR element above the row.
	 * 
	 * @param details
	 */
	void attachDetails(RowDetailsPanel details) 
	{
		if(details != null)
		{
			grid.getTable().setWidget(index + 1, 0, details);
		}
	}
}