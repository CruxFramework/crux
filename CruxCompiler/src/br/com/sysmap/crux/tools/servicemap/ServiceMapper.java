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
package br.com.sysmap.crux.tools.servicemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.com.sysmap.crux.core.server.dispatch.Services;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.scannotation.ClasspathUrlFinder;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A tool for create a map of Crux remote service interfaces and 
 * implementation classes
 * @author Thiago da Rosa de Bustamante
 */
public class ServiceMapper
{
	private File projectDir;

	/**
	 * 
	 */
	public ServiceMapper()
	{
		ClassScanner.initialize(ClasspathUrlFinder.findClassPaths());
	}
	
	/**
	 * @return
	 */
	public File getProjectDir()
	{
		return projectDir;
	}

	/**
	 * @param projectDir
	 */
	public void setProjectDir(File projectDir)
	{
		this.projectDir = projectDir;
	}

	/**
	 * Generates Remote Service map
	 */
	public void generateServicesMap()
	{
		Set<String> searchClassesByInterface = ClassScanner.searchClassesByInterface(RemoteService.class);
		Properties cruxRemote = new Properties();
		
		try
		{
			for (String serviceClass : searchClassesByInterface)
			{
				Class<?> clazz = Class.forName(serviceClass);
				if (clazz.isInterface())
				{
					Class<?> service = Services.getService(serviceClass);
					if (service != null)
					{
						cruxRemote.put(serviceClass, service.getCanonicalName());
					}
				}
			}
			File metaInfFile =new File(projectDir, "META-INF");
			if (metaInfFile.exists())
			{
				if (!metaInfFile.isDirectory())
				{
					throw new ServiceMapperException("Can not create a META-INF directory on "+projectDir.getCanonicalPath());
				}
			}
			else 
			{
				metaInfFile.mkdirs();
			}
			cruxRemote.store(new FileOutputStream(new File(metaInfFile, "crux-remote")), "Crux RemoteServices implementations");
		}
		catch (IOException e)
		{
			throw new ServiceMapperException("Error creating remote service map", e);
		}
		catch (ClassNotFoundException e)
		{
			throw new ServiceMapperException("Error creating remote service map", e);
		}
	}
	
	/**
	 * Creates the console parameters processor for this program
	 * @return
	 */
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("serviceMapper");

		parameter = new ConsoleParameter("projectDir", "The crux project folder .", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}	
	
	/**
	 * @param parameters
	 */
	protected void processParameters(Collection<ConsoleParameter> parameters)
	{
		for (ConsoleParameter parameter : parameters)
        {
			if (parameter.getName().equals("projectDir"))
	        {
	        	setProjectDir(new File(parameter.getValue()));
	        }
        }
    }		
	
	/**
	 * Starts ServiceMapper program
	 * @param args
	 */
	public static void main(String[] args)
	{
		ServiceMapper serviceMapper = new ServiceMapper();
		ConsoleParametersProcessor parametersProcessor = serviceMapper.createParametersProcessor();
		Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

		if (parameters.containsKey("-help") || parameters.containsKey("-h"))
		{
			parametersProcessor.showsUsageScreen();
		}
		else
		{
			serviceMapper.processParameters(parameters.values());
			serviceMapper.generateServicesMap();
		}
	}
}
