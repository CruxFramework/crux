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
package br.com.sysmap.crux.tools.export;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import br.com.sysmap.crux.core.client.utils.StringUtils;

/**
 * A Helper class to create Jar files.
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JarCreator
{
	private static DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private static final String PATTERN_SPECIAL_CHARACTERS = "\\{}[]()+?$&^-|.!";
	private List<Pattern> excludesPatterns = new ArrayList<Pattern>();
	private List<Pattern> includesPatterns = new ArrayList<Pattern>();
	private final File inputDirectory;
	private final int inputDirectoryNameLength;
	private final File outputFile;
	
	public JarCreator(File inputDirectory, File outputFile) throws IOException
    {
		this.inputDirectory = inputDirectory;
		this.inputDirectoryNameLength = inputDirectory.getCanonicalPath().length();
		this.outputFile = outputFile;
    }

	public JarCreator(File inputDirectory, File outputFile, String includes, String excludes) throws IOException
    {
		this(inputDirectory, outputFile);
		processIncludesPatterns(includes);
		processExcludesPatterns(includes);
		
    }

	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @throws IOException
	 */
	public void createJar() throws IOException
	{
		createJar(new HashMap<String, String>());
	}
	
	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @param metaInfAttributes
	 * @throws IOException
	 */
	public void createJar(Map<String, String> metaInfAttributes) throws IOException
	{
		Manifest manifest = new Manifest();
		
		setDefaultAttributes(metaInfAttributes);

		for (String attrName : metaInfAttributes.keySet())
        {
			manifest.getMainAttributes().put(attrName, metaInfAttributes.get(attrName));
        }
		
		JarOutputStream target = new JarOutputStream(new FileOutputStream(outputFile), manifest);
		addFile(inputDirectory, target);
		target.close();
	}

	/**
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	private void addFile(File source, JarOutputStream target) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			String entryName = getEntryName(source);
			if (source.isDirectory())
			{
				String name = entryName;
				if (!name.isEmpty())
				{
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for (File nestedFile : source.listFiles())
				{
					addFile(nestedFile, target);
				}
				return;
			}
			if (isValidEntry(entryName))
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
	 * @return
	 * @throws IOException
	 */
	private String getEntryName(File source) throws IOException
	{
		String name = source.getCanonicalPath().substring(inputDirectoryNameLength).replace("\\", "/");
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
	 * @param entryName
	 * @return
	 */
	private boolean isValidEntry(String entryName)
    {
		boolean isValid = includesPatterns.size() == 0;
	    for (Pattern pattern : includesPatterns)
        {
	        if (pattern.matcher(entryName).matches())
	        {
	        	isValid = true;
	        	break;
	        }
        }
		
	    for (Pattern pattern : excludesPatterns)
        {
	        if (pattern.matcher(entryName).matches())
	        {
	        	isValid = false;
	        	break;
	        }
        }
	    return isValid;
    }

	/**
	 * @param patternChar
	 * @return
	 */
	private boolean needsPatternScape(char patternChar)
    {
		return PATTERN_SPECIAL_CHARACTERS.indexOf(patternChar) >= 0;
    }

	/**
	 * @param excludes
	 */
	private void processExcludesPatterns(String excludes)
    {
	    if (!StringUtils.isEmpty(excludes))
		{
			String[] excludesPatterns = excludes.split(",");
			for (String excludePattern : excludesPatterns)
            {
	            this.excludesPatterns.add(Pattern.compile(translatePattern(excludePattern), Pattern.CASE_INSENSITIVE));
            }
		}
    }

	/**
	 * @param includes
	 */
	private void processIncludesPatterns(String includes)
    {
	    if (!StringUtils.isEmpty(includes))
		{
			String[] includesPatterns = includes.split(",");
			for (String includePattern : includesPatterns)
            {
	            this.includesPatterns.add(Pattern.compile(translatePattern(includePattern), Pattern.CASE_INSENSITIVE));
            }
		}
    }
	
	/**
	 * Sets the default attributes to manifest. If one of those attributes already exists on
	 * metaInfAttributes map, it will be replaced. 
	 * @param metaInfAttributes
	 */
	private void setDefaultAttributes(Map<String, String> metaInfAttributes)
    {
	    metaInfAttributes.put(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		metaInfAttributes.put("Build-Timestamp", dateFormatter.format(new Date()));
    }

	/**
	 * @param includePattern
	 * @return
	 */
	private String translatePattern(String includePattern)
    {
		includePattern = includePattern.replace("\\", "/");
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i < includePattern.length(); i++)
        {
	        if (includePattern.charAt(i) == '*')
	        {
	        	if((i<includePattern.length()-2) && (includePattern.substring(i, i+2).equals("**/")))
	        	{
	        		str.append(".+");
	        	}
	        	else
	        	{
	        		str.append("[^/]+");
	        	}
	        }
	        else
	        {
	        	if (needsPatternScape(includePattern.charAt(i)))
	        	{
	        		str.append("\\");
	        	}
	        	str.append(includePattern.charAt(i));
	        }
        }
		
	    return str.toString();
    }
}
