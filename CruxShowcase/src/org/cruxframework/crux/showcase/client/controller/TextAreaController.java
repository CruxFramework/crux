package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.textarea.TextArea;

import com.google.gwt.user.client.ui.TextBox;

@Controller("textAreaController")
public class TextAreaController {
	
	@Expose
	public void applyMaxLength() {
		try {
			int maxLength = readMaxLength();		
			Screen.get("niceTextArea", TextArea.class).setMaxLength(maxLength);
		} catch (Exception e) {
			MessageBox.show("Bad MaxLength", "Please, type an integer for using as maxLength attribute.", null);
		}
	}

	private int readMaxLength() throws Exception {
		String strMaxLength = Screen.get("maxLength", TextBox.class).getValue();
		return Integer.parseInt(strMaxLength);
	}
}