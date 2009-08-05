package br.com.sysmap.crux.showcase.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.showcase.client.remote.SVNService;

public class SVNServiceImpl implements SVNService{
	
	private static final Log log = LogFactory.getLog(SVNServiceImpl.class);
	
	private String baseXmlURL = "http://crux-framework.googlecode.com/svn/trunk/Crux-Showcase/war/";
	private String baseJavaURL = "http://crux-framework.googlecode.com/svn/trunk/Crux-Showcase/src/br/com/sysmap/crux/showcase/client/controller/";
	
	private static Map<String, String> cachedResources = new ConcurrentHashMap<String,String>();
	
	/* 
	 * @see br.com.sysmap.crux.showcase.client.remote.SVNService#getJavaFile(java.lang.String, boolean)
	 */
	public String getJavaFile(String fileName, boolean escapeHtml){		
		return loadSourceCode(baseJavaURL + fileName, escapeHtml, "java controller");
	}
	
	/* 
	 * @see br.com.sysmap.crux.showcase.client.remote.SVNService#getXmlFile(java.lang.String, boolean)
	 */
	public String getXmlFile(String fileName, boolean escapeHtml){
		return loadSourceCode(baseXmlURL + fileName, escapeHtml, "xml page");
	}
	
	private String loadSourceCode(final String url, boolean escapeHtml, String type){
		
		BufferedReader reader = null;
		try{
			String result = cachedResources.get(url);
			
			if (result == null)
			{
				URL resourceURL = new URL(url); 

				reader = new BufferedReader(new InputStreamReader(resourceURL.openStream()));
				String line;

				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					builder.append(line+"\n");
				}
				result = builder.toString();
				result = StringUtils.replace(result, "\t", "    ");
				
				cachedResources.put(url, result);
			}
			return escapeHtml ? StringEscapeUtils.escapeHtml(result) : result;
		}
		catch(FileNotFoundException t){
			String result = "\n\n\tThis example does not have an associated " + type;
			cachedResources.put(url, result);
			return result;
		}
		catch(Throwable t){
			log.error(t.getMessage(), t);
			return "Error accessing the code repository. Please, try again later.";
		}
		finally{
			if (reader != null){
				try{
					reader.close();
				}
				catch (IOException e){
					log.error(e.getMessage(),e);
				}
			}
		}
	}		
}