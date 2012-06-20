package org.cruxframework.crux.showcase.client.controller;

import java.util.ArrayList;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.showcase.client.remote.SVNServiceAsync;
import org.cruxframework.crux.widgets.client.dialog.Popup;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabs;
import org.cruxframework.crux.widgets.client.event.openclose.OpenEvent;
import org.cruxframework.crux.widgets.client.event.openclose.OpenHandler;


import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

@Controller("baseSourcesController")
public class BaseSourcesController implements BaseSourcesControllerCrossDoc {
	
	public static interface SourcesScreen extends ScreenWrapper
	{
		public DynaTabs getSourcesTabs();
	}
	
	@Create
	protected SourcesScreen screen;
	
	@Create
	protected SVNServiceAsync service;

	
	@Expose
	public void viewSources()
	{
		Popup.show("Example Source Code", "sourcesPopup.html", "" + getPopupWidth(), "" + getPopupHeight(), null, createPopupOpenHandler(), null, true, true);
	}
	
	public final void setSourceTabs(ArrayList<SourceTab> tabs)
	{
		boolean first = true;
		
		for (SourceTab tab : tabs)
		{
			String tabId = tab.getLocation().replaceAll("^[a-zA-Z0-9]", "");
			String url = "../sourceTab.html?isJava=" + tab.isJava() + "&sourceLocation=" + URL.encodeQueryString(tab.getLocation()); 
			if(first)
			{
				screen.getSourcesTabs().openTab(tabId, tab.getLabel(), url, false, false);
				first = false;
			}
			else
			{
				screen.getSourcesTabs().openLazyTab(tabId, tab.getLabel(), url, false, false);
			}
		}		
	}
	
	private OpenHandler createPopupOpenHandler()
	{
		return new OpenHandler()
		{
			public void onOpen(OpenEvent event)
			{
				try 
				{
					ArrayList<SourceTab> sourceTabs = getSourceTabs();
					BaseSourcesControllerCrossDoc crossDoc = GWT.create(BaseSourcesControllerCrossDoc.class);
					((TargetDocument) crossDoc).setTargetWindow(Popup.getWindow());
					crossDoc.setSourceTabs(sourceTabs);
				} 
				catch (Exception e) 
				{
					GWT.log(e.getMessage(), e);
				}
			}
		};
	}

	private static int getPopupWidth()
	{
		return (int) Math.round(Window.getClientWidth()); 
	}
	
	private static int getPopupHeight()
	{
		return (int) Math.round(Window.getClientHeight()); 
	}

	private ArrayList<SourceTab> getSourceTabs()
	{
		ArrayList<SourceTab> tabs = new ArrayList<SourceTab>();
		String screenId = getScreenSimpleId();
				
		if(hasXmlSource())
		{
			tabs.add(new SourceTab(screenId + ".crux.xml", "Page Source", false));
		}
		
		if(hasControllerSource())
		{
			String javaFileName = screenId.substring(0,1).toUpperCase() + screenId.substring(1) + "Controller.java";
			javaFileName =  "client/controller/" + javaFileName;
			tabs.add(new SourceTab(javaFileName, "Controller Source", true));
		}
		
		tabs.addAll(getAdditionalSources());
		
		return tabs;
	}	
	
	protected ArrayList<SourceTab> getAdditionalSources()
	{
		return new ArrayList<SourceTab>();
	}

	protected boolean hasControllerSource()
	{
		return true;
	}

	protected boolean hasXmlSource()
	{
		return true;
	}
	
	private static String getScreenSimpleId() {
		String id = Screen.getId();		
		int slash = id.lastIndexOf("/");
		int dot = id.lastIndexOf(".");
		id = id.substring(slash + 1, dot);
		return id;
	}
}