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
package br.com.sysmap.crux.core.server.screen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Container extends Component implements Cloneable
{
	protected Map<String, Component> components = new HashMap<String, Component>();
	
	public Container() 
	{
	}

	public Container(String id) 
	{
		super(id);
	}

	public Component getComponent(String componentId)
	{
		if (componentId == null) return null;
		return components.get(componentId);
	}
	
	void addComponent(Component component)
	{
		if (component != null)
		{
			component.parent = this;
			component.screen = this.screen;
			components.put(component.getId(), component);
		}
	}
	
	public Iterator<Component> iterateComponents() 
	{
		return components.values().iterator();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		Container result = (Container) super.clone();
		result.components = new HashMap<String, Component>();
		for (String key : components.keySet()) 
		{
			result.addComponent((Component)components.get(key).clone());
		}
		
		return result;
	}
}
