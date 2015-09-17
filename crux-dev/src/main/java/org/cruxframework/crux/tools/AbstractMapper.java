/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.cruxframework.crux.scanner.ClasspathUrlFinder;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;
import org.cruxframework.crux.tools.servicemap.ServiceMapperException;

/**
 * @author thiago
 *
 */
public class AbstractMapper
{
	private boolean initialized;
	private boolean override;
	private File projectDir;

	/**
	 * @return
	 */
	public File getProjectDir()
	{
		return projectDir;
	}

	public boolean isOverride()
	{
		return override;
	}
	
	public void setOverride(boolean override)
    {
		this.override = override;
    }

	/**
	 * @param projectDir
	 */
	public void setProjectDir(File projectDir)
	{
		this.projectDir = projectDir;
	}
	
	/**
	 * Creates the console parameters processor for this program
	 * 
	 * @param programName Program name
	 * @return
	 */
	protected ConsoleParametersProcessor createParametersProcessor(String programName)
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor(programName);

		parameter = new ConsoleParameter("projectDir", "The crux project folder .", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-override", "Override any existing mapping.", false, true));

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}

	protected File getMetaInfFile() throws IOException
    {
	    File metaInfFile = new File(getProjectDir(), "META-INF");
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
	    return metaInfFile;
    }	
	
	protected void initializeScannerURLs()
    {
		if (!initialized)
		{
			Scanners.setSearchURLs(ClasspathUrlFinder.findClassPaths());
			initialized = true;
		}
    }	

	/**
	 * 
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
			if (parameter.getName().equals("-override"))
	        {
	        	setOverride(true);
	        }
        }
    }
}
