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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

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
