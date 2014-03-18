package org.cruxframework.crux.cruxfaces.client.util.dragdrop;

import org.cruxframework.crux.cruxfaces.client.util.dragdrop.GenericDragEventHandler.DragAction;
import org.cruxframework.crux.cruxfaces.client.util.dragdrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.cruxfaces.client.util.dragdrop.GenericDragEventHandler.Draggable;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

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
	public static void addResizeCapability(Resizable<?> resizable, int minWidth, int minHeight)
	{
		GenericDragEventHandler handler = new GenericDragEventHandler(new DragResizeAction(resizable, minWidth, minHeight));
		handler.applyTo(resizable);
	}
	
	/**
	 * The contract to implement a widget which can be resized  
	 * @author Gesse Dafe
	 */
	public static interface Resizable<K extends IsWidget & HasAllMouseHandlers> extends Draggable<K>
	{
		public void setDimensions(int w, int h);
		public int getAbsoluteWidth();
		public int getAbsoluteHeight();
	}
	
	/**
	 * The logic needed to resize a widget when user drags a part of it 
	 * @author Gesse Dafe
	 */
	public static class DragResizeAction extends DragAction<Resizable<?>> 
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
		public DragResizeAction(Resizable<?> resizable, int minWidth, int minHeight)
		{
			super(resizable);
			this.minWidth = minWidth;
			this.minHeight = minHeight;
		}
		
		@Override
		public void onStartDrag()
		{
			super.onStartDrag();
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

		@Override
		public DragAndDropFeature getFeature()
		{
			return DragAndDropFeature.RESIZE;
		}
	}
}
