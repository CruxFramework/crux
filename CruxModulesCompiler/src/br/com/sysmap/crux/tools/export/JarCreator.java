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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private Map<String, String> metaInfAttributes;
	private final File[] inputDirectory;
	private final File outputFile;
	
	public JarCreator(File[] inputDirectory, File outputFile) throws IOException
    {
		this(inputDirectory, outputFile, new HashMap<String, String>());
    }
	
	public JarCreator(File[] inputDirectory, File outputFile, String includes, String excludes, Map<String, String> metaInfAttributes) throws IOException
    {
		this(inputDirectory, outputFile, metaInfAttributes);
		processIncludesPatterns(includes);
		processExcludesPatterns(excludes);
		
    }

	public JarCreator(File[] inputDirectory, File outputFile, Map<String, String> metaInfAttributes) throws IOException
	{
		this.inputDirectory = inputDirectory;
		this.outputFile = outputFile;
		this.metaInfAttributes = metaInfAttributes;
	}

	/**
	 * @param inputDirectory
	 * @param outputFile
	 * @param metaInfAttributes
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

	/**
	 * @param source
	 * @param target
	 * @param inputDirNameLength 
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
				if (!name.isEmpty() && !added.contains(name))
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
	 * @param pattern
	 * @return
	 */
	private String translatePattern(String pattern)
    {
		pattern = pattern.replace("\\", "/");
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i < pattern.length(); i++)
        {
			int increment = isDirectoryNavigation(pattern, i);
			if (increment > 0)
	        {
				i += increment;
        		str.append(".*");
	        }
			else
			{
				if (pattern.charAt(i) == '*')
				{
	        		str.append("[^/]*");
				}
				else
				{
					if (needsPatternScape(pattern.charAt(i)))
					{
						str.append("\\");
					}
					str.append(pattern.charAt(i));
				}
			}
        }
		
	    return str.toString();
    }
	
	/**
	 * @param pattern
	 * @param i
	 * @return
	 */
	private int isDirectoryNavigation(String pattern, int i)
	{
		if (pattern.charAt(i) == '*' || pattern.charAt(i) == '/')
        {
			int length = pattern.length();
			int rest = length - i; 

			if (rest > 3)
			{
				String match = "/**/";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
			}
			if (rest > 2)
			{
				String match = "/**";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
				match = "**/";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
			}			
			if (rest > 1)
			{
				String match = "**";
				if (pattern.substring(i, i+match.length()).equals(match))
				{
					return match.length()-1;
				}
			}			
        }		
		return 0;
	}
}

