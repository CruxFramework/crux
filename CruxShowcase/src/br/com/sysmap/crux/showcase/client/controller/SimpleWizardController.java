package br.com.sysmap.crux.showcase.client.controller;

import java.io.Serializable;

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
	
	@SuppressWarnings("unchecked")
    @Expose
	public void onCancel(CancelEvent event){
		Window.alert("operation canceled! Returnig to first step...");
		((Wizard<WizardData>)event.getSource()).first();
	}

	@SuppressWarnings("unchecked")
    @Expose
	public void onFinish(FinishEvent event){
		WizardData data = ((Wizard<WizardData>)event.getSource()).readData();
		Window.alert("Wizard finished. Information provided:\n Name: "+data.name+".\n Address: "+data.address);
	}

	@Expose
	public void onEnterStep1(EnterEvent<WizardData> event)
	{
		screen.getWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
	
	@Expose
	public void onLeaveStep2(LeaveEvent<WizardData> event)
	{
		WizardData data = new WizardData(); 
		data.name = screen.getName().getText();
		data.address = screen.getAddress().getText();
		event.getWizardAccessor().updateData(data);
	}
	
	@Expose
	public void onEnterStep3(EnterEvent<WizardData> event)
	{
		screen.getWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}

	@Expose
	public void onClick(WizardCommandEvent<WizardData> event){
		Window.alert("Custom command called");
	}

	public static class WizardData implements Serializable
	{
        private static final long serialVersionUID = -882345488890474239L;

        private String name;
		private String address;
	}
	
	public static interface WizardScreen extends ScreenWrapper
	{
		Wizard<WizardData> getWizard();
		TextBox getName();
		TextBox getAddress();
	}
}