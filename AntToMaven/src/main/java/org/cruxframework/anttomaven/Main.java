package org.cruxframework.anttomaven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.StringUtils;
import org.cruxframework.anttomaven.utils.CruxFileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main 
{
	private static Logger LOG = Logger.getLogger(Main.class.getName());

	private static final String JAVA_FILES = ".java";
	private static final String RESOURCES_FILES = ".css .js .png .jpg .gif .jpeg";
	private static final String GWT_CRUX_FILES = ".gwt.xml .module.xml .view.xml .crux.xml .template.xml .xdevice.xml";

	public static void main(String[] args) 
	{
		String file = (args != null) && (args.length > 0) ? args[0] : null;
		if(StringUtils.isEmpty(file))
		{
			//throw new RuntimeException("Please inform a config file!");
			file = "sampleInput.txt";
		}

		String mavenHome = null;
		String workingDirectory = null;
		String targetDirectory = null;
		String archetypeGroupId = null;
		String archetypeJARArtifactId = null;
		String archetypeWARArtifactId = null;
		String archetypeCatalog = null;

		try {
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList globalConfigurations = doc.getElementsByTagName("globalConfigurations");
			for (int temp = 0; temp < globalConfigurations.getLength(); temp++) 
			{
				Node nNode = globalConfigurations.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element eElement = (Element) nNode;
					mavenHome = CruxFileUtils.ensureDirNoLastBars(eElement.getElementsByTagName("mavenHome").item(0).getTextContent());
					workingDirectory = CruxFileUtils.ensureDirNoLastBars(eElement.getElementsByTagName("workingDirectory").item(0).getTextContent());
					targetDirectory = CruxFileUtils.ensureDirNoLastBars(eElement.getElementsByTagName("targetDirectory").item(0).getTextContent());
					archetypeGroupId = eElement.getElementsByTagName("archetypeGroupId").item(0).getTextContent();
					archetypeJARArtifactId = eElement.getElementsByTagName("archetypeJARArtifactId").item(0).getTextContent();
					archetypeWARArtifactId = eElement.getElementsByTagName("archetypeWARArtifactId").item(0).getTextContent();
					archetypeCatalog = eElement.getElementsByTagName("archetypeCatalog").item(0).getTextContent();
				}
			}

			NodeList nList = doc.getElementsByTagName("project");
			for (int temp = 0; temp < nList.getLength(); temp++) 
			{
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element eElement = (Element) nNode;
					LOG.info("Generating project" + eElement.getAttribute("projectName"));

					boolean typeWAR;
					String groupId = null;
					String artifactId = null;
					String version = null;
					String packageStr = null;
					String moduleName = null;
					String resourcesFolder = "resources";
					String moduleShortName = null;
					String moduleDescription = null;
					try {
						LOG.info("Reading params...");
						typeWAR = Boolean.valueOf(eElement.getElementsByTagName("typeWAR").item(0).getTextContent());
						groupId = eElement.getElementsByTagName("groupId").item(0).getTextContent();
						artifactId = eElement.getElementsByTagName("artifactId").item(0).getTextContent();
						version = eElement.getElementsByTagName("version").item(0).getTextContent();
						packageStr = eElement.getElementsByTagName("package").item(0).getTextContent();
						try{
							resourcesFolder = eElement.getElementsByTagName("resourcesFolder").item(0).getTextContent();
						} catch (Exception e) {
							LOG.info("Using default resources folder.");
						}
						moduleName = eElement.getElementsByTagName("moduleName").item(0).getTextContent();
						moduleShortName = eElement.getElementsByTagName("moduleShortName").item(0).getTextContent();
						moduleDescription = eElement.getElementsByTagName("moduleDescription").item(0).getTextContent();
					} catch (Exception e)
					{
						throw new RuntimeException("Argument parse error.", e); 
					}
					
					LOG.info(">>> Removing target <<<<");
					try 
					{
						CruxFileUtils.cleanDirectory(targetDirectory + "\\" + artifactId);
					} catch (Exception e) 
					{
						LOG.info("Unable to clean directory: " + targetDirectory + "\\" + artifactId);
					}
					
					LOG.info("Generating...");
					InvocationRequest request = new DefaultInvocationRequest();
					List<String> params = Collections.singletonList(
							"archetype:generate"
									+ " -DgroupId=" + groupId
									+ " -DartifactId=" + artifactId
									+ " -DarchetypeGroupId=" + archetypeGroupId
									+ " -DarchetypeArtifactId=" + (typeWAR ? archetypeJARArtifactId : archetypeWARArtifactId)  
									+ " -DarchetypeCatalog=" + archetypeCatalog

									+ " -Dversion=" + version
									+ " -Dpackage=" + packageStr
									+ " -Dmodule-name=" + moduleName
									+ " -Dmodule-short-name=" + moduleShortName
									+ " -Dmodule-description='" + moduleDescription + "'"

						+ " -DinteractiveMode=false" );
					request.setGoals(params);

					LOG.info(">>> mvn: " + params.toString());

					Invoker invoker = new DefaultInvoker();
					invoker.setMavenHome(new File(mavenHome));
					invoker.setWorkingDirectory(new File(targetDirectory));
					try
					{
						invoker.execute( request );
					}
					catch (MavenInvocationException e)
					{
						throw new RuntimeException("Error running mvn task.", e);
					}
					LOG.info("Project created.");
					LOG.info("Copying files...");
					copyFiles(workingDirectory, targetDirectory, packageStr.replace(".", "\\"), moduleName, artifactId, typeWAR, resourcesFolder);
					LOG.info("OK");
				}
			}
		} catch (Exception e) 
		{
			throw new RuntimeException("Global exception thrown.", e);
		}
	}

	private static void copyFiles(String workingDirectory, String targetDirectory, String packageStr, String moduleName, String artifactId, boolean typeWAR, String resourcesFolder) 
	{
		LOG.info(">>> Copying web.xml <<<<");
		//from: war\\WEB-INF\\web.xml
		String srcWAR = null;
		if(typeWAR)
		{
			srcWAR = workingDirectory + "\\" + artifactId + "\\war\\WEB-INF\\web.xml";
		} else
		{
			srcWAR = workingDirectory + "\\" + artifactId + "\\war\\web.xml";
		}
		//to: src\\main\\webapp\\WEB-INF\\web.xml
		String destWAR = targetDirectory + "\\" + artifactId + "\\src\\main\\webapp\\WEB-INF\\web.xml";
		
		if(new File(srcWAR).exists())
		{
			try 
			{
				CruxFileUtils.copyFile(new File(srcWAR), new File(destWAR));
			} catch (IOException e) 
			{
				throw new RuntimeException("Error to copy web.xml.", e);
			}
		}
		
		LOG.info(">>> Copying public folder <<<<");
		//from: src\*\public
		String srcPublic = workingDirectory + "\\" + artifactId + "\\" + "src" + "\\" + packageStr + "\\public";
		//to: src\main\resources\*\public
		String destPublic = targetDirectory + "\\" + artifactId + "\\" + "src\\main\\resources" + "\\" + packageStr + "\\public";
		try
		{
			CruxFileUtils.copyDirectoryStructure(new File(srcPublic), new File(destPublic));
		} catch (IOException e) 
		{
			throw new RuntimeException("Error to copy public folder.", e);
		}

		LOG.info(">>> Copying build folder <<<<");
		//from: build\*
		String srcBuild = workingDirectory + "\\" + artifactId + "\\" + "build";
		//to: build\*
		String destBuild = targetDirectory + "\\" + artifactId + "\\" + "build";
		try
		{
			CruxFileUtils.copyDirectoryStructure(new File(srcBuild), new File(destBuild));
		} catch (IOException e) 
		{
			throw new RuntimeException("Error to copy build folder.", e);
		}

		LOG.info(">>> Copying resources folder <<<<");
		//from: src\*\resources\*
		File workingResourcesDir = CruxFileUtils.search(new File(workingDirectory + "\\" + artifactId), resourcesFolder, true);
		if(workingResourcesDir != null)
		{
			//Copy resources
			//to: src\main\resources\*
			try
			{
				String destResources = targetDirectory + "\\" + artifactId + "\\src\\main\\" + resourcesFolder;
				CruxFileUtils.copyDirectory(workingResourcesDir, new File(destResources), new FileFilter() {
					public boolean accept(File pathname) 
					{
						return CruxFileUtils.checkExtensionFile(pathname, RESOURCES_FILES);
					}
				}, true);

				String destJavaResources = targetDirectory + "\\" + artifactId + "\\src\\main\\java\\" + resourcesFolder;
				CruxFileUtils.copyDirectory(workingResourcesDir, new File(destJavaResources), new FileFilter() {
					public boolean accept(File pathname) 
					{
						return CruxFileUtils.checkExtensionFile(pathname, JAVA_FILES);
					}
				}, true);

			} catch (IOException e) 
			{
				throw new RuntimeException("Error to copy resources folder.", e);
			}
		}

		LOG.info(">>> Copying Client and Server folders <<<<");
		//Skip:
		//-> public folder
		//-> *.gwt.xml
		//-> *.module.xml
		//-> Resources: *.jpg, png, gif, ...
		//from: src\*
		String workingJavaSrc = workingDirectory + "\\" + artifactId + "\\" + "src";
		//to: src\main\java\
		String destJavaSrc = targetDirectory + "\\" + artifactId + "\\src\\main\\java";
		try
		{
			CruxFileUtils.copyDirectory(new File(workingJavaSrc), new File(destJavaSrc), new FileFilter() {
				public boolean accept(File pathname) 
				{
					return CruxFileUtils.checkExtensionFile(pathname, JAVA_FILES);
				}
			}, true);
		} catch (IOException e) 
		{
			throw new RuntimeException("Error to copy Client and Server folders.", e);
		}

		LOG.info(">>> Copying *.gwt.xml *.module.xml files <<<<");
		//from: src\*\*.gwt.xml
		String workingGWTFiles = workingDirectory + "\\" + artifactId + "\\" + "src" + "\\";
		//to: src\main\resources\*\*.gwt.xml
		String destGWTFiles = targetDirectory + "\\" + artifactId + "\\src\\main\\resources\\";
		try
		{
			CruxFileUtils.copyDirectory(new File(workingGWTFiles), new File(destGWTFiles), new FileFilter() {
				public boolean accept(File pathname) 
				{
					return CruxFileUtils.checkExtensionFile(pathname, GWT_CRUX_FILES);
				}
			}, true);
		} catch (IOException e) 
		{
			throw new RuntimeException("Error to copy GWT files.", e);
		}

		LOG.info(">>> Copying properties files <<<<");
		//from: src\*.properties
		String workingPropertiesFiles = workingDirectory + "\\" + artifactId + "\\src";
		//to: src\main\resources\*.properties
		String destPropertiesFiles = targetDirectory + "\\" + artifactId + "\\src\\main\\resources\\";
		try
		{
			CruxFileUtils.copyDirectory(new File(workingPropertiesFiles), new File(destPropertiesFiles), new FileFilter() {
				public boolean accept(File pathname) 
				{
					return CruxFileUtils.checkExtensionFile(pathname, ".properties");
				}
			}, true);
		} catch (IOException e) 
		{
			throw new RuntimeException("Error to copy Properties files.", e);
		}

		LOG.info(">>> Copying dependencies at pom file <<<<");
		//from: build\ivy\pom.xml
		String workingPomFile = workingDirectory + "\\" + artifactId + "\\" + "build\\ivy\\pom.xml";
		//to: pom.xml
		String destPomFile = targetDirectory + "\\" + artifactId + "\\" + "pom.xml";
		//---->>> <dependencies>*</dependencies>
		try
		{
			String pomOrigContent = CruxFileUtils.readFileContent(new File(workingPomFile));
			String pomDestContent = CruxFileUtils.readFileContent(new File(destPomFile));
			
			String newDependencies = pomOrigContent.substring(
					pomOrigContent.indexOf("<dependencies>") + "<dependencies>".length(), pomOrigContent.lastIndexOf("</dependencies>"));
			
			StringBuffer newPom = new StringBuffer();
			newPom.append(pomDestContent.substring(0, pomDestContent.indexOf("<dependencies>")));
			newPom.append("<dependencies>");
			newPom.append(newDependencies);
			newPom.append(pomDestContent.substring(pomDestContent.lastIndexOf("<dependencies>") + "</dependencies>".length(), pomDestContent.length()));
			FileWriter writer = new FileWriter(destPomFile);
			writer.write(newPom.toString());
			
			writer.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
