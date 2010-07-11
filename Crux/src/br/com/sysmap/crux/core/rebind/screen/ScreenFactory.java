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
 
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

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
		if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableWebRootScannerCache()))
		{
			return getScreenWithCache(id);
		}
		else
		{
			return getScreenWithoutCache(id);
		}
	}

	/**
	 * @param id
	 * @return
	 * @throws ScreenConfigException
	 */
	private Screen getScreenWithoutCache(String id) throws ScreenConfigException
	{
		try 
		{
			InputStream stream = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenResource(id);
			if (stream == null)
			{
				throw new ScreenConfigException(messages.screenFactoryScreeResourceNotFound(id));
			}
			
			return parseScreen(id, stream);
		} 
		catch (Throwable e) 
		{
			throw new ScreenConfigException(messages.screenFactoryErrorRetrievingScreen(id, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * @param id
	 * @return
	 * @throws ScreenConfigException
	 */
	private Screen getScreenWithCache(String id) throws ScreenConfigException
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
	private Widget createWidget(Source source, Element element, Screen screen) throws ScreenConfigException
	{
		String widgetId = element.getAttributeValue("id");
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

	private String getScreenModule(List<?> scriptList) throws ScreenConfigException
	{
		String result = null;
		for (Object object : scriptList)
		{
			Element element = (Element)object;
			
			String src = element.getAttributeValue("src");
			
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
		if ("span".equalsIgnoreCase(element.getName()))
		{
			String type = element.getAttributeValue("_type");
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
		if ("span".equalsIgnoreCase(element.getName()))
		{
			String type = element.getAttributeValue("_type");
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
			String type = element.getAttributeValue("_type");
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
		Source source = new Source(stream);
		source.fullSequentialParse();

		String screenModule = getScreenModule(source.getAllElements("script"));
		
		if(screenModule != null)
		{
			screen = new Screen(id, screenModule);
			
			List<?> elementList = source.getAllElements("span");
			
			for (Object object : elementList) 
			{
				Element compCandidate = (Element) object;
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
	 */
	private void parseScreenElement(Screen screen, Element compCandidate) 
	{
		Element elem = (Element) compCandidate;
		
		Attributes attrs =  elem.getAttributes();
		for (Object object : attrs) 
		{
			Attribute attr = (Attribute)object;
			String attrName = attr.getName();
			
			if(attrName.equals("_useController"))
			{
				String handlerStr =  attr.getValue();
				if (handlerStr != null)
				{
					String[] handlers = RegexpPatterns.REGEXP_COMMA.split(handlerStr);
					for (String handler : handlers)
					{
						screen.addController(handler.trim());
					}
				}
			}
			else if(attrName.equals("_useSerializable"))
			{
				String serializerStr =  attr.getValue();
				if (serializerStr != null)
				{
					String[] serializers = RegexpPatterns.REGEXP_COMMA.split(serializerStr);
					for (String serializer : serializers)
					{
						screen.addSerializer(serializer.trim());
					}
				}
			}
			else if(attrName.equals("_useFormatter"))
			{
				String formatterStr =  attr.getValue();
				if (formatterStr != null)
				{
					String[] formatters = RegexpPatterns.REGEXP_COMMA.split(formatterStr);
					for (String formatter : formatters)
					{
						screen.addFormatter(formatter.trim());
					}
				}
			}
			else if(attrName.equals("_useDataSource"))
			{
				String datasourceStr =  attr.getValue();
				if (datasourceStr != null)
				{
					String[] datasources = RegexpPatterns.REGEXP_COMMA.split(datasourceStr);
					for (String datasource : datasources)
					{
						screen.addDataSource(datasource.trim());
					}
				}
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
			else if (!attrName.equals("id"))
			{
				if (logger.isDebugEnabled()) logger.debug(messages.screenPropertyError(attrName.substring(1), screen.getId()));
			}
		}
	}
}
