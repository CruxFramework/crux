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
package br.com.sysmap.crux.core.rebind.screen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represent a CRUX Widget at the application's server side. 
 * @author Thiago Bustamante
 */
public class Widget
{
	private int hashValue = 0;
	protected String id;
	protected String type;
	protected String formatter;
	protected String dataSource;

	protected Map<String, Event> events = new HashMap<String, Event>();
	protected Map<String, String> properties = new HashMap<String, String>();
	
	
	public Widget() 
	{
	}

	public Widget(String id) 
	{
		this.id = id;
	}

	protected void addEvent(Event event)
	{
		if (event != null)
		{
			events.put(event.getId(), event);
		}
	}
	
	public String getProperty(String propId)
	{
		return properties.get(propId);
	}
	
	public Iterator<String> iterateProperties()
	{
		return properties.values().iterator();
	}
	
	protected void addProperty(String id, String value)
	{
		if (id != null)
		{
			properties.put(id, value);
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
    	if (!(obj instanceof Widget)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((Widget)obj).getId();
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

	public String getType() 
	{
		return type;
	}

	void setType(String type) 
	{
		this.type = type;
	}

	public String getFormatter() 
	{
		return formatter;
	}

	public void setFormatter(String formatter) 
	{
		this.formatter = formatter;
	}

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}
}
