/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.user.client.ui.ComplexPanel;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
abstract class AbstractList extends ComplexPanel 
{
	protected String className;

	public AbstractList(String className) 
	{
		setElement();
		this.className = className;
		if(!StringUtils.isEmpty(className) || !StringUtils.isEmpty(getDefaultClassName()))
		{
			getElement().setClassName(StringUtils.isEmpty(className) ? getDefaultClassName() : className);
		}
	}
	
	protected abstract void setElement();
	
	protected abstract String getDefaultClassName();
	
	public void add(ListItem w) 
	{
		super.add(w, getElement());
	}

	public void insert(ListItem w, int beforeIndex) 
	{
		super.insert(w, getElement(), beforeIndex, true);
	}
}
