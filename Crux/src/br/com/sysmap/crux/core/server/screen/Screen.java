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

import br.com.sysmap.crux.core.server.screen.formatter.ServerFormatter;

public class Screen implements Cloneable
{
	protected String id;
	protected boolean checkChanges = false;
	protected Container root;
	protected boolean manageHistory = false;
	protected static Map<String, ServerFormatter> formatters = new HashMap<String, ServerFormatter>();
	
	public Screen(String id) 
	{
		this(id, new Container(null));
	}
	
	public Screen(String id, Container root) 
	{
		this.id = id;
		this.root = root;
		root.screen = this;
	}

	public Component getComponent(String componentId)
	{
		return root.getComponent(componentId);
	}

	public Iterator<Component> iterateComponents() 
	{
		return root.iterateComponents();
	}

	protected void addComponent(Component component)
	{
		root.addComponent(component);
	}
	
	public String getId() 
	{
		return id;
	}

	public boolean isCheckChanges() 
	{
		return checkChanges;
	}

	public void setCheckChanges(boolean checkChanges) 
	{
		this.checkChanges = checkChanges;
	}
	
	public boolean isManageHistory() {
		return manageHistory;
	}

	public void setManageHistory(boolean manageHistory) {
		if (isCheckChanges() && this.manageHistory != manageHistory )
		{
			root.dirty = true;
		}
		this.manageHistory = manageHistory;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		Screen result = (Screen)super.clone(); 
		result.root = (Container)this.root.clone();
		updateComponentScreen(result, result.root);
		return result; 
	}
	
	private void updateComponentScreen(Screen screen, Component component)
	{
		component.screen = screen;
		
		if (component instanceof Container)
		{
			for (Component child : ((Container)component).components.values()) 
			{
				updateComponentScreen(screen, child);
			}
		}
	}
	
	public boolean isDirty()
	{
		return root.isDirty();
	}
	
	protected void addEvent(Event event)
	{
		root.addEvent(event);
	}
	
	public Event getEvent(String evtId)
	{
		return root.getEvent(evtId);
	}
	
	public Iterator<Event> iterateEvents()
	{
		return root.iterateEvents();
	}
	
	public ServerFormatter getFormatter(String property)
	{
		return formatters.get(property);
	}
	
	public void setFormatter(String property, ServerFormatter formatter)
	{
		formatters.put(property, formatter);
	}
	
	public boolean containsFormatter(String property)
	{
		return formatters.containsKey(property);
	}
	
}
