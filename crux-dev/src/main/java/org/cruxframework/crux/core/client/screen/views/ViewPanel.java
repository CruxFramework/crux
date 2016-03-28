/*
 * Copyright 2016 cruxframework.org.
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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.screen.binding.NativeWrapper;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewPanel extends HTMLPanel
{
	public ViewPanel(SafeHtml safeHtml)
    {
	    super(safeHtml);
    }

	public ViewPanel(String tag, String html)
    {
	    super(tag, html);
    }

	public ViewPanel(String html)
    {
	    super(html);
    }

	public NativeWrapper wrapNative(String elementId)
	{
		NativeWrapper result = new NativeWrapper(getElementById(elementId));
		getChildren().add(result);
		adopt(result);
		return result;
	}
}
