package br.com.sysmap.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessingException;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;
import br.com.sysmap.crux.tools.schema.SchemaGenerator;

/**
 * Generates Crux project skeletons
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CruxProjectGenerator
{
	private Properties config = new Properties();
	
	private File workspaceDir;
	private File libDir;
	private String projectName;
	private String hostedModeStartupModule;
	private String hostedModeStartupURL;
	private File projectDir;
	private List<String[]> replacements;
	private String moduleSimpleName;
	private String modulePackage;
	private boolean useCruxModuleExtension = false;
	
	/**
	 * @param workspaceDir
	 */
	public CruxProjectGenerator(File workspaceDir)
	{
		this.workspaceDir = workspaceDir;
	}

	/**
	 * @throws IOException
	 */
	private void init() throws Exception
	{
		config.load(this.getClass().getResourceAsStream("/project.properties"));
		
		this.projectName = config.getProperty(Names.projectName);
		this.hostedModeStartupModule = config.getProperty(Names.hostedModeStartupModule);
		this.hostedModeStartupURL = config.getProperty(Names.hostedModeStartupURL);
		this.projectDir = createProjectDir();
		this.moduleSimpleName = getModuleSimpleName();
		this.modulePackage = getModulePackage();
		this.libDir = getLibDir();
		this.useCruxModuleExtension = Boolean.parseBoolean(config.getProperty(Names.useCruxModuleExtension));
	}
	
	/**
	 * @return
	 */
	private String getModuleSimpleName()
	{
		int lastDot = this.hostedModeStartupModule.lastIndexOf(".");
		
		if(lastDot >= 0)
		{
			return this.hostedModeStartupModule.substring(lastDot + 1);
		}
		
		return this.hostedModeStartupModule;
	}
	
	/**
	 * @return
	 */
	public String getModulePackage()
	{
		int lastDot = this.hostedModeStartupModule.lastIndexOf(".");
		return this.hostedModeStartupModule.substring(0, lastDot);
	}

	/**
	 * @return
	 * @throws Exception 
	 */
	private File getLibDir() throws Exception
	{
		String resourceFromClasspathRoot = "/project.properties";
		URL url = this.getClass().getResource(resourceFromClasspathRoot);
		File rootDir = new File(url.toURI()).getParentFile();
		return new File(rootDir, "lib");
	}

	/**
	 * @throws IOException
	 */
	public void generate() throws Exception
	{
		init();
		
		createProjectRootFiles();
		createSources();
		createdBuildFiles();
		createWebRootFiles();
		createClasspathFile();
		createXSDs();
	}

	private void createXSDs() throws IOException
	{
		SchemaGenerator.generateSchemas(projectDir, "xsd", useCruxModuleExtension);
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void createClasspathFile() throws IOException
	{
		StringBuilder libs = new StringBuilder();
		
		for (String jar : listJars(getWebInfLibDir()))
		{
			libs.append("<classpathentry kind=\"lib\" path=\"war/WEB-INF/lib/" + jar + "\"/>\n\t");
		}
		
		for (String jar : listJars(getBuildLibDir()))
		{
			libs.append("<classpathentry kind=\"lib\" path=\"build/lib/" + jar + "\"/>\n\t");
		}
		
		getReplacements().add(new String[]{"classpathLibs", libs.toString()});
		
		createFile(projectDir, ".classpath", "classpath.xml");
	}

	/**
	 * @param buildLibDir
	 * @return
	 */
	private List<String> listJars(File dir)
	{
		List<String> jars = new ArrayList<String>();
		
		File[] files = dir.listFiles();
		for (File file : files)
		{
			String fileName = file.getName();
			if(fileName.endsWith(".jar"))
			{
				jars.add(fileName);
			}
		}
		
		return jars;
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void createSources() throws IOException
	{
		File sourceDir = createDir(projectDir, "src");
		
		String packageDir = this.modulePackage.replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);
		
		File clientPackage = createDir(moduleDir, "client");
		File clientRemotePackage = createDir(clientPackage, "remote");
		File clientControllerPackage = createDir(clientPackage, "controller");
		File serverPackage = createDir(moduleDir, "server");
		
		createFile(clientRemotePackage, "GreetingService.java", "GreetingService.java.txt");
		createFile(clientRemotePackage, "GreetingServiceAsync.java", "GreetingServiceAsync.java.txt");
		createFile(clientControllerPackage, "MyController.java", "MyController.java.txt");
		createFile(serverPackage, "GreetingServiceImpl.java", "GreetingServiceImpl.java.txt");

		if (this.useCruxModuleExtension)
		{
			createFile(sourceDir, "Crux.properties", "modules/crux.properties.txt");
			createFile(sourceDir, "CruxModuleConfig.properties", "modules/cruxModuleConfig.properties.txt");
			createFile(moduleDir, this.moduleSimpleName + ".gwt.xml", "modules/module.xml");
			createFile(moduleDir, this.moduleSimpleName + ".module.xml", "modules/ModuleInfo.module.xml");
		}
		else
		{
			createFile(moduleDir, this.moduleSimpleName + ".gwt.xml", "module.xml");
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private File createProjectDir() throws IOException
	{
		File projectDir = new File(workspaceDir, projectName);
		
		if(projectDir.exists())
		{
			FileUtils.recursiveDelete(projectDir);
		}
		
		boolean created = projectDir.mkdirs();
		if(!projectDir.exists() && !created)
		{
			throw new IOException("Could not create " + projectDir.getCanonicalPath());
		}
		return projectDir;
	}

	/**
	 * @throws IOException
	 */
	private void createWebRootFiles() throws IOException
	{
		FileUtils.copyFilesFromDir(new File(libDir, "web-inf"), getWebInfLibDir());
		String pageName = this.hostedModeStartupURL;
		if (pageName == null || pageName.length() == 0)
		{
			pageName = "index.crux.xml";
		}
		else if (pageName.endsWith(".html"))
		{
			pageName = pageName.substring(0, pageName.length()-5) + ".crux.xml";
		}
		if (this.useCruxModuleExtension)
		{
			FileUtils.copyFilesFromDir(new File(libDir, "modules/web-inf"), getWebInfLibDir());
			createFile(getWebInfLibDir().getParentFile(), "web.xml", "modules/web.xml");
			createFile(getModulePublicDir(), pageName, "modules/index.crux.xml");		
		}
		else
		{
			createFile(getWebInfLibDir().getParentFile(), "web.xml", "web.xml");
			createFile(getWarDir(), pageName, "index.crux.xml");		
		}
	}

	/**
	 * @return
	 */
	private File getModulePublicDir()
	{
		String packageDir = this.modulePackage.replaceAll("\\.", "/");
		File moduleDir = new File(projectDir, "src/"+packageDir);
		
		return createDir(moduleDir, "public");
	}

	/**
	 * @return
	 */
	private File getWarDir()
	{
		return createDir(projectDir, "war");
	}

	/**
	 * @return
	 */
	private File getWebInfLibDir()
	{
		return createDir(projectDir, "war/WEB-INF/lib");
	}
	
	/**
	 * @return
	 */
	private File getBuildLibDir()
	{
		return createDir(projectDir, "build/lib");
	}	

	/**
	 * @throws IOException
	 */
	private void createdBuildFiles() throws IOException
	{
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(libDir, "build"), buildLibDir);
		if (this.useCruxModuleExtension)
		{
			FileUtils.copyFilesFromDir(new File(libDir, "modules/build"), buildLibDir);
			createFile(buildLibDir.getParentFile(), "build.xml", "modules/build.xml");
		}
		else
		{
			createFile(buildLibDir.getParentFile(), "build.xml", "build.xml");
		}
	}

	/**
	 * @param parentDir
	 * @param dirName
	 * @return
	 */
	private File createDir(File parentDir, String dirName)
	{
		File dir = new File(parentDir, dirName + "/");
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * @throws IOException
	 */
	private void createProjectRootFiles() throws IOException
	{
		if (this.useCruxModuleExtension)
		{
			createFile(projectDir, projectName + ".launch", "modules/launch.xml");
		}
		else
		{
			createFile(projectDir, projectName + ".launch", "launch.xml");
		}
		createFile(projectDir, ".project", "project.xml");		
	}

	/**
	 * @param rootDir
	 * @param fileName
	 * @param templateName
	 * @param replacements
	 * @throws IOException
	 */
	private void createFile(File rootDir, String fileName, String templateName) throws IOException
	{
		String templateContent = getTemplateFile(templateName);
		File file = new File(rootDir, fileName);
		file.createNewFile();
		templateContent = replaceParameters(templateContent, getReplacements());
		FileUtils.write(templateContent, file);
	}

	/**
	 * @param text
	 * @param replacements
	 * @return
	 */
	private String replaceParameters(String text, List<String[]> replacements)
	{
		for (String[] replacement : replacements)
		{
			String from = "${" + replacement[0] + "}";
			String to = replacement[1];
			text = StringUtils.replace(text, from, to);
		}
		
		return text;
	}

	/**
	 * @param templateName
	 * @return
	 * @throws IOException 
	 */
	private String getTemplateFile(String templateName) throws IOException
	{
		InputStream in = this.getClass().getResourceAsStream("/templates/" + templateName);
		return FileUtils.read(in);
	}

	/**
	 * @return
	 */
	public List<String[]> getReplacements()
	{
		if(this.replacements == null)
		{
			this.replacements = new ArrayList<String[]>();
			for(Entry<?, ?> entry : config.entrySet())
			{
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				String[] replacement = new String[]{key, value};
				
				this.replacements.add(replacement);
			}
			
			this.replacements.add(new String[]{"moduleSimpleNameUpperCase", this.moduleSimpleName});
			this.replacements.add(new String[]{"moduleSimpleName", this.moduleSimpleName.toLowerCase()});
			this.replacements.add(new String[]{"modulePackage", this.modulePackage});
		}
		
		return this.replacements;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			try
			{
				ConsoleParametersProcessor parametersProcessor = createParametersProcessor();
				Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

				if (parameters.containsKey("-help") || parameters.containsKey("-h"))
				{
					parametersProcessor.showsUsageScreen();
				}
				else
				{
					new CruxProjectGenerator(new File(parameters.get("outputDir").getValue())).generate();
				}
			}
			catch (ConsoleParametersProcessingException e)
			{
				System.out.println("Program aborted");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private static ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("projectGenerator");
		parametersProcessor.addSupportedParameter(new ConsoleParameter("outputDir", "The folder where the files will be created."));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;
	}
}

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
interface Names
{
	String projectName = "projectName";
	String hostedModeStartupURL = "hostedModeStartupURL";
	String hostedModeStartupModule = "hostedModeStartupModule";
	String hostedModeVMArgs = "hostedModeVMArgs";
	String useCruxModuleExtension = "useCruxModuleExtension";
}