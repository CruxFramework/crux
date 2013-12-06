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
package org.cruxframework.crux.core.client.controller.crossdoc;

import org.cruxframework.crux.core.client.Legacy;

/**
 * All possible targets for a cross document call.
 * 
 * @author Thiago da Rosa de Bustamante
 * @see CrossDocument
 */
@Legacy
@Deprecated
public enum Target
{
	/**
	 * The top frame
	 */
	TOP,
	
	/**
	 * The parent frame
	 */
	PARENT,
	
	/**
	 * The top frame located on the outermost window of your application, no matter
     * if executed from inside a frame or other window (e.g. blank, popup, etc.)
	 */
	ABSOLUTE_TOP,
	
	/**
	 * The current window
	 */
	SELF, 
	
	/**
	 * The opener window
	 */
	OPENER
}
