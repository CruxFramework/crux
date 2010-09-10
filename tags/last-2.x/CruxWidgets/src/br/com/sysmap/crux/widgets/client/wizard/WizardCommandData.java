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

import java.io.Serializable;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardCommandData implements Serializable
{
    private static final long serialVersionUID = 5916369163502763564L;

    private String id;
	private String label;
	private int order;
	private String styleName;
	private String width;
	private String height;
	
	public WizardCommandData()
	{
		
	}
	
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
	public String getStyleName()
    {
    	return styleName;
    }
	public void setStyleName(String styleName)
    {
    	this.styleName = styleName;
    }
	public String getWidth()
    {
    	return width;
    }
	public void setWidth(String width)
    {
    	this.width = width;
    }
	public String getHeight()
    {
    	return height;
    }
	public void setHeight(String height)
    {
    	this.height = height;
    }
}
