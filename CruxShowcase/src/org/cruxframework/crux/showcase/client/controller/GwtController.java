package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.TreeItem;

@Controller("gwtController")
public class GwtController {
	
	@Expose
	public void onSelectTreeItem(SelectionEvent<TreeItem> event){
		MessageBox.show("Info", "Selected item: " + event.getSelectedItem().getText(), null);
	}
}