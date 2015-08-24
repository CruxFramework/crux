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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleElement;

/**
 * @author Samuel Cardoso
 *
 */
public class ViewPortHandlerIEMobileImpl extends ViewPortHandlerImpl 
{

	@Override
	public void createViewport(String content, JavaScriptObject wnd) 
	{
		super.createViewport(content, wnd);
		Document document = getWindowDocument(wnd);
		Element header = document.getElementsByTagName("head").getItem(0);
		StyleElement viewPortStyleElement = document.createStyleElement();
		
		String newProperties = handlePropertiesToCSS(content);
		
		viewPortStyleElement.appendChild(document.createTextNode("@-ms-viewport{" + newProperties + "}"));
		header.appendChild(viewPortStyleElement);
		
		JavaScriptObject parentWindow = getParentWindow(wnd);
		if (parentWindow != null && isCruxWindow(parentWindow))
		{
			createViewport(content, parentWindow);
		}
	}

	private String handlePropertiesToCSS(String content) 
	{
		if(content == null)
		{
			return "";
		}
		
		return  content.trim().
				replace("=", ":").
				replace(",", ";").
				replace("minimum-scale", "min-zoom").
				replace("maximum-scale", "max-zoom").
				replace("user-scalable:no", "user-zoom:fixed").
				concat(";");
	}
}
