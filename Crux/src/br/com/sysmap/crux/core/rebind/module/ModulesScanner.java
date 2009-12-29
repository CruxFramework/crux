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
package br.com.sysmap.crux.core.rebind.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.scannotation.archiveiterator.Filter;
import br.com.sysmap.crux.scannotation.archiveiterator.IteratorFactory;
import br.com.sysmap.crux.scannotation.archiveiterator.StreamIterator;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ModulesScanner 
{
	private static final Log logger = LogFactory.getLog(ModulesScanner.class);
	private static final ModulesScanner instance = new ModulesScanner();
	private GeneratorMessages messages = MessagesFactory.getMessages(GeneratorMessages.class);
	private DocumentBuilder documentBuilder;
	private static URL[] urlsForSearch = null;
	private static final Lock lock = new ReentrantLock();
	private String classesDir; 
	
	/**
	 * 
	 */
	private ModulesScanner() 
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
			classesDir = getClassesDir();
		}
		catch (ParserConfigurationException e)
		{
			throw new ModuleException(messages.modulesScannerErrorBuilderCanNotBeCreated(), e);
		}
		catch (Exception e)
		{
			throw new ModuleException(messages.modulesScannerErrorFindingClassesDir(), e);
		}
	}
	
	protected transient String[] ignoredPackages = {"javax", "java", "sun", "com.sun", "org.apache", 
													"net.sf.saxon", "javassist", "org.json", "com.extjs",
													"com.metaparadigm", "junit"};

	/**
	 * 
	 * @return
	 */
	public String[] getIgnoredPackages()
	{
		return ignoredPackages;
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public void setIgnoredPackages(String[] ignoredPackages)
	{
		this.ignoredPackages = ignoredPackages;
	}

	/**
	 * 
	 * @param urls
	 */
	private void scanArchives(URL... urls)
	{
		for (URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String fileName)
				{
					if (fileName.endsWith(".gwt.xml"))
					{
						if (fileName.startsWith("/")) fileName = fileName.substring(1);
						if (!ignoreScan(fileName.replace('/', '.')))
						{
							Document module;
							try
							{
								InputStream inputStream = getClass().getResourceAsStream(fileName);
								if (inputStream != null)
								{
									module = documentBuilder.parse(inputStream);
								}
								else
								{
									inputStream = getClass().getResourceAsStream("/"+fileName);
									if (inputStream != null)
									{
										module = documentBuilder.parse(inputStream);
									}
									else
									{
										module = documentBuilder.parse(new File(fileName));
									}
								}
								Modules.registerModule(getModuleName(fileName), module);
							}
							catch (Exception e)
							{
								logger.error(messages.modulesScannerErrorParsingModuleFile(fileName));
								return false;
							}
							return true;
						}
					}
					return false;
				}
			};

			try
			{
				StreamIterator it = IteratorFactory.create(url, filter);
				while (it.next() != null); // Do nothing, but searches the directories and jars
			}
			catch (IOException e)
			{
				throw new ModuleException(messages.modulesScannerInitializationError(e.getLocalizedMessage()), e);
			}
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
	 * @param fileName
	 * @return
	 */
	private String getModuleName(String fileName)
	{
		fileName = fileName.substring(0, fileName.length() - 8);
		fileName = RegexpPatterns.REGEXP_BACKSLASH.matcher(fileName).replaceAll("/");
		if (fileName.startsWith(classesDir))
		{
			fileName = fileName.substring(classesDir.length());
		}
		
		if (fileName.startsWith("/"))
		{
			fileName = fileName.substring(1);
		}
		
		return fileName.replace('/', '.');
	}
	
	/**
	 * 
	 * @param intf
	 * @return
	 */
	private boolean ignoreScan(String intf)
	{
		for (String ignored : ignoredPackages)
		{
			if (intf.startsWith(ignored + "."))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private String getClassesDir() throws URISyntaxException, IOException
	{
		URL classesPath = ClassPathResolverInitializer.getClassPathResolver().findWebInfClassesPath();
		return classesPath.toString();
/*		File classesDir = new File(classesPath.toURI());
		String canonicalPath = classesDir.getCanonicalPath();
		return RegexpPatterns.REGEXP_BACKSLASH.matcher(canonicalPath).replaceAll("/");
		*/
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
