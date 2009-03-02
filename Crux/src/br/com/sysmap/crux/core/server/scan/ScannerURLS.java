package br.com.sysmap.crux.core.server.scan;

import java.net.URL;
import java.util.Arrays;
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
				urls = Arrays.copyOf((urls!=null?urls:new URL[0]), (urls!=null?urls.length:0)+1);
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
