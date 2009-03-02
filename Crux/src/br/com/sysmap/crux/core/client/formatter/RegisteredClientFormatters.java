package br.com.sysmap.crux.core.client.formatter;

/**
 * 
 * @author thiago
 */
public interface RegisteredClientFormatters 
{
	ClientFormatter getClientFormatter(String id);
}
