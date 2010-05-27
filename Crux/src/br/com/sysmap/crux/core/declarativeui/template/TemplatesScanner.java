/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.declarativeui.template;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.declarativeui.DeclarativeUIMessages;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.scannotation.AbstractScanner;
import br.com.sysmap.crux.scannotation.archiveiterator.Filter;
import br.com.sysmap.crux.scannotation.archiveiterator.IteratorFactory;
import br.com.sysmap.crux.scannotation.archiveiterator.URLIterator;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplatesScanner extends AbstractScanner
{
	private static final Log logger = LogFactory.getLog(TemplatesScanner.class);
	private static final TemplatesScanner instance = new TemplatesScanner();
	private DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);
	private DocumentBuilder documentBuilder;
	private static URL[] urlsForSearch = null;
	private static final Lock lock = new ReentrantLock(); 
	
	/**
	 * 
	 */
	private TemplatesScanner() 
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new TemplateException(messages.templatesScannerErrorBuilderCanNotBeCreated(), e);
		}
	}

	/**
	 * 
	 * @param urls
	 */
	private void scanArchives(URL... urls)
	{
		// used to handle duplicated entries on classpath
		Set<String> foundTemplates = new HashSet<String>();
		
		for (final URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String fileName)
				{
					if (fileName.endsWith(".template.xml"))
					{
						if (!ignoreScan(url, fileName))
						{
							return true;
						}
					}
					return false;
				}
			};

			try
			{
				URLIterator it = IteratorFactory.create(url, filter);
				URL found;
				while ((found = it.next()) != null)
				{
					String urlString = found.toString();
					if (!foundTemplates.contains(urlString))
					{
						try
						{
							foundTemplates.add(urlString);
							Document template = documentBuilder.parse(found.openStream());
							Templates.registerTemplate(getTemplateId(urlString), template);
						}
						catch (Exception e)
						{
							logger.error(messages.templatesScannerErrorParsingTemplateFile(urlString), e);
						}
					}
				}
			}
			catch (IOException e)
			{
				throw new TemplateException(messages.templatesScannerInitializationError(e.getLocalizedMessage()), e);
			}
		}
	}

	/**
	 * 
	 */
	public void scanArchives()
	{
		if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableWebRootScannerCache()))
		{
			if (urlsForSearch == null)
			{
				initialize(ScannerURLS.getWebURLsForSearch());
			}
			scanArchives(urlsForSearch);
		}
		else
		{
			scanArchives(ScannerURLS.getWebURLsForSearch());
		}
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize(URL[] urls)
	{
		lock.lock();
		try
		{
			urlsForSearch = urls;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String getTemplateId(String fileName)
	{
		fileName = fileName.substring(0, fileName.length() - 13);
		fileName = RegexpPatterns.REGEXP_BACKSLASH.matcher(fileName).replaceAll("/");
		int indexStartId = fileName.lastIndexOf('/');
		if (indexStartId > 0)
		{
			fileName = fileName.substring(indexStartId+1);
		}
		
		return fileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public static TemplatesScanner getInstance()
	{
		return instance;
	}
	
}
