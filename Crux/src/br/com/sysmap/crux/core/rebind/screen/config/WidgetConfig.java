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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.screen.WidgetParser;
import br.com.sysmap.crux.core.rebind.screen.config.parser.CruxT;
import br.com.sysmap.crux.core.rebind.screen.config.parser.WidgetT;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.core.server.scan.WidgetConfigScanner;

public class WidgetConfig 
{
	private static Map<String, WidgetConfigData> config = null;
	private static Set<String> registeredLibraries = null;
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Log logger = LogFactory.getLog(WidgetConfig.class);
	private static final Lock lock = new ReentrantLock();

	public static void initializeWidgetConfig()
	{
		initializeWidgetConfig(ScannerURLS.getURLsForSearch());
	}
	
	public static void initializeWidgetConfig(URL[] urls)
	{
		if (config != null) return;
		lock.lock();
		try
		{
			if (config != null) return;
			config = new HashMap<String, WidgetConfigData>(100);
			registeredLibraries = new HashSet<String>();
			WidgetConfigScanner.getInstance().scanArchives(urls);
			if (logger.isInfoEnabled())
			{
				logger.info(messages.widgetCongigWidgetsRegistered());
			}
		}
		catch (RuntimeException e) 
		{
			config = null;
			registeredLibraries = null;
			throw (e);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public static void parseCruxConfigFile(InputStream inputStream)
	{
		try 
		{
			Unmarshaller unmarshaller = JAXBContext.newInstance("br.com.sysmap.crux.core.rebind.screen.config.parser").createUnmarshaller();
			InputSource input = new InputSource(inputStream);
			
			CruxT crux = (CruxT)((JAXBElement<?>) unmarshaller.unmarshal(input)).getValue();
			
			registeredLibraries.add(crux.getModule());
			
			for (WidgetT widget : crux.getWidget())
			{
				WidgetParser widgetParser = (WidgetParser) Class.forName(widget.getServerParserClass()).newInstance();	
				
				WidgetConfigData data = new WidgetConfigData(widget.getClientClass(),
																   widget.getServerClass(), 
																   widgetParser,
																   widget.getParserInput().value());
				config.put(widget.getId(), data);
			}
		} 
		catch (Throwable e) 
		{
			logger.error(messages.widgetConfigParserError(e.getLocalizedMessage()),e);
		}
	}
	
	public static String getServerClass(String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		WidgetConfigData data = config.get(id);
		if (data == null) return null;
		return data.getServerClass();
	}

	public static WidgetParser getWidgetParser(String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		WidgetConfigData data = config.get(id);
		if (data == null) return null;
		return data.getWidgetParser();
	}

	public static String getClientClass(String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		WidgetConfigData data = config.get(id);
		if (data == null) return null;
		return data.getClientClass();
	}

	public static String getParserInput(String id)
	{
		if (config == null)
		{
			initializeWidgetConfig();
		}
		WidgetConfigData data = config.get(id);
		if (data == null) return null;
		return data.getParserInput();
	}
	
	public static Set<String> getRegisteredLibraries()
	{
		if (registeredLibraries == null)
		{
			initializeWidgetConfig();
		}
		
		return registeredLibraries;
	}
	
}
