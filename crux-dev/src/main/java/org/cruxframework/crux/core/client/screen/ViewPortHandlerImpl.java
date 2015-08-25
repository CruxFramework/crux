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
import com.google.gwt.dom.client.MetaElement;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public class ViewPortHandlerImpl extends AbstractViewPortHandler 
{
	@Override
	public void createViewport(String content, JavaScriptObject wnd) 
	{
		Document document = getWindowDocument(wnd);
		MetaElement viewport = document.createMetaElement();
		viewport.setContent(content);
		viewport.setName("viewport");
		document.getElementsByTagName("head").getItem(0).appendChild(viewport);
		JavaScriptObject parentWindow = getParentWindow(wnd);
		if (parentWindow != null && isCruxWindow(parentWindow))
		{
			createViewport(content, parentWindow);
		}
	}
	
}
