package org.cruxframework.cruxsite.client;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.plugin.google.analytics.client.GoogleAnalytics;
import org.cruxframework.crux.widgets.client.rss.feed.Error;
import org.cruxframework.crux.widgets.client.rss.feed.Feed;
import org.cruxframework.crux.widgets.client.rss.feed.FeedApi;
import org.cruxframework.crux.widgets.client.rss.feed.FeedCallback;
import org.cruxframework.crux.widgets.client.rss.feed.Loader;
import org.cruxframework.cruxsite.client.accessor.IndexAccessor;
import org.cruxframework.cruxsite.client.accessor.ManualAccessor;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

@Controller("mainController")
public class MainController 
{

	@Inject
	private IndexAccessor screen;
	
	@Inject
	private ManualAccessor manual;
	
	private static Logger logger = Logger.getLogger(MainController.class.getName());

	@Expose
	public void manualOnLoad()
	{
		String versionManual = new String();
		String urlBaseManual = new String();
		versionManual = "crux50";
		urlBaseManual = "http://files.cruxframework.org/documentation/"+ versionManual +"/";
		// urlBaseManual = "http://cruxfile.appspot.com/documentation/"+ versionManual +"/";
		manual.manualSinglePageBtn().setHref(urlBaseManual + "single-page/index.html");
		manual.manualMultiplePageBtn().setHref(urlBaseManual + "multiple-pages/index.html");
		manual.manualPdfBtn().setHref(urlBaseManual + "pdf/manual-crux-5.0.pdf");
		manual.javadocBtn().setHref(urlBaseManual + "javadoc/index.html");
	}
	
	@Expose
	public void onLoad()
	{
		
		// Código do "Mapa Termico de Cliques" 
		String scriptURL = Window.Location.getProtocol()+"//dnn506yrbagrg.cloudfront.net/pages/scripts/0018/5757.js?"+Math.floor(new Date().getTime()/3600000);
		ScriptInjector.fromUrl(scriptURL).setRemoveTag(false).setWindow(ScriptInjector.TOP_WINDOW).inject();
		
		// Inicia Analytics
		GoogleAnalytics.init("UA-7689544-6"); 
		
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
				FeedApi feedLastBlogEntries = FeedApi.create("http://feeds.feedburner.com/cruxframework");
				//this will only get feeds from cache.
				//feedLastBlogEntries.includeHistoricalEntries();
				feedLastBlogEntries.setNumEntries(3);

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
	
	public void setScreen(IndexAccessor screen) {
		this.screen = screen;
	}

	public void setManual(ManualAccessor manual) {
		this.manual = manual;
	}
	
}
