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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.tools.compile.preprocessor.DeclarativeUIPreProcessor;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MonolithicApplicationCompiler extends AbstractCruxCompiler
{
	private String keepPagesUnder;
	private List<File> cruxPagesDir;

	/**
	 * @see org.cruxframework.crux.tools.compile.AbstractCruxCompiler#initializeProcessors()
	 */
	protected void initializeProcessors()
    {
		DeclarativeUIPreProcessor preProcessor = new DeclarativeUIPreProcessor();
		preProcessor.setOutputDir(pagesOutputDir);
		preProcessor.setIndent(indentPages);
		preProcessor.setKeepGeneratedFiles(keepPagesGeneratedFiles);
		if (!StringUtils.isEmpty(outputCharset))
		{
			preProcessor.setOutputCharset(outputCharset);
		}
		if (!StringUtils.isEmpty(pageFileExtension))
		{
			preProcessor.setPageFileExtension(pageFileExtension);
		}
		if (!StringUtils.isEmpty(keepPagesUnder))
		{
			preProcessor.setKeepDirStructureUnder(keepPagesUnder);
		}
		addPreProcessor(preProcessor);
    }

	@Override
	protected void processParameters(Collection<ConsoleParameter> parameters)
	{
	    super.processParameters(parameters);
	    
	    for (ConsoleParameter parameter : parameters)
        {
	        if (parameter.getName().equals("keepPagesUnder"))
	        {
	        	this.keepPagesUnder = parameter.getValue();
	        }
	        else if (parameter.getName().equals("cruxPagesDir"))
	        {
	        	this.cruxPagesDir = new ArrayList<File>(); 
	        	String[] pageDirs = RegexpPatterns.REGEXP_COMMA.split(parameter.getValue());
	        	for (String dir : pageDirs)
                {
	        		this.cruxPagesDir.add(new File(dir.trim()));
                }
	        }
        }
	}
	
	/**
	 * @see org.cruxframework.crux.tools.compile.AbstractCruxCompiler#initializeCompilerDir()
	 */
	@Override
	protected void initializeCompilerDir() throws IOException, MalformedURLException
	{
	    super.initializeCompilerDir();
	    ClassPathResolverInitializer.getClassPathResolver().setWebBaseDirs(new URL[]{webDir.toURI().toURL()});
	}
	
	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	@Override
	protected List<URL> getURLs() throws Exception
	{
		List<URL> result = new ArrayList<URL>();
		for (File dir : cruxPagesDir)
        {
	        result.addAll(MonolithicAppCompileUtils.getURLs(dir));
        }
		
		if (sourceDir != null)
		{
			result.addAll(MonolithicAppCompileUtils.getURLs(sourceDir));
		}
		if (resourcesDir != null)
		{
			result.addAll(MonolithicAppCompileUtils.getURLs(resourcesDir));
		}
		
		return result;
	}

	@Override
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = super.createParametersProcessor();
		MonolithicAppCompileUtils.addParametersToProcessor(parametersProcessor);		
		return parametersProcessor;	
	}

	/**
	 * @return the keepPagesUnder
	 */
	public String getKeepPagesUnder()
	{
		return keepPagesUnder;
	}

	/**
	 * @param keepPagesUnder the keepPagesUnder to set
	 */
	public void setKeepPagesUnder(String keepPagesUnder)
	{
		this.keepPagesUnder = keepPagesUnder;
	}
}