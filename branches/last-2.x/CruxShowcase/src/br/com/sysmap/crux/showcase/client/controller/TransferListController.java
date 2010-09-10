package br.com.sysmap.crux.showcase.client.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.event.moveitem.BeforeMoveItemsEvent;
import br.com.sysmap.crux.widgets.client.transferlist.TransferList;
import br.com.sysmap.crux.widgets.client.transferlist.TransferList.Item;
import br.com.sysmap.crux.widgets.client.transferlist.TransferList.ItemLocation;

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
}