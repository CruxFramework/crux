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
package br.com.sysmap.crux.tools.compile;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.tools.CompilerMessages;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessingException;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxCompiler 
{
	private static final Log logger = LogFactory.getLog(CruxCompiler.class);
	private static CompilerMessages messages = MessagesFactory.getMessages(CompilerMessages.class);
	
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
				compiler.processSourceParameter(parameters.get("sourceDir"));
				compiler.processParameters(parameters.values());
				compiler.execute();
			}
			System.exit(0);
		}
		catch (ConsoleParametersProcessingException e)
		{
			logger.error(messages.cruxCompilerErrorProcessingParameters(e.getLocalizedMessage()));
		}
		catch (CompilerException e)
		{
			logger.error(messages.cruxCompilerErrorCompilingFiles(e.getLocalizedMessage()));
		}
		System.exit(1);
	}	
}
