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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.tools.compile.preprocessor.DeclarativeUIPreProcessor;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ApplicationCompiler extends AbstractCruxCompiler
{
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
		addPreProcessor(preProcessor);
    }
	
	/**
	 * @see org.cruxframework.crux.tools.compile.AbstractCruxCompiler#initializeCompilerDir()
	 */
	@Override
	protected void initializeCompilerDir() throws IOException, MalformedURLException
	{
	    super.initializeCompilerDir();
	    ClassPathResolverInitializer.getClassPathResolver().setWebBaseDir(webDir.toURI().toURL());
	}
	
	@Override
	protected List<Module> getModules() throws Exception
	{
		Set<String> modulesToCompile = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllAppModules();
		List<Module> modules = new ArrayList<Module>();
		for (String moduleId : modulesToCompile)
        {
			modules.add(Modules.getInstance().getModule(moduleId));
        }
		
		return modules;
	}
}