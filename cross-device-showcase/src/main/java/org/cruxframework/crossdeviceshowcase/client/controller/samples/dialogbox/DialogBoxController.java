package org.cruxframework.crossdeviceshowcase.client.controller.samples.dialogbox;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.dialogcontainer.DialogViewContainer;

@Controller("dialogBoxController")
public class DialogBoxController 
{
	private static DialogViewContainer dialog;

	@Expose
	public void openDialog()
	{
		dialog = DialogViewContainer.createDialog("dialogBoxTarget");
		dialog.setTitle("Caixa de Diálogo Contendo uma View");
		dialog.setWidth("600px");
		dialog.setHeight("200px");
		dialog.isModal();
		dialog.openDialog();
		dialog.center();
	}
	
	@Expose
	public void closeDialog()
	{
		dialog.closeDialog();
	}
}
