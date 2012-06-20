package org.cruxframework.crux.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * Generates Crux project skeletons
 * @author Gesse S. F. Dafe
 */
public class CruxProjectGenerator
{
	private final LayoutProjectGenerator layoutProjectGenerator;
	
	/**
	 * @param workspaceDir
	 */
	public CruxProjectGenerator(File workspaceDir, String projectName, String hostedModeStartupModule, String projectLayout)
	{
		layoutProjectGenerator = LayoutProjectGeneratorFactory.getLayoutProjectGenerator(projectLayout, workspaceDir, projectName, hostedModeStartupModule);
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
					Properties config = new Properties();
					config.load(CruxProjectGenerator.class.getResourceAsStream("/project.properties"));
					
					String projectName = config.getProperty(Names.projectName);
					String projectLayout = config.getProperty(Names.projectLayout);
					String hostedModeStartupModule = config.getProperty(Names.hostedModeStartupModule);

					CruxProjectGenerator generator = new CruxProjectGenerator(new File(parameters.get("outputDir").getValue()), projectName, hostedModeStartupModule, projectLayout);
					generator.loadGeneratorOptions(config);
					generator.generate();
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
	public void generate() throws Exception
	{
		layoutProjectGenerator.generate();
	}

	/**
	 * @return
	 */
	public CruxProjectGeneratorOptions getLayoutParameters()
	{
		return layoutProjectGenerator.getCruxProjectGeneratorOptions();
	}
	
	/**
	 * @throws IOException
	 */
	private void loadGeneratorOptions(Properties config) throws Exception
	{
		this.layoutProjectGenerator.loadGeneratorOptions(config);
	}
	
	/**
	 * All basic parameter names for CruxProjectGenerator.
	 * @author Gesse S. F. Dafe
	 */
	public static interface Names
	{
		String appDescription = "appDescription";
		String hostedModeStartupModule = "hostedModeStartupModule";
		String hostedModeStartupURL = "hostedModeStartupURL";
		String hostedModeVMArgs = "hostedModeVMArgs";
		String projectLayout = "projectLayout";
		String projectName = "projectName";
	}	
}