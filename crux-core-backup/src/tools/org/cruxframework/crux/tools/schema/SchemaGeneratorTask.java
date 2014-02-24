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
package org.cruxframework.crux.tools.schema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SchemaGeneratorTask extends Task
{
	private List<Path> classpath = new ArrayList<Path>();
	private List<Argument> jvmArgs = new ArrayList<Argument>();
	private File outputDir;
	private File webDir;
	private String inputCharset = "UTF-8";
	
	public void addClasspath(Path classpath)
	{
		this.classpath.add(classpath);
	}
	
	public void addJvmarg(Argument jvmarg)
	{
		this.jvmArgs.add(jvmarg);
	}
	
	public void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}

	public void setWebDir(File webDir)
    {
    	this.webDir = webDir;
    }

	public void setInputCharset(String inputCharset)
	{
		this.inputCharset = inputCharset;
	}
	
	public void execute() throws BuildException
	{
		try 
		{
			File baseDir = getProject().getBaseDir();
			generateSchemas(baseDir);
		} 
		catch (Throwable e) 
		{
			log(e.getMessage());
			new BuildException(e.getMessage(), e);
		}
	}
	
	/**
	 * Compile files using GWT compiler
	 * @param file
	 * @throws Exception
	 */
	protected void generateSchemas(File baseDir) throws Exception
	{
		log("Generating Schemas to: " + baseDir.getCanonicalPath() + "/" + outputDir);
		Java javatask = (Java) getProject().createTask("java");
		javatask.setClassname(SchemaGenerator.class.getName());
		javatask.setFork(true);

		javatask.createJvmarg().setValue("-Dfile.encoding="+this.inputCharset );
		
		for (Argument arg : jvmArgs)
		{
			if(arg != null)
			{
				javatask.createJvmarg().setValue(arg.getParts()[0]);
			}
		}
		javatask.createArg().setValue(baseDir.getCanonicalPath());
		javatask.createArg().setValue(outputDir.getCanonicalPath());
		addCompilerParameters(javatask);

		for (Path path : this.classpath)
		{
			javatask.setClasspath(path);
		}

		int resultCode = javatask.executeJava();
		if (resultCode != 0)
		{
			log("Error generating schemas.", 1);
		}
	}
	
	/**
	 * @param javatask
	 * @throws IOException
	 */
	protected void addCompilerParameters(Java javatask) throws Exception
    {
		if (webDir != null)
		{
			javatask.createArg().setValue(webDir.getCanonicalPath());
		}		
    }
}
