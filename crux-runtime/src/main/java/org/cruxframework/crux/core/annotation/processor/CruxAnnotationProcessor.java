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
package org.cruxframework.crux.core.annotation.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.lang.model.SourceVersion;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class CruxAnnotationProcessor extends AbstractProcessor
{
	/**
	 * Run the Crux Annotation Processor only if this parameter is at the JVM.
	 * This avoids any unnecessary call when JVM is compiling.
	 */
	public static final String CRUX_RUN_APT = "Crux.apt.run";
	public static final String CRUX_APT_INCREMENTAL = "Crux.apt.incremental";

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

	protected FileObject createResourceFile(final String name) throws IOException
	{
		final Filer filer = processingEnv.getFiler();
		FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", name);
		return file;
	}
	
	protected FileObject getResourceFile(final String name) throws IOException 
	{
		final Filer filer = processingEnv.getFiler();
		FileObject file;
        try
        {
	        file = filer.getResource(StandardLocation.CLASS_OUTPUT, "", name);
        }
        catch (FileNotFoundException e)
        {
	        return null;
        }
		return file;
	}
	
	protected InputStream getResourceFileStream(final String name) throws IOException
	{
        try
        {
        	FileObject previousRestFile = getResourceFile(name);
        	return previousRestFile.openInputStream();
        }
        catch (FileNotFoundException e)
        {
	        return null;
        }
	}
	
	protected boolean isIncremental()
    {
		String incremental = processingEnv.getOptions().get(CRUX_APT_INCREMENTAL);
		return Boolean.parseBoolean(incremental);
    }
	
	protected boolean runAPT()
    {
		String cruxRunAPT = processingEnv.getOptions().get(CRUX_RUN_APT);
		return Boolean.parseBoolean(cruxRunAPT);
    }
}
