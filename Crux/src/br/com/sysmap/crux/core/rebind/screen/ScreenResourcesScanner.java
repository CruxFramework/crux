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
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.IteratorFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class ScreenResourcesScanner 
{
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	private static Map<String, Set<String>> pagesPerModule = null;
	private static Lock lock = new ReentrantLock();
	
	public Set<String> scanArchives()
	{
		URL url = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDir();
		final Set<String> screens = new HashSet<String>();
		Filter filter = new Filter()
		{
			public boolean accepts(String filename)
			{
				if (filename.endsWith(getExtension()))
				{
					if (filename.startsWith("/"))
					{
						filename = filename.substring(1);
					}
					screens.add(filename);
					return true;
				}
				return false;
			}
		};

		try
		{
			IteratorFactory.create(url, filter);
		}
		catch (IOException e)
		{
			throw new ScreenResourcesScannerException(messages.screenResourceScannerInitializationError(e.getLocalizedMessage()), e);
		}
		return screens;
	}
	
	public Set<String> getPages(String module) throws ScreenConfigException
	{
		if (pagesPerModule == null)
		{
			lock.lock();
			try
			{
				if (pagesPerModule == null)
				{
					pagesPerModule = new HashMap<String, Set<String>>();
					
					Set<String> archives = scanArchives();
					for (String screenID : archives)
					{
						Screen screen = ScreenFactory.getInstance().getScreen(screenID);
						Set<String> pages = pagesPerModule.get(screen.getModule());
						if (pages == null)
						{
							pages = new HashSet<String>();
							pagesPerModule.put(module, pages);
						}
						pages.add(screenID);
					}
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
		return pagesPerModule.get(module);
	}
	

	protected abstract String getExtension();
}
