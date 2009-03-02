package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import java.net.URL;

import javax.servlet.ServletContext;

import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;


public class ControllerFactoryImpl implements ControllerFactory 
{
	@Override
	public Object getController(String controllerName) 
	{
		try 
		{
			return Controllers.getController(controllerName).newInstance();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating controller "+controllerName+". Cause: "+e.getMessage(), e);
		} 
	}

	@Override
	public void initialize(ServletContext context) 
	{
		boolean lookupWebInfOnly = ("true".equals(ConfigurationFactory.getConfiguration().lookupWebInfOnly()));
		URL[] urls = ScannerURLS.getURLsForSearch(lookupWebInfOnly?context:null);
		Controllers.initialize(urls);
	}
}
