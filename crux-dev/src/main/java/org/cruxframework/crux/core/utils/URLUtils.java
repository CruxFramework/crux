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
package org.cruxframework.crux.core.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.cruxframework.crux.scanner.URLStreamManager;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class URLUtils
{
	/**
	 * Returns <code>true</code> if the resource referred by an URL really exists
	 * @param url
	 * @return
	 */
	public static boolean existsResource(URL url)
	{
		boolean result = false;
		
		if (url != null)
		{
			if ("file".equals(url.getProtocol()))
			{
				try
				{
					result = new File(url.toURI()).exists();
				}
				catch (URISyntaxException e)
				{
					result = false;
				}
			}
			else
			{
				URLStreamManager manager = new URLStreamManager(url);
				InputStream stream = manager.open();
				manager.close();
				
				result = stream != null;
			}
		}
		
		return result;
	}
	
	/**
	 * @param urlString
	 * @return
	 */
	public static URL isValidURL(String urlString)
	{
		try 
		{
			return new URL(urlString);
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	public static boolean isIdenticResource(URL view1, URL view2, String resourceId)
	{
		if (!view1.toString().equals(view2.toString()))
		{
			if (view1.getProtocol().equals(view2.getProtocol()) && view1.getProtocol().equals("file"))
			{
				File file1;
				File file2;
				try
                {
	                file1 = new File(view1.toURI());
	                file2 = new File(view2.toURI());
                }
                catch (Exception e)
                {
    				throw new ResourceException("Invalid url for resource ["+resourceId+"].", e);
                }
	                
                if (file1.length() == file2.length())
                {
					String view1Content;
					String view2Content;
    				try
                    {
    					view1Content = FileUtils.read(file1);
    					view2Content = FileUtils.read(file1);
                    }
                    catch (Exception e)
                    {
        				throw new ResourceException("Can not read the resource file. View ID["+resourceId+"].", e);
                    }

                	if (!view1Content.equals(view2Content))
                	{
                		return false;
                	}
                }
                else
                {
            		return false;
                }
			}
			else
			{
        		return false;
			}
		}
		return true;
	}
	
	public static class ResourceException extends RuntimeException
	{
        private static final long serialVersionUID = -2282344306575624565L;

        public ResourceException(String message, Throwable cause)
        {
        	super(message, cause);
        }
	}
}
