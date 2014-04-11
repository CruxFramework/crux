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
package org.cruxframework.crux.tools.servicemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.cruxframework.crux.core.rebind.DevelopmentScanners;
import org.cruxframework.crux.core.server.dispatch.Services;
import org.cruxframework.crux.core.server.rest.annotation.RestService;
import org.cruxframework.crux.scanner.ClassScanner;
import org.cruxframework.crux.scanner.ClasspathUrlFinder;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.tools.compile.CruxRegisterUtil;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;

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
		Scanners.setSearchURLs(ClasspathUrlFinder.findClassPaths());
        DevelopmentScanners.initializeScanners();
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
	 * Generates Remote Service map
	 */
	public void generateRestServicesMap()
	{
		try
		{
			Set<String> restServices =  ClassScanner.searchClassesByAnnotation(RestService.class);
			Properties cruxRest = new Properties();
			if (restServices != null)
			{
				for (String service : restServices) 
				{
					try 
					{
						Class<?> serviceClass = Class.forName(service);
						RestService annot = serviceClass.getAnnotation(RestService.class);
						if (cruxRest.containsKey(annot.value()))
						{
							throw new ServiceMapperException("Duplicated rest service [{"+annot.value()+"}]. Overiding previous registration...");
						}
						cruxRest.put(annot.value(), service);
					}
					catch (ClassNotFoundException e) 
					{
						throw new ServiceMapperException("Error initializing rest service class.",e);
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
			cruxRest.store(new FileOutputStream(new File(metaInfFile, "crux-rest")), "Crux RestServices implementations");
		}
		catch (IOException e)
		{
			throw new ServiceMapperException("Error creating rest service map", e);
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
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException
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
			
			adaptArgumentToWebdir(args);
			CruxRegisterUtil.registerFilesCruxBridge(args);
			
			serviceMapper.generateServicesMap();
			serviceMapper.generateRestServicesMap();
		}
	}

	private static void adaptArgumentToWebdir(String[] args) {
		if(args != null)
		{
			for (int i=0; i< (args.length-1); i++)
			{
				String arg = args[i];
				if ("projectDir".equals(arg))
				{
					//change to -war
					args[i] = "-webDir";
					break;
				}
			}
		}
	}
}
