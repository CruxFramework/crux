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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import br.com.sysmap.crux.core.client.utils.StringUtils;

/**
 * Calls the java compiler to compile all java files found under the specified source folder.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Compiler
{
	private String classpath;
	private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private String outputDirectory;
	private String source;
	private String sourcepath;
	private String target;
	private File tempFile;
	
	/**
	 * @param sourceDir
	 * @return
	 * @throws IOException
	 */
	public boolean compile(File sourceDir) throws IOException
	{
		int status = compiler.run(null, null, null, getCompilerOptions(sourceDir));
		if (tempFile != null && tempFile.exists())
		{
			tempFile.delete();
		}
		return status == 0;
	}

	/**
	 * @return
	 */
	public String getClasspath()
    {
    	return classpath;
    }

	/**
	 * @return
	 */
	public String getOutputDirectory()
    {
    	return outputDirectory;
    }

	/**
	 * @return
	 */
	public String getSource()
    {
    	return source;
    }

	/**
	 * @return
	 */
	public String getSourcepath()
    {
    	return sourcepath;
    }

	/**
	 * @return
	 */
	public String getTarget()
    {
    	return target;
    }

	/**
	 * @param classpath
	 */
	public void setClasspath(String classpath)
    {
    	this.classpath = classpath;
    }

	/**
	 * @param outputDirectory
	 * @throws IOException 
	 */
	public void setOutputDirectory(File outputDirectory) throws IOException
    {
    	this.outputDirectory = outputDirectory!=null?outputDirectory.getCanonicalPath().replace('\\', '/'):null;;
    }

	/**
	 * @param source
	 */
	public void setSource(String source)
    {
    	this.source = source;
    }

	/**
	 * @param sourcepath
	 */
	public void setSourcepath(File sourcepath) throws IOException
    {
    	this.sourcepath = sourcepath!=null?sourcepath.getCanonicalPath().replace('\\', '/'):null;;
    }

	/**
	 * @param target
	 */
	public void setTarget(String target)
    {
    	this.target = target;
    }

	/**
	 * @param sourceDir
	 * @return
	 * @throws IOException
	 */
	private String createCompilerFileList(File sourceDir) throws IOException
    {
		tempFile = File.createTempFile("cruxCompiler", "files");
		PrintWriter out = new PrintWriter(tempFile);
		List<String> javaFiles = new ArrayList<String>();
		
		extractJavaFiles(sourceDir, javaFiles);
		for (String javaFile : javaFiles)
        {
			out.println(javaFile);
        }
		out.close();
		return "@"+tempFile.getCanonicalPath();
    }
	
	/**
	 * @param input
	 * @param javaFiles
	 * @throws IOException
	 */
	private void extractJavaFiles(File input, List<String> javaFiles) throws IOException
	{
		if (input.isDirectory())
		{
			File[] files = input.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					extractJavaFiles(file, javaFiles);
				}
			}
		}
		else if (input.getName().toLowerCase().endsWith(".java"))
		{
			javaFiles.add(input.getCanonicalPath().replace('\\', '/'));
		}
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	private String[] getCompilerOptions(File sourceDir) throws IOException
	{
		List<String> options = new ArrayList<String>();
		if (!StringUtils.isEmpty(source))
		{
			options.add("-source");
			options.add(source);
		}
		if (!StringUtils.isEmpty(target))
		{
			options.add("-target");
			options.add(target);
		}
		if (!StringUtils.isEmpty(classpath))
		{
			options.add("-classpath");
			options.add(classpath);
		}
		if (!StringUtils.isEmpty(sourcepath))
		{
			options.add("-sourcepath");
			options.add(sourcepath);
		}
		if (!StringUtils.isEmpty(outputDirectory))
		{
			options.add("-d");
			options.add(outputDirectory);
		}
		
		options.add(createCompilerFileList(sourceDir));
		return options.toArray(new String[options.size()]);
	}
}
