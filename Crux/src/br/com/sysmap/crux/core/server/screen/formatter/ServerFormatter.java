package br.com.sysmap.crux.core.server.screen.formatter;

import br.com.sysmap.crux.core.client.formatter.InvalidFormatException;

/**
 * 
 * @author thiago
 */
public interface ServerFormatter
{
	String format(Object input);
	Object unformat(String input) throws InvalidFormatException;
	String mask(String input);
}
