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
	
	private MaskedTextBox getDateInput()
	{
		return View.of(this).getWidget("dateInput", MaskedTextBox.class);
	}
	
	private Widget getButton()
	{
		return View.of(this).getWidget("btnParseDate");
	}
	
	@Expose
	public void readDate()
	{
		MaskedTextBox widget = this.getDateInput();
		
        if(widget.getValue().isEmpty())
        {
        	FlatMessageBox.show("Campo vazio. Por favor preencha o campo.", MessageType.WARN);
        }
        /*
         * This is a poor date format validation, just as an example, feel free to update it
         * as you need to.
         */
        else if(!widget.getValue().matches("[0-1][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]"))
        {
        	this.changeState("warn");
        	FlatMessageBox.show("Favor informar um valor válido no formato: \"dd/mm/aaaa\".", MessageType.WARN);
        }
        else
        {
			try{
				Date date = (Date) widget.getUnformattedValue();
				this.changeState(null);
				FlatMessageBox.show("A data que você informou: " + date, MessageType.INFO);
			}catch(Exception e){
				this.changeState("warn");
				FlatMessageBox.show("A data que você informou é inválida, tente novamente. ", MessageType.WARN);
			}
        	
        }
	}

	private void changeState(String type)
	{
		if(type == null)
		{
			type = "def";
		}
		
		MaskedTextBox widget = this.getDateInput();
		Widget button = this.getButton();
		
		widget.removeStyleName("warn");
		button.removeStyleName("warn");
		
		widget.setStyleName(type, true);
		button.setStyleName(type, true);
	}
}
