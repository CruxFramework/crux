package br.com.sysmap.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.utils.FileUtils;
import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessingException;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;
import br.com.sysmap.crux.tools.projectgen.CruxProjectGeneratorOptions.ProjectLayout;
import br.com.sysmap.crux.tools.schema.SchemaGenerator;

/**
 * Generates Crux project skeletons
 * @author Gessé S. F. Dafé
 */
public class CruxProjectGenerator
{
	private CruxProjectGeneratorOptions options;
	private List<String[]> replacements;
	
	
	/**
	 * @param workspaceDir
	 */
	public CruxProjectGenerator(CruxProjectGeneratorOptions options)
	{
		this.options = options;
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
					CruxProjectGeneratorOptions options = loadGeneratorOptions(new File(parameters.get("outputDir").getValue()));
					new CruxProjectGenerator(options).generate();
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

	/**
	 * @throws IOException
	 */
	private static CruxProjectGeneratorOptions loadGeneratorOptions(File workspaceDir) throws Exception
	{
		Properties config = new Properties();
		config.load(CruxProjectGenerator.class.getResourceAsStream("/project.properties"));
		
		String projectName = config.getProperty(Names.projectName);
		String hostedModeStartupModule = config.getProperty(Names.hostedModeStartupModule);
		String hostedModeStartupURL = config.getProperty(Names.hostedModeStartupURL);
		ProjectLayout projectLayout = ProjectLayout.valueOf(config.getProperty(Names.projectLayout));
		String hostedModeVMArgs = config.getProperty(Names.hostedModeVMArgs);
		String appDescription = config.getProperty(Names.appDescription);
		
		return new CruxProjectGeneratorOptions(workspaceDir, projectName, hostedModeStartupModule, hostedModeStartupURL, 
											   hostedModeVMArgs, projectLayout, appDescription);
	}

	/**
	 * @throws IOException
	 */
	public void generate() throws Exception
	{
		createProjectRootFiles();
		createSources();
		createdBuildFiles();
		createWebRootFiles();
		createClasspathFile();
		createXSDs();
	}

	/**
	 * @return
	 */
	public List<String[]> getReplacements()
	{
		if(this.replacements == null)
		{
			this.replacements = new ArrayList<String[]>();

			this.replacements.add(new String[]{"projectName", this.options.getProjectName()});
			this.replacements.add(new String[]{"hostedModeStartupURL", this.options.getHostedModeStartupURL()});
			this.replacements.add(new String[]{"hostedModeStartupModule", this.options.getHostedModeStartupModule()});
			this.replacements.add(new String[]{"hostedModeVMArgs", this.options.getHostedModeVMArgs()});
			this.replacements.add(new String[]{"projectLayout", this.options.getProjectLayout().toString()});
			this.replacements.add(new String[]{"appDescription", this.options.getAppDescription()});
			
			this.replacements.add(new String[]{"moduleSimpleNameUpperCase", this.options.getModuleSimpleName()});
			this.replacements.add(new String[]{"moduleSimpleName", this.options.getModuleSimpleName().toLowerCase()});
			this.replacements.add(new String[]{"modulePackage", this.options.getModulePackage()});
		}
		
		return this.replacements;
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
		
		createFile(options.getProjectDir(), ".classpath", "classpath.xml");
	}

	/**
	 * @throws IOException
	 */
	private void createdBuildFiles() throws IOException
	{
		File buildLibDir = getBuildLibDir();
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "build"), buildLibDir);
		
