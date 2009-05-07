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
package br.com.sysmap.crux.core.client.component;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;


/**
 * Base class for create new containers.
 * @author Thiago
 *
 */
public abstract class HasWidgetsFactory<T extends Widget> extends WidgetFactory<T> 
{
	private static int currentId = 0;

	/**
	 * Gives to factory the opportunity to add a child widget
	 * @param parent
	 * @param child
	 * @param parentElement
	 * @param childElement
	 */
	public abstract void add(T parent,  Widget child, Element parentElement, Element childElement);	
	
	/**
	 * Returns the element which is the father of the given one. If it does not have an id, creates a random for it
	 * @param child
	 * @return
	 */
	protected Element getParentElement(Element child)
	{
		Element parent = child.getParentElement();
		
		String id = parent.getId();
		if(id == null || id.trim().length() == 0)
		{
			parent.setId(generateNewId());
		}
		
		return parent;
	}

	/**
	 * Creates a sequential id
	 * @return
	 */
	private static String generateNewId() 
	{
		return "_crux_" + (++currentId );
	}
}
