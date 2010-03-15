/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.transferlist;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.widgets.client.event.moveitem.HasBeforeMoveItemsHandlers;
import br.com.sysmap.crux.widgets.client.event.moveitem.BeforeMoveItemsEvent;
import br.com.sysmap.crux.widgets.client.event.moveitem.BeforeMoveItemsHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A decorated panel, with a title bar.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class TransferList extends Composite implements HasBeforeMoveItemsHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-TransferList" ;
	
	private HorizontalPanel panel;
	private ListBox leftList;
	private ListBox rightList;
	private Button moveToRightButton;
	private Button moveToLeftButton;
	private Label leftListLabel;
	private Label rightListLabel;
	
	/**
	 * TODO - Gessé - Comment this
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
	public static enum ItemLocation
	{
		left, right;
	}
	
	/**
	 * TODO - Gessé - Comment this
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
	public static class Item
	{
		private String label;
		private String value;
		private ItemLocation location;
		
		/**
		 * @param label
		 * @param value
		 */
		public Item(String label, String value)
		{
			this(label, value, ItemLocation.left);
		}
		
		/**
		 * @param label
		 * @param value
		 * @param location
		 */
		public Item(String label, String value, ItemLocation location)
		{
			this.label = label;
			this.value = value;
			this.location = location;
		}

		/**
		 * @return the label
		 */
		public String getLabel()
		{
			return label;
		}

		/**
		 * @return the value
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 * @return the location
		 */
		public ItemLocation getLocation()
		{
			return location;
		}
	}
	
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 */
	public TransferList()
	{
		super();
		this.panel = createPanelCells();
		initWidget(panel);
	}

	/**
	 * @param panel2
	 */
	private HorizontalPanel createPanelCells()
	{
		HorizontalPanel panel = new HorizontalPanel();
		panel.setStyleName(DEFAULT_STYLE_NAME);
		
		VerticalPanel vPanelLeft = new VerticalPanel();
		this.leftListLabel = new Label();
		this.leftList = new ListBox(true);
		this.leftList.setStyleName("leftList");
		vPanelLeft.add(this.leftListLabel);
		vPanelLeft.add(this.leftList);
		panel.add(vPanelLeft);
		
		VerticalPanel commandsPanel = createCommands();
		panel.add(commandsPanel);
		panel.setCellVerticalAlignment(commandsPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		VerticalPanel vPanelRight = new VerticalPanel();
		this.rightListLabel = new Label();
		this.rightList = new ListBox(true);
		this.rightList.setStyleName("rightList");
		vPanelRight.add(this.rightListLabel);
		vPanelRight.add(this.rightList);
		panel.add(vPanelRight);
		
		return panel;
	}

	/**
	 * @return
	 */
	private VerticalPanel createCommands()
	{
		VerticalPanel commandsPanel = new VerticalPanel();
		commandsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		commandsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		commandsPanel.setStyleName("commands");
		commandsPanel.setSpacing(5);
		
		this.moveToRightButton = new Button();
		moveToRightButton.setStyleName("moveToRight");
		moveToRightButton.addClickHandler(new TransferItemClickHandler(this, true));
		commandsPanel.add(this.moveToRightButton);
				
		this.moveToLeftButton = new Button();
		moveToLeftButton.setStyleName("moveToLeft");
		moveToLeftButton.addClickHandler(new TransferItemClickHandler(this, false));
		commandsPanel.add(this.moveToLeftButton);
		
		return commandsPanel;
	}

	/**
	 * @return the moveToRightButton
	 */
	public Button getMoveToRightButton()
	{
		return moveToRightButton;
	}

	/**
	 * @return the moveToLeftButton
	 */
	public Button getMoveToLeftButton()
	{
		return moveToLeftButton;
	}

	/**
	 * @return the leftList
	 */
	protected ListBox getLeftList()
	{
		return leftList;
	}

	/**
	 * @return the rightList
	 */
	protected ListBox getRightList()
	{
		return rightList;
	}
	
	/**
	 * @param text
	 */
	public void setLeftToRightButtonText(String text)
	{
		this.moveToRightButton.setText(text);
	}
	
	/**
	 * @param text
	 */
	public void setRightToLeftButtonText(String text)
	{
		this.moveToLeftButton.setText(text);
	}
	
	/**
	 * @param label
	 */
	public void setLeftListLabel(String label)
	{
		this.leftListLabel.setText(label);
	}
	
	/**
	 * @param label
	 */
	public void setRightListLabel(String label)
	{
		this.rightListLabel.setText(label);
	}
	
	/**
	 * Sets the items to be shown
	 */
	public void setCollection(List<Item> items)
	{
		this.leftList.clear();
		this.rightList.clear();
		
		for (Item item : items)
		{
			if(item.location.equals(ItemLocation.left))
			{
				leftList.addItem(item.getLabel(), item.getValue());
			}
			else
			{
				rightList.addItem(item.getLabel(), item.getValue());
			}
		}	
	}

	/**
	 * @param parseInt
	 */
	public void setVisibleItemCount(int count)
	{
		this.leftList.setVisibleItemCount(count);
		this.rightList.setVisibleItemCount(count);
	}
	
	/**
	 * @return
	 */
	public List<Item> getLeftItens()
	{
		return getItens(getLeftList(), true);
	}
	
	/**
	 * @return
	 */
	public List<Item> getRightItens()
	{
		return getItens(getRightList(), false);
	}
	
	/**
	 * @param listBox
	 * @param left
	 * @return
	 */
	private List<Item> getItens(ListBox listBox, boolean left)
	{
		List<Item> items = new ArrayList<Item>();
		
		for(int i = 0; i < listBox.getItemCount(); i++)
		{
			Item item = new Item(
				listBox.getItemText(i), 
				listBox.getValue(i), 
				left ? ItemLocation.left : ItemLocation.right
			);
			
			items.add(item);
		}
		
		return items;	
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.event.moveitem.HasBeforeMoveItemsHandlers#addBeforeMoveItemsHandler(br.com.sysmap.crux.widgets.client.event.moveitem.BeforeMoveItemsHandler)
	 */
	public HandlerRegistration addBeforeMoveItemsHandler(BeforeMoveItemsHandler handler)
	{
		return addHandler(handler, BeforeMoveItemsEvent.getType());
	}
	
	/**
	 * Click handler for transfer list buttons
	 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
	 */
	private static class TransferItemClickHandler implements ClickHandler
	{
		private TransferList transferList;
		private boolean leftToRight; 
		
		/**
		 * @param transfer
		 * @param leftToRight
		 */
		public TransferItemClickHandler(TransferList transfer, boolean leftToRight)
		{
			this.transferList = transfer;
			this.leftToRight = leftToRight;
		}
		
		/**
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		public void onClick(ClickEvent event)
		{
			ListBox listToRemove = leftToRight ? transferList.getLeftList() : transferList.getRightList();
			ListBox listToAdd = leftToRight ? transferList.getRightList() : transferList.getLeftList();
			
			List<String[]> move = new ArrayList<String[]>();
			List<String[]> keep = new ArrayList<String[]>();
			
			for (int i = 0; i < listToRemove.getItemCount(); i++)
			{
				String text = listToRemove.getItemText(i);
				String value = listToRemove.getValue(i);
				String[] item = new String[]{text, value};
				
				if(listToRemove.isItemSelected(i))
				{
					move.add(item);
				}
				else
				{
					keep.add(item);
				}
			}
			
			if(move.size() > 0)
			{
				ItemLocation itemsLocation = leftToRight ? ItemLocation.left : ItemLocation.right;
				List<Item> itemsForEvet = createItemList(move, itemsLocation);
				BeforeMoveItemsEvent moveEvt = BeforeMoveItemsEvent.fire(transferList, itemsForEvet);
				
				if(!moveEvt.isCanceled())
				{
					listToRemove.clear();
					
					for (String[] item : move)
					{
						listToAdd.addItem(item[0], item[1]);
					}
					
					for (String[] item : keep)
					{
						listToRemove.addItem(item[0], item[1]);
					}
				}
			}
		}
		
		/**
		 * Creates a list of Item from a list of string[] containing labels and values 
		 * @param arrays
		 * @param location
		 * @return
		 */
		private List<Item> createItemList(List<String[]> arrays, ItemLocation location)
		{
			List<Item> items = new ArrayList<Item>();
			for (String[] array : arrays)
			{
				Item item = new Item(array[0], array[1], location);
				items.add(item);
			}
			return items;
		}
	}
}