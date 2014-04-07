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
package org.cruxframework.crux.core.rebind.module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.classpath.PackageFileURLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.server.scan.ScannerURLS;
import org.cruxframework.crux.scannotation.AbstractScanner;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.cruxframework.crux.scannotation.archiveiterator.Filter;
import org.cruxframework.crux.scannotation.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scannotation.archiveiterator.URLIterator;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModulesScanner extends AbstractScanner
{
	private static final Log logger = LogFactory.getLog(ModulesScanner.class);
	private static final ModulesScanner instance = new ModulesScanner();
	private DocumentBuilder documentBuilder;
	private static URL[] urlsForSearch = null;
	private static final Lock lock = new ReentrantLock();
	private String[] classesDir; 
	
	/**
	 * 
	 */
	private ModulesScanner() 
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
			this.documentBuilder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException
				{
					if (systemId.contains("gwt-module.dtd"))
					{
						return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
					}
					else 
					{
						return null;
					}
				}
			});			
		}
		catch (ParserConfigurationException e)
		{
			throw new ModuleException("Error creating modules scanner. Can not create builder object.", e);
		}
		catch (Exception e)
		{
			throw new ModuleException("Can not find the web classes dir.", e);
		}
	}
	
	/**
	 * 
	 */
	public void scanArchives()
	{
		if (urlsForSearch == null)
		{
			initialize(ScannerURLS.getURLsForSearch());
		}
		scanArchives(urlsForSearch);
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
	 * @param urls
	 */
	private void scanArchives(URL... urls)
	{
		for (final URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String fileName)
				{
					if (fileName.endsWith(".gwt.xml"))
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
				if (it == null)
				{
					return;
				}
				URL found = null;
				while ((found = it.next()) != null)
				{
					URLStreamManager urlManager = new URLStreamManager(found);

					try
					{
						InputStream stream = urlManager.open();						
						Document module = documentBuilder.parse(stream);
						Modules.getInstance().registerModule(found, getModuleName(url, found), module);
					}
					catch (Exception e) 
					{
						logger.error("Error parsing module file: ["+found.toString()+"].", e);
					}
					finally
					{
						urlManager.close();
					}
				}
			}
			catch (IOException e)
			{
				throw new ModuleException("Error initializing modulesScanner.", e);
			}
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	private String getModuleName(URL parent, URL resource) throws URISyntaxException, IOException
	{
		String fileName;
		
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(resource.getProtocol());
		if (handler instanceof PackageFileURLResourceHandler)
		{
			PackageFileURLResourceHandler packagehandler = (PackageFileURLResourceHandler)handler;
			fileName = packagehandler.getPackaegResourceName(resource);
			
		}
		else
		{
			fileName = extractResourceFromClassesDir(resource.toString());
			String baseDir = parent.toString();
			if (fileName.startsWith(baseDir))
			{
				fileName = fileName.substring(baseDir.length());
			}
		}
		
		fileName = fileName.substring(0, fileName.length() - 8);
		
		if (fileName.startsWith("/"))
		{
			fileName = fileName.substring(1);
		}
		
		return fileName.replace('/', '.');
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	private String extractResourceFromClassesDir(String fileName) throws URISyntaxException, IOException
	{
		for (String clsDir : getClassesDir())
		{
			if (fileName.startsWith(clsDir))
			{
				return fileName.substring(clsDir.length());
			}
		}
		return fileName;
	}
	
	/**
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public synchronized String[] getClassesDir() throws URISyntaxException, IOException
	{
		if (classesDir == null)
		{
			URL classesPath = ClassPathResolverInitializer.getClassPathResolver().findWebInfClassesPath();
			classesDir = new String[]{classesPath.toString()};
		}
		return classesDir;
	}
	
	/**
	 * @param classesDir
	 */
	public void setClassesDir(String[] classesDir)
	{
		Arrays.sort(classesDir, new Comparator<String>(){
			public int compare(String o1, String o2)
			{
				if (o1==null)
				{
					return (o2==null?0:1);
				}
				if (o2 == null)
				{
					return -1;
				}
				if (o1.length() == o2.length())
				{
					return 0;
				}
				if (o1.length() < o2.length())
				{
					return 1;
				}
				
				return -1;
			}
		});
		this.classesDir = classesDir;
	}

	/**
	 * 
	 * @return
	 */
	public static ModulesScanner getInstance()
	{
		return instance;
	}
}
