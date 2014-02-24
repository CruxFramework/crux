package org.cruxframework.crux.widgets.client.dynatabs;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler;
import org.cruxframework.crux.widgets.client.event.openclose.HasBeforeCloseHandlers;
import org.cruxframework.crux.widgets.client.rollingtabs.SimpleDecoratedPanel;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class FlapPanel extends Composite  implements HasBeforeFocusAndBeforeBlurHandlers, HasBeforeCloseHandlers, HasClickHandlers
{
	private SimpleDecoratedPanel panel;
	private FlapController flapController;	
	
	/**
	 * @param tabs
	 * @param tabId
	 * @param tabLabel
	 * @param closeable
	 */
	public FlapPanel(final DynaTabs tabs, String tabId, String tabLabel, boolean closeable)
	{
		panel = new SimpleDecoratedPanel();
		flapController = new FlapController(tabs, tabId, tabLabel, false, closeable);
		panel.setContentWidget(flapController);
		initWidget(panel);
		Screen.ensureDebugId(panel, tabs.getElement().getId() + "_" + tabId);
	}

	/**
	 * @return the flapController
	 */
	public FlapController getFlapController()
	{
		return flapController;
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeBlurHandlers#addBeforeBlurHandler(org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurHandler)
	 */
	public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
	{
		return addHandler(handler, BeforeBlurEvent.getType());
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.focusblur.HasBeforeFocusHandlers#addBeforeFocusHandler(org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusHandler)
	 */
	public HandlerRegistration addBeforeFocusHandler(BeforeFocusHandler handler)
	{
		return addHandler(handler, BeforeFocusEvent.getType());
	}

	/**
	 * @see org.cruxframework.crux.widgets.client.event.openclose.HasBeforeCloseHandlers#addBeforeCloseHandler(org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseHandler)
	 */
	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler handler)
	{
		return addHandler(handler, BeforeCloseEvent.getType());
	}

	/**
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addHandler(handler, ClickEvent.getType());
	}
}