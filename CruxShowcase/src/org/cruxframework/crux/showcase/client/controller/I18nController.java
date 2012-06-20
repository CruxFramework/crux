package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

@Controller("i18nController")
public class I18nController {
	
	@Create
	protected MyMessages messages;
	
	@Expose
	public void changeLocale(){
		ListBox locales = Screen.get("localesListBox", ListBox.class);
		String locale = locales.getValue(locales.getSelectedIndex());
		Window.Location.replace(Screen.appendDebugParameters("/i18n.html?locale="+locale));
	}
	
	@Expose
	public void onLoad() {
		String locale = Window.Location.getParameter("locale");
		if ("pt_BR".equals(locale))	{
			Screen.get("localesListBox", ListBox.class).setSelectedIndex(1);
		}
	}
}