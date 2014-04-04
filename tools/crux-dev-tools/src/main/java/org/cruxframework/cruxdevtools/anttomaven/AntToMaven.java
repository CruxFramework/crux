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
package org.cruxframework.cruxdevtools.anttomaven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.cruxframework.cruxdevtools.utils.CruxFileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Samuel Almeida Cardoso
 * 
 */
public class AntToMaven
{
	/* Logger */
	private static final Logger LOG = Logger.getLogger(AntToMaven.class.getName());

	private static final String DEFAULT_INPUT_FILE = 
		"src\\main\\resources\\org\\cruxframework\\cruxdevtools\\anttomaven\\sampleInput.xml";
	
	private static final String WAR_WEB_XML_LOCATION = "\\war\\WEB-INF\\web.xml";
	private static final String MODULE_WEB_XML_LOCATION = "\\war\\web.xml";
	private static final String MAVEN_WEB_XML_LOCATION = "\\src\\main\\webapp\\WEB-INF\\web.xml";
	
	private static final String JAVA_FILES = ".java";
	private static final String RESOURCES_FILES = ".css .js .png .jpg .gif .jpeg .txt .xml .html .checkstyle .pmd";
	private static final String GWT_CRUX_FILES = ".gwt.xml .module.xml .view.xml .crux.xml .template.xml .xdevice.xml .ui.xml";
	
	private static GlobalConfigurations globalConfigurations;

	/**
	 * Execute the script.
	 * @param args script arguments
	 */
	public static void main(String[] args)
	{
		String file = getInputFile(args);
		execute(file);
	}
	
