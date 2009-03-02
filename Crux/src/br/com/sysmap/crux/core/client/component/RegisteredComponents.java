package br.com.sysmap.crux.core.client.component;




/**
 * Interface for keep the registered components
 * @author Thiago
 *
 */
public interface RegisteredComponents 
{
	Component createComponent(String id, String componentName) throws InterfaceConfigException;
}
