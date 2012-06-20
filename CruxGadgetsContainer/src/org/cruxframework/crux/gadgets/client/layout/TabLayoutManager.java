package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.gadgets.client.container.ContainerView;
import org.cruxframework.crux.gadgets.client.container.Gadget;
import org.cruxframework.crux.gadgets.client.container.GadgetContainer;
import org.cruxframework.crux.gadgets.client.container.GadgetMetadata;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;
import org.cruxframework.crux.widgets.client.rollingtabs.SimpleDecoratedPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TabLayoutManager extends GridLayoutManager
{
	private FastMap<Integer> openedCanvasGadgetIds = new FastMap<Integer>();
	private FastMap<Widget> openedCanvasGadgetTabs = new FastMap<Widget>();
	protected TabLayoutScreen tabScreen;

	public TabLayoutManager()
    {
	    super();
	    tabScreen = GWT.create(TabLayoutScreen.class);
    }
	
	@Override
	public void openGadget(GadgetMetadata gadgetMetadata, ContainerView view)
	{
		GadgetContainer container = GadgetContainer.get();
		if (view.equals(ContainerView.profile))
		{
			Gadget gadget = container.createGadget(gadgetMetadata, view);
			container.addGadget(gadget);
			addGadgetChrome(gadget.getId());
			container.renderGadget(gadget);
		}
		else
		{
		    RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
		    Gadget canvasGadget = container.createGadget(gadgetMetadata, ContainerView.canvas);
		    container.addGadget(canvasGadget);
		    SimplePanel newGadgetChrome = new SimplePanel();
		    newGadgetChrome.setHeight("100%");
		    newGadgetChrome.setWidth("100%");
		    newGadgetChrome.getElement().setId(getGadgetChromeId(canvasGadget.getId()));
		    tabManager.add(newGadgetChrome, getTabWidget(canvasGadget.getId(), canvasGadget.getTitle(), false));
		    container.renderGadget(canvasGadget);
		    int widgetIndex = tabManager.getWidgetIndex(newGadgetChrome);
		    tabManager.selectTab(widgetIndex);
		    
		    String gadgetKey = Integer.toString(canvasGadget.getId());
		    openedCanvasGadgetIds.put(gadgetKey, canvasGadget.getId());
		    openedCanvasGadgetTabs.put(gadgetKey, newGadgetChrome);
		}
	}
	
	@Override
	public void changeGadgetView(int gadgetId, ContainerView view)
	{
		String gadgetKey = Integer.toString(gadgetId);
		if ((view.equals(ContainerView.profile)))
		{
			if (!openedCanvasGadgetIds.containsKey(gadgetKey))
			{
				openCanvasView(gadgetId);
			}
			else
			{
				closeCanvasView(gadgetId, true);
			}
		}
		else
		{
			if (openedCanvasGadgetIds.containsKey(gadgetKey))
			{
				closeCanvasView(gadgetId, true);
			}
			else
			{
				// There is no profile gadget associated with the gadget on canvas view... so just change 
				// the tab to point to Profile
				tabScreen.getLayoutTabManager().selectTab(0);
			}
		}
	}

	/**
	 * 
	 * @param gadgetId
	 */
	private void openCanvasView(int gadgetId)
    {
		String gadgetKey = Integer.toString(gadgetId);
	    RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
	    GadgetContainer container = GadgetContainer.get();
	    Gadget profileGadget = container.getGadget(gadgetId);

	    profileGadget.deactivate();
	    Gadget canvasGadget = container.createGadget(profileGadget, ContainerView.canvas);
	    container.addGadget(canvasGadget);
	    SimplePanel newGadgetChrome = new SimplePanel();
	    newGadgetChrome.setHeight("100%");
	    newGadgetChrome.setWidth("100%");
	    newGadgetChrome.getElement().setId(getGadgetChromeId(canvasGadget.getId()));
	    tabManager.add(newGadgetChrome, getTabWidget(gadgetId, canvasGadget.getTitle(), true));
	    container.renderGadget(canvasGadget);
	    int widgetIndex = tabManager.getWidgetIndex(newGadgetChrome);
	    tabManager.selectTab(widgetIndex);

	    openedCanvasGadgetIds.put(gadgetKey, canvasGadget.getId());
	    openedCanvasGadgetTabs.put(gadgetKey, newGadgetChrome);
    }

	/**
	 * 
	 * @param gadgetId
	 */
	private void closeCanvasView(int gadgetId, boolean activateProfile)
    {
		String gadgetKey = Integer.toString(gadgetId);
	    RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
	    GadgetContainer container = GadgetContainer.get();

	    int canvasGadgetId = openedCanvasGadgetIds.remove(gadgetKey);
	    Widget canvasGadgetChrome = openedCanvasGadgetTabs.remove(gadgetKey);
	    
	    container.getGadget(canvasGadgetId).remove();
	    int widgetIndex = tabManager.getWidgetIndex(canvasGadgetChrome);
		if (widgetIndex == tabManager.getSelectedTab())
	    {
	    	tabManager.selectTab(0);
	    }

	    tabManager.remove(widgetIndex);
	    if (activateProfile)
	    {
	    	Gadget profileGadget = container.getGadget(gadgetId);
	    	profileGadget.activate();
	    }
    }
	
	/**
	 * 
	 * @param profileGadgetId
	 * @param gadgetTitle
	 * @return
	 */
	public Panel getTabWidget(final int profileGadgetId, String gadgetTitle, final boolean activateProfile)
	{
		SimpleDecoratedPanel flap = new SimpleDecoratedPanel();
		
		HorizontalPanel flapHPanel = new HorizontalPanel();
		flapHPanel.setSpacing(0);

		Label title = new Label(gadgetTitle);
		title.setStyleName("flapLabel");
		flapHPanel.add(title);

		FocusWidget closeButton = new FocusWidget(new Label(" ").getElement()) {};
		closeButton.setStyleName("tabCloseButton");
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				closeCanvasView(profileGadgetId, activateProfile);
			}
		});

		closeButton.addKeyDownHandler(new KeyDownHandler()
		{
			public void onKeyDown(KeyDownEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					event.stopPropagation();
					closeCanvasView(profileGadgetId, activateProfile);
				}
			}
		});
		closeButton.setVisible(true);
		flapHPanel.add(closeButton);
		flap.setContentWidget(flapHPanel);
		
		return flap;
	}

	/**
	 * 
	 */
	@Override
	protected GadgetShindigClassHandler createShindigClassHandler()
	{
	    return new TabGadgetShindigClassHandler(this);
	}
	
	/**
	 * Screen wrapper for template widgets.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface TabLayoutScreen extends ScreenWrapper
	{
		RollingTabPanel getLayoutTabManager();
	}	
}