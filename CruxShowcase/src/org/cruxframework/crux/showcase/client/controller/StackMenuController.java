package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.stackmenu.StackMenuItem;

import com.google.gwt.event.logical.shared.SelectionEvent;

@Controller("stackMenuController")
public class StackMenuController {
	
	@Expose
	public void onSelectItem(SelectionEvent<StackMenuItem> evt){
		StackMenuItem selectedItem = evt.getSelectedItem();
		String itemLabel = selectedItem.getLabel();
		String parentLabel = selectedItem.getParentItem().getLabel();
		MessageBox.show("Info", "You choose the item '" + itemLabel 
				+ "', from '" + parentLabel + "'", null);
	}
	
}