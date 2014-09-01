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

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public abstract class IocContainer implements ViewAware
{
	private RuntimeIoCContainer runtimeIoCContainer = null;
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
		runtimeIoCContainer = GWT.create(RuntimeIoCContainer.class);
		runtimeIoCContainer.setIoCContainer(this);
    }
	
	/**
	 * Retrieve an object from IoCContainer
	 * @param clazz object class
	 * @return object instance
	 */
	public <T> T get(Class<T> clazz)
	{
		return get(clazz, Scope.LOCAL, null);
	}

	/**
	 * Retrieve an object from IoCContainer
	 * @param clazz object class
	 * @param scope scope to search for the instance
	 * @return object instance
	 */
	public <T> T get(Class<T> clazz, Scope scope)
	{
		return get(clazz, scope, null);
	}

	/**
	 * Retrieve an object from IoCContainer
	 * @param clazz object class
	 * @param scope scope to search for the instance
	 * @param subscope subscope to search for the instance
	 * @return object instance
	 */
	public <T> T get(Class<T> clazz, Scope scope, String subscope)
	{
		return runtimeIoCContainer.get(clazz, scope, subscope);
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
}
