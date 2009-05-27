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
package br.com.sysmap.crux.core.server.scan;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public class ClassScanner
{
	private static final Log logger = LogFactory.getLog(ClassScanner.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();

	private ScannerDB scannerDB;
	private static ClassScanner instance = null;
	
	private ClassScanner(URL[] urls) 
	{
		scannerDB = new ScannerDB();
		scannerDB.setIgnoredPackages(new String[]{"javax", "java", "sun", "com.sun", "org.apache", 
												  "javassist", "br.com.sysmap.crux.core", 
												  "org.json", "com.metaparadigm", "junit"});
		scannerDB.setScanFieldAnnotations(false);
		scannerDB.setScanMethodAnnotations(false);
		scannerDB.setScanParameterAnnotations(false);
		scannerDB.setScanClassAnnotations(true);
		buildIndex(urls);
	}
	
	
	private void buildIndex(URL[] urls) throws ClassScannerException
	{
		try
		{
			if (logger.isInfoEnabled())logger.info(messages.annotationScannerBuildIndex());

			scannerDB.scanArchives(urls);
		}
		catch (IOException e)
		{
			throw new ClassScannerException(messages.annotationScannerBuildIndexError(e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * Search into the internal index for the set of classes that contains the given annotation.
	 * @param annotationClass
	 * @return
	 */
	public Set<String> searchClassesByAnnotation(Class<? extends Annotation> annotationClass)
	{		
		return scannerDB.getAnnotationIndex().get(annotationClass.getName());
	}
	
	/**
	 * Search into the internal index for the set of classes that implements the given interface.
	 * @param annotationClass
	 * @return
	 */
	public Set<String> searchClassesByInterface(Class<?> interfaceClass)
	{
		return scannerDB.getInterfacesIndex().get(interfaceClass.getName());
	}
	
	/**
	 * Get a singleton instance of Annotation scanner.
	 * @return
	 */
	public static ClassScanner getInstance(URL[] urls)
	{
		if (instance == null)
		{
			lock.lock();
			try
			{
				if (instance == null)
				{
					instance = new ClassScanner(urls);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		return instance;
	}
}
