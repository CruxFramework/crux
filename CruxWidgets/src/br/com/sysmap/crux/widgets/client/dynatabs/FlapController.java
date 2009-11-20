package br.com.sysmap.crux.widgets.client.dynatabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class FlapController extends Composite
{
	private Label title;
	
	/**
	 * @param tabs
	 * @param tabId
	 * @param tabLabel
	 * @param closeable
	 */
	public FlapController(final DynaTabs tabs, final String tabId, String tabLabel, boolean closeable)
	{
		HorizontalPanel flap = new HorizontalPanel();
		
		initWidget(flap);
		
		flap.setSpacing(0);

		title = new Label(tabLabel);
		title.setStyleName("flapLabel");
		flap.add(title);

		FocusPanel closeButton = new FocusPanel();
		Label empty = new Label("");
		empty.getElement().getStyle().setProperty("fontSize", "1px");
		closeButton.add(empty);
		closeButton.setStyleName("flapCloseButton");
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				tabs.closeTab(tabId, false);
			}
		});

		closeButton.setVisible(closeable);
		
		flap.add(closeButton);
	}
	
	/**
	 * @param title
	 */
	public void setTabTitle(String title)
	{
		this.title.setText(title);
	}
}