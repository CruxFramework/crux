package org.cruxframework.crux.widgets.client.stackmenu;

import org.cruxframework.crux.widgets.client.event.HasSelectHandlers;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.util.TextSelectionUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The clickable area of the menu item
 * @author Gesse Dafe
 */
class StackMenuItemCaption extends Composite
{
	private FocusAreaStackImpl widget;
	private Grid canvas;
	private SimplePanel hasSubItemsIndicator;

	/**
	 * Package-protected constructor
	 * @param label
	 * @param stackMenuItem
	 */
	StackMenuItemCaption(String label, StackMenuItem stackMenuItem)
	{
		canvas = new Grid();
		canvas.setStyleName("item");
		canvas.setWidth("100%");
		canvas.resize(3, 3);
		canvas.setCellPadding(0);
		canvas.setCellSpacing(0);
		canvas.getElement().getStyle().setTableLayout(TableLayout.FIXED);

		createTopBorders();
		createBottomBorders();
		createBody(label);

		widget = GWT.create(FocusAreaStackImpl.class); 
		widget.setWidget(canvas);
		widget.setStackMenuItem(stackMenuItem);

		initWidget(widget);
	}

	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * Focus area (clicked or touched area) implementation.
	 */
	static abstract class FocusAreaStackImpl extends FocusPanel implements HasSelectHandlers
	{
		protected boolean preventDefaultTouchEvents = false;
		protected abstract void select();
		StackMenuItem stackMenuItem;

		public HandlerRegistration addSelectHandler(SelectHandler handler)
		{
			return addHandler(handler, SelectEvent.getType());
		}

		protected void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
		{
			this.preventDefaultTouchEvents = preventDefaultTouchEvents;
		}

		public void setStackMenuItem(StackMenuItem stackMenuItem) {
			this.stackMenuItem = stackMenuItem;
		}
	}

	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * Implementation for notouch devices.
	 *
	 */
	static class NoTouchImpl extends FocusAreaStackImpl 
	{
		public NoTouchImpl()
		{
			addMouseOverHandler(createMouseOverHandler());
			addMouseOutHandler(createMouseOutHandler());
			addKeyUpHandler(createKeyUpHandler());
			addClickHandler();
		}

