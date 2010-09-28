/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A Window JavaScriptObject. Useful for accessing foreign native JS objects and functions.
 * @author Gesse S. F. Dafe
 */
public class JSWindow extends JavaScriptObject
{
	/**
	 * Returns the current window native object;
	 * @return
	 */
	public native static JSWindow currentWindow()/*-{
		return $wnd;
	}-*/;
	
	/**
	 * Returns the topmost window native object. No matter if the current window is a frame, a popup or whatever.
	 * @return
	 */
	public native static JSWindow getAbsoluteTop()/*-{
		var who = $wnd.top;
		var op = $wnd.opener;
		while (op != null)
		{
			who = op.top;
			op = op.opener;
		}
		return who;
	}-*/;
	
	/**
	 * Obeying to GWT JSNI restrictions
	 */
	protected JSWindow()
	{
	}
}