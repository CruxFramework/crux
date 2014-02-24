/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.file;


import com.google.gwt.dom.client.PartialSupport;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class URL
{
	public static native String createObjectURL(Blob blob)/*-{
		var url = ($wnd.URL || $wnd.webkitURL);
		return url.createObjectURL(blob);
	}-*/;
	
	public static native boolean isSupported()/*-{
		if ($wnd.URL || $wnd.webkitURL)
		{
			return true;
		}
		return false;
	}-*/;
}
