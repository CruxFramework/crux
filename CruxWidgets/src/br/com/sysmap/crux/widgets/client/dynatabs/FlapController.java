package br.com.sysmap.crux.widgets.client.dynatabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class FlapController extends Composite
{
	private Label title;
	private FocusWidget closeButton;
	/**
	 * @param tabs
	 * @param tabId
	 * @param tabLabel
	 * @param closeable
	 */
	public FlapController(final DynaTabs tabs, final String tabId, String tabLabel, boolean asHTML, boolean closeable)
	{
		HorizontalPanel flap = new HorizontalPanel();
		
		initWidget(flap);
		
		flap.setSpacing(0);

		if (asHTML)
		{
			title = new HTML(tabLabel);
		}
		else
		{
			title = new Label(tabLabel);
		}
		title.setStyleName("flapLabel");
		flap.add(title);

		if (closeable)
		{
			closeButton = new FocusWidget(new Label(" ").getElement()) {};


			closeButton.setStyleName("flapCloseButton");
			closeButton.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					event.stopPropagation();
					tabs.closeTab(tabId, false);
				}
			});

			closeButton.addKeyDownHandler(new KeyDownHandler()
			{
				public void onKeyDown(KeyDownEvent event)
				{
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					{
						event.stopPropagation();
						tabs.closeTab(tabId, false);
					}
				}
			});

			closeButton.setVisible(closeable);

			flap.add(closeButton);
		}
	}
	
	/**
	 * @param title
	 */
	public void setTabTitle(String title)
	{
		this.title.setText(title);
	}
	
	void setCloseButtonEnabled(boolean enabled)
	{
		if (enabled)
		{
			closeButton.removeStyleDependentName("disable");
		}
		else
		{
			closeButton.addStyleDependentName("disable");
		}
	}
}