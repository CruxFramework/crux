package br.com.sysmap.crux.advanced.client.util;

import com.google.gwt.dom.client.Element;

public class TextSelectionUtils
{
	/**
	 * @param element
	 */
	public static void makeUnselectable(Element element)
	{
		if(element!=null)
		{	
			element.setPropertyString("unselectable", "on");
			element.getStyle().setProperty("MozUserSelect", "none");
		}
	}
}
