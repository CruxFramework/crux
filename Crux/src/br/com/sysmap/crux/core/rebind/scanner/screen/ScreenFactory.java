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
package br.com.sysmap.crux.core.rebind.scanner.screen;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.controller.ClientControllers;
import br.com.sysmap.crux.core.rebind.scanner.screen.datasource.DataSources;
import br.com.sysmap.crux.core.rebind.scanner.screen.formatter.Formatters;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.core.utils.XMLUtils;
import br.com.sysmap.crux.core.utils.XMLUtils.XMLException;

/**
 * Factory for screens at the application's server side. It is necessary for GWT generators 
 * and for parameters binding.
 *  
 * @author Thiago Bustamante
 *
 */
public class ScreenFactory 
{
	private static ScreenFactory instance = new ScreenFactory();
	private static final Log logger = LogFactory.getLog(ScreenFactory.class);
	
	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static final Lock screenLock = new ReentrantLock();	

	private Map<String, Screen> screenCache = new HashMap<String, Screen>();	
		
	/**
	 * Singleton Constructor
	 */
	private ScreenFactory() 
	{
	}
	
	/**
	 * Singleton method
	 * @return
	 */
	public static ScreenFactory getInstance()
	{
		return instance;
	}
	
	/**
	 * Factory method for screens.
	 * @param id
	 * @return
	 * @throws ScreenConfigException
	 */
	public Screen getScreen(String id) throws ScreenConfigException
	{
		try 
		{
			Screen screen = screenCache.get(id);
			if (screen != null)
			{
				return screen;
			}

			InputStream stream = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(id);
			if (stream == null)
			{
				throw new ScreenConfigException(messages.screenFactoryScreeResourceNotFound(id));
			}
			
			screenLock.lock();
			try
			{
				if (screenCache.get(id) == null)
				{
					screen = parseScreen(id, stream);
					if(screen != null)
					{
						screenCache.put(id, screen);
					}
				}
			}
			finally
			{
				screenLock.unlock();
			}
			return screenCache.get(id);
			
		} 
		catch (Throwable e) 
		{
			throw new ScreenConfigException(messages.screenFactoryErrorRetrievingScreen(id, e.getLocalizedMessage()), e);
		}
	}

	/**
	 * Creates a widget based in its &lt;span&gt; tag definition.
	 * 
	 * @param source
	 * @param element
	 * @param screen
	 * @return
	 * @throws ScreenConfigException
	 */
	private Widget createWidget(Document source, Element element, Screen screen) throws ScreenConfigException
	{
		String widgetId = element.getAttribute("id");
		if (widgetId == null || widgetId.trim().length() == 0)
		{
			throw new ScreenConfigException(messages.screenFactoryWidgetIdRequired());
		}
		Widget widget = screen.getWidget(widgetId);
		if (widget != null)
		{
			throw new ScreenConfigException(messages.screenFactoryErrorDuplicatedWidget(widgetId));
		}
		
		widget = newWidget(element, widgetId);
		if (widget == null)
		{
			throw new ScreenConfigException(messages.screenFactoryErrorCreateWidget(widgetId));
		}
		
		screen.addWidget(widget);
		return widget;
	}

