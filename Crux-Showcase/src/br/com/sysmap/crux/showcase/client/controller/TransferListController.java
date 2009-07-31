package br.com.sysmap.crux.showcase.client.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.advanced.client.transferlist.TransferList;
import br.com.sysmap.crux.advanced.client.transferlist.TransferList.Item;
import br.com.sysmap.crux.advanced.client.transferlist.TransferList.ItemLocation;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

@Controller("transferListController")
public class TransferListController {
	
	@Expose
	public void loadItems(){
		TransferList transferList = Screen.get("transferList", TransferList.class);
		List<Item> items = new ArrayList<Item>();
		items.add(new TransferList.Item("Item 1 ", "Item1Value", ItemLocation.LEFT));
		items.add(new TransferList.Item("Item 2 ", "Item2Value", ItemLocation.LEFT));
		items.add(new TransferList.Item("Item 3 ", "Item3Value", ItemLocation.RIGHT));
		items.add(new TransferList.Item("Item 4 ", "Item4Value", ItemLocation.LEFT));
		
		transferList.setCollection(items);
	}
}