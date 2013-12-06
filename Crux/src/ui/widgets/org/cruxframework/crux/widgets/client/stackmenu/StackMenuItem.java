package org.cruxframework.crux.widgets.client.stackmenu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A stack menu's item. When it contains child items, the action of clicking or 
 * pressing ENTER or SPACE BAR over it makes it show or hide its children.
 * 
 * @author Gesse Dafe
 */
@SuppressWarnings("deprecation")
public class StackMenuItem extends Composite
{
	private final String key;
	private String label;
	private boolean open = false;
	
	private List<StackMenuItem> subItems = new ArrayList<StackMenuItem>();
	private StackMenuItem parentItem;
	private StackMenu parentMenu;
	
	private VerticalPanel wrappingCanvas;
	private StackMenuItemCaption itemCaption;
	private FlowPanel subItemsCanvas;
	private Object userData;
	
	/**
	 * Constructs a menu item with the given label and a given key.
	 *
	 * @param key a textual mark useful to identify which item was selected by the user 
	 * @param label the text to be displayed on item
	 */
	public StackMenuItem(String key, String label)
	{
		this.key = key;
		this.label = label;
		this.itemCaption = new StackMenuItemCaption(label, this);
		
		this.wrappingCanvas = new VerticalPanel();
		this.wrappingCanvas.setStyleName("itemWrapper");
		this.wrappingCanvas.setWidth("100%");
		this.wrappingCanvas.getElement().getStyle().setTableLayout(TableLayout.FIXED);
		this.wrappingCanvas.add(itemCaption);
		
		this.subItemsCanvas = new FlowPanel();
		this.subItemsCanvas.setStyleName("subItemsWrapper");
		this.subItemsCanvas.setWidth("100%");
		this.wrappingCanvas.add(subItemsCanvas);
		this.wrappingCanvas.setCellVerticalAlignment(subItemsCanvas, HasVerticalAlignment.ALIGN_TOP);
		
		showSubItens(false);
		initWidget(this.wrappingCanvas);
		setStyleName("crux-StackMenuItem");
		
		Accessibility.setRole(getElement(), Accessibility.ROLE_MENUITEM);
	}
	
	/**
	 * Adds a child item.
	 * @param subItem
	 */
	public void add(StackMenuItem subItem)
	{
		subItems.add(subItem);
		subItemsCanvas.add(subItem);
		itemCaption.showSubItensIndicator(true);
		subItem.setParentItem(this);
	}
	
	/**
	 * Removes a child item.
	 * @param subItem
	 */
	public void remove(StackMenuItem subItem)
	{
		subItems.remove(subItem);
		subItemsCanvas.remove(subItem);
		subItem.setParentItem(null);
		
		if(this.subItems.size() == 0)
		{
			itemCaption.showSubItensIndicator(false);
		}
	}
	
	/**
	 * If the item has children, shows or hides them. Else, fires a selection event.
	 */
	void select()
	{
		if(this.subItems.size() > 0)
		{
			setOpen(!this.open);
		}
		else
		{
			SelectionEvent.fire(this.getParentMenu(), this);
		}
	}

	/**
	 * Returns <code>true</code> if the children items are visible.
	 */
	public boolean isOpen()
	{
		return open;
	}

	/**
	 * Shows or hides the child items. 
	 * @param open
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
	 * @param label
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

	/**
	 * Shows or hides the child items.
	 * @param show
	 */
	private void showSubItens(boolean show)
	{
		// Hides the TR which contains the cell where the sub items DIV resides. It is necessary because if
		// only the sub items DIV is made hidden, an undesired space will be displayed under the item caption.
		subItemsCanvas.getElement().getParentElement().getParentElement().getStyle().setProperty("display", show ? "" : "none");
	}

	/**
	 * Returns <code>true</code> if the item has child items.
	 */
	public boolean hasChildren()
	{
		return subItems.size() > 0;
	}

