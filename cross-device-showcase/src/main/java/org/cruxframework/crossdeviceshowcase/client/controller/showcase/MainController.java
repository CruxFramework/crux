package org.cruxframework.crossdeviceshowcase.client.controller.showcase;

import java.util.List;

import org.cruxframework.crossdeviceshowcase.client.remote.showcase.SVNServiceAsync;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewActivateEvent;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;
import org.cruxframework.crux.widgets.client.dialogcontainer.DialogViewContainer;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;
import org.cruxframework.crux.widgets.client.disposal.panelchoicedisposal.PanelChoiceDisposal;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

@Controller("mainController")
public class MainController 
{
	@Inject
	public SVNServiceAsync service;
	
	@Expose
	public void wellcome()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showView("wellcome", Direction.FORWARD);
	}
	
	@Expose
	public void showMenu()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showMenu();
		//menuDisposal.setTogglable(false);
		//menuDisposal.setTransitionSpeed(50);
		
	}
	
	@Expose
	public void navigateToSite()
	{
		Window.open("http://www.cruxframework.org", "cruxSite", null);
	}
	
	@Expose
	public void navigateToProject()
	{
		Window.open("https://code.google.com/p/crux-framework", "cruxProject", null);
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
					showSourcesDialog(result);
				}
			}			
		});
	}

	/**
	 * Shows a dialog box with the source files contents
	 * @param files
	 */
	private void showSourcesDialog(final List<String> files)
	{
		DialogViewContainer dialog = DialogViewContainer.createDialog("sourcesPopup");
		dialog.openDialog();
		dialog.center();
		
		final PanelChoiceDisposal sourceChoice = dialog.getView().getWidget("sourceChoice", PanelChoiceDisposal.class);
		
		for (int i = files.size() - 1; i >= 0; i--)
		{
			final String path = files.get(i);
			final String fileName = getFileName(path);
			addSourceChoice(sourceChoice, path, fileName);
		}
		
		if(files.size() > 0)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					String fileName = getFileName(files.get(0));
					sourceChoice.choose(fileName, fileName);
				}
			});
		}						
	}

	/**
	 * @param sourceChoice
	 * @param path
	 * @param fileName
	 */
	private void addSourceChoice(final PanelChoiceDisposal sourceChoice, final String path, final String fileName)
	{
		sourceChoice.addChoice(fileName, fileName, "sourceCode", new ViewActivateHandler()
		{
			private boolean loaded = false;
			
			@Override
			public void onActivate(ViewActivateEvent event)
			{
				if(!loaded)
				{
					loaded = true;
					
					service.getSourceFile(path, new Callback<String>()
					{
						@Override
						public void applyResult(String source)
						{
							View view = View.getView(fileName);
							Widget sourceEditor = view.getWidget("sourceEditor");
							Element editor = sourceEditor.getElement();
							String brush = "class=\"brush:" + (fileName.endsWith("java") ? "java": "xml") + "\"";
							source = new SafeHtmlBuilder().appendEscaped(source).toSafeHtml().asString();
							editor.setInnerHTML("<pre " + brush + ">" + source + "</pre>");
							syntaxHighlight();
						}
					});
				}
			}
		});
	}

	/**
	 * @param path
	 * @return
	 */
	private String getFileName(final String path)
	{
		return path.indexOf("/") >= 0 ? path.substring(path.lastIndexOf("/") + 1) : path;
	}
	
	public native void syntaxHighlight()/*-{
		$wnd.doHighlight();
	}-*/;
}
