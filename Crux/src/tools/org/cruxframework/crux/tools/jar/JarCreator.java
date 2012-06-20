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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.utils.FilePatternHandler;
import org.cruxframework.crux.core.utils.FileUtils;


/**
 * A Helper class to create Jar files.
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JarCreator extends FilePatternHandler
{
	public static final String MANIFEST_BUILD_TIMESTAMP_PROPERTY = "Build-Timestamp";
	private static DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private Map<String, String> metaInfAttributes;
	private final File[] inputDirectory;
	private final File outputFile;
	private final boolean unpackaged;
	
	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @throws IOException
	 */
	public JarCreator(File[] inputDirectory, File outputFile) throws IOException
    {
		this(inputDirectory, outputFile, new HashMap<String, String>());
    }
	
	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @param metaInfAttributes
	 * @throws IOException
	 */
	public JarCreator(File[] inputDirectory, File outputFile, Map<String, String> metaInfAttributes) throws IOException
	{
		this(inputDirectory, outputFile, null, null, metaInfAttributes, false);
	}

	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @param includes
	 * @param excludes
	 * @param metaInfAttributes
	 * @throws IOException
	 */
	public JarCreator(File[] inputDirectory, File outputFile, String includes, String excludes, Map<String, String> metaInfAttributes, boolean unpackaged) throws IOException
	{
		super(includes, excludes);
		this.inputDirectory = inputDirectory;
		this.outputFile = outputFile;
		this.metaInfAttributes = metaInfAttributes;
		this.unpackaged = unpackaged;
	}

	/**
	 * @throws IOException
	 */
	public void createJar() throws IOException
	{
		Manifest manifest = new Manifest();
		
		setDefaultAttributes(metaInfAttributes);

		for (String attrName : metaInfAttributes.keySet())
        {
			manifest.getMainAttributes().putValue(attrName, metaInfAttributes.get(attrName));
        }
		
		removeOldJar();		
		
		if (unpackaged)
		{
			for (File inputDir : inputDirectory)
			{
				FileUtils.copyFilesFromDir(inputDir, outputFile, getIncludes(), getExcludes());
			}
			File metaInfDir = new File(outputFile, "META-INF");
			metaInfDir.mkdirs();
			manifest.write(new FileOutputStream(new File(metaInfDir, "MANIFEST.MF")));
		}
		else
		{
			File parentFile = outputFile.getParentFile();
			if (parentFile != null && !parentFile.exists())
			{
				parentFile.mkdirs();
			}

			JarOutputStream target = new JarOutputStream(new FileOutputStream(outputFile), manifest);

			if (inputDirectory != null)
			{
				Set<String> added = new HashSet<String>();
				for (File inputDir : inputDirectory)
				{
					addFile(inputDir, target, inputDir.getCanonicalPath().length(), added);
				}
			}
			target.close();
		}
	}

	/**
	 * Removes the old generated jar if it exists.
	 * @throws IOException
	 */
	private void removeOldJar() throws IOException 
	{
		if(outputFile.exists())
		{
			if(outputFile.isDirectory())
			{
				if(!FileUtils.recursiveDelete(outputFile))
				{
					throw new IOException("Could not delete file [" + outputFile.getAbsolutePath() + "]");
				}
			}
			else
			{
				if(!outputFile.delete())
				{
					throw new IOException("Could not delete file [" + outputFile.getAbsolutePath() + "]");
				}
			}
		}
	}

	/**
	 * @param source
	 * @param target
	 * @param inputDirNameLength 
	 * @param added
	 * @throws IOException
	 */
	private void addFile(File source, JarOutputStream target, int inputDirNameLength, Set<String> added) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			String entryName = getEntryName(source, inputDirNameLength);
			if (source.isDirectory())
			{
				String name = entryName;
				if (!StringUtils.isEmpty(name) && !added.contains(name))
				{
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
					added.add(name);
				}
				for (File nestedFile : source.listFiles())
				{
					addFile(nestedFile, target, inputDirNameLength, added);
				}
				return;
			}
			if (isValidEntry(entryName) && !added.contains(entryName))
			{
				JarEntry entry = new JarEntry(entryName);
				entry.setTime(source.lastModified());
				target.putNextEntry(entry);
				in = new BufferedInputStream(new FileInputStream(source));

				byte[] buffer = new byte[1024];
				while (true)
				{
					int count = in.read(buffer);
					if (count == -1)
					{
						break;
					}
					target.write(buffer, 0, count);
				}
				target.closeEntry();
				added.add(entryName);

			}
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}
	}

	/**
	 * @param source
	 * @param inputDirNameLength
	 * @return
	 * @throws IOException
	 */
	private String getEntryName(File source, int inputDirNameLength) throws IOException
	{
		String name = source.getCanonicalPath().substring(inputDirNameLength).replace("\\", "/");
		if (name.startsWith("/"))
		{
			name = name.substring(1);
		}
		if (source.isDirectory() && !name.endsWith("/"))
		{
			name += "/";
		}
			
		return name;
	}

	/**
	 * Sets the default attributes to manifest. If one of those attributes already exists on
	 * metaInfAttributes map, it will be replaced. 
	 * @param metaInfAttributes
	 */
	private void setDefaultAttributes(Map<String, String> metaInfAttributes)
    {
	    metaInfAttributes.put(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		metaInfAttributes.put(MANIFEST_BUILD_TIMESTAMP_PROPERTY, dateFormatter.format(new Date()));
    }
}