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
package org.cruxframework.crux.widgets.client.util.draganddrop;

import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAction;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.Draggable;

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
