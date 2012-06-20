package org.cruxframework.crux.widgets.client.stackmenu;

import org.cruxframework.crux.widgets.client.util.TextSelectionUtils;

import com.google.gwt.dom.client.Style.TableLayout;
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
import com.google.gwt.user.client.Element;
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
	private FocusPanel widget;
	private Grid canvas;
	private SimplePanel hasSubItemsIndicator;
	private final StackMenuItem stackMenuItem;

	/**
	 * Package-protected constructor
	 * @param label
	 * @param stackMenuItem
	 */
	StackMenuItemCaption(String label, StackMenuItem stackMenuItem)
	{
		this.stackMenuItem = stackMenuItem;
		canvas = new Grid();
		canvas.setStyleName("item");
		canvas.setWidth("100%");
		canvas.resize(3, 3);
		canvas.setCellPadding(0);
		canvas.setCellSpacing(0);
		canvas.getElement().getStyle().setTableLayout(TableLayout.FIXED);

		ClickHandler clickHandler = createBaseClickHandler();

		createTopBorders();
		createBody(label, clickHandler);
		createBottomBorders();

		widget = new FocusPanel(canvas);
		widget.addClickHandler(clickHandler);
		widget.addMouseOverHandler(createMouseOverHandler());
		widget.addMouseOutHandler(createMouseOutHandler());
		widget.addKeyUpHandler(createKeyUpHandler());

		initWidget(widget);
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
	private Label createBody(String label, ClickHandler clickHandler)
	{
		canvas.setHTML(1, 0, "&nbsp;");
		canvas.getCellFormatter().setStyleName(1, 0, "item-border-w");

		Label menuItemLabel = new Label(label);
		menuItemLabel.setStyleName("itemLabel");
		TextSelectionUtils.makeUnselectable(menuItemLabel.getElement());
		menuItemLabel.addClickHandler(clickHandler);
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
	 * Changes the item's appearance when mouse is out of it
	 * @return
	 */
	private MouseOutHandler createMouseOutHandler()
	{
		return new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event)
			{
				canvas.removeStyleDependentName("over");
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
				canvas.addStyleDependentName("over");
				event.stopPropagation();
			}
		};
	}

	/**
	 * Fires the action associated with the selection of the item when user clicks it.
	 * @return
	 */
	private ClickHandler createBaseClickHandler()
	{
		return new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				fireAction(event);
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
}