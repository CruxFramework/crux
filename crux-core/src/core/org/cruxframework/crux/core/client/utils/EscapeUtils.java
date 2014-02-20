/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.client.utils;

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
