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
package br.com.sysmap.crux.advanced.client.transferlist;

import br.com.sysmap.crux.basic.client.CompositeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Transfer List widget
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class TransferListFactory extends CompositeFactory<TransferList>
{
	@Override
	protected TransferList instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new TransferList();
	}

	@Override
	protected void processAttributes(TransferList widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		String leftToRightButtonText = element.getAttribute("_leftToRightButtonText");
		if(leftToRightButtonText != null && leftToRightButtonText.length() > 0)
		{
			widget.setMoveToRightButtonText(ScreenFactory.getInstance().getDeclaredMessage(leftToRightButtonText));
		}
		
		String rightToLeftButtonText = element.getAttribute("_rightToLeftButtonText");
		if(rightToLeftButtonText != null && rightToLeftButtonText.length() > 0)
		{
			widget.setMoveToLeftButtonText(ScreenFactory.getInstance().getDeclaredMessage(rightToLeftButtonText));
		}
		
		String leftListLabel = element.getAttribute("_leftListLabel");
		if(leftListLabel != null && leftListLabel.length() > 0)
		{
			widget.setLeftListLabel(ScreenFactory.getInstance().getDeclaredMessage(leftListLabel));
		}
		
		String rightListLabel = element.getAttribute("_rightListLabel");
		if(rightListLabel != null && rightListLabel.length() > 0)
		{
			widget.setRightListLabel(ScreenFactory.getInstance().getDeclaredMessage(rightListLabel));
		}	
		
		String visibleItems = element.getAttribute("_visibleItemCount");
		if(visibleItems != null && visibleItems.length() > 0)
		{
			widget.setVisibleItemCount(Integer.parseInt(visibleItems));
		}		
	}
}