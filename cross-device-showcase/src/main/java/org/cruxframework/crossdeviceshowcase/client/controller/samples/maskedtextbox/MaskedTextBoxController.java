package org.cruxframework.crossdeviceshowcase.client.controller.samples.maskedtextbox;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.dialog.FlatMessageBox;
import org.cruxframework.crux.widgets.client.dialog.FlatMessageBox.MessageType;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

@Controller("maskedTextBoxController")
public class MaskedTextBoxController 
{
	@Expose
	public void readDate()
	{
		MaskedTextBox widget = View.of(this).getWidget("dateInput", MaskedTextBox.class);
		Date date = (Date) widget.getUnformattedValue();
		FlatMessageBox.show("Você informou a data: " + date, MessageType.INFO);
	}
}
