/*
 * Copyright 2014 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
