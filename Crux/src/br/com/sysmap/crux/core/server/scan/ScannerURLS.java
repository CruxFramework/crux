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

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scannotation.ClasspathUrlFinder;
import org.scannotation.WarUrlFinder;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public abstract class ScannerURLS 
{
	static URL[] urls;
	private static final Lock lock = new ReentrantLock();

	private static final Log logger = LogFactory.getLog(ScannerURLS.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	public static URL[] getURLsForSearch(ServletContext context)
	{
		if (urls != null) return urls;
		lock.lock();
		if (urls != null) return urls;
		try
		{
			if (context == null)
			{
				urls = ClasspathUrlFinder.findClassPaths(); 
			}
			else
			{
				try
				{
					urls = WarUrlFinder.findWebInfLibClasspaths(context);
				}
				catch (Throwable e) 
				{
					logger.error(messages.scannerURLSErrorSearchingLibDir(e.getLocalizedMessage()), e);
				}
				
				urls = urls != null ? urls : new URL[0];
				URL[] tempUrls = new URL[urls.length + 1];
				System.arraycopy(urls, 0, tempUrls, tempUrls.length - 2, tempUrls.length + 1);
				
				urls = tempUrls;		
				
				try
				{
					urls[urls.length -1] = WarUrlFinder.findWebInfClassesPath(context);
				}
				catch (Throwable e) 
				{
					logger.error(messages.scannerURLSErrorSearchingClassesDir(e.getLocalizedMessage()), e);
				}
			}
		}
		finally
		{
			lock.unlock();
		}
		
		return urls;
	}
}
