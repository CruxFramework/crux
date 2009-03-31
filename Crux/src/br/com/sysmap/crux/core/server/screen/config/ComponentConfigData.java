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
package br.com.sysmap.crux.core.server.screen.config;

import br.com.sysmap.crux.core.server.screen.ComponentParser;

public class ComponentConfigData 
{
	public static final String PARSER_INPUT_DOM = "dom";
	public static final String PARSER_INPUT_JERICHO = "jericho";
	public static final String PARSER_INPUT_STRING = "string";
	
	protected String clientClass;
	protected String serverClass;
	protected ComponentParser componentParser;
	protected String parserInput;
	protected String clientConstructorParams;

	public ComponentConfigData(String clientClass, 
							   String clientConstructorParams,
			                   String serverClass, 
							   ComponentParser componentParser,
							   String parserInput) 
	{
		this.clientClass = clientClass;
		this.clientConstructorParams = clientConstructorParams;
		this.serverClass = serverClass;
		this.componentParser = componentParser;
		this.parserInput = parserInput;
	}
	
	public String getClientClass() 
	{
		return clientClass;
	}
	public void setClientClass(String clientClass) 
	{
		this.clientClass = clientClass;
	}
	public String getServerClass() 
	{
		return serverClass;
	}
	public void setServerClass(String serverClass) 
	{
		this.serverClass = serverClass;
	}
	public ComponentParser getComponentParser() 
	{
		return componentParser;
	}
	public void setComponentParser(ComponentParser componentParser) 
	{
		this.componentParser = componentParser;
	}
	public String getParserInput() 
	{
		return parserInput;
	}
	public void setParserInput(String parserInput) 
	{
		this.parserInput = parserInput;
	}
	public String getClientConstructorParams() 
	{
		return clientConstructorParams;
	}
	public void setClientConstructorParams(String clientConstructorParams) 
	{
		this.clientConstructorParams = clientConstructorParams;
	}
}
