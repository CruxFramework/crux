package org.cruxframework.crux.tools.compile;

import java.io.IOException;
import java.net.URL;

import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.rebind.module.Module;


public interface CruxPostProcessor
{
	URL postProcess(URL url, Module module) throws IOException, InterfaceConfigException;
	void initialize(URL[] urls);
}
