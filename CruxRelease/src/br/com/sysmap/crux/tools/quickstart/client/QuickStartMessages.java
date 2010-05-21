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

import br.com.sysmap.crux.core.client.i18n.Name;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@Name("quickMsg")
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

	@DefaultMessage("GWT VM Args:")
	String hostedModeVMArgs();

	@DefaultMessage("Module Project:")
	String useCruxModuleExtension();

	@DefaultMessage("Module Description:")
	String cruxModuleDescription();

	@DefaultMessage("Welcome")
	String welcomeQuickstartLabel();

	@DefaultMessage("Welcome to Crux Project Generator. Choose next to continue.")
	String welcomeQuickstartMsg();

	@DefaultMessage("Summary")
	String summaryQuickstartLabel();

	@DefaultMessage("Your project is ready to be generated. Choose finish to generate it now.")
	String summaryQuickstartMsg();

	@DefaultMessage("Project Generated.")
	String generateAppSuccessTitle();

	@DefaultMessage("Your project was successfuly Generated.")
	String generateAppSuccessMessage();

	@DefaultMessage("Error generating project.")
	String generateAppFailureTitle();

	@DefaultMessage("Your project could not be generated. See the log on console for more information.")
	String generateAppFailureMessage();
	
	@DefaultMessage("Eclipse Information")
	String ideInfoLabel();
	
	@DefaultMessage("Workspace Directory:")
	String workspaceDir();
}
