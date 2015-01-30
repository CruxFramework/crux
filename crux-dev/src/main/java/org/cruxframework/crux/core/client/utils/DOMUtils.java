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
package org.cruxframework.crux.core.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DOMUtils
{
	/**
	 * Return the rectangle describing the elements bounds (top, left, right and bottom)
	 * @param element
	 * @return
	 */
	public static native TextRectangle getBoundingClientRect(Element element)/*-{
		return element.getBoundingClientRect();
	}-*/;
	
	public static class TextRectangle extends JavaScriptObject
	{
		protected TextRectangle(){}
		
		public native final int getTop()/*-{
			return this.top;
		}-*/;
		
		public native final int getLeft()/*-{
			return this.left;
		}-*/;
		
		public native final int getRight()/*-{
			return this.right;
		}-*/;

		public native final int getBottom()/*-{
			return this.bottom;
		}-*/;
	}

	public static boolean isAttached(Element element)
	{
		boolean attached = false;
		Element documentElement = element.getOwnerDocument().getDocumentElement();
		
		while (element.getParentNode() != null)
		{
			if (element.equals(documentElement))
			{
				attached = true;
				break;
			}
			element = element.getParentElement();
		}
		
		return attached;
	}
	
	public static boolean isRootNode(Element e)
	{
		return "html".equalsIgnoreCase(e.getTagName()) || e == Document.get().getBody();
	}

	public static double getMarginLeft(Element e)
	{
	    return getMargin(e.getStyle().getMarginLeft());
	}

	public static double getMarginTop(Element e)
	{
	    return getMargin(e.getStyle().getMarginTop());
	}

	public static double getBorderWidth(Element e)
	{
	    return getMargin(e.getStyle().getBorderWidth());
	}

	private static double getMargin(String val)
    {
		if (StringUtils.isEmpty(val))
		{
			return 0;
		}
	    if ("thick".equalsIgnoreCase(val)) 
	    {
	        return (5);
		}
		else if ("medium".equalsIgnoreCase(val))
		{
			return (3);
		}
		else if ("thin".equalsIgnoreCase(val))
		{
			return (1);
		}
		val = val.trim().replaceAll("[^\\d\\.\\-]+.*$", "");
		return val.length() == 0 ? 0 : Double.parseDouble(val);
    }
	
	
	public static Element getScrollParent(final Element element)
	{
		Element scrollParent = null;

		String pos = element.getStyle().getPosition();
		Position position = (pos == null? null : Position.valueOf(pos));
		if (Position.FIXED == position)
		{
			return Document.get().getBody();
		}

		Element parent = element.getParentElement();
		Element root = Document.get().getDocumentElement();
		
		while (parent != root)
        {
			pos = parent.getStyle().getPosition();
			Position parentPosition = (pos!= null ? Position.valueOf(pos):null);
			if (isOverflowEnabled(parent))
			{
				if (position == Position.ABSOLUTE)
				{
					if (parentPosition == Position.RELATIVE || parentPosition == Position.ABSOLUTE || parentPosition == Position.FIXED)
					{
						scrollParent = parent;
						break;
					}
				}
				else
				{
					scrollParent = parent;
					break;
				}
			}
			
			parent = parent.getParentElement();
        }

		return scrollParent != null ? scrollParent : Document.get().getBody();
	}

	public static boolean isOverflowEnabled(Element e)
	{
		Style style = e.getStyle();
		String overflow = style.getOverflow() + style.getOverflowX() + style.getOverflowY();
		return overflow.contains("auto") || overflow.contains("scroll");
	}
	
	public static native void addOneTimeHandler(Element el, String eventName, EvtHandler evtHandler)/*-{
		var func;
		func = function(evt) 
		{
			el.removeEventListener(eventName, func);
			evtHandler.@org.cruxframework.crux.core.client.utils.DOMUtils.EvtHandler::onEvent(Lcom/google/gwt/dom/client/NativeEvent;)(evt);
		};
		el.addEventListener(eventName, func); 			
	}-*/;
	
	public static interface EvtHandler
	{
		void onEvent(NativeEvent evt);
	}
	
	
}
