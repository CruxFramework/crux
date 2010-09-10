package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.ScreenWrapper;
import br.com.sysmap.crux.widgets.client.event.CancelEvent;
import br.com.sysmap.crux.widgets.client.event.FinishEvent;
import br.com.sysmap.crux.widgets.client.wizard.EnterEvent;
import br.com.sysmap.crux.widgets.client.wizard.LeaveEvent;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;
import br.com.sysmap.crux.widgets.client.wizard.WizardCommandEvent;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;

@Controller("simpleWizardController")
public class SimpleWizardController {
	
	@Create
	protected WizardScreen screen;
	
	@Expose
	public void onCancel(CancelEvent event){
		Window.alert("operation canceled! Returnig to first step...");
		((Wizard)event.getSource()).first();
	}

	@Expose
	public void onFinish(FinishEvent event){
		WizardData data = ((Wizard)event.getSource()).readContext(WizardData.class);
		Window.alert("Wizard finished. Information provided:\n Name: "+data.name+".\n Address: "+data.address);
	}

	@Expose
	public void onEnterStep1(EnterEvent event)
	{
		screen.getWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
	
	@Expose
	public void onLeaveStep2(LeaveEvent event)
	{
		WizardData data = new WizardData(); 
		data.name = screen.getName().getText();
		data.address = screen.getAddress().getText();
		event.getWizardAccessor().updateContext(data);
	}
	
	@Expose
	public void onEnterStep3(EnterEvent event)
	{
		screen.getWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}

	@Expose
	public void onClick(WizardCommandEvent event){
		Window.alert("Custom command called");
	}

	public static class WizardData
	{
		private String name;
		private String address;
	}
	
	public static interface WizardScreen extends ScreenWrapper
	{
		Wizard getWizard();
		TextBox getName();
		TextBox getAddress();
	}
}