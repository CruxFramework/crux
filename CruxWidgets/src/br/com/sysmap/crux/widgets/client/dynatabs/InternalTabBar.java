/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.dynatabs;

import br.com.sysmap.crux.widgets.client.rollingpanel.RollingPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Modified version of GWT TabBar that uses a RollingPanel wrapping its Tabs.
 * 
 * @author Thiago da Rosa de Bustamante -
 *         <code>tr_bustamante@yahoo.com.br</code>
 * 
 */
class InternalTabBar extends Composite implements HasBeforeSelectionHandlers<Integer>, HasSelectionHandlers<Integer>
{
	private static final String STYLENAME_DEFAULT = "crux-TabBarItem";

	private RollingPanel panel;
	private Widget selectedTab;

	/**
	 * Creates an empty tab bar.
	 */
	InternalTabBar()
	{
		panel = new RollingPanel(false);

		initWidget(panel);
		sinkEvents(Event.ONCLICK);
		setStyleName("crux-TabBar");
		panel.setSpacing(0);
		panel.setScrollToAddedWidgets(true);

		// Add a11y role "tablist"
		Accessibility.setRole(panel.getElement(), Accessibility.ROLE_TABLIST);

		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		HTML first = new HTML("&nbsp;", true), rest = new HTML("&nbsp;", true);
		first.setStyleName("crux-TabBarFirst");
		rest.setStyleName("crux-TabBarRest");
		first.setHeight("100%");
		rest.setHeight("100%");

		panel.add(first);
		panel.add(rest);
		first.setHeight("100%");
		panel.setCellHeight(first, "100%");
		panel.setCellWidth(rest, "100%");
		setStyleName(first.getElement().getParentElement(),
		        "crux-TabBarFirst-wrapper");
		setStyleName(rest.getElement().getParentElement(),
		        "crux-TabBarRest-wrapper");
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers#addBeforeSelectionHandler(com.google.gwt.event.logical.shared.BeforeSelectionHandler)
	 */
	public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler)
	{
		return addHandler(handler, BeforeSelectionEvent.getType());
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
	 */
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
		return panel.getWidgetIndex(selectedTab) - 1;
	}

	/**
	 * Gets the number of tabs present.
	 * 
	 * @return the tab count
	 */
	public int getTabCount()
	{
		return panel.getWidgetCount() - 2;
	}

	/**
	 * Inserts a new tab at the specified index.
	 * 
	 * @param widget
	 *            widget to be used in the new tab
	 * @param beforeIndex
	 *            the index before which this tab will be inserted
	 */
	public void insertTab(Widget widget, int beforeIndex)
	{
		insertTabWidget(widget, beforeIndex);
	}

