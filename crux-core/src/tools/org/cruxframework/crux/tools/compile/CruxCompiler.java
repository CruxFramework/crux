/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.tools.compile;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxCompiler 
{
	private static final Log logger = LogFactory.getLog(CruxCompiler.class);
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			AbstractCruxCompiler compiler = CruxCompilerFactory.createCompiler();
			ConsoleParametersProcessor parametersProcessor = compiler.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				if(parameters.get("classpathDir") != null)
				{
					compiler.processClasspathParameter(parameters.get("classpathDir"));
				}
				
				if(parameters.get("resourcesDir") != null)
				{
					compiler.processResourcesParameter(parameters.get("resourcesDir"));
				}
				
				compiler.processSourceParameter(parameters.get("sourceDir"));
				compiler.processParameters(parameters.values());
				compiler.execute();
			}
			System.exit(0);
		}
		catch (ConsoleParametersProcessingException e)
		{
			logger.error("Error processing program parameters: "+e.getLocalizedMessage()+". Program aborted.");
		}
		catch (CompilerException e)
		{
			logger.error("Error compiling files: "+e.getLocalizedMessage()+". Program aborted.");
		}
		System.exit(1);
	}	
}
