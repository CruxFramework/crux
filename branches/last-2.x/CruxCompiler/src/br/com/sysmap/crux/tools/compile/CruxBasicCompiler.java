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

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.tools.compile.preprocessor.DeclarativeUIPreProcessor;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxBasicCompiler extends AbstractCruxCompiler
{
	private String keepPagesUnder;
	private List<File> cruxPagesDir;

	/**
	 * @see br.com.sysmap.crux.tools.compile.AbstractCruxCompiler#initializeProcessors()
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
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	@Override
	protected List<URL> getURLs() throws Exception
	{
		List<URL> result = new ArrayList<URL>();
		for (File dir : cruxPagesDir)
        {
	        result.addAll(getURLs(dir));
        }
		
		return result;
	}

	/**
	 * Gets the list of URLs that will be compiled
	 * @return
	 */
	protected List<URL> getURLs(File pagesDir) throws Exception
	{
		List<URL> files = new LinkedList<URL>();
		File[] listFiles = pagesDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return pathname.isDirectory() || pathname.getName().endsWith(".crux.xml");
			}
		});
		
		if (listFiles != null)
		{
			for (File file : listFiles)
			{
				if (file.isDirectory())
				{
					files.addAll(getURLs(file));
				}
				else
				{
					files.add(file.toURI().toURL());
				}
			}
		}

		return files;
	}
	
	@Override
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = super.createParametersProcessor();
		ConsoleParameter parameter = new ConsoleParameter("keepPagesUnder", "A Directory that will be used as parent of generated files.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("cruxPagesDir", "The source folder(s), where crux.xml files will be searched.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirNames", "Folder name(s). Separeted by commas"));
		parametersProcessor.addSupportedParameter(parameter);
		
		return parametersProcessor;	
	}	
}
