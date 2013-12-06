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

import java.io.Serializable;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.JSWindow;

/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
@Legacy
@Deprecated
public class WizardAccessor<T extends Serializable>
{
	private final WizardProxy<T> proxy;

	/**
	 * @param proxy
	 */
	WizardAccessor(WizardProxy<T> proxy)
    {
		this.proxy = proxy;
    }
	
	/**
	 * @return
	 */
	public boolean first()
	{
		return proxy.first();
	}

	/**
	 * @return
	 */
	public boolean next()
	{
		return proxy.next();
	}

	/**
	 * @return
	 */
	public boolean back()
	{
		return proxy.back();
	}

	/**
	 * @return
	 */
	public void cancel()
	{
		proxy.cancel();
	}
	
	/**
	 * @return
	 */
	public boolean finish()
	{
		return proxy.finish();
	}
	
	/**
	 * @param id
	 * @return
	 */
	public boolean selectStep(String id)
	{
		return selectStep(id, false);
	}
	
	public void setStepEnabled(int stepOrder, boolean enabled)
	{
		proxy.setStepEnabled(stepOrder, enabled);
	}
	
	public void setStepEnabled(String stepId, boolean enabled)
	{
		proxy.setStepEnabled(stepId, enabled);
	}

	/**
	 * @param id
	 * @param fireLeaveEvent
	 * @return
	 */
	public boolean selectStep(String id, boolean fireLeaveEvent)
	{
		return proxy.selectStep(id, !fireLeaveEvent);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public int getStepOrder(String id)
	{
		return proxy.getStepOrder(id);
	}
	
	/**
	 * @return
	 */
	public WizardControlBarAccessor getControlBar()
    {
    	return proxy.getControlBar();
    }
	
	/**
	 * @param data
	 */
	public void updateData(T data)
	{
		proxy.updateData(data);
	}

	/**
	 * @param <T>
	 * @param dataType
	 * @return
	 */
	public T readData()
	{
		return proxy.readData();
	}
	
	public JSWindow getStepWindow(String stepId)
	{
		return proxy.getStepWindow(stepId);
	}
}
