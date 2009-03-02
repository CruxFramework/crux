package br.com.sysmap.crux.core.server.screen;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.config.Crux;

public class ScreenStateManagerInitializer 
{
	private static final Log logger = LogFactory.getLog(ScreenStateManagerInitializer.class);
	private static ScreenStateManager screenStateManager;
	private static final Lock lock = new ReentrantLock();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public static ScreenStateManager getScreenStateManager() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (screenStateManager != null) return screenStateManager;
		
		try
		{
			lock.lock();
			if (screenStateManager != null) return screenStateManager;
			screenStateManager = (ScreenStateManager) Class.forName(ConfigurationFactory.getConfiguration().screenStateManager()).newInstance(); 
		}
		catch (Throwable e)
		{
			if (logger.isInfoEnabled()) logger.info(messages.screenStateManagerInitializerUsingDefaultFactory());
			screenStateManager = (ScreenStateManager) Class.forName(Crux.DEFAULT_SCREEN_STATE_MANAGER).newInstance(); 
		}
		finally
		{
			lock.unlock();
		}
		return screenStateManager;
	}

}
