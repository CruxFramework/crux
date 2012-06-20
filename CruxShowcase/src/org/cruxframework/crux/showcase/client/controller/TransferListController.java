package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.moveitem.BeforeMoveItemsEvent;
import org.cruxframework.crux.widgets.client.transferlist.TransferList;
import org.cruxframework.crux.widgets.client.transferlist.TransferList.Item;
import org.cruxframework.crux.widgets.client.transferlist.TransferList.ItemLocation;

import com.google.gwt.user.client.Window;

@Controller("transferListController")
public class TransferListController {
	
	@Expose
	public void loadItems(){
		TransferList transferList = Screen.get("transferList", TransferList.class);
		List<Item> items = new ArrayList<Item>();
		items.add(new TransferList.Item("Item 1 ", "Item1Value", ItemLocation.left));
		items.add(new TransferList.Item("Item 2 ", "Item2Value", ItemLocation.left));
		items.add(new TransferList.Item("Item 3 ", "Item3Value", ItemLocation.right));
		items.add(new TransferList.Item("Item 4 ", "Item4Value", ItemLocation.left));
		transferList.setCollection(items);
	}
	
	@Expose
	public void onBeforeMoveItems(BeforeMoveItemsEvent event)
	{
		List<Item> items = event.getItems();
		
		if(items.size() > 0)
		{
			boolean plural = items.size() > 1;
			String pronoun = plural ? "these" : "this";
			String noun = plural ? "items" : "item";
			String destination = event.isMovingToLeft() ? "left" : "right";
			
			String message = "Do you really want to move " + pronoun  + " " + items.size() + " " + noun + " to " + destination + "?";
			
			if(!Window.confirm(message))
			{
				event.cancel();
			}
		}
		else
		{
			MessageBox.show("Ops...", "You have to select the items you want to move.", null);
		}
	}
}