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
package org.cruxframework.crux.gwt.client;

import org.cruxframework.crux.core.client.screen.HTMLContainer;
import org.cruxframework.crux.core.client.screen.ViewFactoryUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxHTMLPanel extends HTMLPanel implements HTMLContainer
{
	/**
	 * Constructor
	 * @param element
	 * @param wrapDOMElement if true create an wrapper element and insert the original DOM structure into it. 
	 * If false, copy all child nodes to the panel element, preserving the _crux_ enclosing panel as the widget wrapper 
	 */
	public CruxHTMLPanel(String wrapperElementId, boolean wrapDOMElement)
	{
		super("");
		Element panelElement = ViewFactoryUtils.getEnclosingPanelElement(wrapperElementId);
		assert Document.get().getBody().isOrHasChild(panelElement);
		if (wrapDOMElement)
		{
			panelElement.removeFromParent();
			getElement().appendChild(panelElement);
		}
		else
		{
			Element htmlPanelElement = getElement();
			while (panelElement.getChildCount() > 0)
			{
				Node child = panelElement.getChild(0);
				child.removeFromParent();
				htmlPanelElement.appendChild(child);
			}
		}
		
		panelElement.getStyle().setDisplay(Display.BLOCK);
	}

	@Override
	public void onAttach()
	{
		super.onAttach();
	}
}
