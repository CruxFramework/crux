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
package org.cruxframework.crux.core.declarativeui.template;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scanner.AbstractScanner;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.Scanners;
import org.cruxframework.crux.scanner.Scanners.ScannerCallback;
import org.cruxframework.crux.scanner.URLStreamManager;
import org.cruxframework.crux.scanner.archiveiterator.Filter;
import org.w3c.dom.Document;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplatesScanner extends AbstractScanner
{
	private static final TemplatesScanner instance = new TemplatesScanner();
	private DocumentBuilder documentBuilder;
	private static boolean initialized = false;
	
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
			throw new TemplateException("Error creating XML Parser.", e);
		}
	}

	public static synchronized void initializeScanner()
	{
		if (!initialized)
		{
			Scanners.registerScanner(getInstance());
			initialized = true;
		}
	}
	
	@Override
	public Filter getScannerFilter()
	{
		return new Filter()
		{
			public boolean accepts(String fileName)
			{
				if (fileName.endsWith(".template.xml"))
				{
					return true;
				}
				return false;
			}
		};
	}
	
	@Override
	public ScannerCallback getScannerCallback()
	{
	    return new ScannerCallback(){
			@Override
            public void onFound(List<ScannerMatch> scanResult)
            {
				for (ScannerMatch match : scanResult)
				{
					URL found = match.getMatch();
					
					String urlString = found.toString();
					try
					{
						URLStreamManager manager = new URLStreamManager(found);
						InputStream stream = manager.open();
						Document template = documentBuilder.parse(stream);
						manager.close();
						Templates.registerTemplate(getTemplateId(urlString), template, found);
					}
					catch (TemplateException e)
					{
						throw e;
					}
					catch (Exception e)
					{
						throw new TemplateException("Error parsing template file: ["+urlString+"]", e);
					}
				}
				Templates.setInitialized();
            }
		};
	}
	
	@Override
	public void resetScanner()
	{
		Templates.reset();
	}
	
	/**
	 * 
	 * @param urls
	 */
	public void scanArchives()
	{
		runScanner();
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
