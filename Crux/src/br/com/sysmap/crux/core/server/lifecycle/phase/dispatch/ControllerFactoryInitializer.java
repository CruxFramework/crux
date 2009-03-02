package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.config.Crux;

public class ControllerFactoryInitializer 
{
	private static final Log logger = LogFactory.getLog(ControllerFactoryInitializer.class);
	private static ControllerFactory controllerFactory;
	private static final Lock lock = new ReentrantLock();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public static ControllerFactory getControllerFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (controllerFactory != null) return controllerFactory;
		
		try
		{
			lock.lock();
			if (controllerFactory != null) return controllerFactory;
			controllerFactory = (ControllerFactory) Class.forName(ConfigurationFactory.getConfiguration().controllerFactory()).newInstance(); 
		}
		catch (Throwable e)
		{
			if (logger.isInfoEnabled()) logger.info(messages.controllerFactoryInitializerUsingDefaultFactory());
			controllerFactory = (ControllerFactory) Class.forName(Crux.DEFAULT_CONTROLLER_FACTORY).newInstance(); 
		}
		finally
		{
			lock.unlock();
		}
		return controllerFactory;
	}

}
