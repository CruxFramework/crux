package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TreeItem;

@Controller("gwtController")
public class GwtController {
	
	@Expose
	public void onSelectTreeItem(SelectionEvent<TreeItem> event){
		Window.alert("Selected item: "+event.getSelectedItem().getText());
	}
}