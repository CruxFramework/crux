package br.com.sysmap.crux.advanced.collapsepanel;

import br.com.sysmap.crux.advanced.decoratedpanel.DecoratedPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Panel based on a 3x3 table, with collapse/expand feature. Similar to GWT's DisclosurePanel
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CollapsePanel extends DecoratedPanel
{
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param expanded
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean expanded)
	{
		this(width, height, styleName, collapsible, expanded, (CollapsePanelImages) GWT.create(CollapsePanelImages.class));
	}	
	
	/**
	 * @param width
	 * @param height
	 * @param styleName
	 * @param collapsible
	 * @param expanded
	 * @param images
	 */
	public CollapsePanel(String width, String height, String styleName, boolean collapsible, boolean expanded, CollapsePanelImages images)
	{
		super(width, height);
		AbstractImagePrototype proto = expanded ? images.collapse() : images.expand();
		DOM.appendChild(getTopRightCell(), proto.createElement().<Element> cast());
	}
}