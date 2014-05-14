package org.cruxframework.crux.widgets.client.grid;

import java.util.Date;

import org.cruxframework.crux.core.client.animation.HorizontalMotionAnimation;
import org.cruxframework.crux.core.client.animation.VerticalMotionAnimation;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.executor.BeginEndExecutor;
import org.cruxframework.crux.widgets.client.grid.AbstractTablelessGridStructure.Line;
import org.cruxframework.crux.widgets.client.grid.AbstractTablelessGridStructure.Lines;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Element;

/**
 * Handles the positioning of rows and cells during scrolling
 * @author Gesse Dafe
 */
class FrozenCellsScrollHandler extends BeginEndExecutor implements ScrollHandler
{
	private static final int MAX_SCROLL_INACTIVITY_MILLIS = 200;

	private static final int LAST_FROZEN_COLUMN_INDEX_CACHE_TTL_MILLIS = 5000;

	private AbstractTablelessGridStructure tablelessStruct;
	private long lastDiscoverCall = 0;
	private int lastFrozenColumnIndex = -1;
	
	private int lastHorizontalScrollPosition = 0;
	private int lastVerticalScrollPosition = 0;
	
	/**
	 * @param grid
	 * @param lines
	 */
	public FrozenCellsScrollHandler(AbstractTablelessGridStructure tablelessStruct) 
	{
		super(MAX_SCROLL_INACTIVITY_MILLIS);
		this.tablelessStruct = tablelessStruct;
	}

	/**
	 * @see com.google.gwt.event.dom.client.ScrollHandler#onScroll(com.google.gwt.event.dom.client.ScrollEvent)
	 */
	public void onScroll(ScrollEvent event) 
	{
		super.execute();
	}
	
	@Override
	protected void doEndAction() 
	{
		int numLines = tablelessStruct.getLines().getWidgetCount();
		
		if(numLines > 0)
		{
			int vertScrollPos = tablelessStruct.getGrid().getScrollingArea().getScrollPosition();
			int horScrollPos = tablelessStruct.getGrid().getScrollingArea().getHorizontalScrollPosition();

			maybeFreezeHeaders(vertScrollPos);
			maybeFreezeColumns(horScrollPos);

			lastVerticalScrollPosition = vertScrollPos;
			lastHorizontalScrollPosition = horScrollPos;
		}
	}

	@Override
	protected void doBeginAction() 
	{
		// nothing to do
	}

	/**
	 * Freezes the grid columns that are assigned with <code>frozen=true</code>  
	 * @param newHorizontalScrollPosition
	 */
	private void maybeFreezeColumns(int newHorizontalScrollPosition) 
	{
		boolean isHorizontalScroll = newHorizontalScrollPosition != lastHorizontalScrollPosition;
		
		if(isHorizontalScroll && tablelessStruct.getGrid().hasFrozenColumns())
		{
			Lines lines = tablelessStruct.getLines();
			int numLines = lines.getWidgetCount();
			int lastFrozenCol = discoverLastFrozenColIndex(lines.getLine(0));

			for(int i = 0; i < numLines ; i++)
			{
				if(tablelessStruct.canFreezeColumns(i))
				{
					Line line = lines.getLine(i);
					
					for(int j = 0; j <= lastFrozenCol; j++)
					{
						Element cellElem = line.getCell(j).getElement();
						
						Style style = cellElem.getStyle();
						style.setZIndex(i == 0 ? 4 : 2);
						style.setOpacity(1);

						HorizontalMotionAnimation columnAnimation = new HorizontalMotionAnimation(cellElem);
						columnAnimation.move(getIntegerPixelMeasure(cellElem.getStyle().getLeft()), newHorizontalScrollPosition, 200);
					}
				}
			}
		}
	}

	private int getIntegerPixelMeasure(String measure) 
	{
		if(measure.contains("px"))
		{
			measure = measure.replace("px", "");
		}
		measure = measure.trim();
		if(measure.length() > 0)
		{
			return (int) Math.round(Double.parseDouble(measure));
		}
		
		return 0;
	}

	/**
	 * Freezes the header line if the grid is assigned with <code>freezeHeaders=true</code>
	 */
	private void maybeFreezeHeaders(int newVerticalScrollPosition) 
	{
		boolean isVerticalScroll = newVerticalScrollPosition != lastVerticalScrollPosition;
		
		if(isVerticalScroll && tablelessStruct.getGrid().hasFrozenHeaders())
		{
			Element headerLineElement = tablelessStruct.getLines().getLine(0).getElement();

			Style style =  headerLineElement.getStyle();
			style.setZIndex(3);
			style.setOpacity(1);
			
			VerticalMotionAnimation headerAnimation = new VerticalMotionAnimation(headerLineElement);
			headerAnimation.move(headerLineElement.getOffsetTop(), newVerticalScrollPosition, 200);
		}
	}

	/**
	 * Finds the last (left to right) column assigned with <code>frozen=true</code>
	 * @param headerLine
	 * @return
	 */
	private int discoverLastFrozenColIndex(Line headerLine) 
	{
		long now = new Date().getTime();
		boolean skipCache = (now - lastDiscoverCall) > LAST_FROZEN_COLUMN_INDEX_CACHE_TTL_MILLIS;

		if(skipCache)
		{
			lastDiscoverCall = now;
			
			this.lastFrozenColumnIndex = -1;
			AbstractGrid<?> grid = tablelessStruct.getGrid();
			FastList<ColumnDefinition> defs = grid.getColumnDefinitions().getDefinitions();
			int offset = (grid.hasRowDetailsIconColumn() ? 1 : 0) + (grid.hasSelectionColumn() ? 1 : 0);
			int numCols = defs.size();
			
			for(int i = 0; i < numCols; i++)
			{
				ColumnDefinition def = defs.get(i);
				if(def.isVisible() && def.isFrozen())
				{
					this.lastFrozenColumnIndex = i + offset;
				}
			}
		}
		
		return this.lastFrozenColumnIndex;
	}
}