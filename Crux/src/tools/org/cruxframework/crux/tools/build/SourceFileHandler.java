/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.tools.build;

import java.io.File;
import java.io.IOException;

import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SourceFileHandler
{
	public static String mergeModuleFile(File originalFile, File legacyFile) throws IOException
	{
		String originalContent = FileUtils.read(originalFile);
		String legacyContent = FileUtils.read(legacyFile).replace("<module>", "").replace("</module>", "");
		
		originalContent = originalContent.replace("</module>", "\n"+ legacyContent + "</module>");
		return originalContent;
	}

	public static String mergeCssFile(File originalFile, File legacyFile) throws IOException
    {
		String originalContent = (originalFile!=null?FileUtils.read(originalFile):"");
		String legacyContent = (legacyFile!=null?FileUtils.read(legacyFile):"");
		
		originalContent = originalContent + "\n"+ legacyContent;
		return originalContent;
    }
}
