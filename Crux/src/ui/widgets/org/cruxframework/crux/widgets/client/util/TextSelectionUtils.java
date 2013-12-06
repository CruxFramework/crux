package org.cruxframework.crux.widgets.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;

public class TextSelectionUtils
{
	private static Unselectable unselectable = null;
	
	
	/**
	 * @param element
	 */
	public static void makeUnselectable(Element element)
	{
		if (unselectable == null)
		{
			unselectable = GWT.create(Unselectable.class);
		}
		
		unselectable.makeUnselectable(element);
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class Unselectable
	{
		public void makeUnselectable(Element element)
		{
		}
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class UnselectableIEImpl extends Unselectable
	{
		public void makeUnselectable(Element element)
		{
			if(element!=null)
			{	
				element.setPropertyString("unselectable", "on");
			}
		}
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class UnselectableMozImpl extends Unselectable
	{
		public void makeUnselectable(Element element)
		{
			if(element!=null)
			{	
				element.getStyle().setProperty("MozUserSelect", "none");
			}
		}
	}
	
}
