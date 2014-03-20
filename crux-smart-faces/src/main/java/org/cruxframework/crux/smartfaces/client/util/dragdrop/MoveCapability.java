package org.cruxframework.crux.smartfaces.client.util.dragdrop;

import org.cruxframework.crux.smartfaces.client.util.dragdrop.GenericDragEventHandler.DragAction;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.smartfaces.client.util.dragdrop.GenericDragEventHandler.Draggable;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Encloses the logic and contracts needed to move a widget when user drags it
 * @author Gesse Dafe
 */
public class MoveCapability
{
	/**
	 * Makes a widget able to me moved by dragging it. 
	 * @param movable
	 */
	public static void addMoveCapability(Movable<?> movable)
	{
		GenericDragEventHandler handler = new GenericDragEventHandler(new DragMoveAction(movable));
		handler.applyTo(movable);
	}
	
	/**
	 * The contract to implement a widget which can be moved along the screen  
	 * @author Gesse Dafe
	 */
	public static interface Movable<K extends IsWidget & HasAllMouseHandlers> extends Draggable<K>
	{
		public void setPosition(int x, int y);
		public int getAbsoluteLeft();
		public int getAbsoluteTop();
	}
	
	/**
	 * The logic needed to move a widget when user drags it
	 * @author Gesse Dafe
	 */
	public static class DragMoveAction extends DragAction<Movable<?>>
	{
		private int originalLeft;
		private int originalTop;
		
		/**
		 * @param movable
		 * @param move 
		 */
		public DragMoveAction(Movable<?> movable)
		{
			super(movable);
		}
		
		@Override
		public void onStartDrag()
		{
			super.onStartDrag();
			originalLeft = getDraggable().getAbsoluteLeft();
			originalTop = getDraggable().getAbsoluteTop();
		}

		@Override
		public void onDrag(int x, int y, int dragStartX, int dragStartY)
		{
			getDraggable().setPosition(originalLeft + (x - dragStartX), originalTop + (y - dragStartY));
		}

		@Override
		public DragAndDropFeature getFeature()
		{
			return DragAndDropFeature.MOVE;
		}
	}
}
