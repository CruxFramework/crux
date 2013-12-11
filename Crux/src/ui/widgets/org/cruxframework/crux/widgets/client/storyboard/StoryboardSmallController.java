package org.cruxframework.crux.widgets.client.storyboard;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.selectablepanel.SelectablePanel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardSmallController")
public class StoryboardSmallController extends DeviceAdaptiveController implements Storyboard
{
	protected FlowPanel storyboard;
	protected String itemHeight;
	protected String itemWidth;
	protected boolean fixedHeight = true;
	protected boolean fixedWidth = true;

	@Override
	public Widget getWidget(int index)
	{
		return ((SimplePanel)storyboard.getWidget(index)).getWidget();
	}

	public void add(Widget widget)
	{
		storyboard.add(createClickablePanelForCell(widget));
	}
	
	@Override
    public void clear()
    {
		storyboard.clear();	    
    }

	@Override
    public Iterator<Widget> iterator()
    {
	    return new Iterator<Widget>()
		{
	    	private int index = -1;
	    	
			@Override
            public boolean hasNext()
            {
			      return index < (getWidgetCount() - 1);
            }

			@Override
            public Widget next()
			{
				if (index >= getWidgetCount()) 
				{
					throw new NoSuchElementException();
				}
				return getWidget(++index);
            }

			@Override
            public void remove()
            {
				if ((index < 0) || (index >= getWidgetCount())) 
				{
					throw new IllegalStateException();
				}
				StoryboardSmallController.this.remove(index--);
            }
		};
    }

	@Override
    public boolean remove(Widget w)
    {
	    int index = getWidgetIndex(w);
	    if (index >= 0)
	    {
	    	return remove(index);
	    }
	    return false;
    }

	@Override
	public int getWidgetCount()
	{
		return storyboard.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget child)
	{
		int count = getWidgetCount();
		for (int i=0; i< count; i++)
		{
			if (getWidget(i).equals(child))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean remove(int index)
	{
		return storyboard.remove(index);
	}

	@Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler)
    {
		 return addHandler(handler, SelectionEvent.getType());
	}

	protected Widget createClickablePanelForCell(Widget widget)
	{
		final SelectablePanel panel = new SelectablePanel();
		panel.add(widget);
		panel.setStyleName("item");
		configHeightWidth(panel);
		
		panel.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardSmallController.this, index);
			}
		});
		return panel;
	}

	protected void configHeightWidth(final Widget panel) {
		if (!StringUtils.isEmpty(itemHeight))
		{
			if(fixedHeight)
			{
				panel.setHeight(itemHeight);
			}
			else
			{
				panel.getElement().getStyle().setProperty("minHeight", itemHeight);
			}
		}
		
		if (!StringUtils.isEmpty(itemWidth))
		{
			if(fixedWidth)
			{
				panel.setWidth(itemWidth);
			}
			else
			{
				panel.getElement().getStyle().setProperty("minWidth", itemWidth);
			}
		}
	}
		
	@Override
	protected void init()
    {
		storyboard = getChildWidget("storyboard");
		this.itemHeight = "75px";
		this.itemWidth = "100%";
		setStyleName("crux-Storyboard");
    }

	@Override
    public String getLargeDeviceItemWidth()
    {
	    return null;
    }

	@Override
    public void setLargeDeviceItemWidth(String width)
    {
    }

	@Override
    public String getSmallDeviceItemHeight()
    {
	    return this.itemHeight;
    }

	@Override
    public void setSmallDeviceItemHeight(String height)
    {
		this.itemHeight = height;	    
    }

	@Override
    public String getLargeDeviceItemHeight()
    {
	    return null;
    }

	@Override
    public void setLargeDeviceItemHeight(String height)
    {
    }

	@Override
    public void setHorizontalAlignment(HorizontalAlignmentConstant value)
    {
		//DO Nothing
    }

	@Override
    public void setVerticalAlignment(VerticalAlignmentConstant value)
    {
    	storyboard.getElement().getStyle().setProperty("verticalAlign", value.getVerticalAlignString());
    }

	@Override
    public void setSmallDeviceItemHeight(IsWidget child, String height)
    {
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setHeight(height);
    }

	@Override
	public void setLargeDeviceItemHeight(IsWidget child, String height)
	{
	}
	
	@Override
    public void setLargeDeviceItemWidth(IsWidget child, String width)
    {
    }

	@Override
    public void setHorizontalAlignment(IsWidget child, HorizontalAlignmentConstant value)
    {
		child.asWidget().getParent().getElement().getStyle().setProperty("textAlign", value.getTextAlignString());
    }

	@Override
    public void setVerticalAlignment(IsWidget child, VerticalAlignmentConstant value)
    {
		child.asWidget().getParent().getElement().getStyle().setProperty("verticalAlign", value.getVerticalAlignString());
    }

	@Override
	public void setFixedHeight(boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

	@Override
	public void setFixedWidth(boolean fixedWidth) {
		this.fixedWidth = fixedWidth;
	}
}
