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
package br.com.sysmap.crux.module.validation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.module.CruxModuleMessages;
import br.com.sysmap.crux.module.config.CruxModuleConfigurationFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxModuleVersionCheckerInitializer
{
	private static final Log logger = LogFactory.getLog(CruxModuleVersionCheckerInitializer.class);
	private static CruxModuleVersionChecker versionChecker;
	private static final Lock lock = new ReentrantLock();
	private static CruxModuleMessages messages = (CruxModuleMessages)MessagesFactory.getMessages(CruxModuleMessages.class);

	public static CruxModuleVersionChecker getVersionChecker()
	{
		if (versionChecker != null) return versionChecker;
		
		try
		{
			lock.lock();
			if (versionChecker != null) return versionChecker;
			versionChecker = (CruxModuleVersionChecker) Class.forName(CruxModuleConfigurationFactory.getConfigurations().cruxModuleVersionChecker()).newInstance(); 
		}
		catch (Throwable e)
		{
			logger.error(messages.versionChekerInitializerError(e.getMessage()), e);
		}
		finally
		{
			lock.unlock();
		}
		return versionChecker;
	}

	public static void registerVersionChecker(CruxModuleVersionChecker versionChecker)
	{
		CruxModuleVersionCheckerInitializer.versionChecker = versionChecker;
	}
}
