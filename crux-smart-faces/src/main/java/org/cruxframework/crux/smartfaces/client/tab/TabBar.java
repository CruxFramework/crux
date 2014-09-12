/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.tab;

import org.cruxframework.crux.smartfaces.client.label.HTML;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;
import org.cruxframework.crux.smartfaces.client.rollingpanel.RollingPanel;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Modified version of GWT TabBar that uses a RollingPanel wrapping its Tabs.
 * 
 * @author Thiago da Rosa de Bustamante
 * @author Bruno Medeiros
 * 
 * 
 */
class TabBar extends Composite implements HasBeforeSelectionHandlers<Integer>, HasSelectionHandlers<Integer>
{
	private static final String TAB_BAR_ITEM_SELECTED_STYLE_NAME = "faces-TabBar-item--selected";
	private static final String FLAP_LABEL_STYLE_NAME = "faces-TabBar-flapLabel";
	private static final String DEFAULT_STYLE_NAME = "faces-TabBar";
	private static final String ITEM_STYLE_NAME = "faces-TabBar-item";

	private RollingPanel panel;
	private Widget selectedTab;

	/**
	 * Creates an empty tab bar.
	 */
	TabBar()
	{
		panel = new RollingPanel();

		initWidget(panel);
		setStyleName(DEFAULT_STYLE_NAME);
		panel.setScrollToAddedWidgets(true);

		// Add a11y role "tablist"
		Roles.getTablistRole().set(panel.getElement());
	}

