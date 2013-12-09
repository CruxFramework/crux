package org.cruxframework.crossdeviceshowcase.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crossdeviceshowcase.client.remote.SVNService;

/**
 * @author Gesse
 *
 */
public class SVNServiceImpl implements SVNService
{
	private static final long TOKEN = new Date().getTime(); 
	private static final Log log = LogFactory.getLog(SVNServiceImpl.class);	
	private static final String BASE_XML_URL = "http://crux-framework.googlecode.com/svn/branches/5.0.0/cross-device-showcase/src/main/resources/org/cruxframework/crossdeviceshowcase/public/views/";
	private static final String BASE_MODULE_URL = "http://crux-framework.googlecode.com/svn/branches/5.0.0/cross-device-showcase/src/main/resources/org/cruxframework/crossdeviceshowcase/";
	
	private static Map<String, String> cachedResources = new ConcurrentHashMap<String,String>();
	
	@Override
	public String getJavaFile(String fileName, boolean escapeHtml)
	{		
		return loadSourceCode(BASE_MODULE_URL + fileName, escapeHtml, "java file");
	}

	@Override
	public String getXmlFile(String fileName, boolean escapeHtml)
	{
		return loadSourceCode(BASE_XML_URL + fileName, escapeHtml, "xml page");
	}
	
	private String loadSourceCode(final String url, boolean escapeHtml, String type)
	{
		BufferedReader reader = null;
		
		try
		{
			String result = cachedResources.get(url);
			
			if (result == null)
			{
				URL resourceURL = new URL(url + "?" + TOKEN); 

				reader = new BufferedReader(new InputStreamReader(resourceURL.openStream()));
				String line;

				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) 
				{
					builder.append(line+"\n");
				}
				
				result = builder.toString();
				result = StringUtils.replace(result, "\t", "    ");
				
				if (result.contains("<title>404 Not Found</title>"))
				{
					throw new FileNotFoundException();
					// When application runs in google app engine, URL.openStream does not raise this Exception, 
					// but return an HTML with error message. 
				}
				
				cachedResources.put(url, result);
			}
			
			return escapeHtml ? StringEscapeUtils.escapeHtml(result) : result;
		}
		catch(FileNotFoundException t)
		{
			String result = "\n\n\tThis example does not have an associated " + type;
			cachedResources.put(url, result);
			return result;
		}
		catch(Throwable t)
		{
			log.error(t.getMessage(), t);
			return "Error accessing the code repository. Please, try again later.";
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					log.error(e.getMessage(),e);
				}
			}
		}
	}		
}