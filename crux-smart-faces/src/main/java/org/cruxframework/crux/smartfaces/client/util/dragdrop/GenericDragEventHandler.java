package org.cruxframework.crux.smartfaces.client.util.dragdrop;

import org.cruxframework.crux.core.client.utils.StyleUtils;

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
	public static final String DRAGGING_STYLE = "dragging";
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
		draggable.getHandle(action.getFeature()).addMouseDownHandler(this);
		draggable.getHandle(action.getFeature()).addMouseMoveHandler(this);
		draggable.getHandle(action.getFeature()).addMouseUpHandler(this);			
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

			DOM.setCapture(action.getDraggable().getHandle(action.getFeature()).asWidget().getElement());

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
		action.onEndDrag();
		DOM.releaseCapture(action.getDraggable().getHandle(action.getFeature()).asWidget().getElement());
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
		 * Action to be taken when drag starts
		 */
		public void onStartDrag()
		{
			StyleUtils.addStyleName(Document.get().getBody(), DRAGGING_STYLE);
		}
		
		/**
		 * Gets the feature associated to this action
		 * @return
		 */
		public abstract DragAndDropFeature getFeature();
		
		/**
		 * Action to be taken when drag happens
		 * @param x
		 * @param y
		 * @param dragStartX
		 * @param dragStartY
		 */
		public abstract void onDrag(int x, int y, int dragStartX, int dragStartY);

		/**
		 * Action to be taken when drag ends
		 */
		public void onEndDrag()
		{
			StyleUtils.removeStyleName(Document.get().getBody(), DRAGGING_STYLE);
		}
		
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
		K getHandle(DragAndDropFeature feature);
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