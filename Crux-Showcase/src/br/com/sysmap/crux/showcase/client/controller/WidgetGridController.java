package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;
import br.com.sysmap.crux.widgets.client.grid.impl.DataRow;
import br.com.sysmap.crux.widgets.client.grid.impl.Grid;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Image;

@Controller("widgetGridController")
public class WidgetGridController {
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("widgetGrid", Grid.class);
		grid.loadData();
	}
	
	@Expose
	public void onClickCall(ClickEvent event) {
				
		Image image = (Image) event.getSource();
		Grid grid = Screen.get("widgetGrid", Grid.class);
		DataRow row = grid.getRow(image);
		
		Contact contact = (Contact) row.getBindedObject();
		
		MessageBox.show(
			"Information", 
			"Calling " + contact.getName() + " - [" + contact.getPhone() + "]", 
			null
		);
	}
}