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
package org.cruxframework.crux.smartfaces.client.rollingpanel;

import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class RollingPanel extends Composite implements InsertPanel
{
	static final String DEFAULT_STYLE_NAME = "faces-RollingPanel";
	
	private PanelImplementation impl = GWT.create(PanelImplementation.class);
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static interface PanelImplementation extends InsertPanel, IsWidget
	{
		void clear();
		int getScrollPosition();
		boolean isScrollToAddedWidgets();
		boolean remove(Widget toRemove);
		void scrollToWidget(Widget widget);
		void setScrollPosition(int position);
		void setScrollToAddedWidgets(boolean scrollToAddedWidgets);
		void setStyleName(String style);
		void setStyleName(String style, boolean add);
	}
		
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	
	public RollingPanel()
    {
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
	    initWidget(impl.asWidget());
	    setStyleName(DEFAULT_STYLE_NAME);
    }
	
	@Override
    public Widget getWidget(int index)
    {
	    return impl.getWidget(index);
    }

	@Override
    public int getWidgetCount()
    {
	    return impl.getWidgetCount();
    }

	@Override
    public int getWidgetIndex(Widget child)
    {
	    return impl.getWidgetIndex(child);
    }

	@Override
    public boolean remove(int index)
    {
	    return impl.remove(index);
    }

	@Override
    public void add(Widget w)
    {
		impl.add(w);
    }

	@Override
    public void insert(Widget w, int beforeIndex)
    {
		impl.insert(w, beforeIndex);
    }
	
    public void clear()
    {
		impl.clear();
    }

    public int getScrollPosition()
    {
        return impl.getScrollPosition();
    }

    public boolean isScrollToAddedWidgets()
    {
        return impl.isScrollToAddedWidgets();
    }

    public void remove(Widget toRemove)
    {
    	impl.remove(toRemove);
    }

    public void scrollToWidget(Widget widget)
    {
    	impl.scrollToWidget(widget);
    }

    public void setScrollPosition(int position)
    {
    	impl.setScrollPosition(position);			
    }

    public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
    	impl.setScrollToAddedWidgets(scrollToAddedWidgets);
    }
    
    @Override
    public void setStyleName(String style)
    {
        impl.setStyleName(style);
    }
    
    @Override
    public void setStyleName(String style, boolean add)
    {
        impl.setStyleName(style, add);
    }
}
