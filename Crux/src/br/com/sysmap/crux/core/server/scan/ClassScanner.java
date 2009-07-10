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
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();

	private static ScannerDB scannerDB;

	/**
	 * 
	 */
	private ClassScanner()
	{
		
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize(URL[] urls) 
	{
		scannerDB = new ScannerDB();
		scannerDB.setIgnoredPackages(new String[]{"javax", "java", "sun", "com.sun", "org.apache", 
				"javassist", "org.json", "net.sf.saxon", 
				"com.metaparadigm", "junit"});
		scannerDB.setScanFieldAnnotations(false);
		scannerDB.setScanMethodAnnotations(false);
		scannerDB.setScanParameterAnnotations(false);
		scannerDB.setScanClassAnnotations(true);
		buildIndex(urls);
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize() 
	{
		if (!isInitialized())
		{
			lock.lock();
			try
			{
				if (!isInitialized())
				{
					initialize(ScannerURLS.getURLsForSearch());
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	/**
	 * 
	 * @param urls
	 * @throws ClassScannerException
	 */
	private static void buildIndex(URL[] urls) throws ClassScannerException
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
	public static Set<String> searchClassesByAnnotation(Class<? extends Annotation> annotationClass)
	{		
		if (!isInitialized())
		{
			initialize();
		}
		return scannerDB.getAnnotationIndex().get(annotationClass.getName());
	}
	
	/**
	 * Search into the internal index for the set of classes that implements the given interface.
	 * @param annotationClass
	 * @return
	 */
	public static Set<String> searchClassesByInterface(Class<?> interfaceClass)
	{
		if (!isInitialized())
		{
			initialize();
		}
		return scannerDB.getInterfacesIndex().get(interfaceClass.getName());
	}
	
	/**
	 * return true if the scanner was already initialized.
	 * @return
	 */
	public static boolean isInitialized()
	{
		return scannerDB != null;
	}
}
