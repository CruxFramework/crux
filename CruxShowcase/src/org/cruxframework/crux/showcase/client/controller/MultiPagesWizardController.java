package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.JSWindow;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.FinishEvent;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.wizard.Wizard;


@Controller("multiWizardController")
public class MultiPagesWizardController {
	
	@Create
	protected MultiPagesWizardPersonalInfoStepControllerCrossDoc personalInfoStep;
	
	@Create
	protected WizardScreen screen;
	
    @Expose
	public void onCancel(final CancelEvent cancelEvt){
		((Wizard<?>) cancelEvt.getSource()).first();
		clearFields();
	}

	@SuppressWarnings("unchecked")
    @Expose
	public void onFinish(final FinishEvent finishEvt){
		final Wizard<Person> wizard = (Wizard<Person>) finishEvt.getSource();
		String name = wizard.readData().getName();
		MessageBox.show(
			null, "Good Bye, " + name + "!", new OkHandler() {
				public void onOk(OkEvent ok) {
					wizard.first();
					clearFields();
				}
			}
		);
	}
	
	private void clearFields() {
		JSWindow stepWindow = screen.getWizard().getPageStep("personalInfoStep").getWindow();
		((TargetDocument) personalInfoStep).setTargetWindow(stepWindow);
		personalInfoStep.clearFields();
	}
	
	/**
	 * Screen fields
	 */
	public static interface WizardScreen extends ScreenWrapper {
		Wizard<Person> getWizard();
	}
}