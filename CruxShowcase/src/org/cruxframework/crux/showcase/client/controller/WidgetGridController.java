package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.widgets.client.dialog.ProgressDialog;
import org.cruxframework.crux.widgets.client.grid.DataRow;
import org.cruxframework.crux.widgets.client.grid.Grid;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Timer;
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
		
		Contact contact = (Contact) row.getBoundObject();
		
		ProgressDialog.show("Calling " + contact.getName() + " " + contact.getPhone() + "...");
		
		new Timer() {
			public void run() {
				ProgressDialog.hide();
			}
		}.schedule(3000);
	}
}