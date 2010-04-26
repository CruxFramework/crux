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
package br.com.sysmap.crux.widgets.client.wizard;

import br.com.sysmap.crux.core.client.screen.CruxSerializable;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class WizardCommandData implements CruxSerializable
{
	private String id;
	private String label;
	private int order;
	
	public WizardCommandData(String id, String label, int order)
    {
		this.id = id;
		this.label = label;
		this.order = order;
    }
	
	public String getId()
    {
    	return id;
    }
	public void setId(String id)
    {
    	this.id = id;
    }
	public String getLabel()
    {
    	return label;
    }
	public void setLabel(String label)
    {
    	this.label = label;
    }
	public int getOrder()
    {
    	return order;
    }
	public void setOrder(int order)
    {
    	this.order = order;
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#deserialize(java.lang.String)
	 */
	public Object deserialize(String serializedData)
    {
		if (serializedData != null && serializedData.length() > 0)
		{
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			return new WizardCommandData(data.getAttribute("id"), 
								 data.getAttribute("label"), 
								 Integer.parseInt(data.getAttribute("order")));
		}
		return null;
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#newArray(int)
	 */
	public Object[] newArray(int size)
    {
	    return new WizardCommandData[size];
    }

	/**
	 * @see br.com.sysmap.crux.core.client.screen.CruxSerializable#serialize()
	 */
	public String serialize()
    {
		Document document = XMLParser.createDocument();

		Element data = document.createElement("data");
		document.appendChild(data);
		
		data.setAttribute("id", id);
		data.setAttribute("label", label);
		data.setAttribute("order", Integer.toString(order));
		
		return document.toString();
    }
}
