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
package org.cruxframework.crux.tools.compile;

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
 * 
 * @author Thiago da Rosa de Bustamante
 */

public abstract class AbstractCruxCompilerTask extends Task
{
	private List<Path> classpath = new ArrayList<Path>();
	private List<Argument> args = new ArrayList<Argument>();
	private List<Argument> jvmArgs = new ArrayList<Argument>();
	private File outputDir;
	private File webDir;
	private File pagesOutputDir;
	private File srcDir;
	private String scanAllowedPackages;
	private String scanIgnoredPackages;
	private String outputCharset;
	private String inputCharset = "UTF-8";
	private String pageFileExtension;
	private Boolean indentPages;
	private Boolean keepPagesGeneratedFiles;
	private boolean failOnError = true;
	private Boolean doNotPreCompileJavaSource;
	
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
	
	public void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}

	public void setWebDir(File webDir)
	{
		this.webDir = webDir;
	}
	
	public void setPagesOutputDir(File pagesOutputDir)
    {
    	this.pagesOutputDir = pagesOutputDir;
    }

	public void setScanAllowedPackages(String scanAllowedPackages)
    {
    	this.scanAllowedPackages = scanAllowedPackages;
    }

	public void setScanIgnoredPackages(String scanIgnoredPackages)
    {
    	this.scanIgnoredPackages = scanIgnoredPackages;
    }

	public void setOutputCharset(String outputCharset)
    {
    	this.outputCharset = outputCharset;
    }

	public void setInputCharset(String inputCharset)
	{
		this.inputCharset = inputCharset;
	}

	public void setPageFileExtension(String pageFileExtension)
    {
    	this.pageFileExtension = pageFileExtension;
    }

	public void setIndentPages(Boolean indentPages)
    {
    	this.indentPages = indentPages;
    }

	public void setKeepPagesGeneratedFiles(Boolean keepPagesGeneratedFiles)
    {
    	this.keepPagesGeneratedFiles = keepPagesGeneratedFiles;
    }
	
	public void setDoNotPreCompileJavaSource(Boolean doNotPreCompileJavaSource)
    {
    	this.doNotPreCompileJavaSource = doNotPreCompileJavaSource;
    }

	public void setSrcDir(File srcDir)
    {
    	this.srcDir = srcDir;
    }

	public void setFailOnError(Boolean failOnError)
	{
		this.failOnError = failOnError;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
		try 
		{
			compileFile();
		} 
		catch (Throwable e) 
		{
			log(e.getMessage(), Project.MSG_ERR);
			new BuildException(e.getMessage(), e);
		}
	}
	
	/**
	 * Compile files using GWT compiler
	 * @throws Exception
	 */
	protected void compileFile() throws Exception
	{
		log("Compiling...");
		Java javatask = (Java) getProject().createTask("java");
		javatask.setClassname(getProgramClassName());
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
		addCompilerParameters(javatask);

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
				throw new CompilerException("Crux Compiler returned errors.");
			}
			else
			{
				log("Crux Compiler returned errors.", Project.MSG_ERR);				
			}
		}
	}

	/**
	 * @return
	 */
	protected String getProgramClassName()
	{
		return "org.cruxframework.crux.tools.compile.CruxCompiler";
	}

	/**
	 * @param javatask
	 * @throws IOException
	 */
	protected void addCompilerParameters(Java javatask) throws Exception
    {
	    if (this.outputDir != null)
		{
			javatask.createArg().setValue("outputDir");
			javatask.createArg().setValue(this.outputDir.getCanonicalPath());
		}

	    if (this.srcDir != null)
		{
			javatask.createArg().setValue("sourceDir");
			javatask.createArg().setValue(this.srcDir.getCanonicalPath());
		}

	    if (this.webDir != null)
		{
			javatask.createArg().setValue("webDir");
			javatask.createArg().setValue(this.webDir.getCanonicalPath());
		}
	    
		if (this.pagesOutputDir != null)
		{
			javatask.createArg().setValue("pagesOutputDir");
			javatask.createArg().setValue(this.pagesOutputDir.getCanonicalPath());
		}
		
		if (this.scanAllowedPackages != null)
		{
			javatask.createArg().setValue("scanAllowedPackages");
			javatask.createArg().setValue(this.scanAllowedPackages);
		}

		if (this.scanIgnoredPackages != null)
		{
			javatask.createArg().setValue("scanIgnoredPackages");
			javatask.createArg().setValue(this.scanIgnoredPackages);
		}
		
		if (this.outputCharset != null)
		{
			javatask.createArg().setValue("outputCharset");
			javatask.createArg().setValue(this.outputCharset);
		}

		if (this.pageFileExtension != null)
		{
			javatask.createArg().setValue("pageFileExtension");
			javatask.createArg().setValue(this.pageFileExtension);
		}

		if (this.indentPages != null && this.indentPages)
		{
			javatask.createArg().setValue("-indentPages");
		}

		if (this.keepPagesGeneratedFiles != null && this.keepPagesGeneratedFiles)
		{
			javatask.createArg().setValue("-keepPagesGeneratedFiles");
		}
		
		if (this.doNotPreCompileJavaSource != null && this.doNotPreCompileJavaSource)
		{
			javatask.createArg().setValue("-doNotPreCompileJavaSource");
		}
		
    }
}
