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
package br.com.sysmap.crux.ext.client.component;

import java.util.Iterator;
import java.util.NoSuchElementException;

import br.com.sysmap.crux.core.client.component.Component;

/**
 * 
 * @author Thiago Bustamante
 *
 */
public class SimplePanel extends Panel
{
	protected com.google.gwt.user.client.ui.SimplePanel simplePanelWidget;
	protected Component component;

	public SimplePanel(String id)
	{
		this(id, new com.google.gwt.user.client.ui.SimplePanel());
	}

	protected SimplePanel(String id, com.google.gwt.user.client.ui.SimplePanel widget) 
	{
		super(id, widget);
		this.simplePanelWidget = widget;
	}

	@Override
	public void addComponent(Component component) 
	{
		super.addComponent(component);
		this.component = component;
		simplePanelWidget.add(getComponentWidget(component));
	}

	/**
	 * Gets the panel's child component.
	 * 
	 * @return the child component, or <code>null</code> if none is present
	 */
	public Component getComponent() 
	{
		return component;
	}

	public Iterator<Component> iterator() 
	{
		return new Iterator<Component>() 
		{
			boolean hasElement = component != null;
			Component returned = null;

			public boolean hasNext() 
			{
				return hasElement;
			}

			public Component next() 
			{
				if (!hasElement || (component == null)) 
				{
					throw new NoSuchElementException();
				}
				hasElement = false;
				return (returned = component);
			}

			public void remove() 
			{
				if (returned != null) 
				{
					SimplePanel.this.remove(returned);
				}
			}
		};
	}

	public boolean remove(Component c) 
	{
		if (c!= null && c.equals(component))
		{
			simplePanelWidget.remove(getComponentWidget(c));
			component = null;
			return true;
		}
		return false;
	}

	/**
	 * Sets this panel's component. Any existing child component will be removed.
	 * 
	 * @param component the panel's new component, or <code>null</code> to clear the panel
	 */
	public void setComponent(Component component) 
	{
		simplePanelWidget.setWidget(getComponentWidget(component));
		this.component = component;
	}
}
