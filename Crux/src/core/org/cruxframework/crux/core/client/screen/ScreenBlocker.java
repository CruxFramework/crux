package org.cruxframework.crux.core.client.screen;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class ScreenBlocker
{
	/**
	 * Creates a DIV element to display over the screen contents
	 * @param blockingDivStyleName
	 * @param body
	 * @return
	 */
	public Element createBlockingDiv(String blockingDivStyleName, Element body)
	{
		Element containerDiv = DOM.createDiv();
		
		int width = body.getScrollWidth();
		int height = body.getScrollHeight();
		
		if(body.getClientWidth() > width)
		{
			width = body.getClientWidth();
		}
		
		if(body.getClientHeight() > height)
		{
			height = body.getClientHeight();
		}	
		
		containerDiv.getStyle().setProperty("position","absolute");
		containerDiv.getStyle().setPropertyPx("top", 0);
		containerDiv.getStyle().setPropertyPx("left", 0);
		containerDiv.getStyle().setPropertyPx("width", width);
		containerDiv.getStyle().setPropertyPx("height", height);
				
		appendChildren(containerDiv, blockingDivStyleName);
		
		return containerDiv;
	}
	
	/**
	 * Browser specific implementations
	 * @param blockingDivStyleName 
	 * @param blockingDiv
	 * @param body 
	 */
	protected void appendChildren(Element containerDiv, String blockingDivStyleName)
	{
		Element blockingDiv = DOM.createDiv();
		blockingDiv.getStyle().setProperty("position", "absolute");
		blockingDiv.getStyle().setPropertyPx("top", 0);
		blockingDiv.getStyle().setPropertyPx("left", 0);
		blockingDiv.getStyle().setProperty("width", "100%");
		blockingDiv.getStyle().setProperty("height", "100%");
		blockingDiv.setClassName(blockingDivStyleName);
		containerDiv.appendChild(blockingDiv);
	}
}
