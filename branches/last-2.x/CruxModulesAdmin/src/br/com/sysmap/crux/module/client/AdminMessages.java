package br.com.sysmap.crux.module.client;

import br.com.sysmap.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

@MessageName("adminMessages")
public interface AdminMessages extends Messages
{
	@DefaultMessage("Module name not informed")
	String moduleInfoModuleNotInformed();
	
	@DefaultMessage("Required Modules")
	String requiredModulesTab();

	@DefaultMessage("Dependent Modules")
	String dependentModulesTab();

	@DefaultMessage("Pages")
	String pagesTab();

	@DefaultMessage("Serializables")
	String serializablesTab();

	@DefaultMessage("Controllers")
	String controllersTab();

	@DefaultMessage("Name")
	String moduleName();

	@DefaultMessage("Min. Version")
	String moduleMinVersion();

	@DefaultMessage("Max. Version")
	String moduleMaxVersion();
	
	@DefaultMessage("Status")
	String moduleStatusVersion();
	
	@DefaultMessage("Pages")
	String pagesLabel();
	
	@DefaultMessage("Controller")
	String controllerLabel();

	@DefaultMessage("Class")
	String classNameLabel();

	@DefaultMessage("Lazy")
	String lazyLabel();

	@DefaultMessage("Fragment")
	String fragmentLabel();
	
	@DefaultMessage("Auto Bind")
	String autoBindLabel();

	@DefaultMessage("Statefull")
	String statefulLabel();
	
	@DefaultMessage("Formatter")
	String formatterLabel();

	@DefaultMessage("Serializable")
	String serializableLabel();

	@DefaultMessage("DataSource")
	String datasourceLabel();

	@DefaultMessage("DataSources")
	String datasourcesTab();

	@DefaultMessage("Formatters")
	String formattersTab();

	@DefaultMessage("Required Modules")
	String requiredModulesLabel();

	@DefaultMessage("Dependent Modules")
	String dependentModulesLabel();

	@DefaultMessage("Config")
	String configPopupTitle();
	
	@DefaultMessage("User")
	String loginScreenUserLabel();

	@DefaultMessage("Password")
	String loginScreenPasswdLabel();
	
	@DefaultMessage("Repository")
	String loginScreenRepositoryLabel();

	@DefaultMessage("Login")
	String loginScreenLoginBtn();

	@DefaultMessage("Authenticating on Repositories")
	String loginAuthenticating();
	
	@DefaultMessage("Select a module on left menu for more information")
	String selectModule();
	
	@DefaultMessage("Modules Admin Console")
	String modulesAdminConsoleTitle();
	
	@DefaultMessage("Module: ")
	String moduleLabel();
	
	@DefaultMessage("Description: ")
	String descriptionLabel();
	
	@DefaultMessage("Please enter user name and password for the source repositories")
	String loginDescription();

	@DefaultMessage("Name")
	String parameterNameLabel();

	@DefaultMessage("Type")
	String parameterTypeLabel();

	@DefaultMessage("Required")
	String parameterRequiredLabel();

	@DefaultMessage("Parameters for page ")
	String parametersPopupTitle();
}
