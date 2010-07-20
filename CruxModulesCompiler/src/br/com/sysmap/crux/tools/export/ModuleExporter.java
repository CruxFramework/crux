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

import br.com.sysmap.crux.tools.parameters.ConsoleParameter;
import br.com.sysmap.crux.tools.parameters.ConsoleParameterOption;
import br.com.sysmap.crux.tools.parameters.ConsoleParametersProcessor;


/**
 * A tool to export modules
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleExporter
{
	private String moduleName;
	
	public boolean exportModule()
	{
		return true;
	}
	
	
	public static void main(String[] args)
    {
//	    File tempDir = new File (FileUtils.getTempDirFile(), "crux_export"+System.currentTimeMillis());
//	    tempDir.mkdirs();

    }
	
	/**
	 * @return
	 */
	protected ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("ModuleExporter");

		parameter = new ConsoleParameter("outputDir", "The folder where the compiled files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("webDir", "The application web root folder.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("scanAllowedPackages", 
				"A list of packages (separated by commas) that will be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("allowed", "Allowed packages"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter =new ConsoleParameter("scanIgnoredPackages", 
				"A list of packages (separated by commas) that will not be scanned to find Controllers, Modules and Templates", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("ignored", "Ignored packages"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("outputCharset", "Charset used on output files", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("charset", "Output charset"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("pageFileExtension", "Extension of the pages generated", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("fileExtension", "File Extension"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
		/*		
		parameter = new ConsoleParameter("pagesOutputDir", "The folder where the generated page files will be created.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("output", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-indentPages", "If true, the output pages will be indented.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-keepPagesGeneratedFiles", 
				"If false, the output pages will be removed after compilation.", false, true));

		parameter = new ConsoleParameter("-gen", "Specify the folder where the GWT generators will output generated classes.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("genFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-style", "Specify the output style for GWT generated code.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("style", "GWT output Style"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("-extra", "The directory into which extra files, not intended for deployment, will be written.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("extraFolder", "Folder Name"));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-validateOnly", " Validate all source code, but do not compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-compileReport", "Create a compile report that tells the Story of Your Compile.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-draftCompile", "Disable compiler optimizations and run faster.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
*/
	}
	
}
