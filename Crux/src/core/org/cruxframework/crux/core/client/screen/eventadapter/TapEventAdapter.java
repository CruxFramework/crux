package org.cruxframework.crux.core.client.screen.eventadapter;

import org.cruxframework.crux.core.client.Crux;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of Google FastButton {@link http://code.google.com/mobile/articles/fast_buttons.html} for
 * better performance on touch mobile devices.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse Dafe
 *
 */
public class TapEventAdapter extends Composite 
{
	private boolean touchHandled = false;
	private boolean touchMoved = false;
	private int startY;
	private int startX;
	private boolean isHasEnabled = false;
	
	public TapEventAdapter(Widget child)
	{
		assert (child instanceof HasAllTouchHandlers) : Crux.getMessages().tapEventAdapterInvalidWidget();
		assert (child instanceof HasClickHandlers) : Crux.getMessages().tapEventAdapterInvalidWidget();
		isHasEnabled = (child instanceof HasEnabled);
		initWidget(child);
		sinkEvents(Event.TOUCHEVENTS | Event.ONCLICK);
	}

	@Override
	public Widget getWidget()
	{
	    return super.getWidget();
	}
	
	@Override
	public void onBrowserEvent(Event event) 
	{
		switch (DOM.eventGetType(event)) 
		{
			case Event.ONTOUCHSTART:
			{
				onTouchStart(event);
				break;
			}
			case Event.ONTOUCHEND:
			{
				onTouchEnd(event);
				break;
			}
			case Event.ONTOUCHMOVE:
			{
				onTouchMove(event);
				break;
			}
			case Event.ONCLICK:
			{
				onClick(event);
				return;
			}
		}
		
		super.onBrowserEvent(event);
	}

	/**
	 * 
	 * @param event
	 */
	private void onClick(Event event) 
	{
		event.stopPropagation();
		
		if(touchHandled)
		{
			touchHandled = false;
			super.onBrowserEvent(event);
		}
		else
		{
			event.preventDefault();
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchEnd(Event event) 
	{
		if (!touchMoved)
		{
			touchHandled = true;
			fireClick();
		}
	}
 
	/**
	 * 
	 * @param event
	 */
	private void onTouchMove(Event event) 
	{
		if (!touchMoved)
		{
			Touch touch = event.getTouches().get(0);
			int deltaX = Math.abs(startX - touch.getClientX()); 
			int deltaY = Math.abs(startY - touch.getClientY());

			if (deltaX > 5 || deltaY > 5)
			{
				touchMoved = true;
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchStart(Event event) 
	{
		Touch touch = event.getTouches().get(0);
		this.startX = touch.getClientX();
		this.startY = touch.getClientY();		
		touchMoved = false;
	}

	/**
	 * @param executor
	 * @return
	 */
	private void fireClick() 
	{
		if (!isHasEnabled || ((HasEnabled)getWidget()).isEnabled())
		{
			NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
					false, false, false);
			getElement().dispatchEvent(evt);
		}
	}
}
