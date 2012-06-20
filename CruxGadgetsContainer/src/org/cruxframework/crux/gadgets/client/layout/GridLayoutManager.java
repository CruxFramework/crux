package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.gadgets.client.GadgetContainerMsg;
import org.cruxframework.crux.gadgets.client.container.ContainerView;
import org.cruxframework.crux.gadgets.client.container.Gadget;
import org.cruxframework.crux.gadgets.client.container.GadgetContainer;
import org.cruxframework.crux.gadgets.client.container.GadgetMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GridLayoutManager implements LayoutManager
{
	protected GadgetContainerMsg messages;
	protected LayoutScreen screen;

	public GridLayoutManager()
    {
	    messages = GWT.create(GadgetContainerMsg.class);
	    screen = GWT.create(LayoutScreen.class);
    }
		
	/**
	 * Configure the Gadgets Dashboard. Called once, when the container page is loaded.
	 * @param configuration
	 */
	public void configure()
	{
		GadgetShindigClassHandler shindigClassHandler = createShindigClassHandler();
		shindigClassHandler.createNativeLayoutManager();
		shindigClassHandler.createNativeGadgetClass();
	    createdGridDashboard();
		makeDashboardSortable();
	}

	/**
	 * 
	 * @param gadget
	 * @param referenceElement
	 */
	public void openMenuOptions(Gadget gadget, final Element referenceElement)
	{
		class Wrapper extends UIObject
		{
			public Wrapper()
            {
	            setElement(referenceElement);
            }
		};

		DialogBox optionsDialog = new DialogBox(true);
		VerticalPanel optionsFlowPanel = new VerticalPanel();
		optionsDialog.setWidget(optionsFlowPanel);
		
		Anchor delete = createDeleteGadgetButton(gadget.getId(), optionsDialog);
		optionsFlowPanel.add(delete);
		
		if (gadget.hasViewablePrefs())
		{
			Anchor settings = createSettingsGadgetButton(gadget.getId(), optionsDialog);
			optionsFlowPanel.add(settings);
		}
		Anchor about = new Anchor(messages.aboutGadgetLink());
		optionsFlowPanel.add(about);

		optionsDialog.setStyleName(gadget.getCssOptionsMenu());
		optionsDialog.showRelativeTo(new Wrapper());
	}
	
	/**
	 * 
	 * @param gadgetId
	 * @param profileView
	 */
	public void changeGadgetView(int gadgetId, ContainerView view)
	{
		UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
		if (view.equals(ContainerView.profile))
		{
			String gadgetUrl = GadgetContainer.get().getGadget(gadgetId).getUrl();
			if (StringUtils.isEmpty(gadgetUrl))
			{
				Crux.getErrorHandler().handleError(messages.gadgetNotFound(gadgetId));
			}
			urlBuilder.setParameter("url", gadgetUrl);
		}
		else
		{
			urlBuilder.removeParameter("url");
		}
		Window.Location.assign(urlBuilder.buildString());
	}
	
	/**
	 * Retrieve the DIV element used as wrapper to render the requested gadget 
	 * @param gadgetId gadget identifier
	 * @return
	 */
	public Element getGadgetChrome(int gadgetId)
	{
		Element element = DOM.getElementById(getGadgetChromeId(gadgetId));
		return element;
	}
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId
	 * @return
	 */
	public String getGadgetChromeId(int gadgetId)
	{
		return "gadget-chrome-"+gadgetId;
	}
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId
	 * @return
	 */
	public String getGadgetId(Element gadgetChrome)
	{
		return gadgetChrome.getId().substring(14);
	}
	
	@Override
    public void openGadget(GadgetMetadata gadgetMetadata, ContainerView view)
    {
		if (view.equals(ContainerView.profile))
		{
			GadgetContainer container = GadgetContainer.get();
			if (container.getCurrentView().equals(ContainerView.profile))
			{
				Gadget gadget = container.createGadget(gadgetMetadata, view);
				container.addGadget(gadget);
				addGadgetChrome(gadget.getId());
				container.renderGadget(gadget);
			}
			else
			{
				// Defer the refresh...to allow container to save state first
				Scheduler.get().scheduleDeferred(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
						urlBuilder.removeParameter("url");
						Window.Location.assign(urlBuilder.buildString());
					}
				});
			}
		}
		else
		{
			// Canvas view 
			UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
			String gadgetUrl = gadgetMetadata.getUrl();
			urlBuilder.setParameter("url", gadgetUrl);
			Window.Location.assign(urlBuilder.buildString());
		}
    }
	
	/**
	 * 
	 * @return
	 */
	protected GadgetShindigClassHandler createShindigClassHandler()
	{
		return new GadgetShindigClassHandler(this);
	}

	/**
	 * Report error caused by a duplicated layoutManager.
	 */
	protected void reportNativeConfigureFunctionError()
	{
		Crux.getErrorHandler().handleError(messages.duplicatedLayoutManager());
	}

	/**
	 * Create the container grid for gadgets.
	 */
	protected void createdGridDashboard()
    {
	    Element gridContainer = screen.getGridContainer().getElement();
		gridContainer.setInnerHTML(generateGridDashboard());
    }

	/**
	 * 
	 * @param gadgetId
	 * @param optionsDialog 
	 * @return
	 */
	protected Anchor createSettingsGadgetButton(final int gadgetId, final DialogBox optionsDialog)
    {
	    Anchor settings = new Anchor(messages.settingsGadgetLink());
	    settings.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				GadgetContainer.get().getGadget(gadgetId).handleOpenUserPrefsDialog();
				optionsDialog.hide();
			}
		});
	    return settings;
    }

	/**
	 * 
	 * @param gadgetId
	 * @param optionsDialog
	 * @return
	 */
	protected Anchor createDeleteGadgetButton(final int gadgetId, final DialogBox optionsDialog)
    {
	    Anchor delete = new Anchor(messages.deleteGadgetLink());
	    delete.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				GadgetContainer.get().removeGadget(gadgetId);
				removeDom(gadgetId);
				optionsDialog.hide();
			}

			private void removeDom(int gadgetId)
            {
				Element gadgetChrome = DOM.getElementById(getGadgetChromeId(gadgetId));
				gadgetChrome.removeFromParent();
            }
		});
	    return delete;
    }
		
	/**
	 * Generate the HTML structure for the Gadgets dashboard.
	 * @return
	 */
	protected String generateGridDashboard()
	{
		StringBuilder tableHtml = new StringBuilder();
		Array<Array<GadgetMetadata>> gadgetConfigs = GadgetContainer.get().getMetadata();
		tableHtml.append("<div class='LayoutGrid' id ='LayoutGrid' style='width:100%;float:left;position:relative;'>"); 
		
		int numColumns = gadgetConfigs.size();
		int colWidth = 100 / numColumns;
		int gadgetId = 0;
		for (int i=0; i< numColumns; i++)
		{
			Array<GadgetMetadata> column = gadgetConfigs.get(i);
			int numRows = column.size();
			tableHtml.append("<div id='gadgets-grid-column-"+i+"' class='LayoutColumn' style='width:"+colWidth+"%;float:left;padding-bottom:100px;vertical-align:top'>"); 
			for (int j=0; j< numRows; j++)
			{
				tableHtml.append("<div id='"+getGadgetChromeId(gadgetId++)+"' class='gadgets-gadget-chrome'></div>"); 
			}
			tableHtml.append("</div>");
		}
		tableHtml.append("</div>");
		return tableHtml.toString();
	}
	
	/**
	 * Create a new wrapper for the given gadget
	 * @param gadgetId
	 */
	protected void addGadgetChrome(int gadgetId)
	{
		Element column = DOM.getElementById("gadgets-grid-column-0");
		Element gadgetChrome = DOM.createDiv();
		gadgetChrome.setId(getGadgetChromeId(gadgetId));
		gadgetChrome.setClassName("gadgets-gadget-chrome");
		column.insertFirst(gadgetChrome);
		makeDashboardSortable();
	}
	
	/**
	 * Transform the Gadgets dashboard's HTML structure into a dragable container. It will consist in a collection of 
	 * columns with sortable dragable elements.
	 */
	protected void makeDashboardSortable()
    {
	    Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				DashboardConfig dashboardConfig = GWT.create(DashboardConfig.class);
				dashboardConfig.makeDashboardSortable(GridLayoutManager.this);
			}
		});
    }
	
	public static class DashboardConfig
	{
		/**
		 * Transform the Gadgets dashboard's HTML structure into a dragable container. It will consist in a collection of 
		 * columns with sortable dragable elements.
		 * @param manager
		 */
		public void makeDashboardSortable(LayoutManager manager)
		{
			makeDashboardSortableNative(manager);
		}
		
		private native void makeDashboardSortableNative(LayoutManager manager)/*-{
			$wnd.$(function() {
				var draggingFrame;
				$wnd.$( ".LayoutColumn" ).sortable({
					connectWith: ".LayoutColumn",
					appendTo: '#LayoutGrid',
					containment: '#LayoutGrid',
					forcePlaceholderSize: true,
					helper: 'original',
					start: function(event, ui){
						var gadgetId = manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
						
						var framePrefix = $wnd.shindig.container.gadgetClass.prototype.GADGET_IFRAME_PREFIX_;
						draggingFrame = $doc.getElementById( framePrefix+gadgetId );
						draggingFrame.style.display = 'none';
					},
					stop: function(event, ui){
						var gadgetId = manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
						if (draggingFrame) {
							draggingFrame.style.display = '';
							draggingFrame = null;
						}
						var gadget = $wnd.shindig.container.getGadget(gadgetId);
	  			    	gadget.refresh();
					}
				}).disableSelection();
			});	
		}-*/;
	}

	public static class DashboardConfigIE extends DashboardConfig
	{
		/**
		 * Transform the Gadgets dashboard's HTML structure into a dragable container. It will consist in a collection of 
		 * columns with sortable dragable elements.
		 * @param manager
		 */
		@Override
		public void makeDashboardSortable(LayoutManager manager)
		{
			makeDashboardSortableNative(manager);
		}
		
		private native void makeDashboardSortableNative(LayoutManager manager)/*-{
			$wnd.$(function() {
				var draggingFrame;
				$wnd.$( ".LayoutColumn" ).sortable({
					connectWith: ".LayoutColumn",
					appendTo: '#LayoutGrid',
					containment: '#LayoutGrid',
					forcePlaceholderSize: true,
					helper: 'clone',
					placeholder: 'ui-sortable-placeholder-ie',
					start: function(event, ui){
						var gadgetId = manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
						
						var framePrefix = $wnd.shindig.container.gadgetClass.prototype.GADGET_IFRAME_PREFIX_;
						draggingFrame = $doc.getElementById( framePrefix+gadgetId );
						draggingFrame.style.display = 'none';
					},
					stop: function(event, ui){
						var gadgetId = manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
						if (draggingFrame) {
							draggingFrame.style.display = '';
							draggingFrame = null;
						}
						var gadget = $wnd.shindig.container.getGadget(gadgetId);
	  			    	gadget.refresh();
					}
				}).disableSelection();
			});	
		}-*/;
	}
	
	/**
	 * Screen wrapper for template widgets.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface LayoutScreen extends ScreenWrapper
	{
		HTML getGridContainer();
	}
}