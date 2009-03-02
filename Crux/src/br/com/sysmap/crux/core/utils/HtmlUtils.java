package br.com.sysmap.crux.core.utils;

public class HtmlUtils 
{
	public static String filterValue(Object val)
	{
		if(val == null) return "";        
		String value = val.toString();        
		if (value.length() == 0) return "";

		StringBuilder result = null;
		String filtered = null;
		for (int i = 0; i < value.length(); i++)
		{
			filtered = null;
			switch (value.charAt(i))
			{
			case '<':
				filtered = "&lt;";
				break;
			case '>':
				filtered = "&gt;";
				break;
			case '&':
				filtered = "&amp;";
				break;
			case '"':
				filtered = "&quot;";
				break;
			case '\'':
				filtered = "&#39;";
				break;
			}

			if (result == null)
			{
				if (filtered != null)
				{
					result = new StringBuilder(value.length() + 50);
					if (i > 0)
					{
						result.append(value.substring(0, i));
					}
					result.append(filtered);
				}
			}
			else
			{
				if (filtered == null)
				{
					result.append(value.charAt(i));
				}
				else
				{
					result.append(filtered);
				}
			}
		}

		return result == null ? value : result.toString();
	}
}
