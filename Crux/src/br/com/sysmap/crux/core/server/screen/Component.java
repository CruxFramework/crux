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

public class Component implements Cloneable
{
	private int hashValue = 0;
	protected String id;
	protected boolean dirty = false;
	protected Screen screen;
	protected Container parent;
	protected String type;
	protected String serverBind;
	protected String width;
	protected String height;
	protected String className;
	protected String formatter;
	protected String style;

	protected Map<String, Event> events = new HashMap<String, Event>();
	
	
	public Component() 
	{
	}

	public Component(String id) 
	{
		this.id = id;
	}

	protected void addEvent(Event event)
	{
		if (event != null)
		{
			if (isCheckChanges())
			{
				dirty = true;
			}
			events.put(event.getId(), event);
		}
	}
	
	public Event getEvent(String evtId)
	{
		return events.get(evtId);
	}
	
	public Iterator<Event> iterateEvents()
	{
		return events.values().iterator();
	}
	
	public boolean equals(Object obj) 
	{
    	if (obj == null) return false;
    	if (!(obj instanceof Component)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((Component)obj).getId();
    	return (compId1 == null?compId2==null:compId1.equals(compId2));
    }

	public String getId() 
	{
		return id;
	}

	void setId(String id)
	{
		this.id = id;
	}
	
	public Container getParent() 
	{
		return parent;
	}

	public Screen getScreen() 
	{
		return screen;
	}
	
	public int hashCode()
    {
        if (this.hashValue == 0)
        {
            int result = 17;
            String compStr = this.getId();
            int idComp = compStr == null ? 0 : compStr.hashCode();
            result = result * 37 + idComp;
            this.hashValue = result;
        }
        return this.hashValue;
    }

	protected boolean isCheckChanges()
	{
		return (screen!=null && screen.isCheckChanges());
	}

	public boolean isDirty() 
	{
		if(!dirty)
		{
			Container aParent = parent;
			while(aParent != null)
			{
				if(aParent.isDirty())
				{
					return true;
				}
				aParent = aParent.parent;
			}
		}
		return dirty;
	}
	
	void setParent(Container parent) 
	{
		this.parent = parent;
	}
	
	void setScreen(Screen screen) 
	{
		this.screen = screen;
	}

	public String getType() 
	{
		return type;
	}

	void setType(String type) 
	{
		this.type = type;
	}

	public String getServerBind() 
	{
		return serverBind;
	}

	public void setServerBind(String serverBind) 
	{
		if (isCheckChanges() &&
			((serverBind != null && this.serverBind == null) || (serverBind == null && this.serverBind != null) ||
			 (serverBind != null && this.serverBind != null && !serverBind.equals(this.serverBind))))
		{
			dirty = true;
		}
		this.serverBind = serverBind;
	}

	public String getWidth() 
	{
		return width;
	}

	public void setWidth(String width) 
	{
		if (isCheckChanges() &&
			((width != null && this.width == null) || (width == null && this.width != null) || (width != null && this.width != null && !width.equals(this.width))))
		{
			dirty = true;
		}
		this.width = width;
	}

	public String getHeight() 
	{
		return height;
	}

	public void setHeight(String height) 
	{
		if (isCheckChanges() &&
			((height != null && this.height == null) || (height == null && this.height != null) || (height != null && this.height != null && !height.equals(this.height))))
		{
			dirty = true;
		}
		this.height = height;
	}

	public String getClassName() 
	{
		return className;
	}

	public void setClassName(String className) 
	{
		if (isCheckChanges() &&
			((className != null && this.className == null) || (className == null && this.className != null) || 
			 (className != null && this.className != null && !className.equals(this.className))))
		{
			dirty = true;
		}
		this.className = className;
	}

	public String getStyle() 
	{
		return style;
	}

	public void setStyle(String style) 
	{
		if (isCheckChanges() &&
			((style != null && this.style == null) || (style == null && this.style != null) || (style != null && this.style != null && !style.equals(this.style))))
		{
			dirty = true;
		}
		this.style = style;
	} 
	
	public String getFormatter() 
	{
		return formatter;
	}

	public void setFormatter(String formatter) 
	{
		if (isCheckChanges() &&
			((formatter != null && this.formatter == null) || (formatter == null && this.formatter != null) || (formatter != null && this.formatter != null && !formatter.equals(this.formatter))))
		{
			dirty = true;
		}
		this.formatter = formatter;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		Component result = (Component)super.clone();
		result.events = new HashMap<String, Event>();
		for (String key : events.keySet()) 
		{
			result.addEvent((Event)events.get(key).clone());
		}
		
		return result;
	}
}
