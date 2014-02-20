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
package org.cruxframework.crux.core.server.scan;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ClassScanner
{
	private static final Log logger = LogFactory.getLog(ClassScanner.class);
	private static final Lock lock = new ReentrantLock();

	private static ScannerDB scannerDB = new ScannerDB();
	private static boolean initialized = false;

	/**
	 * 
	 */
	private ClassScanner()
	{
		
	}
	
	/**
	 * 
	 */
	public static void initialize() 
	{
		initialize(ScannerURLS.getURLsForSearch());
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize(URL[] urls) 
	{
		if (!isInitialized())
		{
			lock.lock();
			try
			{
				if (!isInitialized())
				{
					initializeAllowedPackages();
					scannerDB.setScanFieldAnnotations(false);
					scannerDB.setScanMethodAnnotations(false);
					scannerDB.setScanParameterAnnotations(false);
					scannerDB.setScanClassAnnotations(true);
					buildIndex(urls);
					initialized = true;
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
	 */
	private static void initializeAllowedPackages()
	{
		scannerDB.addIgnoredPackage("org.cruxframework.crux.core.rebind.screen.wrapper");
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getIgnoredPackages()
	{
		return scannerDB.getIgnoredPackages();
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setIgnoredPackages(String[] ignoredPackages)
	{
		scannerDB.setIgnoredPackages(ignoredPackages);
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static void addIgnoredPackage(String ignoredPackage)
	{
		scannerDB.addIgnoredPackage(ignoredPackage);
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getAllowedPackages()
	{
		return scannerDB.getAllowedPackages();
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setAllowedPackages(String[] allowedPackages)
	{
		scannerDB.setAllowedPackages(allowedPackages);
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static void addAllowedPackage(String allowedPackage)
	{
		scannerDB.addAllowedPackage(allowedPackage);
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getRequiredPackages()
	{
		return scannerDB.getRequiredPackages();
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setRequiredPackages(String[] requiredPackages)
	{
		scannerDB.setRequiredPackages(requiredPackages);
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static void addRequiredPackage(String requiredPackage)
	{
		scannerDB.addRequiredPackage(requiredPackage);
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
			if (logger.isInfoEnabled())
			{
				logger.info("Building index of annotations for classes.");
			}

			scannerDB.scanArchives(urls);
		}
		catch (IOException e)
		{
			throw new ClassScannerException("Error creating index of annotations.", e);
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
		if (!interfaceClass.isInterface())
		{
			throw new ClassScannerException("The class ["+interfaceClass.getName()+"] is not an interface.");
		}
		return searchClassesByInterface(interfaceClass.getName(), true);
	}

	/**
	 * 
	 * @param className
	 * @param deep
	 * @return
	 */
	public static Set<String> searchClassesByInterface(String className, boolean deep)
		{
		if (!isInitialized())
		{
			initialize();
		}
		Set<String> result = new HashSet<String>();
		Set<String> classes = scannerDB.getInterfacesIndex().get(className);
		
		if (classes != null && classes.size() > 0)
		{
			result.addAll(classes);
			if (deep)
			{
				for(String c: classes)
				{
					Set<String> deepInterfaces = searchClassesByInterface(c, deep);
					if (deepInterfaces != null)
					{
						result.addAll(deepInterfaces);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * return true if the scanner was already loaded.
	 * @return
	 */
	public static boolean isInitialized()
	{
		return initialized;
	}
}
