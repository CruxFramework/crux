package org.cruxframework.crossdeviceshowcase.client.controller.samples.cruxbutton;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.views.View;

import com.google.gwt.user.client.ui.Widget;

@Controller("cruxButtonController")
public class CruxButtonController 
{
	
	private void setState(String state)
	{
		Widget button = View.of(this).getWidget("cruxBtn");
		
		button.removeStyleName("success");
		button.removeStyleName("warn");
		button.removeStyleName("error");
		button.setStyleName("crux-Button " + state);
	}
	
	
	@Expose
	public void handleDefault()
	{
		this.setState("");
	}
	
	@Expose
	public void handleSuccess()
	{
		this.setState("success");
	}
	
	@Expose
	public void handleWarning()
	{
		this.setState("warn");
	}
	
	@Expose
	public void handleError()
	{
		this.setState("error");
	}
}
