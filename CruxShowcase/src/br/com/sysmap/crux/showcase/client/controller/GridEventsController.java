package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.showcase.client.dto.Contact;
import br.com.sysmap.crux.showcase.client.dto.Contact.Gender;
import br.com.sysmap.crux.widgets.client.dialog.MessageBox;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.grid.DataRow;
import br.com.sysmap.crux.widgets.client.grid.Grid;

import com.google.gwt.user.client.ui.Image;

@Controller("gridEventsController")
public class GridEventsController {
	
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
			"\nbirthday: " + Screen.getFormatter("birthday").format(contact.getBirthday());
		MessageBox.show("Contact Details", detail, null);
	}
}