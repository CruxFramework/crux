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
package org.cruxframework.crux.tools.compile.preprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.declarativeui.ViewProcessor;
import org.cruxframework.crux.core.declarativeui.template.TemplatesScanner;
import org.cruxframework.crux.core.declarativeui.view.ViewsScanner;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.cruxframework.crux.tools.compile.CruxPreProcessor;
import org.w3c.dom.Document;


/**
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractDeclarativeUIPreProcessor implements CruxPreProcessor
{
	private static final Log logger = LogFactory.getLog(AbstractDeclarativeUIPreProcessor.class);

	protected boolean keepGeneratedFiles = false;
	protected String pageFileExtension = "html";
	protected boolean indent = false;
	protected String outputCharset;
	protected File outputDir = null;
	
	/**
	 * @see org.cruxframework.crux.tools.compile.CruxPreProcessor#preProcess(java.io.File)
	 */
	@Override
	public URL preProcess(URL url, Module module) throws IOException, InterfaceConfigException
	{
		logger.info("Pre-processing file: " + url.toString());

		File preprocessedFile = null;
		
		if (keepGeneratedFiles)
		{
			File parentDir = null;
			if (url.getProtocol().equals("file"))
			{
				File urlFile;
				try
				{
					urlFile = new File(url.toURI());
				}
				catch (URISyntaxException e)
				{
					throw new InterfaceConfigException(e.getMessage(), e);
				}
				
				if(outputDir == null)
				{
					parentDir = urlFile.getParentFile();
				}
				else
				{
					parentDir = getDestDir(url);
				}
				
				preprocessedFile = getProcessedFile(parentDir, urlFile.getName());
			}	
			else if (outputDir == null)
			{
				preprocessedFile = File.createTempFile(getResourceName(url), pageFileExtension);
			}
			else
			{
				parentDir = getDestDir(url);
				preprocessedFile = getProcessedFile(parentDir, getResourceName(url));
			}
		}	
		else
		{
			preprocessedFile = File.createTempFile(getResourceName(url), pageFileExtension);
		}
		
		ViewProcessor.setForceIndent(indent);
		ViewProcessor.setOutputCharset(outputCharset);
		FileOutputStream out = new FileOutputStream(preprocessedFile);
		URLStreamManager manager = new URLStreamManager(url);
		Document screen = ViewProcessor.getView(manager.open(), null);
		ViewProcessor.generateHTML(url.toString(), screen, out);
		manager.close();
		out.flush();
		out.close();
		
		return preprocessedFile.toURI().toURL();
	}

	/**
	 * @param parentDir
	 * @param fileName
	 * @return
	 */
	private File getProcessedFile(File parentDir, String fileName) {
		File preprocessedFile;
		if(!parentDir.exists())
		{
			parentDir.mkdirs();
		}
			
		int index = fileName.indexOf(".");
		if (index > 0)
		{
			fileName = fileName.substring(0, index) + "."+ pageFileExtension;
		}
		preprocessedFile = new File (parentDir, fileName);
		return preprocessedFile;
	}

	/**
	 * @param urlFile
	 * @return
	 * @throws IOException
	 */
	protected abstract File getDestDir(URL urlFile) throws IOException;
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private String getResourceName(URL url)
	{
		String name = url.toString();
		int index = name.lastIndexOf("/");
		if (index > 0)
		{
			name = name.substring(index+1);
		}
		
		index = name.lastIndexOf(".");
		if (index > 0)
		{
			name = name.substring(0, index);
		}
		
		return name;
	}
	
	public void setKeepGeneratedFiles(boolean keepGeneratedFiles)
	{
		this.keepGeneratedFiles = keepGeneratedFiles;
	}
	
	public void setPageFileExtension(String pageFileExtension)
	{
		this.pageFileExtension = pageFileExtension;
	}

	public void setIndent(boolean indent)
	{
		this.indent = indent;
	}
	
	public void setOutputCharset(String outputCharset)
	{
		this.outputCharset = outputCharset;
	}

	public void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}

	public void initialize(URL[] urls)
	{
		ClassScanner.initialize(urls);
		TemplatesScanner.initialize(urls);
		ViewsScanner.initialize(urls);
	}
}