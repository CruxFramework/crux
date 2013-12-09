package org.cruxframework.crux.widgets.client.util;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.user.client.ui.Widget;

/**
 * Handles the logic necessary to move widgets by drag and drop gestures.
 * 
 * @author Gesse Dafe
 * 
 */
public class DragAndDropUtils
{
	/**
	 * Makes a widget capable of being moved by dragging it 
	 * @param targetWidget
	 * @param knob
	 */
	public static void addDragAndDropMoveBehavior(Widget targetWidget, HasAllDragAndDropHandlers knob)
	{
		DraggingAndDropMoveBehavior feature = new DraggingAndDropMoveBehavior(knob, targetWidget);
		feature.apply();
	}
	
	/**
	 * Makes teh widget being moved when dragged.
	 * @author Gesse Dafe
	 */
	public static class DraggingAndDropMoveBehavior
	{
		private Widget targetWidget;
		private HasAllDragAndDropHandlers knob;

		private int dragStartX;
		private int dragStartY;
		
		private int originalTop;
		private int originalLeft;

		/**
		 * Instantiates the feature 
		 * @param knob
		 * @param targetWidget
		 */
		public DraggingAndDropMoveBehavior(HasAllDragAndDropHandlers knob, Widget targetWidget)
		{
			this.knob = knob;
			this.targetWidget = targetWidget;
		}

		/**
		 * Applies the dragging move feature to the target widget
		 */
		public void apply()
		{
			((HasAllDragAndDropHandlers) knob).addDragStartHandler(new DragStartHandler()
			{
				@Override
				public void onDragStart(DragStartEvent event)
				{
					dragStartX = event.getNativeEvent().getScreenX();
					dragStartY = event.getNativeEvent().getScreenY();
					
					originalTop = targetWidget.getElement().getOffsetTop();
					originalLeft = targetWidget.getElement().getOffsetLeft();
				}
			});
			
			((HasAllDragAndDropHandlers) knob).addDragHandler(new DragHandler()
			{
				@Override
				public void onDrag(DragEvent event)
				{
					int newX = event.getNativeEvent().getScreenX();
					int newY = event.getNativeEvent().getScreenY();
					
					int deltaX = newX - dragStartX;
					int deltaY = newY - dragStartY;
					
					targetWidget.getElement().getStyle().setTop(originalTop + deltaY, Unit.PX);
					targetWidget.getElement().getStyle().setLeft(originalLeft + deltaX, Unit.PX);
				}
			});			
		}
	}
}
