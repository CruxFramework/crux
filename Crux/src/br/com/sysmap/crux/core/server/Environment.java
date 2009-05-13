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
import java.net.URL;

import javax.servlet.ServletContext;

import com.google.gwt.dev.HostedMode;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class Environment
{
	/**
	 * Determine if we are running in GWT Hosted Mode
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isHostedMode()
	{
		Exception utilException = new Exception();
		try
		{
			StackTraceElement[] stackTrace = utilException.getStackTrace();
			StackTraceElement stackTraceElement = stackTrace[stackTrace.length -1];
			return (stackTraceElement.getClassName().equals(HostedMode.class.getName()) ||
					stackTraceElement.getClassName().equals(com.google.gwt.dev.GWTShell.class.getName()) );
		}
		catch (Throwable e) 
		{
			return false;
		}
	}
	
	public static File getWebBaseDir(ServletContext context) throws Exception
	{
		URL urlClassesDir = context.getResource("/");
		File result = new File(urlClassesDir.toURI());
		return result;
	}
}
