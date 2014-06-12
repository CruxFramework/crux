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
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
abstract class AbstractListItem extends ComplexPanel 
{
	protected String className;

	public AbstractListItem(String className) 
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
	
	@Override
	public void add(Widget w) 
	{
		super.add(w, getElement());
	}

	public void insert(Widget w, int beforeIndex) 
	{
		super.insert(w, getElement(), beforeIndex, true);
	}
}
