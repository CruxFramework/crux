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
package br.com.sysmap.crux.tools.quickstart.client;

import br.com.sysmap.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@MessageName("quickMsg")
public interface QuickStartMessages extends Messages 
{
	@DefaultMessage("Generate New Crux Application")
	String generateApp();

	@DefaultMessage("Run Crux Examples")
	String viewExamples();
	
	@DefaultMessage("View Crux User Manual")
	String viewUserManual();
	
	@DefaultMessage("View Crux API Docs")
	String viewJavadoc();
	
	@DefaultMessage("Crux QuickStart")
	String appTitle();
	
	@DefaultMessage("Project Information")
	String projectInfo();

	@DefaultMessage("Project Name:")
	String projectName();

	@DefaultMessage("Startup URL:")
	String hostedModeStartupURL();

	@DefaultMessage("Startup Module:")
	String hostedModeStartupModule();

	@DefaultMessage("DevMode JVM Args:")
	String hostedModeVMArgs();

	@DefaultMessage("Project Layout:")
	String projectLayout();

	@DefaultMessage("Module Description:")
	String cruxModuleDescription();

	@DefaultMessage("Welcome")
	String welcomeQuickstartLabel();

	@DefaultMessage("Welcome to Crux Project Generator. Click ''next'' to continue.")
	String welcomeQuickstartMsg();

	@DefaultMessage("Summary")
	String summaryQuickstartLabel();

	@DefaultMessage("Your project is ready to be generated. Click ''finish'' to generate it now.")
	String summaryQuickstartMsg();

	@DefaultMessage("Project Generated.")
	String generateAppSuccessTitle();

	@DefaultMessage("Your project was successfuly generated.")
	String generateAppSuccessMessage();

	@DefaultMessage("Error generating project.")
	String generateAppFailureTitle();

	@DefaultMessage("Your project could not be generated. See the log on console for more information.")
	String generateAppFailureMessage();
	
	@DefaultMessage("Eclipse Information")
	String ideInfoLabel();
	
	@DefaultMessage("Select the output directory:")
	String workspaceDir();
	
	@DefaultMessage("Select a Crux Application:")
	String examplesMsg();
	
	@DefaultMessage("Fill in the basic information of your project:")
	String projectInfoTitle();
	
	@DefaultMessage("The name of your project. This will be used to generate an Eclipse Project for you. Because some project''s resources will inherit this name, we recomend you to avoid special or blank characters.")
	String projectNameHelpText();
	
	@DefaultMessage("The name of the page you want to be used as the welcome page of your application. We recomend you to use ''.html'' extension. If you choose another file extension, you will have to confuigure the ''web.xml'' file of your generated project, in order to set the ''ModulesDeclarativeUIFilter'' to handle the URLs properly.")
	String startupURLHelpText();
	
	@DefaultMessage("The main GWT module of your application. The freshly generated project will have only a single module, but you are free to create others by yourself.")
	String startupModuleHelpText();
	
	@DefaultMessage("A set of arguments that will passed to the Java Virtual Machine for running the GWT development console (GWT DevMode).")
	String devModeJVMArgsHelpText();
	
	@DefaultMessage("The layout of your project. If you don''t know what this means, please consult this link:\n http://code.google.com/p/crux-framework/wiki/UserManual#6_Project_Layouts, or leave the default option selected.")
	String projectLayoutOptionHelpText();
	
	@DefaultMessage("A brief textual description of your application, which will be used for documentation purposes.")
	String moduleDescriptionHelpText();

	@DefaultMessage("version {0}")
	String cruxVersion(String result);
	
	@DefaultMessage("Generating your project. Please wait...")
	String waitGeneratingProject();
	
	@DefaultMessage("Back to Main Menu")
	String backToMainMenu();
	
	@DefaultMessage("Crux Showcase")
	String openShowcase();
	
	@DefaultMessage("Hello World")
	String openHelloWorld();
}