package br.com.sysmap.crux.core.client.utils;

public class EscapeUtils 
{
	public static String simpleQuote(String str, boolean nullToBlank)
    {
        if(str == null) return (nullToBlank ? "''" : null);
        
        int cursor = 0;
        int strLenght = str.length();
        StringBuilder sb = new StringBuilder(strLenght + 16);

        sb.append("'");
        while(cursor < strLenght)
        {
            char nextchar = str.charAt(cursor++);
            if(nextchar == '\\')
            {
                sb.append("\\\\"); // Substitue \ por \\
                continue;
            }
            else if(nextchar == '\'')
            {
                sb.append("\\'"); // Substitue ' por \'
                continue;
            }
            else if(nextchar == '\r')
            {
                if((cursor + 1) < strLenght && str.charAt(cursor+1) == '\n') cursor++;
                sb.append("\\n"); // Substitue quebra de linha por \n
                continue;
            }
            else if(nextchar == '\n')
            {
                sb.append("\\n"); // Substitue quebra de linha por \n
                continue;
            }
            sb.append(nextchar);
        }
        sb.append("'");
        
        return sb.toString();
    }
	
	public static String simpleQuote(String str)
	{
		return simpleQuote(str, true);
	}	
	
	public static String quote(String str, boolean nullToBlank)
    {
        if(str == null) return (nullToBlank ? "\"\"" : null);
        
        int cursor = 0;
        int strLenght = str.length();
        StringBuilder sb = new StringBuilder(strLenght + 16);

        sb.append("\"");
        while(cursor < strLenght)
        {
            char nextchar = str.charAt(cursor++);
            if(nextchar == '\\')
            {
                sb.append("\\\\"); // Substitue \ por \\
                continue;
            }
            else if(nextchar == '"')
            {
                sb.append("\\\""); // Substitue " por \"
                continue;
            }
            else if(nextchar == '\r')
            {
                if((cursor + 1) < strLenght && str.charAt(cursor+1) == '\n') cursor++;
                sb.append("\\n"); // Substitue quebra de linha por \n
                continue;
            }
            else if(nextchar == '\n')
            {
                sb.append("\\n"); // Substitue quebra de linha por \n
                continue;
            }
            sb.append(nextchar);
        }
        sb.append("\"");
        
        return sb.toString();
    }
	
	public static String quote(String str)
	{
		return quote(str, true);
	}		
}
