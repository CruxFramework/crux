package br.com.sysmap.crux.core.client.formatter;

/**
 * 
 * @author thiago
 */
public interface ClientFormatter
{
	String format(Object input);
	Object unformat(String input) throws InvalidFormatException;
	String mask(String input);
}
