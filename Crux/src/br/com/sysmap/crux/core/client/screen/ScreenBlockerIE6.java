package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;

public class ScreenBlockerIE6 extends ScreenBlocker
{
	/**
	 * In IE6, list boxes are always displayed over HTML blocks. 
	 * This is that same old, well known, IFRAME-based workaround. 
	 * @param containerDiv
	 */
	@Override
	protected void appendChildren(Element containerDiv, String blockingDivStyleName)
	{
		IFrameElement frame = DOM.createIFrame().cast();
		
		frame.getStyle().setProperty("position","absolute");
		frame.getStyle().setPropertyPx("top", 0);
		frame.getStyle().setPropertyPx("left", 0);
		frame.getStyle().setProperty("width", "100%");
		frame.getStyle().setProperty("height", "100%");
		frame.getStyle().setProperty("opacity", "0.0");
		frame.getStyle().setProperty("filter", "alpha(opacity=0)");
		
		containerDiv.appendChild(frame);
		
		super.appendChildren(containerDiv, blockingDivStyleName);
	}
}
