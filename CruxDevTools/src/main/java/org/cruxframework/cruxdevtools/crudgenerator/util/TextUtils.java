package org.cruxframework.cruxdevtools.crudgenerator.util;

/**
 * Helper class to handle text values
 * 
 * @author Gesse Dafe
 */
public class TextUtils 
{
	/**
	 * Creates a valid java identifier for the given String
	 * @param name
	 * @param firstUpper
	 * @return
	 */
	public static String toJavaIdentifier(String name, boolean firstUpper) 
	{
		name = camelize(name);
		name = name.replaceAll("[^a-zA-Z0-9]", "");
		
		if(firstUpper)
		{
			name = name.substring(0,1).toUpperCase() + name.substring(1); 
		}
		else
		{
			name = name.substring(0,1).toLowerCase() + name.substring(1);
		}
		
		return name;
	}

	/**
	 * Changes the first character of each word to put them upper case
	 * @param name
	 * @return
	 */
	private static String camelize(String name) 
	{
		StringBuilder identifier = new StringBuilder();
		boolean allUpper = isAllUpper(name);
		
		int length = name.length();
		for(int i = 0; i < length; i++)
		{
			char chr = name.charAt(i);
			if(i > 0)
			{
				if(Character.isLetter(chr))
				{
					char prev = name.charAt(i - 1);
					
					if(!Character.isLetter(prev))
					{
						chr = Character.toUpperCase(chr);
					}
					else if(allUpper)
					{
						chr = Character.toLowerCase(chr);
					}
				}
			}
			
			identifier.append(chr);
		}
		
		return identifier.toString();
	}

	/**
	 * Check if the given name has only upper case characters 
	 * @param name
	 * @return
	 */
	private static boolean isAllUpper(String name) 
	{
		int length = name.length();
		
		for(int i = 0; i < length; i++)
		{
			char chr = name.charAt(i);
			if(Character.isLetter(chr) && Character.isLowerCase(chr))
			{
				return false;
			}
		}
		
		return true;
	}
}