	@Override
	public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler)
	{
		return addHandler(handler, BeforeSelectionEvent.getType());
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler)
	{
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * Gets the tab that is currently selected.
	 * 
	 * @return the selected tab
	 */
	public int getSelectedTab()
	{
		if (selectedTab == null)
		{
			return -1;
		}
		return panel.getWidgetIndex(selectedTab);
	}

	/**
	 * Gets the number of tabs present.
	 * 
	 * @return the tab count
	 */
	public int getTabCount()
	{
		return panel.getWidgetCount();
	}

	/**
	 * Enable or disable a tab. When disabled, users cannot select the tab.
	 * 
	 * @param index the index of the tab to enable or disable
	 * @param enabled true to enable, false to disable
	 */
	public void setTabEnabled(int index, boolean enabled)
	{
		assert (index >= 0) && (index < getTabCount()) : "Flap index out of bounds";

		// Style the wrapper
		ClickDelegatePanel delPanel = (ClickDelegatePanel) panel.getWidget(index);
		delPanel.setEnabled(enabled);

		if (enabled)
		{
			delPanel.removeStyleDependentName("disabled");
		}
		else
		{
			delPanel.addStyleDependentName("disabled");
		}
	}

	public void setTabWordWrap(int index, boolean wordWrap)
	{
		assert (index >= 0) && (index < getTabCount()) : "Flap index out of bounds";

		// Style the wrapper
		ClickDelegatePanel delPanel = (ClickDelegatePanel) panel.getWidget(index);
		delPanel.setWordWrap(wordWrap);
	}
	
	/**
	 * Inserts a new tab at the specified index.
	 * 
	 * @param widget widget to be used in the new tab
	 * @param beforeIndex the index before which this tab will be inserted
	 */
	public void insertTab(Widget widget, int beforeIndex)
	{
		insertTabWidget(widget, beforeIndex);
	}

	/**
	 * Inserts a new tab at the specified index.
	 * 
	 * @param text the new tab's text
	 * @param asHTML <code>true</code> to treat the specified text as HTML
	 * @param beforeIndex the index before which this tab will be inserted
	 */
	public void insertTab(String text, boolean asHTML, int beforeIndex)
	{
		checkInsertBeforeTabIndex(beforeIndex);

		Widget item;
		if (asHTML)
		{
			HTML label = new HTML(text);
			label.setWordWrap(false);
			item = label;
		}
		else
		{
			Label label = new Label(text);
			label.setWordWrap(false);
			item = label;
		}
		item.setStyleName(FLAP_LABEL_STYLE_NAME);
		insertTabWidget(item, beforeIndex);
	}

	/**
	 * Programmatically selects the specified tab. Use index -1 to specify that no tab should be selected.
	 * 
	 * @param index the index of the tab to be selected
	 * @return <code>true</code> if successful, <code>false</code> if the change is denied by the {@link BeforeSelectionHandler}.
	 */
	public boolean selectTab(int index)
	{
		checkTabIndex(index);
		BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, index);

		if (event != null && event.isCanceled())
		{
			return false;
		}

		// Check for -1.
		setSelectionStyle(selectedTab, false);
		if (index == -1)
		{
			selectedTab = null;
			return true;
		}

		selectedTab = panel.getWidget(index);
		setSelectionStyle(selectedTab, true);
		panel.scrollToWidget(selectedTab);
		SelectionEvent.fire(this, index);
		return true;
	}

	/**
	 * Inserts a new tab at the specified index.
	 * 
	 * @param widget widget to be used in the new tab
	 * @param beforeIndex the index before which this tab will be inserted
	 */
	protected void insertTabWidget(Widget widget, int beforeIndex)
	{
		checkInsertBeforeTabIndex(beforeIndex);

		ClickDelegatePanel delWidget = new ClickDelegatePanel(widget);
		delWidget.setStyleName(ITEM_STYLE_NAME);

		// Add a11y role "tab"
		SelectablePanel focusablePanel = delWidget.getFocusablePanel();
		Roles.getTabRole().set(focusablePanel.getElement());
		
		panel.insert(delWidget, beforeIndex);
	}

	/**
	 * <b>Affected Elements:</b>
	 * <ul>
	 * <li>-tab# = The element containing the contents of the tab.</li>
	 * <li>-tab-wrapper# = The cell containing the tab at the index.</li>
	 * </ul>
	 * 
	 * @see UIObject#onEnsureDebugId(String)
	 */
	@Override
	protected void onEnsureDebugId(String baseID)
	{
		super.onEnsureDebugId(baseID);

		int numTabs = getTabCount();
		for (int i = 0; i < numTabs; i++)
		{
			ClickDelegatePanel delPanel = (ClickDelegatePanel) panel.getWidget(i);
			SelectablePanel focusablePanel = delPanel.getFocusablePanel();
			ensureDebugId(focusablePanel.getElement(), baseID, "tab" + i);
		}
	}

	/**
	 * Removes the tab at the specified index.
	 * 
	 * @param index the index of the tab to be removed
	 */
	protected void removeTab(int index)
	{
		checkTabIndex(index);

		Widget toRemove = panel.getWidget(index);
		if (toRemove == selectedTab)
		{
			selectedTab = null;
		}
		panel.remove(toRemove);
	}

	private void checkInsertBeforeTabIndex(int beforeIndex)
	{
		if ((beforeIndex < 0) || (beforeIndex > getTabCount()))
		{
			throw new IndexOutOfBoundsException();
		}
	}

	private void checkTabIndex(int index)
	{
		if ((index < -1) || (index >= getTabCount()))
		{
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Selects the tab corresponding to the widget for the tab. To be clear the widget for the tab is not the widget INSIDE of the tab; it
	 * is the widget used to represent the tab itself.
	 * 
	 * @param tabWidget The widget for the tab to be selected
	 * @return true if the tab corresponding to the widget for the tab could located and selected, false otherwise
	 */
	private boolean selectTabByTabWidget(Widget tabWidget)
	{
		int numTabs = panel.getWidgetCount();

		for (int i = 0; i < numTabs; ++i)
		{
			if (panel.getWidget(i) == tabWidget)
			{
				return selectTab(i);
			}
		}

		return false;
	}

	private void setSelectionStyle(Widget item, boolean selected)
	{
		if (item != null)
		{
			if (selected)
			{
				item.addStyleName(TAB_BAR_ITEM_SELECTED_STYLE_NAME);
			}
			else
			{
				item.removeStyleName(TAB_BAR_ITEM_SELECTED_STYLE_NAME);
			}
		}
	}

	/**
	 * <code>ClickDelegatePanel</code> decorates any widget with the minimal amount of machinery to receive clicks for delegation to the
	 * parent. {@link SourcesClickEvents} is not implemented due to the fact that only a single observer is needed.
	 */
	private class ClickDelegatePanel extends Composite
	{
		private boolean enabled = true;
		private SelectablePanel focusablePanel;

		ClickDelegatePanel(Widget child)
		{

			focusablePanel = new SelectablePanel();
			focusablePanel.getElement().setTabIndex(0);
			focusablePanel.setWidget(child);
			initWidget(focusablePanel);
			
			sinkEvents(Event.ONKEYDOWN);
		}

		SelectablePanel getFocusablePanel()
		{
			return focusablePanel;
		}

		public boolean hasWordWrap()
		{
			return focusablePanel.getChildWidget() instanceof HasWordWrap;
		}

		@Override
		public void onBrowserEvent(Event event)
		{
			if (!enabled)
			{
				return;
			}

			// No need for call to super.
			switch (DOM.eventGetType(event))
			{

				case Event.ONKEYDOWN:
					if (((char) event.getKeyCode()) == KeyCodes.KEY_ENTER)
					{
						TabBar.this.selectTabByTabWidget(this);
					}
				break;
			}
			super.onBrowserEvent(event);
		}

		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}

		public void setWordWrap(boolean wrap)
		{
			if (hasWordWrap())
			{
				((HasWordWrap) focusablePanel.getChildWidget()).setWordWrap(wrap);
			}
			else
			{
				throw new UnsupportedOperationException("Widget does not implement HasWordWrap");
			}
		}
	}
}
