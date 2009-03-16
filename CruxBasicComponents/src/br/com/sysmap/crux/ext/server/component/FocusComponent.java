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
package br.com.sysmap.crux.ext.server.component;

import br.com.sysmap.crux.core.server.screen.Component;;

public class FocusComponent extends Component 
{
	protected int tabIndex = -1;
	protected boolean enabled = true;
	protected char accessKey;
	
	public int getTabIndex() 
	{
		return tabIndex;
	}
	
	public void setTabIndex(int tabIndex) 
	{
		if (isCheckChanges() && (tabIndex != this.tabIndex))
		{
			dirty = true;
		}
		this.tabIndex = tabIndex;
	}
	
	public boolean isEnabled() 
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled) 
	{
		if (isCheckChanges() && (enabled != this.enabled))
		{
			dirty = true;
		}
		this.enabled = enabled;
	}
	
	public char getAccessKey() 
	{
		return accessKey;
	}
	
	public void setAccessKey(char accessKey) 
	{
		if (isCheckChanges() && (accessKey != this.accessKey))
		{
			dirty = true;
		}
		this.accessKey = accessKey;
	}
}
