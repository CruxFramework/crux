package org.cruxframework.crossdeviceshowcase.client.controller.samples.maskedtextbox;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.dialog.FlatMessageBox;
import org.cruxframework.crux.widgets.client.dialog.FlatMessageBox.MessageType;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.user.client.ui.Widget;

@Controller("maskedTextBoxController")
public class MaskedTextBoxController 
{
	@Expose
	public void readDate()
	{
		MaskedTextBox widget = View.of(this).getWidget("dateInput", MaskedTextBox.class);
		Widget button = View.of(this).getWidget("btnParseDate");
		
		Date date = (Date) widget.getUnformattedValue();
		if(date != null){
			FlatMessageBox.show("The date you've informed: " + date, MessageType.INFO);
		}else{
			FlatMessageBox.show("Did you forgot to fill the input?", MessageType.WARN);
			widget.setStyleName("warn", true);
			button.setStyleName("warn", true);
		}
	}
}
