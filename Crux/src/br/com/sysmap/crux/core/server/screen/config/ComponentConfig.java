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
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ComponentConfigScanner;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.core.server.screen.ComponentParser;
import br.com.sysmap.crux.core.server.screen.ComponentRenderer;
import br.com.sysmap.crux.core.server.screen.config.parser.ComponentT;
import br.com.sysmap.crux.core.server.screen.config.parser.CruxT;

public class ComponentConfig 
{
	private static Map<String, ComponentConfigData> config = null;
	private static Set<String> registeredLibraries = null;
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Log logger = LogFactory.getLog(ComponentConfig.class);
	private static final Lock lock = new ReentrantLock();

	public static void initializeComponentConfig()
	{
		initializeComponentConfig(ScannerURLS.getURLsForSearch(null));
	}
	
	public static void initializeComponentConfig(URL[] urls)
	{
		if (config != null) return;
		lock.lock();
		try
		{
			if (config != null) return;
			config = new HashMap<String, ComponentConfigData>(100);
			registeredLibraries = new HashSet<String>();
			ComponentConfigScanner.getInstance().scanArchives(urls);
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
			Unmarshaller unmarshaller = JAXBContext.newInstance("br.com.sysmap.crux.core.server.screen.config.parser").createUnmarshaller();
			InputSource input = new InputSource(inputStream);
			
			CruxT crux = (CruxT)((JAXBElement<?>) unmarshaller.unmarshal(input)).getValue();
			
			registeredLibraries.add(crux.getModule());
			
			for (ComponentT component : crux.getComponent())
			{
				ComponentParser componentParser = (ComponentParser) Class.forName(component.getServerParserClass()).newInstance();	
				ComponentRenderer componentRenderer = (ComponentRenderer) Class.forName(component.getServerRendererClass()).newInstance();	
				
				ComponentConfigData data = new ComponentConfigData(component.getClientClass(),
																   component.getClientConstructorParams(), 
																   component.getServerClass(), 
																   componentRenderer, 
																   componentParser,
																   component.getParserInput().value());
				config.put(component.getId(), data);
			}
		} 
		catch (Throwable e) 
		{
			logger.error(messages.componentConfigParserError(e.getLocalizedMessage()),e);
		}
	}
	
	public static String getServerClass(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getServerClass();
	}

	public static ComponentParser getComponentParser(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getComponentParser();
	}

	public static ComponentRenderer getComponentRenderer(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getComponentRenderer();
	}

	public static String getClientClass(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getClientClass();
	}

	public static String getClientConstructorParams(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getClientConstructorParams();
	}

	public static String getParserInput(String id)
	{
		if (config == null)
		{
			initializeComponentConfig();
		}
		ComponentConfigData data = config.get(id);
		if (data == null) return null;
		return data.getParserInput();
	}
	
	public static Set<String> getRegisteredLibraries()
	{
		if (registeredLibraries == null)
		{
			initializeComponentConfig();
		}
		
		return registeredLibraries;
	}
	
}