	private String getScreenModule(NodeList nodeList) throws ScreenConfigException
	{
		String result = null;
		
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++)
		{
			Element item = (Element) nodeList.item(i);
			
			String src = item.getAttribute("src");
			
			if (src != null && src.endsWith(".nocache.js"))
			{
				if (result != null)
				{
					throw new ScreenConfigException(messages.screenFactoryErrorMultipleModulesOnPage());
				}
				
				int lastSlash = src.lastIndexOf("/");
				
				if(lastSlash >= 0)
				{
					int firstDotAfterSlash = src.indexOf(".", lastSlash);
					result = src.substring(lastSlash + 1, firstDotAfterSlash);
				}
				else
				{
					int firstDot = src.indexOf(".");
					result = src.substring(0, firstDot);
				}
			}
		}
		return result;
	}
	
	/**
	 * Test if a target HTML element represents a Screen definition for Crux.
	 * @param element
	 * @return
	 */
	private boolean isScreenDefinitions(Element element)
	{
		if ("span".equalsIgnoreCase(element.getLocalName()))
		{
			String type = element.getAttribute("_type");
			if (type != null && "screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Test if a target HTML element represents a widget definition for Crux.
	 * @param element
	 * @return
	 */
	private boolean isValidWidget(Element element)
	{
		if ("span".equalsIgnoreCase(element.getLocalName()))
		{
			String type = element.getAttribute("_type");
			if (type != null && type.trim().length() > 0 && !"screen".equals(type))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Builds a new widget, based on its &lt;span&gt; tag definition.
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws ScreenConfigException
	 */
	private Widget newWidget(Element element, String widgetId) throws ScreenConfigException
	{
		try 
		{
			String type = element.getAttribute("_type");
			WidgetParser parser = new WidgetParserImpl();
			Widget widget = new Widget();
			widget.setId(widgetId);
			widget.setType(type);
			parser.parse(widget, element);
			return widget;
		} 
		catch (Throwable e) 
		{
			throw new ScreenConfigException(messages.screenFactoryErrorCreateWidget(widgetId), e);
		} 
	}
	
	/**
	 * Parse the HTML page and build the Crux Screen. 
	 * @param id
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws ScreenConfigException 
	 */
	private Screen parseScreen(String id, InputStream stream) throws IOException, ScreenConfigException
	{
		Screen screen = null;
		Document source = null;
		
		try
		{
			source = XMLUtils.createNSUnawareDocument(stream);
		}
		catch (XMLException e)
		{
			logger.error(e.getMessage(), e);
			throw new ScreenConfigException(messages.screenFactoryErrorParsingScreen(id, e.getMessage()));
		}

		String screenModule = getScreenModule(source.getElementsByTagName("script"));
		
		if(screenModule != null)
		{
			screen = new Screen(id, screenModule);
			
			NodeList elementList = source.getElementsByTagName("span");
			
			int length = elementList.getLength();
			for (int i = 0; i < length; i++) 
			{
				Element compCandidate = (Element) elementList.item(i);
				if (isValidWidget(compCandidate))
				{
					try 
					{
						createWidget(source, compCandidate, screen);
					} 
					catch (ScreenConfigException e) 
					{
						logger.error(messages.screenFactoryGenericErrorCreateWidget(id, e.getLocalizedMessage()));
					}
				}
				else if (isScreenDefinitions(compCandidate))
				{
					parseScreenElement(screen,compCandidate);
				}
			} 
		}
		
		return screen;
	}

	/**
	 * Parse screen element
	 * @param screen
	 * @param compCandidate
	 * @throws ScreenConfigException 
	 */
	private void parseScreenElement(Screen screen, Element compCandidate) throws ScreenConfigException 
	{
		Element elem = (Element) compCandidate;
		
		NamedNodeMap attributes = elem.getAttributes();
		
		int length = attributes.getLength();
		
		for (int i = 0; i < length; i++) 
		{
			Attr attr = (Attr) attributes.item(i);
			String attrName = attr.getName();
			
			if(attrName.equals("_useController"))
			{
				parseScreenUseControllerAttribute(screen, attr);
			}
			else if(attrName.equals("_useSerializable"))
			{
				parseScreenUseSerializableAttribute(screen, attr);
			}
			else if(attrName.equals("_useFormatter"))
			{
				parseScreenUseFormatterAttribute(screen, attr);
			}
			else if(attrName.equals("_useDataSource"))
			{
				parseScreenUseDatasourceAttribute(screen, attr);
			}
			else if (attrName.startsWith("_on"))
			{
				Event event = EventFactory.getEvent(attrName, attr.getValue());
				if (event != null)
				{
					screen.addEvent(event);
				}
			}
			else if (attrName.equals("_title"))
			{
				String title = attr.getValue();
				if (title != null && title.length() > 0)
				{
					screen.setTitle(title);
				}
			}
			else if (!attrName.equals("id") && !attrName.equals("_type"))
			{
				if (logger.isDebugEnabled()) logger.debug(messages.screenPropertyError(attrName.substring(1), screen.getId()));
			}
		}
	}

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseDatasourceAttribute(Screen screen, Attr attr) throws ScreenConfigException
    {
	    String datasourceStr =  attr.getValue();
	    if (datasourceStr != null)
	    {
	    	String[] datasources = RegexpPatterns.REGEXP_COMMA.split(datasourceStr);
	    	for (String datasource : datasources)
	    	{
	    		datasource = datasource.trim();
	    		if (!StringUtils.isEmpty(datasource))
	    		{
	    			if (DataSources.getDataSource(datasource) == null)
	    			{
	    				throw new ScreenConfigException(messages.screenFactoryInvalidDataSource(datasource, screen.getId()));
	    			}
	    			screen.addDataSource(datasource);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseFormatterAttribute(Screen screen, Attr attr) throws ScreenConfigException
    {
	    String formatterStr =  attr.getValue();
	    if (formatterStr != null)
	    {
	    	String[] formatters = RegexpPatterns.REGEXP_COMMA.split(formatterStr);
	    	for (String formatter : formatters)
	    	{
	    		formatter = formatter.trim();
	    		if (!StringUtils.isEmpty(formatter))
	    		{
	    			if (Formatters.getFormatter(formatter) == null)
	    			{
	    				throw new ScreenConfigException(messages.screenFactoryInvalidFormatter(formatter, screen.getId()));
	    			}
	    			screen.addFormatter(formatter);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	@SuppressWarnings("deprecation")
    private void parseScreenUseSerializableAttribute(Screen screen, Attr attr) throws ScreenConfigException
    {
	    String serializerStr =  attr.getValue();
	    if (serializerStr != null)
	    {
	    	String[] serializers = RegexpPatterns.REGEXP_COMMA.split(serializerStr);
	    	for (String serializer : serializers)
	    	{
	    		serializer = serializer.trim();
	    		if (!StringUtils.isEmpty(serializer))
	    		{
	    			if (br.com.sysmap.crux.core.rebind.scanner.screen.serializable.Serializers.getCruxSerializable(serializer) == null)
	    			{
	    				throw new ScreenConfigException(messages.screenFactoryInvalidSerializable(serializer, screen.getId()));
	    			}
	    			screen.addSerializer(serializer);
	    		}
	    	}
	    }
    }

	/**
	 * @param screen
	 * @param attr
	 * @throws ScreenConfigException 
	 */
	private void parseScreenUseControllerAttribute(Screen screen, Attr attr) throws ScreenConfigException
    {
	    String handlerStr =  attr.getValue();
	    if (handlerStr != null)
	    {
	    	String[] handlers = RegexpPatterns.REGEXP_COMMA.split(handlerStr);
	    	for (String handler : handlers)
	    	{
	    		handler = handler.trim();
	    		if (!StringUtils.isEmpty(handler))
	    		{
	    			if (ClientControllers.getController(handler) == null)
	    			{
	    				throw new ScreenConfigException(messages.screenFactoryInvalidController(handler, screen.getId()));
	    			}
	    			screen.addController(handler);
	    		}
	    	}
	    }
    }
}
