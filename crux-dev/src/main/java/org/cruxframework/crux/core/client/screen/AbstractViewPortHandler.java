/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public abstract class AbstractViewPortHandler 
{
	public abstract void createViewport(String content, JavaScriptObject wnd);
	
	/**
	 * Return the parent window from the given window.
	 * @return
	 */
	protected static native JavaScriptObject getParentWindow(JavaScriptObject wnd)/*-{
		return (wnd.parent !== wnd?wnd.parent:null);
	}-*/;
	
	protected static native Document getWindowDocument(JavaScriptObject wnd)/*-{
		return wnd.document;
	}-*/;
	
	/**
	 * If the window is a crux frame (used in situations like offline loading), return 
	 * true.
	 * @return
	 */
	protected static native boolean isCruxWindow(JavaScriptObject wnd)/*-{
		if (wnd && wnd.__Crux_Frame){
			return true;
		}
		return false;
	}-*/;
}
