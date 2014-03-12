/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.server.logging;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;

/**
 * @author Samuel Cardoso
 *
 */
@Aspect
public class LoggingErrorHandlerAspect 
{
	private static Logger logger = Logger.getLogger(LoggingErrorHandlerAspect.class.getName());
	
	@After("execution(void com.google.gwt.core.ext.TreeLogger.log(com.google.gwt.core.ext.TreeLogger.Type, java.lang.String, java.lang.Throwable, com.google.gwt.core.ext.TreeLogger.HelpInfo))")
	public void saveLogFilesystem(JoinPoint jp) throws Throwable 
	{
		if(jp != null && jp.getArgs() != null && jp.getArgs().length > 0)
		{
			TreeLogger.Type type = (Type) jp.getArgs()[0];
			
			if(type != null && TreeLogger.Type.ERROR.equals(type))
			{
				Throwable exception = (Throwable) jp.getArgs()[2];
				
				if(exception == null)
				{
					return;
				}
				
				logger.info("weaving GWT log: processing method: " + jp.getSignature());
				LoggingErrorDAO.append(exception);
			}
		}
	}
}