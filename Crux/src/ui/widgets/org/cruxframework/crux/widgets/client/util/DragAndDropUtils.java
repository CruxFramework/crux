package org.cruxframework.crux.widgets.client.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
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
	public static void addDragAndDropMoveBehavior(Widget targetWidget, HasAllMouseHandlers knob)
	{
		DraggingAndDropMoveBehavior feature = new DraggingAndDropMoveBehavior(knob, targetWidget);
		feature.apply();
	}
	
	/**
	 * Makes the widget being moved when dragged.
	 * @author Gesse Dafe
	 */
	public static class DraggingAndDropMoveBehavior
	{
		private Widget targetWidget;
		private HasAllMouseHandlers knob;

		private int dragStartX;
		private int dragStartY;
		
		private int originalTop;
		private int originalLeft;

		private HandlerRegistration moveHandlerRegistration;
		
		/**
		 * Instantiates the feature 
		 * @param knob
		 * @param targetWidget
		 */
		public DraggingAndDropMoveBehavior(HasAllMouseHandlers knob, Widget targetWidget)
		{
			this.knob = knob;
			this.targetWidget = targetWidget;
		}

		/**
		 * Applies the dragging move feature to the target widget
		 */
		public void apply()
		{
			final Logger logger = Logger.getLogger("dragger");
			MouseMoveHandler moveHandler = createMouseMoveHandler(logger);
			
			knob.addMouseDownHandler(createMouseDownHandler(moveHandler, logger));
			RootPanel.get().addDomHandler(createMouseUpHandler(), MouseUpEvent.getType());
		}

		/**
		 * @return
		 */
		private MouseUpHandler createMouseUpHandler() 
		{
			return new MouseUpHandler()
			{				
				@Override
				public void onMouseUp(MouseUpEvent event)
				{
					if(moveHandlerRegistration != null)
					{
						moveHandlerRegistration.removeHandler();
						moveHandlerRegistration = null;
					}
				}
			};
		}

		/**
		 * @param moveHandler 
		 * @param logger
		 * @return
		 */
		private MouseDownHandler createMouseDownHandler(final MouseMoveHandler moveHandler, final Logger logger) 
		{
			return new MouseDownHandler()
			{
				@Override
				public void onMouseDown(MouseDownEvent event)
				{
					if(moveHandlerRegistration == null)
					{
						moveHandlerRegistration = RootPanel.get().addDomHandler(moveHandler, MouseMoveEvent.getType());
					}
					
					dragStartX = event.getNativeEvent().getScreenX();
					dragStartY = event.getNativeEvent().getScreenY();
					
					originalTop = targetWidget.getElement().getOffsetTop();
					originalLeft = targetWidget.getElement().getOffsetLeft();
					
					
					logger.log(Level.WARNING, "\n\n\n========START=======");
					logger.log(Level.WARNING, "dragStartX: " + dragStartX);
					logger.log(Level.WARNING, "dragStartY: " + dragStartY);
					logger.log(Level.WARNING, "originalTop: " + originalTop);
					logger.log(Level.WARNING, "originalLeft: " + originalLeft);
					
				}
			};
		}

		/**
		 * @param logger
		 * @return
		 */
		private MouseMoveHandler createMouseMoveHandler(final Logger logger)
		{
			MouseMoveHandler handler = new MouseMoveHandler()
			{
				@Override
				public void onMouseMove(MouseMoveEvent event)
				{
					int newX = event.getNativeEvent().getScreenX();
					int newY = event.getNativeEvent().getScreenY();
					
					int deltaX = newX - dragStartX;
					int deltaY = newY - dragStartY;
					
					int newTop = originalTop + deltaY;
					int newLeft = originalLeft + deltaX;
					
					targetWidget.getElement().getStyle().setTop(newTop, Unit.PX);
					targetWidget.getElement().getStyle().setLeft(newLeft, Unit.PX);
					
					logger.log(Level.WARNING, "deltaX:" + deltaX + " deltaY:" + deltaY + " newTop:" + newTop + " newLeft:" + newLeft);
				}
			};
			
			return handler;
		}
	}
}
