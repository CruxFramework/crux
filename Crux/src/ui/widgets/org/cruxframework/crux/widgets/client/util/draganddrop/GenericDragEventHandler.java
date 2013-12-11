package org.cruxframework.crux.widgets.client.util.draganddrop;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A generic handler which captures mouse events, translates them into drag events and invokes actions associated to them.  
 * @author Gesse Dafe
 */
public class GenericDragEventHandler implements MouseDownHandler, MouseUpHandler, MouseMoveHandler
{
	private DragAction<?> action;
	
	private int dragStartX;
	private int dragStartY;
	private int clientLeft;
	private int clientTop;
	private int windowWidth;
	private boolean dragging;

	/**
	 * @param action
	 */
	public GenericDragEventHandler(DragAction<?> action)
	{
		this.action = action;
	}

	/**
	 * Apply this handler to a given draggable
	 * @param draggable
	 */
	public void applyTo(Draggable<?> draggable)
	{
		draggable.getKnob(action.getFeature()).addMouseDownHandler(this);
		draggable.getKnob(action.getFeature()).addMouseMoveHandler(this);
		draggable.getKnob(action.getFeature()).addMouseUpHandler(this);			
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		if (DOM.getCaptureElement() == null)
		{
			windowWidth = Window.getClientWidth();
			clientLeft = Document.get().getBodyOffsetLeft();
			clientTop = Document.get().getBodyOffsetTop();
			
			dragging = true;

			DOM.setCapture(action.getDraggable().getKnob(action.getFeature()).asWidget().getElement());

			dragStartX = event.getClientX();
			dragStartY = event.getClientY();

			action.onStartDrag();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		if(dragging)
		{
			int x = event.getClientX();
			int y = event.getClientY();
			
			if (x < clientLeft || x >= windowWidth || y < clientTop)
			{
				return;
			}
			
			action.onDrag(x, y, dragStartX, dragStartY);
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		dragging = false;
		DOM.releaseCapture(action.getDraggable().getKnob(action.getFeature()).asWidget().getElement());
	}
	
	/**
	 * The contract to implement the logic associated to a drag action
	 * @author Gesse Dafe
	 */
	public static abstract class DragAction<D extends Draggable<?>>
	{
		private D draggable;

		/**
		 * @param draggable
		 * @param move 
		 */
		public DragAction(D draggable)
		{
			this.draggable = draggable;
		}
		
		/**
		 * Action to me taken when drag starts
		 */
		public abstract void onStartDrag();
		
		/**
		 * Gets the feature associated to this action
		 * @return
		 */
		public abstract DragAndDropFeature getFeature();
		
		/**
		 * Action to me taken when drag happens
		 * @param x
		 * @param y
		 * @param dragStartX
		 * @param dragStartY
		 */
		public abstract void onDrag(int x, int y, int dragStartX, int dragStartY);

		/**
		 * @return
		 */
		public D getDraggable()
		{
			return draggable;
		}
	}
	
	/**
	 * The contract to implement a widget which can be resized  
	 * @author Gesse Dafe
	 */
	public static interface Draggable<K extends IsWidget & HasAllMouseHandlers>
	{
		public K getKnob(DragAndDropFeature feature);
	}
	
	/**
	 * Features offered by existing drag and drop capabilities 
	 * @author Gesse Dafe
	 *
	 */
	public static enum DragAndDropFeature
	{
		MOVE,
		RESIZE
	}
}