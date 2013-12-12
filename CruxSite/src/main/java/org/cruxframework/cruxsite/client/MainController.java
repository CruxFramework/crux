package org.cruxframework.cruxsite.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.disposal.topmenudisposal.TopMenuDisposal;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.rss.feed.Error;
import org.cruxframework.crux.widgets.client.rss.feed.Feed;
import org.cruxframework.crux.widgets.client.rss.feed.FeedApi;
import org.cruxframework.crux.widgets.client.rss.feed.FeedCallback;
import org.cruxframework.crux.widgets.client.rss.feed.Loader;
import org.cruxframework.cruxsite.client.accessor.IndexAccessor;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

@Controller("mainController")
public class MainController 
{

	@Inject
	private IndexAccessor screen;

	private static Logger logger = Logger.getLogger(MainController.class.getName());

	@Expose
	public void onLoad()
	{
		Loader.init("ABQIAAAArGIZjhmsan61DtT58_d6cRQNU4gAv_Jc96TUa1T-tg6v_fuASxRtwAMNaJHgnp12SaDI9Cs17oKAzw", new Loader.LoaderCallback()
		{
			public void onError(Throwable t)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					Window.alert("Erro carregando a API de feed.");
					logger.log(Level.SEVERE, "Error loading Google Feed API...");
				}
			}

			public void onLoad()
			{
				FeedApi feedLastBlogEntries = FeedApi.create("http://feeds.feedburner.com/cruxframework/blog");
				feedLastBlogEntries.includeHistoricalEntries();
				feedLastBlogEntries.setNumEntries(5);

				feedLastBlogEntries.load(new FeedCallback()
				{
					@Override
					public void onLoad(Feed feed)
					{
						screen.lastBlogEntries().setFeed(feed);
						//screen.lastBlogEntries().
					}

					@Override
					public void onError(Error error) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
	}

	@Expose
	public void showViewInfo(SelectEvent evt)
	{
		showView("info", evt);
	}

	@Expose
	public void showViewFast(SelectEvent evt)
	{
		showView("desempenho", evt);
	}
	
	@Expose
	public void showViewJava(SelectEvent evt)
	{
		showView("escrevaJava", evt);
	}

	@Expose
	public void showViewCrossDev(SelectEvent evt)
	{
		showView("multiplataforma", evt);
	}

	@Expose
	public void showViewOpenSource(SelectEvent evt)
	{
		showView("opensource", evt);
	}

	@Expose
	public void showViewAbout(SelectEvent evt)
	{
		showView("sobreocrux", evt);
	}

	@Expose
	public void showViewExamples(SelectEvent evt)
	{
		showView("exemplos", evt);
	}

	@Expose
	public void showAppHelloCross(SelectEvent evt)
	{
		showSampleApp(evt, "CrossDeviceHelloWorld");
	}

	@Expose
	public void showAppSite(SelectEvent evt)
	{
		showSampleApp(evt, "CruxSite");
	}

	@Expose
	public void showAppShowcase(SelectEvent evt)
	{
		showSampleApp(evt, "CrossDeviceShowcase");
	}

	/**
	 * @param evt
	 * @param nomeAplicacao
	 */
	private void showSampleApp(SelectEvent evt, String nomeAplicacao) 
	{
		showView("aplicacaoExemplo", evt);
		Label appName = (Label) View.getView("aplicacaoExemplo").getWidget("sampleAppName");
		appName.setText(nomeAplicacao);
	}

	/**
	 * @param viewName
	 * @param evt 
	**/
	private void showView(String viewName, SelectEvent evt) 
	{
		if(evt != null)
		{
			evt.setCanceled(true);
		}

		TopMenuDisposal disposal = (TopMenuDisposal) Screen.get("menuDisposal");
		disposal.showView(viewName, true);
	}

	public void setScreen(IndexAccessor screen) {
		this.screen = screen;
	}
}
