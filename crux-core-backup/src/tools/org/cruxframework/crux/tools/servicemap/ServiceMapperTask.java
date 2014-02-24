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
package org.cruxframework.crux.tools.servicemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline.Argument;

/**
 * An ant task to call the ServiceMapper program 
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ServiceMapperTask extends Task
{
	private List<Path> classpath = new ArrayList<Path>();
	private List<Argument> args = new ArrayList<Argument>();
	private List<Argument> jvmArgs = new ArrayList<Argument>();
	private String inputCharset = "UTF-8";
	private boolean failOnError = true;
	private File projectDir;

	public void addClasspath(Path classpath)
	{
		this.classpath.add(classpath);
	}
	
	public void addArg(Argument arg)
	{
		this.args.add(arg);
	}

	public void addJvmarg(Argument jvmarg)
	{
		this.jvmArgs.add(jvmarg);
	}
	
	public void setInputCharset(String inputCharset)
	{
		this.inputCharset = inputCharset;
	}


	public void setFailOnError(Boolean failOnError)
	{
		this.failOnError = failOnError;
	}
	
	public void setProjectDir(File projectDir)
	{
		this.projectDir = projectDir;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
		try 
		{
			generateMap();
		} 
		catch (Throwable e) 
		{
			log(e.getMessage(), Project.MSG_ERR);
			new BuildException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	private void generateMap()
	{
		log("Generating map...");
		Java javatask = (Java) getProject().createTask("java");
		javatask.setClassname(ServiceMapper.class.getCanonicalName());
		javatask.setFork(true);
		javatask.setFailonerror(true);

		javatask.createJvmarg().setValue("-Dfile.encoding="+this.inputCharset );
		
		for (Argument arg : jvmArgs)
		{
			if(arg != null)
			{
				javatask.createJvmarg().setValue(arg.getParts()[0]);
			}
		}

		addServiceMapperParameters(javatask);
		
		for (Argument arg : args)
		{
			if(arg != null)
			{
				javatask.createArg().setValue(arg.getParts()[0]);
			}
		}

		for (Path path : this.classpath)
		{
			javatask.setClasspath(path);
		}
		
		int resultCode = javatask.executeJava();
		if (resultCode != 0)
		{
			if (this.failOnError )
			{
				throw new ServiceMapperException("Crux Service Mapper returned errors.");
			}
			else
			{
				log("Crux Service Mapper returned errors.", Project.MSG_ERR);				
			}
		}
	}

	/**
	 * @param javatask
	 */
	private void addServiceMapperParameters(Java javatask)
	{
		if (projectDir == null)
		{
			throw new ServiceMapperException("Missing required parameter <projectDir>.");
		}
		else
		{
			javatask.createArg().setValue("projectDir");
			try
			{
				javatask.createArg().setValue(projectDir.getCanonicalPath());
			}
			catch (IOException e)
			{
				throw new ServiceMapperException("Invalid <ProjectDir>.", e);
			}
		}
	}
}
