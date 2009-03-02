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
