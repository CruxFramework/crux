package br.com.sysmap.crux.advanced.client.dynatabs;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeBlurEvent;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeBlurHandler;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusEvent;
import br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusHandler;
import br.com.sysmap.crux.advanced.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseHandler;
import br.com.sysmap.crux.advanced.client.event.openclose.HasBeforeCloseHandlers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class FlapPanel extends Composite  implements HasBeforeFocusAndBeforeBlurHandlers, HasBeforeCloseHandlers, HasClickHandlers
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
		flapController = new FlapController(tabs, tabId, tabLabel, closeable);
		panel.setContentWidget(flapController);
		initWidget(panel);
	}

	/**
	 * @return the flapController
	 */
	public FlapController getFlapController()
	{
		return flapController;
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.focusblur.HasBeforeBlurHandlers#addBeforeBlurHandler(br.com.sysmap.crux.advanced.client.event.focusblur.BeforeBlurHandler)
	 */
	public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
	{
		return addHandler(handler, BeforeBlurEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.focusblur.HasBeforeFocusHandlers#addBeforeFocusHandler(br.com.sysmap.crux.advanced.client.event.focusblur.BeforeFocusHandler)
	 */
	public HandlerRegistration addBeforeFocusHandler(BeforeFocusHandler handler)
	{
		return addHandler(handler, BeforeFocusEvent.getType());
	}

	/**
	 * @see br.com.sysmap.crux.advanced.client.event.openclose.HasBeforeCloseHandlers#addBeforeCloseHandler(br.com.sysmap.crux.advanced.client.event.openclose.BeforeCloseHandler)
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