	/**
	 * Programmatically selects the specified tab. Use index -1 to specify that
	 * no tab should be selected.
	 * 
	 * @param index
	 *            the index of the tab to be selected
	 * @return <code>true</code> if successful, <code>false</code> if the change
	 *         is denied by the {@link BeforeSelectionHandler}.
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

		selectedTab = panel.getWidget(index + 1);
		setSelectionStyle(selectedTab, true);
		panel.scrollToWidget(selectedTab);
		SelectionEvent.fire(this, index);
		return true;
	}

	/**
	 * Inserts a new tab at the specified index.
	 * 
	 * @param widget
	 *            widget to be used in the new tab
	 * @param beforeIndex
	 *            the index before which this tab will be inserted
	 */
	protected void insertTabWidget(Widget widget, int beforeIndex)
	{
		checkInsertBeforeTabIndex(beforeIndex);

		ClickDelegatePanel delWidget = new ClickDelegatePanel(widget);
		delWidget.setStyleName(STYLENAME_DEFAULT);

		// Add a11y role "tab"
		SimplePanel focusablePanel = delWidget.getFocusablePanel();
		Accessibility.setRole(focusablePanel.getElement(),
		        Accessibility.ROLE_TAB);

		panel.insert(delWidget, beforeIndex + 1);

		setStyleName(DOM.getParent(delWidget.getElement()), STYLENAME_DEFAULT
		        + "-wrapper", true);
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
			ClickDelegatePanel delPanel = (ClickDelegatePanel) panel
			        .getWidget(i + 1);
			SimplePanel focusablePanel = delPanel.getFocusablePanel();
			ensureDebugId(focusablePanel.getElement(), baseID, "tab" + i);
			ensureDebugId(DOM.getParent(delPanel.getElement()), baseID,
			        "tab-wrapper" + i);
		}
	}

	/**
	 * Removes the tab at the specified index.
	 * 
	 * @param index
	 *            the index of the tab to be removed
	 */
	protected void removeTab(int index)
	{
		checkTabIndex(index);

		// (index + 1) to account for 'first' placeholder widget.
		Widget toRemove = panel.getWidget(index + 1);
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
	 * Selects the tab corresponding to the widget for the tab. To be clear the
	 * widget for the tab is not the widget INSIDE of the tab; it is the widget
	 * used to represent the tab itself.
	 * 
	 * @param tabWidget
	 *            The widget for the tab to be selected
	 * @return true if the tab corresponding to the widget for the tab could
	 *         located and selected, false otherwise
	 */
	private boolean selectTabByTabWidget(Widget tabWidget)
	{
		int numTabs = panel.getWidgetCount() - 1;

		for (int i = 1; i < numTabs; ++i)
		{
			if (panel.getWidget(i) == tabWidget)
			{
				return selectTab(i - 1);
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
				item.addStyleName("crux-TabBarItem-selected");
				setStyleName(DOM.getParent(item.getElement()),
				        "crux-TabBarItem-wrapper-selected", true);
			}
			else
			{
				item.removeStyleName("crux-TabBarItem-selected");
				setStyleName(DOM.getParent(item.getElement()),
				        "crux-TabBarItem-wrapper-selected", false);
			}
		}
	}

	/**
	 * Set of characteristic interfaces supported by {@link TabBar} tabs.
	 * 
	 * Note that this set might expand over time, so implement this interface at
	 * your own risk.
	 */
	public interface Tab extends HasAllKeyHandlers, HasClickHandlers,
	        HasWordWrap
	{
		/**
		 * Check if the underlying widget implements {@link HasWordWrap}.
		 * 
		 * @return true if the widget implements {@link HasWordWrap}
		 */
		boolean hasWordWrap();
	}

	/**
	 * <code>ClickDelegatePanel</code> decorates any widget with the minimal
	 * amount of machinery to receive clicks for delegation to the parent.
	 * {@link SourcesClickEvents} is not implemented due to the fact that only a
	 * single observer is needed.
	 */
	private class ClickDelegatePanel extends Composite implements Tab
	{
		private boolean enabled = true;
		private SimplePanel focusablePanel;

		ClickDelegatePanel(Widget child)
		{

			focusablePanel = new SimplePanel();
			focusablePanel.getElement().setTabIndex(0);
			focusablePanel.setWidget(child);
			initWidget(focusablePanel);

			sinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
		}

		public HandlerRegistration addClickHandler(ClickHandler handler)
		{
			return addHandler(handler, ClickEvent.getType());
		}

		public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
		{
			return addHandler(handler, KeyDownEvent.getType());
		}

		public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
		{
			return addDomHandler(handler, KeyPressEvent.getType());
		}

		public HandlerRegistration addKeyUpHandler(KeyUpHandler handler)
		{
			return addDomHandler(handler, KeyUpEvent.getType());
		}

		public SimplePanel getFocusablePanel()
		{
			return focusablePanel;
		}

		public boolean getWordWrap()
		{
			if (hasWordWrap())
			{
				return ((HasWordWrap) focusablePanel.getWidget()).getWordWrap();
			}
			throw new UnsupportedOperationException(
			        "Widget does not implement HasWordWrap");
		}

		public boolean hasWordWrap()
		{
			return focusablePanel.getWidget() instanceof HasWordWrap;
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
			case Event.ONCLICK:
				InternalTabBar.this.selectTabByTabWidget(this);
				break;

			case Event.ONKEYDOWN:
				if (((char) DOM.eventGetKeyCode(event)) == KeyCodes.KEY_ENTER)
				{
					InternalTabBar.this.selectTabByTabWidget(this);
				}
				break;
			}
			super.onBrowserEvent(event);
		}

		public void setWordWrap(boolean wrap)
		{
			if (hasWordWrap())
			{
				((HasWordWrap) focusablePanel.getWidget()).setWordWrap(wrap);
			}
			else
			{
				throw new UnsupportedOperationException(
				        "Widget does not implement HasWordWrap");
			}
		}
	}
}
