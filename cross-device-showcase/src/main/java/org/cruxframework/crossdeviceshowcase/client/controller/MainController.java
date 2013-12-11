package org.cruxframework.crossdeviceshowcase.client.controller;

import org.cruxframework.crossdeviceshowcase.client.remote.SVNServiceAsync;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
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
		
		service.getXmlFile(viewId + ".view.xml", false, new AsyncCallbackAdapter<String>() 
		{
			@Override
			public void onComplete(String result) 
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
		});
	}
	
	public native void syntaxHighlight()/*-{
		$wnd.doHighlight();
	}-*/;
}
