/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.gadgets.client.container;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.gadgets.client.GadgetContainerMsg;
import org.cruxframework.crux.gadgets.client.container.Gadgets.MetadataCallback;
import org.cruxframework.crux.gadgets.client.layout.LayoutManager;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("gadgetConfigController")
public class GadgetConfigController
{
	@Create
	protected GadgetContainerMsg messages;
	private LayoutManager layoutManager;

	/**
	 * Constructor
	 */
	public GadgetConfigController()
	{
		layoutManager = LayoutManagerFactory.getLayoutManager();
	}

	@Expose
	public void onLoadProduction()
	{
		load(false, "", "");
	}

	@Expose
	public void onLoadDebug()
	{
		load(true, "", ""); //TODO: resolver autenticacao
	}
	
	/**
	 * Load container configuration
	 * @param debug
	 */
	public void load(final boolean debug, final String userId, final String groupId)
	{
		configure(debug, userId, groupId);
	}
	
	//TODO: receber esses dados
	private native Array<String> getGadgets()/*-{
	    return $wnd.currentGadgets;
    }-*/;

	
	/**
	 * 
	 * @param debug
	 * @param groupId 
	 * @param userId 
	 */
	protected void configure(boolean debug, String userId, String groupId)
    {
		GadgetContainer container = GadgetContainer.getUnconfigured();
		container.setDebug(debug);
		String url = Window.Location.getParameter("url");
//		configureContainerURL();
		configureLocale();
		configureCache();
		configureCurrentView(url);
		configureGadgetCanvasHeight();
		configureContainerParentUrl();
		
		Array<String> gadgets;
		if (StringUtils.isEmpty(url))
		{
			gadgets = getGadgets();
		}
		else
		{
			gadgets = CollectionFactory.createArray();
			gadgets.add(url);
		}
		Gadgets.loadGadgetsMetadata(container.getCountry(), container.getLanguage(), container.getCurrentView(), 
				container.getSecureToken(), gadgets, new MetadataCallback()
		{
			@Override
			public void onMetadataLoaded(Array<GadgetMetadata> metadata)
			{
				onGadgetsMetadataLoaded(metadata);
			}
		});
    }

	/**
	 * 
	 */
	protected void configureGadgetCanvasHeight()
    {
		GadgetContainer.getUnconfigured().setGadgetCanvasHeight(getGadgetCanvasHeight());
		GadgetContainer.getUnconfigured().setGadgetCanvasWidth("100%");
    }

	/*
	 * 
	 *
	protected void configureContainerURL()
    {
	    String containerURL = getContainerURL();
	    GadgetContainer.getUnconfigured().setContainerUrl(containerURL);
    }*/
	
	/**
	 * 
	 */
	protected void configureContainerParentUrl()
    {   //TODO: checar isso aki.... ta setando duas propriedades com o mesmo valor
		GadgetContainer.getUnconfigured().setParentUrl(getContainerURL());
    }

	protected void onGadgetsMetadataLoaded(Array<GadgetMetadata> metadata) //TODO: ajustar isso aki
	{
		//TODO: carregar configuracoes de urlbase, parentcontainerurl, currentView, userId, groupId
		GadgetContainer.getUnconfigured().setMetadata(getGadgetsMetadata(metadata));
		GadgetContainer.configure();
	}

	protected void configureCurrentView(String url)
    {
		ContainerView currentView = (StringUtils.isEmpty(url)?ContainerView.profile:ContainerView.canvas);
		GadgetContainer.getUnconfigured().setCurrentView(currentView);
    }

	protected void configureCache()
    {
		String nocache = Window.Location.getParameter("nocache");
		boolean cacheEnabled = (nocache==null || !nocache.equals("1"));
		GadgetContainer.getUnconfigured().setCacheEnabled(cacheEnabled);
    }	
	
	protected void configureLocale()
    {
	    String country = Window.Location.getParameter("country");
		String language = Window.Location.getParameter("lang");
		if (StringUtils.isEmpty(language) && StringUtils.isEmpty(country))
		{
			String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
			String[] localeParts = localeName.split("_");
			if (localeParts != null && localeParts.length > 0)
			{
				language = localeParts[0];
				if (localeParts.length > 1)
				{
					country = localeParts[1];
				}
			}
		}
		if (StringUtils.isEmpty(language))
		{
			language = "default";
		}
		if (StringUtils.isEmpty(country))
		{
			country = "default";
		}
		GadgetContainer.getUnconfigured().setLanguage(language);
		GadgetContainer.getUnconfigured().setCountry(country);
    }

	/**
	 * Retrieve the height to be used for gadgets when rendering on canvas view.
	 * @return
	 */
	protected native String getGadgetCanvasHeight()/*-{
	    return $wnd._gadgetCanvasHeight || null;
    }-*/;

	/**
	 * 
	 * @return
	 */
	protected native String getContainerURL()/*-{
		return ($doc.location + '')
	}-*/;
		
	/**
	 * 
	 * @param metadata
	 * @return
	 */
	protected Array<Array<GadgetMetadata>> getGadgetsMetadata(Array<GadgetMetadata> metadata)
    {
	    Array<Array<GadgetMetadata>> array = CollectionFactory.createArray();
		array.add(metadata);
		Array<GadgetMetadata> column1 = CollectionFactory.createArray();
		array.add(column1);
		Array<GadgetMetadata> column2 = CollectionFactory.createArray();
		array.add(column2);
	    return array;
    }
}
