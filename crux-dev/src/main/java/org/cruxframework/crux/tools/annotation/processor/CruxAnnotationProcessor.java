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
package org.cruxframework.crux.tools.annotation.processor;

import java.io.IOException;

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
	protected FileObject getResourceFile(final String name) throws IOException
	{
		final Filer filer = processingEnv.getFiler();
		FileObject file;
		try
		{
			file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", name);
		}
		catch (IOException e)
		{
			file = filer.getResource(StandardLocation.CLASS_OUTPUT, "", name);
		}
		return file;
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
}
