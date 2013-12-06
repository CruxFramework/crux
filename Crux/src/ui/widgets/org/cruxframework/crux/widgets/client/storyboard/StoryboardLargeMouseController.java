package org.cruxframework.crux.widgets.client.storyboard;

import org.cruxframework.crux.core.client.controller.Controller;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardLargeMouseController")
public class StoryboardLargeMouseController extends StoryboardLargeController
{
	@Override
	protected Widget createClickablePanelForCell(Widget widget)
	{
		final FocusPanel panel = new FocusPanel();
		panel.add(widget);
		panel.setStyleName("item");
		configHeightWidth(panel);

		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardLargeMouseController.this, index);
			}
		});
		panel.getElement().getStyle().setProperty("display", "inline-table");
		panel.getElement().getStyle().setProperty("verticalAlign", "bottom");
		panel.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
				{
					int index = storyboard.getWidgetIndex(panel);
					SelectionEvent.fire(StoryboardLargeMouseController.this, index);
				}
			}
		});
	    
		return panel;
	}
}
