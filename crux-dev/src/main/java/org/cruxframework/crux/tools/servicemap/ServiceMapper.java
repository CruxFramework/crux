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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.dispatch.Services;
import org.cruxframework.crux.core.server.rest.annotation.RestService;
import org.cruxframework.crux.scanner.ClassScanner;
import org.cruxframework.crux.tools.AbstractMapper;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A tool for create a map of Crux remote service interfaces and 
 * implementation classes
 * @author Thiago da Rosa de Bustamante
 */
public class ServiceMapper extends AbstractMapper
{
	private static final Log logger = LogFactory.getLog(ServiceMapper.class);
		
	/**
	 * Generates Remote Service map
	 */
	public void generateRestServicesMap()
	{
		try
		{
			File metaInfFile = getMetaInfFile();
			File serviceMapFile = new File(metaInfFile, "crux-rest");
			if (serviceMapFile.exists() && !isOverride())
			{
				logger.info("REST Service map already exists. Skipping generation...");
				return;
			}
			initializeScannerURLs();
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

			cruxRest.store(new FileOutputStream(serviceMapFile), "Crux RestServices implementations");
		}
		catch (IOException e)
		{
			throw new ServiceMapperException("Error creating rest service map", e);
		}
	}

	/**
	 * Generates Remote Service map
	 */
	public void generateServicesMap()
	{
		try
		{
			File metaInfFile = getMetaInfFile();
			File serviceMapFile = new File(metaInfFile, "crux-remote");
			if (serviceMapFile.exists() && !isOverride())
			{
				logger.info("Service map already exists. Skipping generation...");
				return;
			}
			initializeScannerURLs();
			Set<String> searchClassesByInterface = ClassScanner.searchClassesByInterface(RemoteService.class);
			Properties cruxRemote = new Properties();
		
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
			if (metaInfFile.exists())
			{
				if (!metaInfFile.isDirectory())
				{
					throw new ServiceMapperException("Can not create a META-INF directory on "+getProjectDir().getCanonicalPath());
				}
			}
			else 
			{
				metaInfFile.mkdirs();
			}
			cruxRemote.store(new FileOutputStream(serviceMapFile), "Crux RemoteServices implementations");
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
	 * Starts ServiceMapper program
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException
	{
		ServiceMapper serviceMapper = new ServiceMapper();
		ConsoleParametersProcessor parametersProcessor = serviceMapper.createParametersProcessor("serviceMapper");
		Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);
		if (parameters.containsKey("-help") || parameters.containsKey("-h"))
		{
			parametersProcessor.showsUsageScreen();
		}
		else
		{
			serviceMapper.processParameters(parameters.values());
			
			serviceMapper.generateServicesMap();
			serviceMapper.generateRestServicesMap();
		}
	}
}
