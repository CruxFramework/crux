package br.com.sysmap.crux.core.server.screen;

import java.io.PrintWriter;

public interface ComponentRenderer 
{
	void renderStart(Component component, PrintWriter writer);
	void renderEnd(Component component, PrintWriter writer);
}
