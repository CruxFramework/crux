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
package org.cruxframework.crux.core.client.screen;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * CRUX INTERNAL CLASS. DO NOT USE IT DIRECTLY.
 * 
 * Helper class to improve performance of HTMLPanel creations on Crux.
 * HTMLPanel has a poor performance when it is attached on an element that is 
 * not attached to DOM. This class provides an workaround that avoid this situation, 
 * that can occur frequently when using HTMLPanels on Crux Views.
 *  
 * @author Thiago da Rosa de Bustamante
 */
public class HTMLPanelHelper
{
	private static Element hiddenDiv;

	/**
	 * Ensure that the given HTMLPanel will be attached to DOM. It must be ensured before
	 * the HTMLPanel children widgets are processed.
	 * @param panel HTML panel
	 * @return HTMLPanelInfo object, containing information about where HTMLPanel was attached early
	 */
	public static HTMLPanelInfo attachToDom(HTMLPanel panel)
	{
		// If the hidden DIV has not been created, create it.
		if (hiddenDiv == null)
		{
			hiddenDiv = Document.get().createDivElement();
			UIObject.setVisible(hiddenDiv, false);
			RootPanel.getBodyElement().appendChild(hiddenDiv);
		}

		// Hang on to the panel's original parent and sibling elements so that
		// it
		// can be replaced.
		Element origParent = panel.getElement().getParentElement();
		Element origSibling = panel.getElement().getNextSiblingElement();
		
		HTMLPanelInfo panelInfo = new HTMLPanelInfo(origParent, origSibling);

		// Attach the panel's element to the hidden div.
		hiddenDiv.appendChild(panel.getElement());

		return panelInfo;
	}
	
	/**
	 * Restore the HTMLPanel into its original parent 
	 * @param panel HTML Panel
	 * @param panelInfo HTMLPanelInfo object, containing information about where HTMLPanel was attached early
	 */
	public static void restorePanelParent(HTMLPanel panel, HTMLPanelInfo panelInfo)
	{
		// Put the panel's element back where it was.
		if (panelInfo.origParent != null)
		{
			panelInfo.origParent.insertBefore(panel.getElement(), panelInfo.origSibling);
		}
		else
		{
			hiddenDiv.removeChild(panel.getElement());
		}
	}
	
	/**
	 * Contains information about where HTMLPanel was attached early
	 * @author Thiago da Rosa de Bustamante
	 */
	public static class HTMLPanelInfo
	{
		private final Element origParent;
		private final Element origSibling;

		/**
		 * Constructor
		 * @param origParent HTML panel parent element
		 * @param origSibling HTML sibling element
		 */
		public HTMLPanelInfo(Element origParent, Element origSibling)
        {
			this.origParent = origParent;
			this.origSibling = origSibling;
        }

		/**
		 * Return the original HTML panel parent element
		 * @return HTML panel parent element
		 */
		public Element getOrigParent()
		{
			return origParent;
		}

		/**
		 * Return the original HTML panel sibling element
		 * @return HTML panel sibling element
		 */
		public Element getOrigSibling()
		{
			return origSibling;
		}
	}
}
