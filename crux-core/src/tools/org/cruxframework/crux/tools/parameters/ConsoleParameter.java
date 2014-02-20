/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.tools.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConsoleParameter implements Cloneable
{
	private String name;
	private String[] values;
	private String description;
	private boolean required;
	private boolean flagParameter;

	private List<ConsoleParameterOption> parameterOptions = new ArrayList<ConsoleParameterOption>(); 
	
	/**
	 * @param name
	 * @param description
	 */
	public ConsoleParameter(String name, String description)
	{
		this(name, description, true, false);
	}
	
	/**
	 * @param name
	 * @param description
	 */
	public ConsoleParameter(String name, String description, boolean required, boolean flagParameter)
	{
		if (name == null)
		{
			throw new NullPointerException("Name parameter is null");
		}
		this.name = name;
		this.description = description;
		this.required = required;
		this.flagParameter = flagParameter;
	}
	
	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return
	 */
	public String getValue()
	{
		return values!= null && values.length == 1? values[0]:null;
	}
	
	/**
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param option
	 */
	public void addParameterOption(ConsoleParameterOption option)
	{
		if (!isFlagParameter())
		{
			throw new ConsoleParametersProcessingException("Non flag parameters can not have options");
		}
		parameterOptions.add(option);
	}

	/**
	 * @return
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * @param required
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
	/**
	 * @return
	 */
	public boolean isFlagParameter()
	{
		return flagParameter;
	}

	/**
	 * @param flagParameter
	 */
	public void setFlagParameter(boolean flagParameter)
	{
		this.flagParameter = flagParameter;
	}

	/**
	 * @return
	 */
	public Iterator<ConsoleParameterOption> iterateOptions()
	{
		return parameterOptions.iterator();
	}
	
	/**
	 * @return
	 */
	public boolean hasOptions()
	{
		return parameterOptions.size() > 0;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		ConsoleParameter result = (ConsoleParameter) super.clone();
		
		result.parameterOptions = new ArrayList<ConsoleParameterOption>();
		for (ConsoleParameterOption option : parameterOptions)
		{
			result.parameterOptions.add((ConsoleParameterOption)option.clone());
		}
		
		return result;
	}

	public String[] getValues()
	{
		return values;
	}

	void setValues(String[] values)
	{
		this.values = values;
	}
	
	
}
