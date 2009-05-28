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
package br.com.sysmap.crux.core.server;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;

import com.google.gwt.dev.HostedMode;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class Environment
{
	private static Boolean isProduction = null;
	private static final Lock lock = new ReentrantLock();

	/**
	 * Determine if we are running in GWT Hosted Mode 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isProduction()
	{
		if (isProduction == null)
		{
			lock.lock();
			try
			{
				if (isProduction == null)
				{
					Exception utilException = new Exception();
					try
					{
						StackTraceElement[] stackTrace = utilException.getStackTrace();
						StackTraceElement stackTraceElement = stackTrace[stackTrace.length -1];
						isProduction = (!stackTraceElement.getClassName().equals(HostedMode.class.getName()) &&
								!stackTraceElement.getClassName().equals(com.google.gwt.dev.GWTShell.class.getName()) );

						if (isProduction)
						{
							List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
							for (String arg : inputArgs)
							{
								if (arg.contains("-Xdebug") || arg.contains("-agentlib:jdwp"))
								{
									isProduction = false;
									break;
								}
							}							
						}
					}
					catch (Throwable e) 
					{
						isProduction = true;
					}
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		return isProduction;
	}

	/**
	 * Gets the web base dir
	 * @return
	 * @throws Exception
	 */
	public static File getWebBaseDir() throws Exception
	{
		return new File(ClassPathResolverInitializer.getClassPathResolver().findWebBaseDir().toURI());
	}
}
