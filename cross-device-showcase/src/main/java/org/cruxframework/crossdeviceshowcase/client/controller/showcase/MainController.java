package org.cruxframework.crossdeviceshowcase.client.controller.showcase;

import java.util.List;

import org.cruxframework.crossdeviceshowcase.client.remote.showcase.SVNServiceAsync;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialogcontainer.DialogViewContainer;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

@Controller("mainController")
public class MainController 
{
	@Inject
	public SVNServiceAsync service;
	
	@Expose
	public void wellcome()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showView("wellcome");
	}
	
	@Expose
	public void showMenu()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showMenu();
	}
	
	@Expose
	public void exibirCodigoFonte()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		String viewId = menuDisposal.getCurrentView();
		
		service.listSourceFilesForView(viewId, new Callback<List<String>>()
		{
			@Override
			public void applyResult(List<String> result)
			{
				if(result.size() > 0)
				{
					String firstFile = result.get(0);
					service.getSourceFile(firstFile, new Callback<String>()
					{
						@Override
						public void applyResult(String result)
						{
							showSourcesDialog(result);
						}
					});
				}
			}			
		});
	}

	/**
	 * Shows a dialog box with the source files contents
	 * @param result
	 */
	private void showSourcesDialog(String result)
	{
		DialogViewContainer dialog = DialogViewContainer.createDialog("sourceCode");
		dialog.setWidth("80%");
		dialog.setHeight("90%");
		dialog.openDialog();
		dialog.center();
		
		Element editor = DOM.getElementById("sourceEditor");
		String brush = "class=\"brush:xml\"";
		result = new SafeHtmlBuilder().appendEscaped(result).toSafeHtml().asString();
		editor.setInnerHTML("<pre " + brush + ">" + result + "</pre>");
		syntaxHighlight();						
	}
	
	public native void syntaxHighlight()/*-{
		$wnd.doHighlight();
	}-*/;
}
