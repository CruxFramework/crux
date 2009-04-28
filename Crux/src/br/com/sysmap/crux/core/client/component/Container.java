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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for create new containers.
 * @author Thiago
 *
 */
public abstract class Container extends Component 
{
	protected Map<String, Component> components = new HashMap<String, Component>(30);

	protected Container(String id, Widget widget) 
	{
		super(id, widget);
	}

	public void addComponent(Component component)
	{
		components.put(component.getId(), component);
		addWidget(component.widget);
	}
	
	public void removeComponent(Component component)
	{
		components.remove(component.getId());
		removeWidget(component.widget);
	}
	
	public Component getComponent (String id)
	{
		return components.get(id);
	}
	
	public Iterator<Component> iterateComponents() 
	{
		return components.values().iterator();
	}
	
	public void clear()
	{
		components.clear();
		clearWidgetChildren(this.widget);
	}
	
	/**
	 * Provide access to component's widget. Used for container subclasses that
	 * need to access widgets of their children. 
	 * @param component
	 * @return
	 */
	protected Widget getComponentWidget(Component component)
	{
		return (component.widget!=null?component.widget:null);
	}
	
	protected abstract void addWidget(Widget widget);
	
	protected abstract void removeWidget(Widget widget);

	protected abstract void clearWidgetChildren(Widget widget);
}
