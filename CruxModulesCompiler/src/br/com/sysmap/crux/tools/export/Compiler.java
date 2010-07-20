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
	
	/**
	 * @param sourceDir
	 * @return
	 * @throws IOException
	 */
	public boolean compile(File sourceDir) throws IOException
	{
		int status = compiler.run(null, null, null, getCompilerOptions() + createCompilerFileList(sourceDir));
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
	 */
	public void setOutputDirectory(String outputDirectory)
    {
    	this.outputDirectory = outputDirectory;
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
	public void setSourcepath(String sourcepath)
    {
    	this.sourcepath = sourcepath;
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
		File tempFile = File.createTempFile("cruxCompiler", "files");
		PrintWriter out = new PrintWriter(tempFile);
		List<String> javaFiles = new ArrayList<String>();
		
		String sourcePath = sourceDir.getCanonicalPath().replace('\\', '/');
		if (!sourcePath.endsWith("/"))
		{
			sourcePath +="/";
		}
		extractJavaFiles(sourceDir, javaFiles, sourcePath.length());
		for (String javaFile : javaFiles)
        {
			out.println(javaFile);
        }
		out.close();
		return " @"+tempFile.getCanonicalPath();
    }
	
	/**
	 * @param input
	 * @param javaFiles
	 * @param sourcePathNameLength
	 * @throws IOException
	 */
	private void extractJavaFiles(File input, List<String> javaFiles, int sourcePathNameLength) throws IOException
	{
		if (input.isDirectory())
		{
			File[] files = input.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					extractJavaFiles(file, javaFiles, sourcePathNameLength);
				}
			}
		}
		else if (input.getName().toLowerCase().endsWith(".java"))
		{
			javaFiles.add(input.getCanonicalPath().substring(sourcePathNameLength));
		}
	}
	
	/**
	 * @return
	 */
	private String getCompilerOptions()
	{
		StringBuilder str = new StringBuilder();
		if (!StringUtils.isEmpty(source))
		{
			str.append(" -source "+ source);
		}
		if (!StringUtils.isEmpty(target))
		{
			str.append(" -target "+ target);
		}
		if (!StringUtils.isEmpty(classpath))
		{
			str.append(" -classpath "+ classpath);
		}
		if (!StringUtils.isEmpty(sourcepath))
		{
			str.append(" -sourcepath "+ sourcepath);
		}
		if (!StringUtils.isEmpty(outputDirectory))
		{
			str.append(" -d "+ outputDirectory);
		}
		
		
		return str.toString();
	}
}