	private static void execute(String file)
	{
		try
		{
			// Open xml file
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			// Read global configurations
			readGlobalConfigurations(doc);
			
			// Recreate target directory
			CruxFileUtils.deleteDirectory(globalConfigurations.getTargetDirectory());
			CruxFileUtils.mkdir(globalConfigurations.getTargetDirectory());

			// Read project configurations
			NodeList nodeList = doc.getElementsByTagName("project");
			for (int temp = 0; temp < nodeList.getLength(); temp++)
			{
				Node node = nodeList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					// Get project configurations
					Element element = (Element) node;
					ProjectConfigurations projectConfigurations = readProjectConfigurations(element);
					
					// Remove target directory
					removeTargetDirectory(projectConfigurations);
					
					// Build maven goals and invoke maven
					List<String> mavenGoals = buildMavenGoals(projectConfigurations);
					invokeMaven(mavenGoals);
					
					// Copy files to new structure
					copyFiles(projectConfigurations);
					LOG.info("OK");
				}
			}
		} catch (Exception exception)
		{
			LOG.severe("Global exception thrown.");
			throw new RuntimeException(exception);
		}
	}
	
	private static final String getInputFile(String[] args)
	{
		String file = (args != null) && (args.length > 0) ? args[0] : null;
		if (StringUtils.isEmpty(file))
		{
			file = DEFAULT_INPUT_FILE;
		}
		
		return file;
	}
	
	private static void readGlobalConfigurations(Document doc)
	{
		NodeList globalConfigurationsNode = 
			doc.getElementsByTagName("globalConfigurations");
		
		for (int i = 0; i < globalConfigurationsNode.getLength(); i++)
		{
			Node node = globalConfigurationsNode.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				globalConfigurations = new GlobalConfigurations(element);
			}
		}
	}
	
	private static ProjectConfigurations readProjectConfigurations(Element element)
	{
		LOG.info("Generating project" + element.getAttribute("projectName"));
		ProjectConfigurations projectParams = null;
		try
		{
			LOG.info("Reading params...");
			projectParams = new ProjectConfigurations(element);
		} catch (Exception e)
		{
			LOG.severe("Argument parse error.");
			throw new RuntimeException(e);
		}
		
		return projectParams;
	}
	
	private static void removeTargetDirectory(ProjectConfigurations projectParams)
	{
		LOG.info(">>> Removing target <<<<");
		String artifactId = projectParams.getArtifactId();
		String targetDirectory = globalConfigurations.getTargetDirectory();
		try
		{
			String targetDirectoryName = targetDirectory + "\\" + artifactId;
			File directory = new File(targetDirectoryName);
			if (directory.exists())
			{
				CruxFileUtils.cleanDirectory(targetDirectoryName);
			}
		} catch (Exception e)
		{
			LOG.severe("Unable to clean directory: " + targetDirectory + "\\" + artifactId);
			throw new RuntimeException(e);
		}
	}
	
	private static void invokeMaven(List<String> mavenGoals)
	{
		LOG.info(">>> mvn: " + mavenGoals.toString());
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals(mavenGoals);

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(globalConfigurations.getMavenHome()));
		invoker.setWorkingDirectory(new File(globalConfigurations.getTargetDirectory()));
		try
		{
			InvocationResult result = invoker.execute(request);
			
			if (result.getExecutionException() != null)
			{
				throw result.getExecutionException();
			}
			
		} catch (CommandLineException e)
		{
			LOG.severe(e.getMessage());
			throw new RuntimeException(e);
		}
			catch (MavenInvocationException e)
		{
			LOG.severe("Error running mvn task.");
			throw new RuntimeException(e);
		}
		
		LOG.info("Project created.");
	}

	private static List<String> buildMavenGoals(ProjectConfigurations projectParams)
	{
		LOG.info("Generating maven goals...");
		
		String archetypeGroupId = globalConfigurations.getArchetypeGroupId();
		String archetypeJARArtifactId = globalConfigurations.getArchetypeJARArtifactId();
		String archetypeWARArtifactId = globalConfigurations.getArchetypeWARArtifactId();
		String archetypeCatalog = globalConfigurations.getArchetypeCatalog();
		
		Boolean typeWAR = projectParams.isTypeWAR();
		String groupId = projectParams.getGroupId();
		String artifactId = projectParams.getArtifactId();
		String version = projectParams.getVersion();
		String packageStr = projectParams.getPackageStr();
		String moduleName = projectParams.getModuleName();
		String moduleShortName = projectParams.getModuleShortName();
		String moduleDescription = projectParams.getModuleDescription();
		
		List<String> params = Collections.singletonList(
			"archetype:generate" + " -DgroupId=" + groupId + 
			" -DartifactId=" + artifactId + 
			" -DarchetypeGroupId=" + archetypeGroupId + 
			" -DarchetypeArtifactId=" + (typeWAR ? archetypeWARArtifactId : archetypeJARArtifactId) + 
			" -DarchetypeCatalog='" + archetypeCatalog + "'" + 
			" -Dversion=" + version + 
			" -Dpackage=" + packageStr + 
			" -Dmodule-name=" + moduleName + 
			" -Dmodule-short-name=" + moduleShortName + 
			" -Dmodule-description='" + moduleDescription + "'" + 
			" -DinteractiveMode=false");
		
		return params;
	}
	
	/*************************************************************
	 * Copy files methods
	 *************************************************************/
	
	private static void copyFiles(ProjectConfigurations projectParams)
	{
		LOG.info("Copying files...");
		
		String workingDirectory = globalConfigurations.getWorkingDirectory();
		String targetDirectory = globalConfigurations.getTargetDirectory();
		
		Boolean typeWAR = projectParams.isTypeWAR();
		String artifactId = projectParams.getArtifactId();
		String packageStr = projectParams.getPackageStr();
		boolean convertToUtf8 = projectParams.isConvertUtf8();
		
		copyWebXmlFiles(workingDirectory, targetDirectory, artifactId, typeWAR);
		copyPublicFolder(workingDirectory, targetDirectory, packageStr, artifactId);
		copyBuildFolder(workingDirectory, targetDirectory, artifactId);
		copyResourceFiles(workingDirectory, targetDirectory, artifactId);
		copyJavaFiles(workingDirectory, targetDirectory, artifactId, convertToUtf8);
		copyCruxXmlFiles(workingDirectory, targetDirectory, artifactId);
		copyPropertyFiles(workingDirectory, targetDirectory, artifactId);
		buildPomXmlFile(workingDirectory, targetDirectory, artifactId);
	}
	
	/**
	 * Copy web.xml from 'war\WEB-INF\web.xml' to 
	 * 'src\main\webapp\WEB-INF\web.xml' directory.
	 */
	private static void copyWebXmlFiles(String workingDirectory, 
		String targetDirectory, String artifactId, boolean typeWAR)
	{
		LOG.info(">>> Copying web.xml <<<<");
		String srcWAR =	typeWAR ? workingDirectory + "\\" + artifactId + WAR_WEB_XML_LOCATION : 
			workingDirectory + "\\" + artifactId + MODULE_WEB_XML_LOCATION;
	
		String destWAR = targetDirectory + "\\" + artifactId + MAVEN_WEB_XML_LOCATION;

		if (new File(srcWAR).exists())
		{
			try
			{
				CruxFileUtils.copyFile(new File(srcWAR), new File(destWAR));
			} catch (IOException e)
			{
				LOG.log(Level.SEVERE, "Error to copy web.xml.", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * from: src\*\public
	 * to: src\main\resources\*\public
	 */
	private static void copyPublicFolder(String workingDirectory, 
		String targetDirectory, String packageStr, String artifactId)
	{
		LOG.info(">>> Copying public folder <<<<");
		String srcDirectory =  packageStr.replace(".", "\\");
		String srcPublic = workingDirectory + "\\" + artifactId + "\\" + "src" + "\\" + srcDirectory + "\\public";
		String destPublic = targetDirectory + "\\" + artifactId + "\\" + "src\\main\\resources" + "\\" + srcDirectory + "\\public";
		copyDirectoryStructure(srcPublic, destPublic);
	}
	
	/**
	 * copy directory: from: 'build\*'  to: 'build\*'
	 */
	private static void copyBuildFolder(String workingDirectory, 
		String targetDirectory, String artifactId)
	{
		LOG.info(">>> Copying build folder <<<<");
		String srcBuild = workingDirectory + "\\" + artifactId + "\\" + "build";
		String destBuild = targetDirectory + "\\" + artifactId + "\\" + "build";
		copyDirectoryStructure(srcBuild, destBuild);
	}
	
	/**
	 * from: src\*\*.gwt.xml
	 * to: src\main\resources\*\*.gwt.xml
	 */
	private static void copyCruxXmlFiles(String workingDirectory, String targetDirectory, 
		String artifactId)
	{
		LOG.info(">>> Copying *.gwt.xml *.module.xml files <<<<");
		String workingGWTFiles = workingDirectory + "\\" + artifactId + "\\" + "src" + "\\";
		String destGWTFiles = targetDirectory + "\\" + artifactId + "\\src\\main\\resources\\";
		copyFiles(workingGWTFiles, destGWTFiles, GWT_CRUX_FILES);
	}
	
	/**
	 * from: src\*.properties
	 * to: src\main\resources\*.properties
	 */
	private static void copyPropertyFiles(String workingDirectory, 
		String targetDirectory, String artifactId)
	{
		LOG.info(">>> Copying properties files <<<<");
		String workingPropertiesFiles = workingDirectory + "\\" + artifactId + "\\src";
		String destPropertiesFiles = targetDirectory + "\\" + artifactId + "\\src\\main\\resources\\";
		copyFiles(workingPropertiesFiles, destPropertiesFiles, ".properties");
	}
	
	/**
	 * copy directory: from: 'src' to: 'src\main\resources\'
	 */
	private static void copyResourceFiles(String workingDirectory, String targetDirectory, 
		String artifactId)
	{
		LOG.info(">>> Copying resources folder <<<<");
		String workingResourceSrc = workingDirectory + "\\" + artifactId + "\\" + "src";
		String destResourceSrc = targetDirectory + "\\" + artifactId + "\\src\\main\\resources";
		copyFiles(workingResourceSrc, destResourceSrc, RESOURCES_FILES);
	}
	
	private static void copyFiles(String srcDirectory, String destDirectory, 
		final String filter)
	{
		LOG.info(">>> Copying files folder <<<<");
		try
		{
			CruxFileUtils.copyDirectory(new File(srcDirectory), 
				new File(destDirectory), new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return CruxFileUtils.checkExtensionFile(pathname, filter);
				}
			}, true);
		} catch (IOException e)
		{
			LOG.severe("Error to copy Client and Server folders.");
			throw new RuntimeException(e);
		}
	}
	
	private static void copyDirectoryStructure(String srcFolder, String destFolder)
	{
		try
		{
			CruxFileUtils.copyDirectoryStructure(new File(srcFolder), 
				new File(destFolder));
		} catch (IOException e)
		{
			LOG.severe("Error to copy build folder.");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Skip:
	 * -> public folder
	 * -> *.gwt.xml
	 *	 -> *.module.xml
	 *	 -> Resources: *.jpg, png, gif, ...
	 *
	 * from: src\*
	 * to: src\main\java\
	 */
	private static void copyJavaFiles(String workingDirectory, 
		 String targetDirectory, String artifactId, boolean convertToUtf8)
	{
		LOG.info(">>> Copying Client and Server folders <<<<");
		String workingJavaSrc = workingDirectory + "\\" + artifactId + "\\" + "src";
		String destJavaSrc = targetDirectory + "\\" + artifactId + "\\src\\main\\java";
		try
		{
			CruxFileUtils.copyDirectory(new File(workingJavaSrc), new File(destJavaSrc), new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return CruxFileUtils.checkExtensionFile(pathname, JAVA_FILES);
				}
			}, true, convertToUtf8);
		} catch (IOException e)
		{
			LOG.severe("Error to copy Client and Server folders.");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * from: build\ivy\pom.xml
	 * to: pom.xml
	 */
	private static void buildPomXmlFile(String workingDirectory, 
		String targetDirectory, String artifactId)
	{
		LOG.info(">>> Copying dependencies at pom file <<<<");
		// from: build\ivy\pom.xml
		String workingPomFile = workingDirectory + "\\" + artifactId + "\\" + "build\\ivy\\pom.xml";
		// to: pom.xml
		String destPomFile = targetDirectory + "\\" + artifactId + "\\" + "pom.xml";
		// ---->>> <dependencies>*</dependencies>
		try
		{
			String pomOrigContent = CruxFileUtils.readFileContent(new File(workingPomFile));
			String pomDestContent = CruxFileUtils.readFileContent(new File(destPomFile));

			String newDependencies = pomOrigContent.substring(pomOrigContent.indexOf("<dependencies>") + "<dependencies>".length(), pomOrigContent.lastIndexOf("</dependencies>"));

			StringBuffer newPom = new StringBuffer();
			newPom.append(pomDestContent.substring(0, pomDestContent.indexOf("<dependencies>")));
			newPom.append("<dependencies>");
			newPom.append(newDependencies);
			newPom.append(pomDestContent.substring(pomDestContent.lastIndexOf("<dependencies>") + "</dependencies>".length(), pomDestContent.length()));
			FileWriter writer = new FileWriter(destPomFile);
			writer.write(newPom.toString());
			writer.close();
		} catch (IOException ioe)
		{
			LOG.severe("Error building pom.xml file from ivy configuration");
			throw new RuntimeException(ioe);
		}
	}
}

class GlobalConfigurations
{
	private static final String MAVEN_HOME = "mavenHome";
	private static final String WORKING_DIRECTORY = "workingDirectory";
	private static final String TARGET_DIRECTORY = "targetDirectory";
	private static final String ARCHETYPE_JAR_ARTIFACT_ID = "archetypeJARArtifactId";
	private static final String ARCHETYPE_WAR_ARTIFACT_ID = "archetypeWARArtifactId";
	private static final String ARCHETYPE_CATALOG = "archetypeCatalog";
	private static final String ARCHETYPE_GROUP_ID = "archetypeGroupId";
	
	private final String mavenHome;
	private final String workingDirectory;
	private final String targetDirectory;
	private final String archetypeJARArtifactId;
	private final String archetypeWARArtifactId;
	private final String archetypeCatalog;
	private final String archetypeGroupId;
	
	public GlobalConfigurations(Element element)
	{
		mavenHome = CruxFileUtils.ensureDirNoLastBars(
			element.getElementsByTagName(MAVEN_HOME).item(0).getTextContent());
		
		workingDirectory = CruxFileUtils.ensureDirNoLastBars(
			element.getElementsByTagName(WORKING_DIRECTORY).item(0).getTextContent());
		
		targetDirectory = CruxFileUtils.ensureDirNoLastBars(
			element.getElementsByTagName(TARGET_DIRECTORY).item(0).getTextContent());
		
		archetypeGroupId = element.getElementsByTagName(
			ARCHETYPE_GROUP_ID).item(0).getTextContent();
		
		archetypeJARArtifactId = element.getElementsByTagName(
			ARCHETYPE_JAR_ARTIFACT_ID).item(0).getTextContent();
		
		archetypeWARArtifactId = element.getElementsByTagName(
			ARCHETYPE_WAR_ARTIFACT_ID).item(0).getTextContent();
		
		archetypeCatalog = element.getElementsByTagName(
			ARCHETYPE_CATALOG).item(0).getTextContent();
	}
	
	/************************************************
	 * Getters e setters
	 ************************************************/
	
	public String getMavenHome()
	{
		return mavenHome;
	}

	public String getWorkingDirectory()
	{
		return workingDirectory;
	}

	public String getTargetDirectory()
	{
		return targetDirectory;
	}

	public String getArchetypeJARArtifactId()
	{
		return archetypeJARArtifactId;
	}

	public String getArchetypeWARArtifactId()
	{
		return archetypeWARArtifactId;
	}

	public String getArchetypeCatalog()
	{
		return archetypeCatalog;
	}

	public String getArchetypeGroupId()
	{
		return archetypeGroupId;
	}
}

class ProjectConfigurations
{
	private static final Logger LOG = Logger.getLogger(ProjectConfigurations.class.getName());
	
	private static final String TYPE_WAR = "typeWAR";
	private static final String GROUP_ID = "groupId";
	private static final String ARTIFACT_ID = "artifactId";
	private static final String VERSION = "version";
	private static final String PACKAGE_STR = "package";
	private static final String MODULE_NAME = "moduleName";
	private static final String RESOURCES_FOLDER = "resourcesFolder";
	private static final String MODULE_SHORT_NAME = "moduleShortName";
	private static final String MODULE_DESCRIPTION = "moduleDescription";
	private static final String CONVERT_TO_UTF8 = "convertToUtf8";
	
	private static final String DEFAULT_RESOURCES_DIR = "resources";
	
	private final boolean typeWAR;
	private final String groupId;
	private final String artifactId;
	private final String version;
	private final String packageStr;
	private final String moduleName;
	private String resourcesFolder;
	private final String moduleShortName;
	private final String moduleDescription;
	private boolean convertUtf8;
	
	public ProjectConfigurations(Element element)
	{
		typeWAR = Boolean.valueOf(
			element.getElementsByTagName(TYPE_WAR).item(0).getTextContent());
		
		groupId = element.getElementsByTagName(
			GROUP_ID).item(0).getTextContent();
		
		artifactId = element.getElementsByTagName(
			ARTIFACT_ID).item(0).getTextContent();
		
		version = element.getElementsByTagName(
			VERSION).item(0).getTextContent();
		
		packageStr = element.getElementsByTagName(
			PACKAGE_STR).item(0).getTextContent();
		
		try
		{
			resourcesFolder = element.getElementsByTagName(
				RESOURCES_FOLDER).item(0).getTextContent();
		} catch (Exception e)
		{
			resourcesFolder = DEFAULT_RESOURCES_DIR;
			LOG.info("Using default resources folder.");
		}
		
		try
		{
			convertUtf8 = Boolean.valueOf(
				element.getElementsByTagName(CONVERT_TO_UTF8).item(0).getTextContent());
		} catch (Exception e)
		{
			convertUtf8 = false;
		}
		
		if (!convertUtf8)
		{
			LOG.info("Conversion to utf-8 wont be performed.");
		}
		
		moduleName = element.getElementsByTagName(
			MODULE_NAME).item(0).getTextContent();
		
		moduleShortName = element.getElementsByTagName(
			MODULE_SHORT_NAME).item(0).getTextContent();
		
		moduleDescription = element.getElementsByTagName(
			MODULE_DESCRIPTION).item(0).getTextContent();
	}
	
	/************************************************
	 * Getters e setters
	 ************************************************/

	public boolean isTypeWAR()
	{
		return typeWAR;
	}

	public String getGroupId()
	{
		return groupId;
	}

	public String getArtifactId()
	{
		return artifactId;
	}

	public String getVersion()
	{
		return version;
	}

	public String getPackageStr()
	{
		return packageStr;
	}

	public String getModuleName()
	{
		return moduleName;
	}

	public String getResourcesFolder()
	{
		return resourcesFolder;
	}

	public String getModuleShortName()
	{
		return moduleShortName;
	}

	public String getModuleDescription()
	{
		return moduleDescription;
	}

	public boolean isConvertUtf8()
	{
		return convertUtf8;
	}
}