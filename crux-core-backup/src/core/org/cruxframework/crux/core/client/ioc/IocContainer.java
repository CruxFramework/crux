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
package org.cruxframework.crux.core.client.ioc;

import org.cruxframework.crux.core.client.ioc.Inject.Scope;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public abstract class IocContainer implements ViewAware
{
	private static IocLocalScope _localScope = new IocLocalScope();
	private static IocPersistentScope _documentScope = new IocPersistentScope();
	private IocPersistentScope _viewScope = new IocPersistentScope();
	private View view;
	
	/**
	 * Constructor
	 * @param view
	 */
	public IocContainer(View view)
    {
		this.view = view;
    }
	
	/**
	 * Retrieve the scope controller for the requested scope
	 * @param scope
	 * @return
	 */
	protected IocScope _getScope(Scope scope)
	{
		switch (scope)
		{
			case LOCAL: return _localScope;
			case DOCUMENT: return _documentScope;
			case VIEW: return this._viewScope;
			default: return _localScope;
		}
	}
	
	@Override
	public String getBoundCruxViewId()
	{
	    return (this.view == null? null:this.view.getId());
	}

	@Override
	public View getBoundCruxView()
	{
	    return this.view;
	}
}
