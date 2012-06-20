/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.tools.projectgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * A program to check for Crux required Jar and, eventually, 
 * download and install them.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DependenciesChecker
{
	private static final String REPO_GWT_SERVLET_DEPS_JAR = "repo/gwt-servlet-deps.jar";
	private static final String REPO_GWT_SERVLET_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-servlet/2.2.0/gwt-servlet-2.2.0.jar";
	private static final String REPO_GWT_USER_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-user/2.2.0/gwt-user-2.2.0.jar";
	private static final String REPO_GWT_DEV_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-dev/2.2.0/gwt-dev-2.2.0.jar";
	private static final String REPO_SHINDIG_WAR = "http://repo2.maven.org/maven2/org/apache/shindig/shindig-server/2.0.0/shindig-server-2.0.0.war";
	private static final String REPO_JSP_API_JAR = "http://repo1.maven.org/maven2/javax/servlet/jsp/jsp-api/2.1/jsp-api-2.1.jar";

	private static final int GWT_DEV_TOTAL_BYTES = 27914742;
	private static final int GWT_USER_TOTAL_BYTES = 10682696;
	private static final int GWT_SERVLET_TOTAL_BYTES = 4380952;
	private static final int GWT_SERVLET_DEPS_TOTAL_BYTES = 84088;
	private static final int SHINDIG_TOTAL_BYTES = 18902990;
	private static final int JSP_API_TOTAL_BYTES = 100636;

	private boolean downloadDependenciesIfNeeded;
	private boolean downloadOptionalDependenciesIfNeeded;
	private String gwtFolder;
	private String shindigFolder;
	
	/**
     * Check all crux dependencies. If needed (and requested) install the jars.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
    {
		try
		{
			ConsoleParametersProcessor parametersProcessor = createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);
			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
				System.exit(1);
			}
			else
			{
				boolean downloadDependenciesIfNeeded = parameters.containsKey("-downloadDependencies");
				boolean downloadOptionalDependenciesIfNeeded = parameters.containsKey("-downloadOptionalDependencies");
				String gwtFolder = parameters.containsKey("gwtFolder")?parameters.get("gwtFolder").getValue():null;
				String shindigFolder = parameters.containsKey("shindigFolder")?parameters.get("shindigFolder").getValue():null;
				new DependenciesChecker(downloadDependenciesIfNeeded, downloadOptionalDependenciesIfNeeded, gwtFolder, shindigFolder).checkDependencies();
			}
			
			System.out.println("\n=================================================================================================================\n");
		}
		catch (ConsoleParametersProcessingException e)
		{
			System.out.println("Program aborted");
			System.exit(1);
		}
		catch (Exception e) 
		{
			System.out.flush();
			System.out.println("\n\n--------------------------------------------------");
			System.out.println("An error occurred during installation:");
			System.out.println("--------------------------------------------------\n");
			System.out.println(e.getMessage());
			System.out.println("\n--------------------------------------------------");
			System.exit(1);
		}
    }

	/**
	 * @param downloadDependenciesIfNeeded - if true, makes Crux to try retrieve the jars from a maven repository.
	 * @param downloadOptionalDependenciesIfNeeded if true, makes Crux to try retrieve optional dependencies from a maven repository.
	 * @param gwtFolder If not null, try to install the jars from a folder on disk.
	 * @param shindigFolder If not null, try to install the shindig from a folder on disk.
	 */
	private DependenciesChecker(boolean downloadDependenciesIfNeeded, boolean downloadOptionalDependenciesIfNeeded, String gwtFolder, String shindigFolder)
	{
		this.downloadDependenciesIfNeeded = downloadDependenciesIfNeeded;
		this.downloadOptionalDependenciesIfNeeded = downloadOptionalDependenciesIfNeeded;
		this.gwtFolder = gwtFolder;
		this.shindigFolder = shindigFolder;
	}
	
	/**
	 * Ensure that all required jars are present. If not try to install them or raise an error.
	 */
	private void checkDependencies()
    {
		List<Dependency> requiredDeps = new ArrayList<Dependency>();
		
		File jarFile = new File("./lib/build/gwt-dev.jar");
		if (!jarFile.exists() || jarFile.length() < GWT_DEV_TOTAL_BYTES)
		{
			requiredDeps.add(new Dependency("gwt-dev.jar", "./lib/build", REPO_GWT_DEV_JAR, GWT_DEV_TOTAL_BYTES));
		}
		jarFile = new File("./lib/build/gwt-user.jar");
		if (!jarFile.exists() || jarFile.length() < GWT_USER_TOTAL_BYTES)
		{
			requiredDeps.add(new Dependency("gwt-user.jar", "./lib/build", REPO_GWT_USER_JAR, GWT_USER_TOTAL_BYTES));
		}
		jarFile = new File("./lib/web-inf/gwt-servlet.jar");
		if (!jarFile.exists() || jarFile.length() < GWT_SERVLET_TOTAL_BYTES)
		{
			requiredDeps.add(new Dependency("gwt-servlet.jar", "./lib/web-inf", REPO_GWT_SERVLET_JAR, GWT_SERVLET_TOTAL_BYTES));
		}
/*		jarFile = new File("./lib/web-inf/gwt-servlet-deps.jar");
		if (!jarFile.exists())
		{
			requiredDeps.add(new Dependency("gwt-servlet-deps.jar", "./lib/web-inf", REPO_GWT_SERVLET_DEPS_JAR, GWT_SERVLET_DEPS_TOTAL_BYTES));
		}
*/		
		if (requiredDeps.size() > 0)
		{
			getRequiredDependencies();

			if (gwtFolder != null && gwtFolder.length() > 0)
			{
				copyCruxDependencies(requiredDeps, new File (gwtFolder));
			}
			else if (downloadDependenciesIfNeeded)
			{
				downloadCruxDependencies(requiredDeps);
			}
		}
		
		checkOptionalDependencies();
    }

	/**
	 * Check if optional dependencies are present. If not ask user if we need to try to install them.
	 */
	private void checkOptionalDependencies()
    {
		File warFile = new File("./shindig/shindig.war");
		if (!warFile.exists())
		{
			getOptionalDependencies();
			if (shindigFolder != null && shindigFolder.length() > 0)
			{
		    	try
	            {
		    	    System.out.println("Copying file: shindig.war");
		            FileUtils.copyFilesFromDir(new File(shindigFolder), warFile, null, null);
		            if(!warFile.exists())
		            {
		            	throw new RuntimeException("The folder " + shindigFolder + " does not contain a Shindig Server.");
		            }
	            }
	            catch (IOException e)
	            {
	            	throw new RuntimeException("Error copying shindig from shindigFolder.", e);
	            }
			}
			else if (downloadOptionalDependenciesIfNeeded)
			{
				downloadDependency(new Dependency("zipedShindig.war", "./shindig", REPO_SHINDIG_WAR, SHINDIG_TOTAL_BYTES));
				try
                {
	                File zippedFile = new File("./shindig/zipedShindig.war");
					FileUtils.unzip(zippedFile, warFile);
					zippedFile.delete();
					downloadDependency(new Dependency("jsp-api-2.1.jar", "./shindig/shindig.war/WEB-INF/lib", REPO_JSP_API_JAR, JSP_API_TOTAL_BYTES));
                }
                catch (IOException e)
                {
                	FileUtils.recursiveDelete(warFile);
                	throw new RuntimeException("Error unzipping shindig file", e);
                }
			}
		}
    }

	/**
	 * Ask user if we need to retrieve the optional dependencies
	 */
	private void getOptionalDependencies()
    {
	    try
        {
	        if (!downloadOptionalDependenciesIfNeeded && (shindigFolder == null || shindigFolder.length() == 0))
	        {
	        	System.out.println("\n=================================================================================================================\n");

	        	System.out.println("Do you want to install the Shindig Server (required to develop gadgets)? Type one of the options and press enter:\n");
	        	String option = null;
	        	
	        	while (option == null || (!option.equals("1") && !option.equals("2") && !option.equals("3")))
	        	{
	        		System.out.println("\t1) To download from the web.");
	        		System.out.println("\t2) To copy from a folder on your disk.");
	        		System.out.println("\t3) To finish the installation.\n");

	        		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        		option = reader.readLine();

	        		if (option.equals("1"))
	        		{
	        			downloadOptionalDependenciesIfNeeded = true;
	        		}
	        		else if (option.equals("2"))
	        		{
		        		System.out.println("\n\tType the shindig folder and press [ENTER]:\n");
		        		shindigFolder = reader.readLine();
	        		}
	        		else if (option.equals("3"))
	        		{
		        		System.out.println("\n\tIf you want to download the optional dependencies later, just run this program again.");
	        		}
	        	}
	        	
	        }
        }
        catch (IOException e)
        {
	        throw new RuntimeException("Error reading system input.", e);
        }
    }

	/**
	 * Retrieve the dependencies
	 */
	private void getRequiredDependencies()
    {
	    try
        {
	        if (!downloadDependenciesIfNeeded && (gwtFolder == null || gwtFolder.length() == 0))
	        {
	        	System.out.println("\n=============================================================================================================\n");
	        	System.out.println("To complete the installation, you need the GWT 2.2 jars. Type one of the following options and press [ENTER]: \n");
	        	String option = null;
	        	
	        	while (option == null || (!option.equals("1") && !option.equals("2")))
	        	{
	        		System.out.println("\t1) To download them from the web.");
	        		System.out.println("\t2) To copy them from a folder on your disk.");
	        		System.out.println("\t3) To abort the installation.\n");

	        		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        		option = reader.readLine();

	        		if (option.equals("1"))
	        		{
	        			downloadDependenciesIfNeeded = true;
	        		}
	        		else if (option.equals("2"))
	        		{
		        		System.out.println("\n\tType the GWT folder and press [ENTER]:\n");
		        		gwtFolder = reader.readLine();
	        		}
	        		else if (option.equals("3"))
	        		{
		        		System.out.println("\tAborting installation...");
		        		System.exit(1);
	        		}
	        	}
	        	
	        }
        }
        catch (IOException e)
        {
	        throw new RuntimeException("Error reading system input.", e);
        }
    }

	/**
	 * Install the jars from a folder on disk.
	 * @param requiredDeps Dependencies to install
	 * @param gwtFolder The folder
	 */
	private void copyCruxDependencies(List<Dependency> requiredDeps, File gwtFolder)
    {
    	System.out.println("\n-----------------------------------------------------------------------------------\n");

	    System.out.println("Copying required jars from folder "+gwtFolder.getName()+"...");
		if (!gwtFolder.exists())
	    {
	    	throw new RuntimeException("The GWT folder you entered does not exist!");
	    }
	    
	    for (Dependency dependency : requiredDeps)
        {
	    	try
            {
	    	    System.out.println("Copying file: "+dependency.getJarName());
	            FileUtils.copyFilesFromDir(gwtFolder, dependency.getDestFolder(), dependency.getJarName(), null);
	            File copy = new File(dependency.getDestFolder(), dependency.getJarName());
	            if(!copy.exists())
	            {
	            	throw new RuntimeException("The required file " + dependency.getJarName() + " was not found at the folder " + gwtFolder + ".");
	            }
            }
            catch (IOException e)
            {
            	throw new RuntimeException("Error copying required jar from GWT folder.", e);
            }
        }
	    
	    System.out.println("\n\nAll required jars installed successfully!\n\n\n");
    }

	/**
	 * Retrieve the jars from the web.
	 * @param requiredDeps Dependencies to install
	 */
	private void downloadCruxDependencies(List<Dependency> requiredDeps)
    {
	    System.out.println("\nDownloading required jars...");
	    for (Dependency dependency : requiredDeps)
        {
	    	downloadDependency(dependency);
        }
	    
	    System.out.println("\nAll required jars installed successfully!\n");
    }

	/**
	 * Retrieve a jar file from the web.
	 * @param requiredDeps Dependencies to install
	 */
	private void downloadDependency(Dependency dependency)
	{
		InputStream in = null;
		OutputStream out = null;
		try
		{
			checkDestinationFolder(dependency);
			
			System.out.println("\nDownloading file "+ dependency.getJarName()+"...");
			URL url = new URL(dependency.getResourceURL());
			URLConnection urlc = url.openConnection();

			in = urlc.getInputStream();
			out = new FileOutputStream(new File(dependency.getDestFolder(), dependency.getJarName()));
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			int blockSize = 4 * 1024;
			byte[] buf = new byte[blockSize]; // 4K buffer
			int bytesRead;
			int progressBytes = 0;
			int updateProgressBar = 0;
			while ((bytesRead = in.read(buf)) != -1) 
			{
				out.write(buf, 0, bytesRead);
				progressBytes+=bytesRead;
				updateProgressBar++;
				if (updateProgressBar > 10)
				{
					double percent = (progressBytes / dependency.getSize());
					System.out.print("\r"+percentFormat.format(percent));
					updateProgressBar = 0;
				}
			}
			out.flush();
			out.close();
			in.close();
			System.out.println("\r100%  ");
		}
		catch (Exception e) 
		{
			throw new RuntimeException("Error downloading file "+ dependency.getJarName(), e);
		}
		finally
		{
			if (in != null)
			{
				try{in.close();}catch (IOException ioe){}
			}
			if (out != null)
			{
				try{out.close();}catch (IOException ioe){}
			}
		}
	}

	/**
	 * @param dependency
	 */
	private void checkDestinationFolder(Dependency dependency)
    {
	    File destFolder = dependency.getDestFolder();
	    if (!destFolder.exists())
	    {
	    	destFolder.mkdirs();
	    }
    }

	/** Create a processor for command line parameters
	 * @return
	 */
	private static ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("start");
		ConsoleParameter parameter = new ConsoleParameter("gwtFolder", "The folder containing GWT jars.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("folderName", "The name of the folder"));
		parametersProcessor.addSupportedParameter(parameter);
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-downloadDependencies", "Download and install dependencies before start.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-downloadOptionalDependencies", "Download and install optional dependencies before start.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;
	}
	
	/**
	 * Represents a jar dependency from Crux project
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class Dependency
	{
		private final String jarName;
		private final File destFolder;
		private final String resourceURL;
		private final int size;

		Dependency(String jarName, String destFolder, String resourceURL, int size)
        {
			this.jarName = jarName;
			this.size = size;
			this.destFolder = new File(destFolder);
			this.resourceURL = resourceURL;
        }
		
		String getJarName()
        {
        	return jarName;
        }
		String getResourceURL()
        {
        	return resourceURL;
        }
		File getDestFolder()
		{
			return destFolder;
		}
		double getSize()
		{
			return size;
		}
	}
}
