package org.cruxframework.crux.widgets.client.storyboard;

import org.cruxframework.crux.core.client.controller.Controller;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardLargeController")
public class StoryboardLargeController extends StoryboardSmallController
{
	@Override
	protected void init()
    {
		super.init();
		this.itemHeight = "200px";
		this.itemWidth = "200px";
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    }

	@Override
	protected Widget createClickablePanelForCell(Widget widget)
	{
	    final Widget panel = super.createClickablePanelForCell(widget);
	    panel.getElement().getStyle().setProperty("display", "inline-table");
		return panel;
	}

	@Override
    public String getLargeDeviceItemWidth()
    {
	    return this.itemWidth;
    }

	@Override
    public void setLargeDeviceItemWidth(String width)
    {
		this.itemWidth = width;
    }

	@Override
    public String getSmallDeviceItemHeight()
    {
	    return null;
    }

	@Override
    public void setSmallDeviceItemHeight(String height)
    {
    }

	@Override
    public String getLargeDeviceItemHeight()
    {
	    return this.itemHeight;
    }

	@Override
    public void setLargeDeviceItemHeight(String height)
    {
		this.itemHeight = height;
    }
	
	@Override
    public void setHorizontalAlignment(HorizontalAlignmentConstant value)
    {
    	storyboard.getElement().getStyle().setProperty("textAlign", value.getTextAlignString());
    }
	
	@Override
	public void setLargeDeviceItemHeight(IsWidget child, String height)
	{
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setHeight(height);
	}
	
	@Override
    public void setLargeDeviceItemWidth(IsWidget child, String width)
    {
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setWidth(width);
    }
	
}