		if (this.options.getProjectLayout().equals(ProjectLayout.MODULE_APP))
		{
			FileUtils.copyFilesFromDir(new File(options.getLibDir(), "modules/build"), buildLibDir);
			createFile(buildLibDir.getParentFile(), "build.xml", "modules/build.xml");
		}
		else if (this.options.getProjectLayout().equals(ProjectLayout.MODULE_CONTAINER_APP))
		{
			FileUtils.copyFilesFromDir(new File(options.getLibDir(), "modules/build"), buildLibDir);
			createFile(buildLibDir.getParentFile(), "build.xml", "modules-container/build.xml");
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
	 * @throws IOException
	 */
	private void createProjectRootFiles() throws IOException
	{
		if (!this.options.getProjectLayout().equals(ProjectLayout.STANDALONE_APP))
		{
			createFile(options.getProjectDir(), options.getProjectName() + ".launch", "modules/launch.xml");
		}
		else
		{
			createFile(options.getProjectDir(), options.getProjectName() + ".launch", "launch.xml");
		}
		createFile(options.getProjectDir(), ".project", "project.xml");		
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void createSources() throws IOException
	{
		File sourceDir = createDir(options.getProjectDir(), "src");
		
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = createDir(sourceDir, packageDir);
		
		File clientPackage = createDir(moduleDir, "client");
		File clientRemotePackage = createDir(clientPackage, "remote");
		File clientControllerPackage = createDir(clientPackage, "controller");
		File serverPackage = createDir(moduleDir, "server");
		
		createFile(clientRemotePackage, "GreetingService.java", "GreetingService.java.txt");
		createFile(clientRemotePackage, "GreetingServiceAsync.java", "GreetingServiceAsync.java.txt");
		createFile(clientControllerPackage, "MyController.java", "MyController.java.txt");
		createFile(serverPackage, "GreetingServiceImpl.java", "GreetingServiceImpl.java.txt");

		if (!this.options.getProjectLayout().equals(ProjectLayout.STANDALONE_APP))
		{
			createFile(sourceDir, "Crux.properties", "modules/crux.properties.txt");
			createFile(sourceDir, "CruxModuleConfig.properties", "modules/cruxModuleConfig.properties.txt");
			createFile(moduleDir, this.options.getModuleSimpleName() + ".gwt.xml", "modules/module.xml");
			createFile(moduleDir, this.options.getModuleSimpleName() + ".module.xml", "modules/ModuleInfo.module.xml");
		}
		else
		{
			createFile(moduleDir, this.options.getModuleSimpleName() + ".gwt.xml", "module.xml");
		}
	}	

	/**
	 * @throws IOException
	 */
	private void createWebRootFiles() throws IOException
	{
		FileUtils.copyFilesFromDir(new File(options.getLibDir(), "web-inf"), getWebInfLibDir());
		String pageName = this.options.getHostedModeStartupURL();
		if (pageName == null || pageName.length() == 0)
		{
			pageName = "index.crux.xml";
		}
		else if (pageName.endsWith(".html"))
		{
			pageName = pageName.substring(0, pageName.length()-5) + ".crux.xml";
		}
		
		if (!this.options.getProjectLayout().equals(ProjectLayout.STANDALONE_APP))
		{
			FileUtils.copyFilesFromDir(new File(options.getLibDir(), "modules/web-inf"), getWebInfLibDir());
			createFile(getWebInfLibDir().getParentFile(), "web.xml", "modules/web.xml");
			
			if(this.options.getProjectLayout().equals(ProjectLayout.MODULE_APP))
			{
				createFile(getModulePublicDir(), pageName, "modules/index.crux.xml");
			}
			else
			{
				createFile(getWarDir(), pageName, "modules/index.crux.xml");
			}
		}
		else
		{
			createFile(getWebInfLibDir().getParentFile(), "web.xml", "web.xml");
			createFile(getWarDir(), pageName, "index.crux.xml");		
		}
	}

	private void createXSDs() 
	{
		boolean generateModuleSchema = !options.getProjectLayout().equals(ProjectLayout.STANDALONE_APP);
		SchemaGenerator.generateSchemas(options.getProjectDir(), new File(options.getProjectDir(),"xsd"), null, generateModuleSchema);
	}
	
	/**
	 * @return
	 */
	private File getBuildLibDir()
	{
		return createDir(options.getProjectDir(), "build/lib");
	}

	/**
	 * @return
	 */
	private File getModulePublicDir()
	{
		String packageDir = this.options.getModulePackage().replaceAll("\\.", "/");
		File moduleDir = new File(options.getProjectDir(), "src/"+packageDir);
		
		return createDir(moduleDir, "public");
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
	private File getWarDir()
	{
		return createDir(options.getProjectDir(), "war");
	}

	/**
	 * @return
	 */
	private File getWebInfLibDir()
	{
		return createDir(options.getProjectDir(), "war/WEB-INF/lib");
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
	 * All possible parameter names for CruxProjectGenerator.
	 * @author Gessé S. F. Dafé
	 */
	public static interface Names
	{
		String appDescription = "appDescription";
		String hostedModeStartupModule = "hostedModeStartupModule";
		String hostedModeStartupURL = "hostedModeStartupURL";
		String hostedModeVMArgs = "hostedModeVMArgs";
		String projectName = "projectName";
		String projectLayout = "projectLayout";
	}	
}