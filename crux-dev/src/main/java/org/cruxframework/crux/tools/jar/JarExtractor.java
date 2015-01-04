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
package org.cruxframework.crux.tools.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.FilePatternHandler;
import org.cruxframework.crux.core.utils.FileUtils;


/**
 * A Helper class to read Jar files.
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JarExtractor extends FilePatternHandler
{
	private final File[] inputFile;
	private final File outputDirectory;
	private final Map<String, String> entryNameReplacements;
	private boolean deleteOutputDirBeforeExtract = false;
	
	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @throws IOException
	 */
	public JarExtractor(File[] inputFile, File outputDirectory) throws IOException
    {
		this(inputFile, outputDirectory, null, null, null, false);
    }

	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @param includes
	 * @param excludes
	 * @param metaInfAttributes
	 * @throws IOException
	 */
	public JarExtractor(File[] inputFile, File outputDirectory, String includes, String excludes, 
						Map<String, String> entryNameReplacements, boolean deleteOutputDirBeforeExtract) throws IOException
	{
		super(includes, excludes);
		this.inputFile = inputFile;
		this.outputDirectory = outputDirectory;
		this.entryNameReplacements = entryNameReplacements;
		this.deleteOutputDirBeforeExtract = deleteOutputDirBeforeExtract;
	}

	/**
	 * @throws IOException
	 */
	public void extractJar() throws IOException
	{
		if (deleteOutputDirBeforeExtract && outputDirectory.exists())
		{
			FileUtils.recursiveDelete(outputDirectory);
		}
		
		if (inputFile != null)
		{
			Set<String> added = new HashSet<String>();
			for (File input : inputFile)
			{
				extractFile(input, added);
			}
		}
	}

	/**
	 * @param source
	 * @param target
	 * @param inputDirNameLength 
	 * @throws IOException
	 */
	private void extractFile(File source, Set<String> added) throws IOException
	{
		JarInputStream inStream = null;
		try
		{
			inStream = new JarInputStream(new FileInputStream(source));

			JarEntry entry;
			byte[] buffer = new byte[1024];

			while ((entry = inStream.getNextJarEntry()) != null) 
			{
				String name = entry.getName();
				if (!StringUtils.isEmpty(name) && !added.contains(name))
				{
					if (isValidEntry(name))
					{
						File outputFile = new File(outputDirectory, getOutputFileName(name));

						if (entry.isDirectory())
						{
							outputFile.mkdirs();
						}
						else
						{
							extractFile(inStream, buffer, outputFile);
						}
					}
					added.add(name);	        		 
				}
			}

			inStream.close();
		}
		finally
		{
			if (inStream != null)
			{
				inStream.close();
			}
		}
	}

	/**
	 * @param entryName
	 * @return
	 */
	private String getOutputFileName(String entryName)
    {
	    if (this.entryNameReplacements != null)
	    {
	    	for (String replacement : entryNameReplacements.keySet())
            {
	            if (entryName.indexOf(replacement) >= 0)
	            {
	            	return entryName.replace(replacement, entryNameReplacements.get(replacement));
	            }
            }
	    }

	    return entryName;
    }

	/**
	 * @param inStream
	 * @param buffer
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void extractFile(JarInputStream inStream, byte[] buffer, File outputFile) throws FileNotFoundException, IOException
    {
	    int nrBytesRead;
	    OutputStream outStream = new FileOutputStream(outputFile);
	    try
	    {
	    	while ((nrBytesRead = inStream.read(buffer)) > 0) 
	    	{
	    		outStream.write(buffer, 0, nrBytesRead);
	    	}
	    }
	    finally
	    {
	    	if (outStream != null)
	    	{
	    		outStream.close();
	    	}
	    }
    }
}