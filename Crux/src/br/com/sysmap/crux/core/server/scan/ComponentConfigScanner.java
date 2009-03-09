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
package br.com.sysmap.crux.core.server.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;
import org.scannotation.archiveiterator.StreamIterator;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.screen.config.ComponentConfig;

public class ComponentConfigScanner 
{
	private static final Log logger = LogFactory.getLog(ComponentConfigScanner.class);
	private static final ComponentConfigScanner instance = new ComponentConfigScanner();
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	private ComponentConfigScanner() 
	{
	}
	
	protected transient String[] ignoredPackages = {"javax", "java", "sun", "com.sun", "org.apache", 
													"com.google", "javassist", "org.json", 
													"com.metaparadigm", "junit"};

	public String[] getIgnoredPackages()
	{
		return ignoredPackages;
	}

	public void setIgnoredPackages(String[] ignoredPackages)
	{
		this.ignoredPackages = ignoredPackages;
	}

	public void scanArchives(URL... urls)
	{
		for (URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String filename)
				{
					if (filename.endsWith("crux.xml"))
					{
						if (filename.startsWith("/")) filename = filename.substring(1);
						if (!ignoreScan(filename.replace('/', '.'))) return true;
						if (logger.isDebugEnabled())logger.debug("IGNORED: " + filename);
					}
					return false;
				}
			};

			try
			{

				StreamIterator it = IteratorFactory.create(url, filter);
				InputStream stream;
				while ((stream = it.next()) != null) ComponentConfig.parseCruxConfigFile(stream);
			}
			catch (IOException e)
			{
				throw new ComponentConfigScannerException(messages.componentConfigScannerInitializationError(e.getLocalizedMessage()), e);
			}
		}
	}

	private boolean ignoreScan(String intf)
	{
		for (String ignored : ignoredPackages)
		{
			if (intf.startsWith(ignored + "."))
			{
				return true;
			}
			else
			{
				if (logger.isDebugEnabled())logger.debug("NOT IGNORING: " + intf);
			}
		}
		return false;
	}

	public static ComponentConfigScanner getInstance()
	{
		return instance;
	}
	
}
