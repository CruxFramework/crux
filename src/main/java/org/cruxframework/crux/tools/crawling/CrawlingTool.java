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
package org.cruxframework.crux.tools.crawling;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.crawling.CrawlingException;
import org.cruxframework.crux.core.server.crawling.CrawlingUtils;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.tools.compile.CruxLauncher;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Creates a static version for DHTML pages, to serve to search engines. Follows the specification described here
 * {@link https://developers.google.com/webmasters/ajax-crawling/} 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CrawlingTool extends CruxLauncher
{
	private static final Log logger = LogFactory.getLog(CrawlingTool.class);

	private File outputDir;
	private WebClient webClient;
	private String applicationBaseURL;
	private List<PageInfo> pages = new ArrayList<CrawlingTool.PageInfo>();
	private boolean stopOnErrors = false;
	
	private final int javascriptTime;
	
	/**
	 * Constructor 
	 * @param outputDir
	 * @param javascriptTime
	 * @param applicationBaseURL
	 */
	public CrawlingTool(File outputDir, int javascriptTime, String applicationBaseURL)
    {
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
		{
			throw new CrawlingException("Invalid output directory");
		}
		this.outputDir = outputDir;
		this.applicationBaseURL = applicationBaseURL;
		this.javascriptTime = javascriptTime;
		webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
		webClient.setCssEnabled(true);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

	/**
	 * 
	 * @return
	 */
	public boolean isStopOnErrors()
    {
    	return stopOnErrors;
    }

	/**
	 * 
	 * @param stopOnErrors
	 */
	public void setStopOnErrors(boolean stopOnErrors)
    {
    	this.stopOnErrors = stopOnErrors;
    }

	/**
	 * 
	 * @param page
	 * @param escapedFragment
	 * @param path
	 */
	public void createSnapshot(String page, String escapedFragment)
	{
		try
        {
			String pagePath = CrawlingUtils.getStaticPageFor(page, escapedFragment);
			if (pagePath != null)
			{
				logger.info("Creating snapshot for page ["+page+"], escapedFragment ["+escapedFragment+"]");
				HtmlPage htmlPage = webClient.getPage(CrawlingUtils.rewriteUrl(applicationBaseURL, page, escapedFragment));
				webClient.waitForBackgroundJavaScript(javascriptTime);
				File outputFile = new File(outputDir, pagePath);
				//htmlPage.save(outputFile) creates the whole site structure, with images and CSS files locally. So use asXML instead
				StreamUtils.write(new ByteArrayInputStream(htmlPage.asXml().getBytes("UTF-8")), new FileOutputStream(outputFile), true);
				webClient.closeAllWindows();
			}
        }
        catch (Exception e)
        {
	        throw new CrawlingException("Error generating snapshot for page ["+page+"], with escapedFragment ["+escapedFragment+"].", e);
        }
	}
	
	public void createSnapshots()
	{
		for (PageInfo pageInfo : pages)
        {
	        try
	        {
	        	createSnapshot(pageInfo.page, pageInfo.escapedFragment);
	        }
	        catch (Exception e) 
	        {
	        	logger.error("Error creating snaphot for page ["+pageInfo.page+"], escapedFragment ["+pageInfo.escapedFragment+"]", e);
	        	if (stopOnErrors)
	        	{
	        		break;
	        	}
			}
        }
	}
	
	/**
	 * 
	 * @param page
	 * @param escapedFragment
	 */
	public void addSnaphot(String page, String escapedFragment)
	{
		pages.add(new PageInfo(page, escapedFragment));
	}
	
	/**
	 * 
	 * @param urls
	 * @throws IOException
	 */
	public void loadUrls(File urls) throws IOException
    {
		BufferedReader reader = new BufferedReader(new FileReader(urls));
		
		String line = reader.readLine();
		
		while (line != null)
		{
			loadURL(line);
			line = reader.readLine();
		}
		reader.close();
    }

	protected void loadURL(String line)
    {
	   String[] parts = line.split(":");
	   if (parts != null && parts.length == 2)
	   {
		   addSnaphot(parts[0], parts[1]);
	   }
    }

	/**
	 * 
	 * @return
	 */
	protected static ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParameter parameter;
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("CrawlingTool");

		parameter = new ConsoleParameter("outputDir", "The folder where the snapshots will be created.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("dirName", "Folder name"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("applicationBaseURL", "Web application base URL.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("baseURL", "web application root URL˛"));
		parametersProcessor.addSupportedParameter(parameter);
		
		parameter = new ConsoleParameter("javascriptTime", "Time to wait for page rendering before takes the snapshot.", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("time", "Time in miliseconds"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("urls", "A file containing the application urls for snapshot generation.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("fileName", "File name"));
		parametersProcessor.addSupportedParameter(parameter);

		parameter = new ConsoleParameter("stopOnErrors", "Inform if the process must stop if an error occurs.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("stop", "True if the process must stop."));
		parametersProcessor.addSupportedParameter(parameter);

		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;	
	}
	
	public static void main(String[] args)
	{
		try
		{
			ConsoleParametersProcessor parametersProcessor = CrawlingTool.createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);

			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
			}
			else
			{
				File outputDir = new File (parameters.get("outputDir").getValue());
				int javascriptTime = Integer.parseInt(parameters.get("javascriptTime").getValue());
				String applicationBaseURL = parameters.get("applicationBaseURL").getValue();
				File urls = new File (parameters.get("urls").getValue());
				boolean stopOnErrors = Boolean.parseBoolean(parameters.get("stopOnErrors").getValue());
				
				CrawlingTool crawlingTool = new CrawlingTool(outputDir, javascriptTime, applicationBaseURL);
				crawlingTool.setStopOnErrors(stopOnErrors);
				crawlingTool.loadUrls(urls);
				crawlingTool.createSnapshots();
			}
			System.exit(0);
		}
		catch (ConsoleParametersProcessingException e)
		{
			logger.error("Error processing program parameters: "+e.getLocalizedMessage()+". Program aborted.", e);
		}
		catch (CrawlingException e)
		{
			logger.error("Error generating files: "+e.getLocalizedMessage()+". Program aborted.", e);
		}
        catch (IOException e)
        {
			logger.error("Error loading urls from file. Program aborted", e);
        }
		System.exit(1);
	}	
	
	private static class PageInfo
	{
		private String page;
		private String escapedFragment;
		
		public PageInfo(String page, String escapedFragment)
        {
			this.page = page;
			this.escapedFragment = escapedFragment;
        }
	}
}
