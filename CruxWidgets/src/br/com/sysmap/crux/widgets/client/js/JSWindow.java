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
package br.com.sysmap.crux.widgets.client.js;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A Window JavaScriptObject. Useful for accessing foreign native JS objects and functions.
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class JSWindow extends JavaScriptObject
{
	/**
	 * Obeying to GWT JSNI restrictions
	 */
	protected JSWindow()
	{
	}
}
