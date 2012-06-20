package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.remote.SVNServiceAsync;


import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

@Controller("sourceTabController")
public class SourceTabController
{
	@Parameter
	protected String sourceLocation;
	
	@Parameter
	protected boolean isJava;
	
	@Create
	protected SVNServiceAsync service;	
	
	@Expose
	public void onLoad()
	{
		if(isJava)
		{
			service.getJavaFile(sourceLocation, false, new SourceRenderer(this, isJava));
		}
		else
		{
			service.getXmlFile(sourceLocation, false, new SourceRenderer(this, isJava));
		}		
	}
	
	public static class SourceRenderer extends AsyncCallbackAdapter<String>
	{
		private final boolean isJava;
		private final SourceTabController controller;

		public SourceRenderer(SourceTabController controller, boolean isJava)
		{
			super(controller);
			this.controller = controller;
			this.isJava = isJava;
			Screen.blockToUser();
		}

		@Override
		public void onComplete(String result)
		{
			try
			{
				Element editor = DOM.getElementById("sourceEditor");
				String brush = "class=\"brush:" + (isJava ? "java" : "xml") + "\"";
				result = new SafeHtmlBuilder().appendEscaped(result).toSafeHtml().asString();
				editor.setInnerHTML("<pre " + brush + ">" + result + "</pre>");
				controller.syntaxHighlight();
			}
			finally
			{
				Screen.unblockToUser();
			}
		}
		
		@Override
		public void onError(Throwable e)
		{
			Screen.unblockToUser();
			super.onError(e);
		}
	}
	
	public native void syntaxHighlight()/*-{
		$wnd.doHighlight();
	}-*/;
}
