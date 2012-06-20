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
package org.cruxframework.crux.crossdevice.client.toptoolbar;

import java.util.Iterator;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeOpenHandler;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("topToolBarLargeController")
public class TopToolBarLargeController extends DeviceAdaptiveController implements TopToolBar
{

	protected FlowPanel floatPanel;
	
	@Override
    public Widget getWidget(int index)
    {
	    return floatPanel.getWidget(index);
    }

	@Override
    public int getWidgetCount()
    {
	    return floatPanel.getWidgetCount();
    }

	@Override
    public int getWidgetIndex(Widget child)
    {
	    return floatPanel.getWidgetIndex(child);
    }

	@Override
    public boolean remove(int index)
    {
	    return floatPanel.remove(index);
    }

	@Override
    public void add(Widget w)
    {
		floatPanel.add(w);
	    w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

	@Override
    public void clear()
    {
		floatPanel.clear();
    }

	@Override
    public Iterator<Widget> iterator()
    {
	    return floatPanel.iterator();
    }

	@Override
    public boolean remove(Widget w)
    {
	    return floatPanel.remove(w);
    }

	@Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
    {
	    return createEmptyHandlerRegistration();
    }

	@Override
    public HandlerRegistration addBeforeOpenHandler(BeforeOpenHandler handler)
    {
	    return createEmptyHandlerRegistration();
    }

	@Override
    public HandlerRegistration addOpenHandler(OpenHandler<TopToolBar> handler)
    {
	    return createEmptyHandlerRegistration();
    }

	@Override
    public HandlerRegistration addCloseHandler(CloseHandler<TopToolBar> handler)
    {
	    return createEmptyHandlerRegistration();
    }

	@Override
    public void setGripWidget(Widget widget)
    {
    }

	@Override
    public Widget getGripWidget()
    {
	    return null;
    }

	@Override
    public void close()
    {
    }

	@Override
    public void open()
    {
    }
	
	@Expose
	@Override
    public void toggle()
    {
    }

	@Override
    public void setGripHeight(int height)
    {
    }

	@Override
    public int getGripHeight()
    {
	    return 0;
    }	

	@Override
	protected void init()
	{
		RootPanel.get().insert(this, 0);
		floatPanel = getChildWidget("topToolBar");
	}

	@Override
    protected void initWidgetDefaultStyleName()
    {
		setStyleName("xdev-TopToolBar");
    }

	@Override
    protected void applyWidgetDependentStyleNames()
    {
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.large.toString());
    }
	
	protected HandlerRegistration createEmptyHandlerRegistration()
	{
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
			}
		};
	}
}
