package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.dto.Contact;
import org.cruxframework.crux.showcase.client.dto.Contact.Gender;
import org.cruxframework.crux.showcase.client.formatter.BirthDateFormatter;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.row.RowClickEvent;
import org.cruxframework.crux.widgets.client.event.row.RowRenderEvent;
import org.cruxframework.crux.widgets.client.grid.DataRow;
import org.cruxframework.crux.widgets.client.grid.Grid;


import com.google.gwt.user.client.ui.Image;

@Controller("gridEventsController")
public class GridEventsController {
	
	private BirthDateFormatter birthDateFormatter = new BirthDateFormatter();
	
	@Expose
	public void onLoad() {
		Grid grid = Screen.get("gridWithEvents", Grid.class);
		grid.loadData();
	}
	
	@Expose
	public void applyAvatar(RowRenderEvent event) {
		DataRow row = (DataRow) event.getRow();
		Contact contact = (Contact) row.getBoundObject();
		Image image = (Image) row.getWidget("avatar");
		
		String url = "../style/img/male_avatar.gif";
		if(Gender.FEMALE.equals(contact.getGender()))
		{
			url = "../style/img/female_avatar.gif";
		}
		
		image.setUrl(url);	
	}
	
	@Expose
	public void showDetail(RowClickEvent event) {
		DataRow row = (DataRow) event.getRow();
		Contact contact = (Contact) row.getBoundObject();
		String detail =
			"name: " + contact.getName() + ", " +
			"\nphone: " + contact.getPhone() + ", " +
			"\nbirthday: " + birthDateFormatter.format(contact.getBirthday());
		MessageBox.show("Contact Details", detail, null);
	}
}