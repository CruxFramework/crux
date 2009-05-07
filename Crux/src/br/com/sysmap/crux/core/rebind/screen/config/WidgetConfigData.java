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
package br.com.sysmap.crux.core.rebind.screen.config;

import br.com.sysmap.crux.core.rebind.screen.WidgetParser;

public class WidgetConfigData 
{
	public static final String PARSER_INPUT_DOM = "dom";
	public static final String PARSER_INPUT_JERICHO = "jericho";
	public static final String PARSER_INPUT_STRING = "string";
	
	protected String clientClass;
	protected String serverClass;
	protected WidgetParser widgetParser;
	protected String parserInput;

	public WidgetConfigData(String clientClass, 							
			                   String serverClass, 
							   WidgetParser widgetParser,
							   String parserInput) 
	{
		this.clientClass = clientClass;
		this.serverClass = serverClass;
		this.widgetParser = widgetParser;
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
	public WidgetParser getWidgetParser() 
	{
		return widgetParser;
	}
	public void setWidgetParser(WidgetParser widgetParser) 
	{
		this.widgetParser = widgetParser;
	}
	public String getParserInput() 
	{
		return parserInput;
	}
	public void setParserInput(String parserInput) 
	{
		this.parserInput = parserInput;
	}
}
