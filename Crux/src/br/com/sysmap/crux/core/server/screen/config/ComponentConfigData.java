package br.com.sysmap.crux.core.server.screen.config;

import br.com.sysmap.crux.core.server.screen.ComponentParser;
import br.com.sysmap.crux.core.server.screen.ComponentRenderer;

public class ComponentConfigData 
{
	public static final String PARSER_INPUT_DOM = "dom";
	public static final String PARSER_INPUT_JERICHO = "jericho";
	public static final String PARSER_INPUT_STRING = "string";
	
	protected String clientClass;
	protected String serverClass;
	protected ComponentRenderer componentRenderer;
	protected ComponentParser componentParser;
	protected String parserInput;
	protected String clientConstructorParams;

	public ComponentConfigData(String clientClass, 
							   String clientConstructorParams,
			                   String serverClass, 
							   ComponentRenderer componentRenderer, 
							   ComponentParser componentParser,
							   String parserInput) 
	{
		this.clientClass = clientClass;
		this.clientConstructorParams = clientConstructorParams;
		this.serverClass = serverClass;
		this.componentRenderer = componentRenderer;
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
	public ComponentRenderer getComponentRenderer() 
	{
		return componentRenderer;
	}
	public void setComponentRenderer(ComponentRenderer componentRenderer) 
	{
		this.componentRenderer = componentRenderer;
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
