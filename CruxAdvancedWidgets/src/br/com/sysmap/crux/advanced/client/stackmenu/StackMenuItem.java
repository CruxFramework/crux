package br.com.sysmap.crux.advanced.client.stackmenu;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.advanced.client.util.TextSelectionUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class StackMenuItem extends Composite implements HasClickHandlers
{
	private String DEFAULT_STYLE_NAME = "itemWrapper";
	
	private String label;
	private boolean open = false;
	private boolean hasAction = false;	
	
	private StackMenuItem parentItem;
	
	private VerticalPanel wrappingCanvas;
	private FlowPanel subItemsCanvas;
	private List<StackMenuItem> subItems = new ArrayList<StackMenuItem>();
	private HandlerRegistration clickHandlerRegistration; 
	
	private StackMenuItemCaption itemCaption;
	
	/**
	 * @param label
	 */
	public StackMenuItem(String label)
	{
		this.label = label;		
		itemCaption = new StackMenuItemCaption(label);
		
		// item wrapping box
		wrappingCanvas = new VerticalPanel();
		wrappingCanvas.setStyleName(DEFAULT_STYLE_NAME);
		wrappingCanvas.add(itemCaption);
		
		// sub items wrapping box
		subItemsCanvas = new FlowPanel();
		subItemsCanvas.setStyleName("subItemsWrapper");
		
		wrappingCanvas.add(subItemsCanvas);
		wrappingCanvas.setCellVerticalAlignment(subItemsCanvas, HasVerticalAlignment.ALIGN_TOP);
		
		showSubItens(false);
		
		initWidget(wrappingCanvas);
	}

	/**
	 * @param wrappingCanvas2
	 * @param subItemsCanvas2
	 * @param b
	 */
	private void showSubItens(boolean show)
	{
		// Hides the TR which contains the cell where the sub items DIV resides.
		// If only the sub items DIV is hidden, an undesired space will be displayed under the item caption.
		subItemsCanvas.getElement().getParentElement().getParentElement().getStyle().setProperty("display", show ? "" : "none");
	}

	/**
	 * @param subItem
	 */
	public void add(StackMenuItem subItem)
	{
		setDefaultClickHandler();
		subItems.add(subItem);
		subItemsCanvas.add(subItem);
		itemCaption.showSubItensIndicator(true);
		subItem.setParentItem(this);
	}

	/**
	 * 
	 */
	private void setDefaultClickHandler()
	{
		final StackMenuItem item = this;
		
		if(subItems.size() == 0)
		{
			if(this.clickHandlerRegistration != null)
			{
				this.clickHandlerRegistration.removeHandler();
			}
		
			this.clickHandlerRegistration = itemCaption.addClickHandler(
				new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						item.setOpen(!item.isOpen());
					}				
				}
			);
			
			this.hasAction = false;
		}
	}
	
	/**
	 * @param subItem
	 */
	public void remove(StackMenuItem subItem)
	{
		subItems.remove(subItem);
		subItemsCanvas.remove(subItem);
		setDefaultClickHandler();
		subItem.setParentItem(null);
		
		if(this.subItems.size() == 0)
		{
			itemCaption.showSubItensIndicator(false);
		}
	}
	
	/**
	 * 
	 */
	public void click()
	{
		if(this.hasAction && this.clickHandlerRegistration != null)
		{
			itemCaption.click();
		}
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		this.setOpen(false);
		this.subItemsCanvas.clear();
		
		if(this.clickHandlerRegistration != null)
		{
			this.clickHandlerRegistration.removeHandler();
		}
		
		this.clickHandlerRegistration = itemCaption.addClickHandler(handler);

		this.hasAction = true;
		
		return this.clickHandlerRegistration;
	}
	
	/**
	 * @return
	 */
	public boolean hasAction()
	{
		return hasAction;
	}

	/**
	 * @return the open
	 */
	public boolean isOpen()
	{
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open)
	{
		this.open = open;
		
		if(open)
		{
			itemCaption.setOpen(true);
			showSubItens(true);
		}
		else
		{
			itemCaption.setOpen(false);
			showSubItens(false);				
		}		
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
		itemCaption.setLabel(label);		
	}

	/**
	 * @return the subItems
	 */
	public List<StackMenuItem> getSubItems()
	{
		return subItems;
	}

	/**
	 * @return the parent
	 */
	public StackMenuItem getParentItem()
	{
		return parentItem;
	}

	/**
	 * @param parent the parent to set
	 */
	private void setParentItem(StackMenuItem parentItem)
	{
		this.parentItem = parentItem;
	}
}

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class StackMenuItemCaption extends Composite implements HasClickHandlers
{
	private FocusPanel widget;
	
	private HorizontalPanel canvas;
	private SimplePanel hasSubItemsIndicator;
	
	public StackMenuItemCaption(String label)
	{
		// sub items indicator
		Label emptyLabel = new Label(" ");
		emptyLabel.getElement().getStyle().setProperty("fontSize", "0px");		
		hasSubItemsIndicator = new SimplePanel();
		hasSubItemsIndicator.setStyleName("hasSubItems");
		hasSubItemsIndicator.add(emptyLabel);
		hasSubItemsIndicator.setVisible(false);
		
		// item label
		Label menuItemLabel = new Label(label);
		menuItemLabel.setStyleName("itemLabel");
		TextSelectionUtils.makeUnselectable(menuItemLabel.getElement());
		
		// item box
		canvas = new HorizontalPanel();
		canvas.setStyleName("item");
		canvas.add(menuItemLabel);
		canvas.setCellVerticalAlignment(menuItemLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		canvas.add(hasSubItemsIndicator);
		canvas.setCellHorizontalAlignment(hasSubItemsIndicator, HasHorizontalAlignment.ALIGN_RIGHT);
		canvas.setCellVerticalAlignment(hasSubItemsIndicator, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// click event target
		widget = new FocusPanel(canvas);
		widget.addClickHandler(createBaseClickHandler());
		widget.addMouseOverHandler(createMouseOverHandler());
		widget.addMouseOutHandler(createMouseOutHandler());
	
		initWidget(widget);
	}

	/**
	 * @param open
	 */
	public void setOpen(boolean open)
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
	 * 
	 */
	public void click()
	{
		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		getElement().dispatchEvent(evt);
	}	
	
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		((Label) this.canvas.getWidget(0)).setText(label);		
	}
	
	/**
	 * @param show
	 */
	public void showSubItensIndicator(boolean show)
	{
		hasSubItemsIndicator.setVisible(show);		
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return widget.addClickHandler(handler);
	}
	
	/**
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
	 * @return
	 */
	private ClickHandler createBaseClickHandler()
	{
		return new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();				
			}
		};
	}
}