package br.com.sysmap.crux.showcase.server;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.HttpStatus;

import br.com.sysmap.crux.showcase.client.remote.SVNService;

public class SVNServiceImpl implements SVNService{
	
	private static final Log log = LogFactory.getLog(SVNServiceImpl.class);
	
	private String baseXmlURL = "http://crux-framework.googlecode.com/svn/trunk/Crux-Showcase/war/";
	private String baseJavaURL = "http://crux-framework.googlecode.com/svn/trunk/Crux-Showcase/src/br/com/sysmap/crux/showcase/client/controller/";
	
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
		
		GetMethod getMethod = null;
		
		try{
			HttpClient httpClient = new HttpClient();
			getMethod = new GetMethod(url);
			int status = httpClient.executeMethod(getMethod);
			String result = getMethod.getResponseBodyAsString();
			
			if(status != HttpStatus.ORDINAL_404_Not_Found)
			{			
				result = StringUtils.replace(result, "\t", "    ");
				return escapeHtml ? StringEscapeUtils.escapeHtml(result) : result;
			}
			else
			{
				return "\n\n\tThis example does not have an associated " + type;
			}
		}
		catch(Throwable t){
			log.error(t.getMessage(), t);
			return "Error accessing the code repository. Please, try again later.";
		}
		finally{			
			if(getMethod != null){
				getMethod.releaseConnection();
			}
		}		
	}		
}