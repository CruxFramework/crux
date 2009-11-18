package br.com.sysmap.crux.widgets.client.util;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class StringUtils
{
	/**
	 * @param src
	 * @param length
	 * @param padding
	 * @return
	 */
	public static String lpad(String src, int length, char padding)
	{
		if(src == null)
		{
			src = "";
		}

		while(src.length() < length)
		{
			src = padding + src;
		}
		
		return src;
	}
}
