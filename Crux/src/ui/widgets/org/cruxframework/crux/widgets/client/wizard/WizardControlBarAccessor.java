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


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardControlBarAccessor
{
	private WizardControlBarProxy proxy;
	
	/**
	 * @param proxy
	 */
	WizardControlBarAccessor(WizardControlBarProxy proxy)
	{
		this.proxy = proxy;
	}
	
	/**
	 * 
	 */
	public void finish()
	{
		proxy.finish();
	}
	
	/**
	 * 
	 */
	public void cancel()
    {
		proxy.cancel();
    }

	/**
	 * 
	 */
	public void next()
    {
	    proxy.next();
    }

	/**
	 * 
	 */
	public void back()
    {
	    proxy.back();
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		proxy.setSpacing(spacing);
	}
	
	/**
	 * @return
	 */
	public int getSpacing()
	{
		return proxy.getSpacing();
	}
	
	/**
	 * @return
	 */
	public String getBackLabel()
    {
    	return proxy.getBackLabel();
    }

	/**
	 * @return
	 */
	public String getNextLabel()
    {
    	return proxy.getNextLabel();
    }

	/**
	 * @return
	 */
	public String getCancelLabel()
    {
    	return proxy.getCancelLabel();
    }

	/**
	 * @return
	 */
	public String getFinishLabel()
    {
    	return proxy.getFinishLabel();
    }

	/**
	 * @param backLabel
	 */
	public void setBackLabel(String backLabel)
	{
		proxy.setBackLabel(backLabel);
	}
	
	/**
	 * @param nextLabel
	 */
	public void setNextLabel(String nextLabel)
	{
		proxy.setNextLabel(nextLabel);
	}
	
	/**
	 * @param cancelLabel
	 */
	public void setCancelLabel(String cancelLabel)
	{
		proxy.setCancelLabel(cancelLabel);
	}
	
	/**
	 * @param finishLabel
	 */
	public void setFinishLabel(String finishLabel)
	{
		proxy.setFinishLabel(finishLabel);
	}
	
	public String getButtonsWidth()
    {
    	return proxy.getButtonsWidth();
    }

	public void setButtonsWidth(String buttonWidth)
    {
		proxy.setButtonsWidth(buttonWidth);
    }

	public String getButtonsHeight()
    {
    	return proxy.getButtonsHeight();
    }

	public void setButtonsHeight(String buttonHeight)
    {
		proxy.setButtonsHeight(buttonHeight);
    }

	public String getButtonsStyle()
    {
    	return proxy.getButtonsStyle();
    }

	public void setButtonsStyle(String buttonStyle)
    {
		proxy.setButtonsStyle(buttonStyle);
    }
	
	public WizardCommandAccessor getCommand(String commandId)
	{
		return proxy.getCommand(commandId);
	}
}