	/**
	 * Sets the parent menu
	 * @param menu
	 */
	public void setParentMenu(StackMenu menu)
	{
		this.parentMenu = menu;
	}

	/**
	 * Gets the userData object associated with this menu item.
	 * @return userData
	 */
	public Object getUserData()
	{
		return userData;
	}
	
	/**
	 * Associate an userData object with this menu item.
	 * @param userData the user data object
	 */
	public void setUserData(Object userData)
	{
		this.userData = userData;
	}
	
	/**
	 * Returns the stack menu that contains this item
	 */
	public StackMenu getParentMenu()
	{
		StackMenu result = this.parentMenu;
		StackMenuItem item = this;
		
		while(result == null && item != null)
		{
			item = item.getParentItem();			
			result = item.getParentMenu(); 
		}
		
		return result;
	}

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * Changes the layout of the item if it is the first one
	 * @param first
	 */
	void setFirst(boolean first)
	{
		itemCaption.setFirst(first);
	}

	/**
	 * Changes the layout of the item if it is the last one
	 * @param last
	 */
	public void setLast(boolean last)
	{
		itemCaption.setLast(last);		
	}
	
	/**
	 * @param style
	 */
	public void setMenuTitleStyleName(String style) 
	{
		super.setStyleName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.setStyleDependentName(String, boolean)
	 * Applied to MenuTitle.
	 */
	public void setMenuTitleStyleDependentName(String styleSuffix, boolean add) 
	{
		super.setStyleDependentName(styleSuffix, add);
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem.setMenuTitleStylePrimaryName(String)
	 * Applied to MenuTitle.
	 */
	public void setMenuTitleStylePrimaryName(String style) 
	{
		super.setStylePrimaryName(style);
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem.removeMenuTitleStyleDependentName(String)
	 * Applied to MenuTitle.
	 */
	public void removeMenuTitleStyleDependentName(String styleSuffix) 
	{
		super.removeStyleDependentName(styleSuffix);
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem.removeMenuTitleStyleName(String)
	 * Applied to MenuTitle.
	 */
	public void removeMenuTitleStyleName(String style) 
	{
		super.removeStyleName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.addStyleName(String)
	 * Applied to MenuTitle.
	 */
	public void addMenuTitleStyleName(String style) 
	{
		super.addStyleName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.addStyleDependentName(String)
	 * Applied to MenuTitle.
	 */
	public void addMenuTitleStyleDependentName(String styleSuffix) 
	{
		super.addStyleDependentName(styleSuffix);
	}
	
	/**
	 * @see org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem.setMenuItemStyleName(String)
	 * Applied to MenuItem.
	 */
	public void setMenuItemStyleName(String style) 
	{
		subItemsCanvas.setStyleName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.setStyleDependentName(String, boolean)
	 * Applied to MenuItem.
	 */
	public void setMenuItemStyleDependentName(String styleSuffix, boolean add) 
	{
		subItemsCanvas.setStyleDependentName(styleSuffix, add);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.setStylePrimaryName(String)
	 * Applied to MenuItem.
	 */
	public void setMenuItemStylePrimaryName(String style) 
	{
		subItemsCanvas.setStylePrimaryName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.removeStyleDependentName(String)
	 * Applied to MenuItem.
	 */
	public void removeMenuItemStyleDependentName(String styleSuffix) 
	{
		subItemsCanvas.removeStyleDependentName(styleSuffix);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.removeStyleName(String)
	 * Applied to MenuItem.
	 */
	public void removeMenuItemStyleName(String style) 
	{
		subItemsCanvas.removeStyleName(style);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.addStyleDependentName(String)
	 * Applied to MenuItem.
	 */
	public void addMenuItemStyleDependentName(String styleSuffix) 
	{
		subItemsCanvas.addStyleDependentName(styleSuffix);
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject.addStyleName(String)
	 * Applied to MenuItem.
	 */
	public void addMenuItemStyleName(String style) 
	{
		subItemsCanvas.addStyleName(style);
	}
}