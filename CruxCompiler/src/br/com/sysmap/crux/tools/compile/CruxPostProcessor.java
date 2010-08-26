package br.com.sysmap.crux.tools.compile;

import java.io.IOException;
import java.net.URL;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.scanner.module.Module;

public interface CruxPostProcessor
{
	URL postProcess(URL url, Module module) throws IOException, InterfaceConfigException;
	void initialize(URL[] urls);
}
