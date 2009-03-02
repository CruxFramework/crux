package br.com.sysmap.crux.core.server.config;

public interface Crux 
{
	String DEFAULT_CONTROLLER_FACTORY = "br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.ControllerFactoryImpl";
	String DEFAULT_SCREEN_STATE_MANAGER = "br.com.sysmap.crux.core.server.screen.ScreenStateManagerClientImpl";
	
	String controllerFactory();
	String screenStateManager();
	String debug();
	String initializeControllersAtStartup();
	String initializeFormattersAtStartup();
	String lookupWebInfOnly();
	String enableHotDeployForScreens();
	String pagesHome();
	String developmentPublicDir();
}
