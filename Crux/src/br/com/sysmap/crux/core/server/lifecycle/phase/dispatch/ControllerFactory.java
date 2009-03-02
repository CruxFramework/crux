package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import javax.servlet.ServletContext;

public interface ControllerFactory 
{
	Object getController(String controllerName);
	void initialize(ServletContext context);
}
