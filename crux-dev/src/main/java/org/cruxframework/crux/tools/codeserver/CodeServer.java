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
package org.cruxframework.crux.tools.codeserver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;
import org.cruxframework.crux.tools.server.JettyDevServer;

import com.google.gwt.dev.codeserver.Options;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CodeServer 
{
	private static final Log logger = LogFactory.getLog(CodeServer.class);

	private String bindAddress = "localhost";
	private String launcherDir;
	private String moduleName;
	private boolean noPrecompile = false;
	private int port = getDefaultPort();
	private CodeServerRecompileListener recompileListener;
	private String sourceDir;
	private String webDir;
	private String workDir;


	public String getModuleName()
    {
    	return moduleName;
    }

	public String getSourceDir()
    {
    	return sourceDir;
    }

	public String getWorkDir()
    {
    	return workDir;
    }

	public void setModuleName(String moduleName)
    {
    	this.moduleName = moduleName;
    }

	public void setSourceDir(String sourceDir)
    {
    	this.sourceDir = sourceDir;
    }

	public void setWorkDir(String workDir)
    {
    	this.workDir = workDir;
    }
	
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("CodeServer");

		parameter = new ConsoleParameter("-moduleName", "The name of the module to be compiled.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("name", "Module name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-sourceDir", "The application source folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Source dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-bindAddress", "The ip address of the code server. Defaults to 127.0.0.1.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ip", "Ip address"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-noprecompile", "If informed, code server will not pre compile the source.", false, true));

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-startJetty", "If informed, starts the default application server (Jetty).", false, true));
		
		parameter = new ConsoleParameter("-locale", "The locale used by hotdeployment scanner to recompile the project.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("locale", "locale"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-port", "The port where the code server will run.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("port", "Port"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-workDir", "The root of the directory tree where the code server will write compiler output. If not supplied, a temporary directory will be used.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Work dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("-webDir", "The directory to be updated by code server compiler. If provided, after each code server compilation, this folder will be updated.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dir", "Web dir"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}
	
	protected int getDefaultPort()
	{
		return 9876;
	}

	protected String[] getServerParameters()
    {
		List<String> args = new ArrayList<String>();

		if (noPrecompile)
		{
			args.add("-noprecompile");
		}
		else
		{
			args.add("-precompile");
			args.add("-noincremental");
		}
		if (launcherDir != null && launcherDir.length() > 0)
		{
			args.add("-launcherDir");
			args.add(launcherDir);
		}
		if (bindAddress != null && bindAddress.length() > 0)
		{
			args.add("-bindAddress");
			args.add(bindAddress);
		}
		if (port  > 0)
		{
			args.add("-port");
			args.add(Integer.toString(port));
		}
		if (workDir != null && workDir.length() > 0)
		{
			args.add("-workDir");
			args.add(workDir);
		}
		if (sourceDir != null && sourceDir.length() > 0)
		{
			args.add("-src");
			args.add(sourceDir);
		}
		if (moduleName != null && moduleName.length() > 0)
		{
//			String moduleFullName = Modules.getInstance().getModule(moduleName).getFullName();
			args.add(moduleName);
		}
		
	    return args.toArray(new String[args.size()]);
    }

	protected void processParameters(Collection<ConsoleParameter> parameters)
    {
		for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("-moduleName"))
	        {
	        	moduleName = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-noprecompile"))
	        {
	        	this.noPrecompile = true;
	        }
	        else if (parameter.getName().equals("-sourceDir"))
	        {
	        	sourceDir = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-bindAddress"))
	        {
	        	try
	        	{
	        		InetAddress bindAddress = InetAddress.getByName(parameter.getValue());
	        		if (bindAddress.isAnyLocalAddress()) 
	        		{
	        			this.bindAddress = InetAddress.getLocalHost().getHostAddress();
	        		}
	        		else 
	        		{
	        			this.bindAddress = parameter.getValue();
	        		}
	        	}
	        	catch(Exception e)
	        	{
	        		//Use default
	        	}
	        }
	        else if (parameter.getName().equals("-port"))
	        {
	        	port =Integer.parseInt(parameter.getValue());
	        }
	        else if (parameter.getName().equals("-workDir"))
	        {
	        	workDir = parameter.getValue();
	        }
	        else if (parameter.getName().equals("-webDir"))
	        {
	        	webDir = parameter.getValue();
	        	launcherDir = parameter.getValue();
	        	recompileListener = new CodeServerRecompileListener(webDir);
	        }
        }
    }

	protected void runGWTCodeServer(String[] args) throws Exception 
	{
		Options options = new Options();
		if (!options.parseArgs(args)) 
		{
			System.exit(1);
		}
		options.setJobChangeListener(recompileListener);
//		options.addTags("-XnoenforceStrictResources");
		try 
		{
			com.google.gwt.dev.codeserver.CodeServer.main(options);
		} 
		catch (Throwable t) 
		{
			logger.error("Error running code server", t);
		}
	}	
	
	public static void main(String[] args)
    {
		try
		{
			CodeServer codeServer = new CodeServer();
			ConsoleParametersProcessor parametersProcessor = codeServer.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				codeServer.processParameters(parameters.values());
				codeServer.runGWTCodeServer(codeServer.getServerParameters());
				
				if(parameters.containsKey("-startJetty"))
				{
					JettyDevServer.main(args);
				}
			}
		}
		catch (ConsoleParametersProcessingException e)
		{
			logger.error("Error processing program parameters: "+e.getLocalizedMessage()+". Program aborted.",e);
		}
		catch (Exception e)
		{
			logger.error("Error running code server: "+e.getLocalizedMessage()+". Program aborted.",e);
		}
    }
}