		protected void addClickHandler() 
		{
			addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					fireAction(event);
				}
			});
		}

		/**
		 * Changes the item's appearance when mouse is out of it
		 * @return
		 */
		private MouseOutHandler createMouseOutHandler()
		{
			return new MouseOutHandler()
			{
				public void onMouseOut(MouseOutEvent event)
				{
					getWidget().removeStyleDependentName("over");
					event.stopPropagation();
				}
			};
		}

		/**
		 * Changes the item's appearance when mouse is over it
		 * @return
		 */
		private MouseOverHandler createMouseOverHandler()
		{
			return new MouseOverHandler()
			{
				public void onMouseOver(MouseOverEvent event)
				{
					getWidget().addStyleDependentName("over");
					event.stopPropagation();
				}
			};
		}

		/**
		 * Fires the action associated with the selection of the item when user presses enter.
		 * @return
		 */
		private KeyUpHandler createKeyUpHandler()
		{
			return new KeyUpHandler()
			{
				public void onKeyUp(KeyUpEvent event)
				{
					int keyCode = event.getNativeEvent().getKeyCode();
					if(keyCode == KeyCodes.KEY_ENTER || keyCode == ' ')
					{
						fireAction(event);
					}
				}
			};
		}

		/**
		 * Cancels the native event and fires the action associated with the selection of the item
		 * @param event
		 */
		private void fireAction(DomEvent<?> event)
		{
			event.preventDefault();
			event.stopPropagation();
			stackMenuItem.select();
		}

		@Override
		protected void select()
		{
			stackMenuItem.select();
		}
	}

	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 * Implementation for touch devices.
	 */
	static class TouchImpl extends NoTouchImpl implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		private static final int TAP_EVENT_THRESHOLD = 5;
		private int startX;
		private int startY;
		private HandlerRegistration touchMoveHandler;
		private HandlerRegistration touchEndHandler;

		public TouchImpl()
		{
			if(!GWT.isProdMode())
			{
				addClickHandler();
			}
			addTouchStartHandler(this);
		}

		@Override
		protected void select()
		{
			stackMenuItem.select();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			event.stopPropagation();
			select();
			resetHandlers();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			if (Math.abs(touch.getClientX() - this.startX) > TAP_EVENT_THRESHOLD || Math.abs(touch.getClientY() - this.startY) > TAP_EVENT_THRESHOLD) 
			{
				this.resetHandlers();
			}
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			event.stopPropagation();
			if (preventDefaultTouchEvents)
			{
				event.preventDefault();
			}
			Touch touch = event.getTouches().get(0);
			startX = touch.getClientX();
			startY = touch.getClientY();
			touchMoveHandler = addTouchMoveHandler(this);
			touchEndHandler = addTouchEndHandler(this);
		}

		private void resetHandlers()
		{
			if(touchMoveHandler != null)
			{
				touchMoveHandler.removeHandler();
				touchMoveHandler = null;
			}
			if(touchEndHandler != null)
			{
				touchEndHandler.removeHandler();
				touchEndHandler = null;
			}
		}

	}

	/**
	 * Creates the top borders of the item.
	 */
	private void createTopBorders()
	{
		canvas.setHTML(0, 0, "&nbsp;");
		canvas.getCellFormatter().setStyleName(0, 0, "item-border-nw");

		canvas.setHTML(0, 1, "&nbsp;");
		canvas.getCellFormatter().setStyleName(0, 1, "item-border-n");

		canvas.setHTML(0, 2, "&nbsp;");
		canvas.getCellFormatter().setStyleName(0, 2, "item-border-ne");
	}

	/**
	 * Creates the bottom borders of the item.
	 */
	private void createBottomBorders()
	{
		canvas.setHTML(2, 0, "&nbsp;");
		canvas.getCellFormatter().setStyleName(2, 0, "item-border-sw");

		canvas.setHTML(2, 1, "&nbsp;");
		canvas.getCellFormatter().setStyleName(2, 1, "item-border-s");

		canvas.setHTML(2, 2, "&nbsp;");
		canvas.getCellFormatter().setStyleName(2, 2, "item-border-se");
	}

	/**
	 * Creates the label of the item
	 * @param label
	 * @param clickHandler
	 * @return
	 */
	private Label createBody(String label)
	{
		canvas.setHTML(1, 0, "&nbsp;");
		canvas.getCellFormatter().setStyleName(1, 0, "item-border-w");

		Label menuItemLabel = new Label(label);
		menuItemLabel.setStyleName("itemLabel");
		TextSelectionUtils.makeUnselectable(menuItemLabel.getElement());
		canvas.setWidget(1, 1, menuItemLabel);
		canvas.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_MIDDLE);

		this.hasSubItemsIndicator = createSubItemsIndicator();
		canvas.setWidget(1, 2, this.hasSubItemsIndicator);
		canvas.getCellFormatter().setStyleName(1, 2, "item-border-e");
		canvas.getCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_MIDDLE);

		return menuItemLabel;
	}

	/**
	 * Creates an icon that indicates the presence of sub items.
	 */
	private SimplePanel createSubItemsIndicator()
	{
		Label emptyLabel = new Label(" ");
		emptyLabel.getElement().getStyle().setProperty("fontSize", "0px");
		SimplePanel indicator = new SimplePanel();
		indicator.setStyleName("hasSubItems");
		indicator.add(emptyLabel);
		indicator.setVisible(false);
		return indicator;
	}

	/**
	 * Changes the appearance of the item depending on its status.
	 * @param open
	 */
	void setOpen(boolean open)
	{
		if(open)
		{
			canvas.addStyleDependentName("open");
		}
		else
		{
			canvas.removeStyleDependentName("open");
		}
	}

	/**
	 * Sets the text of the item
	 * @param label the label to set
	 */
	void setLabel(String label)
	{
		((Label) this.canvas.getWidget(1, 1)).setText(label);
	}

	/**
	 * Shows or hides the icon indicating child items
	 * @param show
	 */
	void showSubItensIndicator(boolean show)
	{
		hasSubItemsIndicator.setVisible(show);
	}

	/**
	 * Changes the layout of the item if it is the first one
	 * @param first
	 */
	void setFirst(boolean first)
	{
		canvas.removeStyleDependentName("first");
		if(first)
		{
			canvas.addStyleDependentName("first");
		}
	}

	/**
	 * Changes the layout of the item if it is the last one
	 * @param last
	 */
	public void setLast(boolean last)
	{
		canvas.removeStyleDependentName("last");
		if(last)
		{
			canvas.addStyleDependentName("last");
		}
	}

	/**
	 * @return the stackMenu wrapping canvas
	 */
	public Grid getCanvas() 
	{
		return canvas;
	}
}