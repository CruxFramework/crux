package org.cruxframework.crux.widgets.client.util.draganddrop;

import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAction;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.Draggable;

/**
 * Encloses the logic and contracts needed resize a widget when user drags a part of it
 * @author Gesse Dafe
 */
public class ResizeCapability
{
	/**
	 * Makes a widget able to be resized by dragging a part of it, usually a right bottom corner. 
	 * @param movable
	 */
	public static void addResizeCapability(Resizable resizable, int minWidth, int minHeight)
	{
		GenericDragEventHandler handler = new GenericDragEventHandler(new DragResizeAction(resizable, minWidth, minHeight));
		handler.applyTo(resizable);
	}
	
	/**
	 * The contract to implement a widget which can be resized  
	 * @author Gesse Dafe
	 */
	public static interface Resizable extends Draggable
	{
		public void setDimensions(int w, int h);
		public int getAbsoluteWidth();
		public int getAbsoluteHeight();
	}
	
	/**
	 * The logic needed to resize a widget when user drags a part of it 
	 * @author Gesse Dafe
	 */
	public static class DragResizeAction extends DragAction<Resizable> 
	{
		private int minWidth;
		private int minHeight;
		private int originalWidth;
		private int originalHeight;
		
		/**
		 * @param movable
		 * @param minWidth
		 * @param minHeight
		 */
		public DragResizeAction(Resizable movable, int minWidth, int minHeight)
		{
			super(movable);
			this.minWidth = minWidth;
			this.minHeight = minHeight;
		}
		
		@Override
		public void onStartDrag()
		{
			originalWidth = getDraggable().getAbsoluteWidth();
			originalHeight = getDraggable().getAbsoluteHeight();
		}

		@Override
		public void onDrag(int x, int y, int dragStartX, int dragStartY)
		{
			int deltaX = x - dragStartX;
			int deltaY = y - dragStartY;
			int newWidth = originalWidth + deltaX;
			int newHeight = originalHeight + deltaY;
			getDraggable().setDimensions(newWidth >= minWidth ? newWidth : minWidth, newHeight >= minHeight ? newHeight : minHeight);
		}
	}
}
