package br.com.sysmap.crux.module;

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;


public interface ModulesAdminMessages
{
	@DefaultServerMessage("Error registering new repository {0}")
	String svnRepositoriesErrorRegistringRepository(String url);

	@DefaultServerMessage("The URL {0} does not point to a dir resource")
	String svnRepositoriesDirExpected(String url);

	@DefaultServerMessage("Error processing module file {0}")
	String svnScannerErrorProcessingModuleFile(String relativePath);

	@DefaultServerMessage("Error initializing svnScanner")
	String svnScannerErrorBuilderCanNotBeCreated();

	@DefaultServerMessage("Error scanning repositories")
	String svnScannerErrorScanningFiles();

	@DefaultServerMessage("Repository {0} not registered")
	String svnRepositoryNotRegistered(String url);

	@DefaultServerMessage("Error authenticating user {0} on repository {1}")
	String svnRepositoriesErrorAuthenticatingUser(String user, String repository);

	@DefaultServerMessage("Error reading page {0}")
	String moduleInfoErrorRetrievingScreen(String page);
	

}
