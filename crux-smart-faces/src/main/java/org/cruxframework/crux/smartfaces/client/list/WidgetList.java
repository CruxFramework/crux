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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.shared.Experimental;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A list of widgets
 * @author Thiago da Rosa de Bustamante
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class WidgetList<T> extends AbstractPageable<T>
{
	private static final String DEFAULT_STYLE_NAME = "faces-WidgetList";
	
	protected FlowPanel contentPanel = new FlowPanel();
	protected final WidgetFactory<T> widgetFactory;

	/**
	 * Constructor
	 * @param widgetFactory
	 */
	public WidgetList(WidgetFactory<T> widgetFactory)
    {
		assert(widgetFactory != null);
		this.widgetFactory = widgetFactory;
		initWidget(contentPanel);
		setStyleName(DEFAULT_STYLE_NAME);
    }
	
	@Override
	public void reset(boolean reloadData)
	{
		clear();
		super.reset(reloadData);
	}
	
	/**
	 * Retrieve the dataObject that is bound to the given widget
	 * @param w
	 * @return
	 */
	public T getDataObject(Widget w)
	{
		int widgetIndex = contentPanel.getWidgetIndex(w);
		if (widgetIndex >= 0)
		{
			return getDataProvider().get(widgetIndex);
		}
		return null;
	}

	/**
	 * Retrieve the widget index
	 * @param w
	 * @return
	 */
	public int getWidgetIndex(Widget w)
	{
		return contentPanel.getWidgetIndex(w);
	}
	
	@Override
	protected void clear()
	{
		contentPanel.clear();
	}
	
	@Override
	protected void clearRange(int start)
	{
		while (contentPanel.getWidgetCount() > start)
		{
			contentPanel.remove(start);
		}
	}
	
	@Override
	protected AbstractPageable.Renderer<T> getRenderer()
	{
	    return new AbstractPageable.Renderer<T>()
	    {
			@Override
            public void render(T value)
            {
				IsWidget widget = widgetFactory.createWidget(value);
				contentPanel.add(widget);
            }
	    };
	}
}
