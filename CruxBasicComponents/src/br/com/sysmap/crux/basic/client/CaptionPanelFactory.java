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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CaptionPanelFactory extends CompositeFactory<CaptionPanel> implements HasWidgetsFactory<CaptionPanel>
{
	@Override
	protected void processAttributes(CaptionPanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		Element firstChild = ensureFirstChildSpan(element, true);
		
		// Is text content
		if(firstChild == null)
		{
			String captionText = element.getAttribute("_text");
			widget.setCaptionText(captionText);
		}
		
		// Is complex content
		else
		{
			Element complexContent = ensureFirstChildSpan(element, true);
			
			// Is widget content
			if(complexContent != null && isWidget(complexContent))
			{
				widget.setContentWidget(createChildWidget(complexContent, complexContent.getId()));
			}
			
			// Is HTML content
			else
			{
				widget.setCaptionHTML(firstChild.getInnerHTML());
			}
		}
	}
	
	@Override
	protected CaptionPanel instantiateWidget(Element element, String widgetId) 
	{
		return new CaptionPanel();
	}

	/**
	 * @see br.com.sysmap.crux.core.client.component.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(CaptionPanel parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException 
	{
		// nothing to do here
	}
}
