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
package org.cruxframework.crux.widgets.client.wizard;

import org.cruxframework.crux.core.client.Legacy;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Legacy
@Deprecated
public class WizardCommandAccessor
{
	private WizardCommandProxy proxy;
	
	WizardCommandAccessor(WizardCommandProxy proxy)
    {
		this.proxy = proxy;
    }
	
	public int getOrder()
    {
    	return proxy.getOrder();
    }

	public void setOrder(int order)
    {
		proxy.setOrder(order);
    }

	public String getLabel()
    {
    	return proxy.getLabel();
    }

	public void setLabel(String label)
    {
		proxy.setLabel(label);
    }

	public boolean isEnabled()
    {
    	return proxy.isEnabled();
    }

	public void setEnabled(boolean enabled)
    {
		proxy.setEnabled(enabled);
    }

	public String getId()
    {
    	return proxy.getId();
    }
	
	public String getStyleName()
	{
		return proxy.getStyleName();
	}
	
	public void setStyleName(String styleName)
	{
		proxy.setStyleName(styleName);
	}
	
	public int getWidth()
	{
		return proxy.getOffsetWidth();
	}

	public void setWidth(String width)
	{
		proxy.setWidth(width);
	}

	public int getHeight()
	{
		return proxy.getOffsetWidth();
	}

	public void setHeight(String height)
	{
		proxy.setHeight(height);
	}
}
