package org.cruxframework.crux.widgets.client.disposal.menutabsdisposal;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Gesse Dafe
 *
 */
//TODO rever estrutura de componentes em todos os disposal
abstract class AbstractMenuTabsDisposalController extends DeviceAdaptiveController implements MenuTabsDisposal
{
	private static final String HISTORY_PREFIX = "view:";
	
	private FlowPanel menuPanel;
	private FlowPanel headerPanel;
	private Map<String, FlowPanel> sections = new HashMap<String, FlowPanel>();
	private FlowPanel lastSectionAdded = null;
	private String lastVisitedView = null;

	public void init(FlowPanel headerPanel, FlowPanel menuPanel)
	{
		this.headerPanel = headerPanel;
		this.menuPanel = menuPanel;
		
		Screen.addHistoryChangedHandler(new ValueChangeHandler<String>() 
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event) 
			{
				String token = event.getValue();
				if(token != null && token.startsWith(HISTORY_PREFIX))
				{
					showView(token.replace(HISTORY_PREFIX, ""), false, Direction.FORWARD);
				}
			}
		});
	}

	protected abstract void doShowView(String targetView, Direction direction);

	@Override
	public final void addMenuEntry(final String label, final String targetView)
	{
		Button menuItem = new Button();
		menuItem.addStyleName("menuEntry");
		menuItem.setText(label);
		menuItem.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				showView(targetView, true, Direction.FORWARD);
			}
		});
		
		if(lastSectionAdded == null)
		{
			menuPanel.add(menuItem);
		}
		else
		{
			((FlowPanel) lastSectionAdded.getWidget(0)).add(menuItem);
		}
	}

	protected void showView(final String targetView, boolean saveHistory, Direction direction) 
	{
		if(saveHistory)
		{
			if(lastVisitedView == null || !lastVisitedView.equals(targetView))
			{
				ScheduledCommand cmd = new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						History.newItem(HISTORY_PREFIX + targetView);
					}
				};
				
				Scheduler.get().scheduleDeferred(cmd );
			}
		}
		
		lastVisitedView = targetView;
		doShowView(targetView, direction);
	}

	@Override
	public final void addMenuSection(final String label, String additionalStyleName)
	{
		final Button section = new Button();
		section.setStyleName("menuSection");
		section.getElement().getStyle().setDisplay(Display.BLOCK);
		section.setText(label);
		
		if(!StringUtils.isEmpty(additionalStyleName))
		{
			section.addStyleName(additionalStyleName);
		}
		
		section.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				FlowPanel items = sections.get(label);
				if(items != null)
				{
					if(isSectionOpen(items))
					{
						closeSection(section, items);
					}
					else
					{
						openSection(section, items);
					}
				}
			}
		});
		
		FlowPanel sectionItems = new FlowPanel();
		sectionItems.setStyleName("menuSectionEntries");
		closeSection(section, sectionItems);
		
		FlowPanel sectionItemsContent = new FlowPanel();
		sectionItemsContent.setStyleName("menuSectionEntriesContent");
		sectionItems.add(sectionItemsContent);
		
		sections.put(label, sectionItems);
		lastSectionAdded = sectionItems;
		
		menuPanel.add(section);
		menuPanel.add(sectionItems);
	}
	
	@Override
	public final void setHeaderContent(IsWidget widget)
	{
		headerPanel.add(widget);
	}
	
	private void openSection(Button section, FlowPanel items) 
	{
		section.removeStyleDependentName("closed");
		items.removeStyleDependentName("closed");
		items.getElement().getStyle().setProperty("height", calculateOpenSectionHeight(items) + "px");
	}
	
	private int calculateOpenSectionHeight(FlowPanel items) 
	{
		Widget content = items.getWidget(0);
		int height = content.getOffsetHeight();
		return height;
	}

	private void closeSection(Button section, FlowPanel items) 
	{
		section.addStyleDependentName("closed");
		items.addStyleDependentName("closed");
		items.getElement().getStyle().setProperty("height", "0px");
	}

	private boolean isSectionOpen(FlowPanel items) 
	{
		return !items.getStyleName().contains("-closed");
	}

	@Override
	public final void showView(String targetView, Direction direction) 
	{
		showView(targetView, true, Direction.FORWARD);
	}
	
	public FlowPanel getMenuPanel()
	{
		return menuPanel;
	}
	
	protected String getLastVisitedView()
	{
		return lastVisitedView;
	}
}
