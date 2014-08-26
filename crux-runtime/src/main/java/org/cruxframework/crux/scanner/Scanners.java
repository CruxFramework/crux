/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.utils.FilePatternHandler;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scanner.ScannerRegistration.ScannerMatch;
import org.cruxframework.crux.scanner.archiveiterator.Filter;
import org.cruxframework.crux.scanner.archiveiterator.IteratorFactory;
import org.cruxframework.crux.scanner.archiveiterator.URLIterator;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public final class Scanners
{
	protected static final String[] DEFAULT_IGNORED_PACKAGES = {"javax", "java", "sun", "com.sun", "org.apache", "net.sf.saxon", "javassist", 
																"junit", "org.cruxframework.crux.core.rebind.screen.wrapper"};
	protected static final String[] DEFAULT_REQUIRED_PACKAGES = {"org.cruxframework.crux", "com.google.gwt.i18n"};
	protected static final String[] DEFAULT_REQUIRED_LIBS = {"gwt-user*.jar", "crux-*.jar"};
	
	protected static Set<String> ignoredPackages = new LinkedHashSet<String>();
	protected static Set<String> allowedPackages = new LinkedHashSet<String>();
	protected static Set<String> requiredPackages = new LinkedHashSet<String>();
	
	private static Map<String, ScannerRegistration> registrations = new HashMap<String, ScannerRegistration>();
	private static URL[] urls;
	private static boolean initialized = false;
	private static FilePatternHandler allowedLibsHandler;
	private static FilePatternHandler requiredLibsHandler;

	private Scanners()
	{
	}

	/**
	 * This method must be called before any scanning attempt
	 * @param registrations
	 */
	public static synchronized void registerScanners(ScannerRegistrations registrations)
	{
		registrations.doRegistrations();
		if (registrations.initializeEagerly())
		{
			scan();
		}
	}
	
	/**
	 * Define the URLs that will be searched during scanning process.
	 * @param urls
	 */
	public static void setSearchURLs(URL... urls)
	{
		Scanners.urls = fiterLibs(urls);
		resetScanners();
	}

	/**
	 * Register a new scanner on Crux scanning engine. All registered scanners are called during the scanning cycle.
	 * @param scanner
	 */
	public static void registerScanner(AbstractScanner scanner)
	{
		try
		{
			registrations.put(scanner.getClass().getCanonicalName(), new ScannerRegistration(scanner));
		}
		catch (Exception e)
		{
			throw new ScannerException("Error registering crux scanner [" + scanner.getClass().getCanonicalName() + "]", e);
		}
	}

	/**
	 * Check if the given scanner is registered as a Crux scanner
	 * @param scannerClass
	 * @return
	 */
	public static boolean isScannerRegistered(Class<? extends AbstractScanner> scannerClass)
	{
		return registrations.containsKey(scannerClass.getCanonicalName());
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getIgnoredPackages()
	{
		return ignoredPackages.toArray(new String[ignoredPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setIgnoredPackages(String[] ignoredPackages)
	{
		Scanners.ignoredPackages = new HashSet<String>();
		if (ignoredPackages != null)
		{
			for (String pkg : ignoredPackages)
			{
				Scanners.ignoredPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static void addIgnoredPackage(String ignoredPackage)
	{
		Scanners.ignoredPackages.add(ignoredPackage);
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getAllowedPackages()
	{
		return allowedPackages.toArray(new String[allowedPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setAllowedPackages(String[] allowedPackages)
	{
		Scanners.allowedPackages = new HashSet<String>();
		if (allowedPackages != null)
		{
			for (String pkg : allowedPackages)
			{
				Scanners.allowedPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static void addAllowedPackage(String allowedPackage)
	{
		allowedPackages.add(allowedPackage);
	}

	/**
	 * 
	 * @return
	 */
	public static String[] getRequiredPackages()
	{
		return requiredPackages.toArray(new String[requiredPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public static void setRequiredPackages(String[] requiredPackages)
	{
		Scanners.requiredPackages = new HashSet<String>();
		if (requiredPackages != null)
		{
			for (String pkg : requiredPackages)
			{
				Scanners.requiredPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public static  void addRequiredPackage(String requiredPackage)
	{
		requiredPackages.add(requiredPackage);
	}

	/**
	 * 
	 * @param intf
	 * @return
	 */
	public static boolean ignoreScan(URL baseURL, String intf)
	{
		String urlString = baseURL.toString();
		if (intf.startsWith(urlString))
		{
			intf = intf.substring(urlString.length());
		}
		else if (intf.startsWith("jar:") || intf.startsWith("zip:"))
		{
			if (intf.indexOf("!/") > 0)
			{
				intf = intf.substring(intf.indexOf("!/")+2);
			}		
		}
		
		if (intf.startsWith("/")) intf = intf.substring(1);
		intf = intf.replace('/', '.');
		
		if (allowedPackages.size() > 0)
		{
			for (String allowed : allowedPackages)
			{
				if (intf.startsWith(allowed + "."))
				{
					return false;
				}
			}
			for (String allowed : requiredPackages)
			{
				if (intf.startsWith(allowed + "."))
				{
					return false;
				}
			}
			return true;
		}
		
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
	 * Reset all scanner results.
	 * @param scannerClass
	 */
	public static void resetScanners()
	{
		for (ScannerRegistration scannerRegistration : registrations.values())
        {
			scannerRegistration.resetScanner();
        }
	}
	
	/**
	 * Reset a specific scanner result. Used to support hot deployment
	 * @param scannerClass
	 */
	public static void resetScanner(Class<? extends AbstractScanner> scannerClass)
	{
		ScannerRegistration scannerRegistration = registrations.get(scannerClass.getCanonicalName());
		if (scannerRegistration != null)
		{
			scannerRegistration.resetScanner();
		}
	}

	/**
	 * Scan the project and returns the result associated to the given scanner
	 * @param scannerClass
	 * @return
	 */
	public static void scan()
	{
		List<ScannerRegistration> scanners = getScannersToRun();
		if (scanners.size() > 0)
		{
			URL[] urls = getSearchURLs();

			if (urls == null)
			{
				/* If URLs is not informed, Crux used default URLs on classpath, plus the web public folders on each
					 Crux module. The process to build the crux modules list needs to use other scanners. So, to avoid
					 an infinite loop, we need to pre scan the URLs that do not need to search the web folders and then
					 continue with web folders scanning. */
				urls = fiterLibs(ScannerURLS.getURLsForSearch());
				scan(scanners, urls);
				if (!Environment.isProduction())
				{
					try
					{
						URL[] webDirs = new URL[]{ClassPathResolverInitializer.getClassPathResolver().findWebBaseDir()};
						webDirs = fiterLibs(webDirs);
						scan(scanners, webDirs);
					}
					catch (Exception e)
					{
						throw new ScannerException("Error scanning resources. Check if Scanners engine was properly initialized. "
								+ "Verify if your application configures DevModeInitializerListener on web.xml properly. "
								+ "Also, check if the resources from project source folder were correctly copied to the target folder. "
								, e);
					}
				}
			}
			else
			{
				scan(scanners, urls);
			}
		}
	}

	public static List<URL> search(URL baseLocation, final Filter filter) throws IOException
	{
		final URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(baseLocation.getProtocol());
		List<ScannerRegistration> scanners = new ArrayList<ScannerRegistration>();
		
		ScannerRegistration scanner = new ScannerRegistration(new AbstractScanner()
		{
			@Override
			public Filter getScannerFilter()
			{
				return filter;
			}
			@Override
			public ScannerCallback getScannerCallback()
			{
			    return null;
			}
			
			@Override
			public void resetScanner()
			{
			}
		});
		scanners.add(scanner);
		
		scanner.setScanned();
		scanner.startScanning();

		URLIterator iterator = resourceHandler.getDirectoryIteratorFactory().create(baseLocation, scanners);
		iterator.search();

		List<URL> result = new ArrayList<URL>();
		
		for (ScannerMatch match : scanner.getAllMatches())
		{
			result.add(match.getMatch());
		}
		
		scanner.resetScanner();
		
		return result;
	}
	
	/**
	 * Scan the project and returns the result associated to the given scanner
	 * @param scanners
	 * @param urls
	 */
	private static void scan(List<ScannerRegistration> scanners, URL[] urls)
    {
	    for (ScannerRegistration scannerRegistration : scanners)
        {
	    	//Incremental scanning. Only notify about the matches on that scanning step.
	    	scannerRegistration.startScanning();
        }
	    for (final URL url : urls)
	    {
	    	try
	    	{
	    		if (resourceExists(url))
	    		{
	    			URLIterator it = IteratorFactory.create(url, scanners);
	    			it.search();
	    		}
	    	}
	    	catch (IOException e)
	    	{
	    		throw new ScannerException("Error running crux scanners.", e);
	    	}
	    }
	    for (ScannerRegistration scannerRegistration : scanners)
        {
	    	//Incremental scanning. Only notify about the matches on that scanning step.
	    	List<ScannerMatch> matches = scannerRegistration.getScanMatches();
	    	if (matches != null && matches.size() > 0)
	    	{
	    		ScannerCallback callback = scannerRegistration.getCallback();
	    		if (callback != null)
	    		{
	    			callback.onFound(matches);
	    		}
	    	}
	    	scannerRegistration.endScanning();
        }
    }

	/**
	 * 
	 * @param lib
	 * @return
	 */
	private static boolean ignoreEntry(String lib)
	{
		String libLowerCase = lib.toLowerCase();
		if (libLowerCase.endsWith(".jar") || libLowerCase.endsWith(".zip"))
		{
			if (allowedLibsHandler != null)
			{
				if (requiredLibsHandler != null && requiredLibsHandler.isValidEntry(lib))
				{
					return false;
				}
				return (!allowedLibsHandler.isValidEntry(lib));
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private static synchronized void initializeDefaultSearchPackages()
	{
		if (!initialized)
		{
			for (String pkg : DEFAULT_IGNORED_PACKAGES)
			{
				addIgnoredPackage(pkg);
			}
			for (String pkg : DEFAULT_REQUIRED_PACKAGES)
			{
				addRequiredPackage(pkg);
			}
			String scanAllowedPackages = ConfigurationFactory.getConfigurations().scanAllowedPackages();
			if (scanAllowedPackages != null && scanAllowedPackages.length() > 0)
			{
				String[] allowedPackages = RegexpPatterns.REGEXP_COMMA.split(scanAllowedPackages);
				for (String allowed : allowedPackages) 
				{
					addAllowedPackage(allowed.trim());
				}
			}

			String scanIgnoredPackages = ConfigurationFactory.getConfigurations().scanIgnoredPackages();
			if (scanIgnoredPackages != null && scanIgnoredPackages.length() > 0)
			{
				String[] ignoredPackages = RegexpPatterns.REGEXP_COMMA.split(scanIgnoredPackages);
				for (String ignored : ignoredPackages) 
				{
					addIgnoredPackage(ignored.trim());
				}
			}

			String scanIgnoredLibs = ConfigurationFactory.getConfigurations().scanIgnoredLibs();
			String scanAllowedLibs = ConfigurationFactory.getConfigurations().scanAllowedLibs();
			if ((scanIgnoredLibs != null && scanIgnoredLibs.length() > 0) ||
				(scanAllowedLibs != null && scanAllowedLibs.length() > 0))	
			{
				allowedLibsHandler = new FilePatternHandler(scanAllowedLibs, scanIgnoredLibs);
				requiredLibsHandler = new FilePatternHandler(getRequiredLibs(), null);
			}
			initialized = true;
		}
	}

	private static String getRequiredLibs()
    {
	    String delim = "";
	    StringBuilder sb = new StringBuilder();
	    for(String i: DEFAULT_REQUIRED_LIBS)
	    {
	        sb.append(delim).append(i);
	        delim = ",";
	    }
	    return sb.toString();
    }	
	
	/**
	 * Returns true if there is a resource with the given URL
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static boolean resourceExists(URL url)
	{
		if (url == null)
		{
			return false;
		}
		return URLResourceHandlersRegistry.getURLResourceHandler(url.getProtocol()).exists(url);
	}

	private static URL[] getSearchURLs()
	{
		return urls;
	}
	
	private static URL[] fiterLibs(URL[] webURLsForSearch)
    {
		if (!initialized)
		{
			initializeDefaultSearchPackages();
		}
	    List<URL> filtered = new ArrayList<URL>();
	    
	    if (webURLsForSearch != null)
	    {
	    	for (URL url : webURLsForSearch)
	    	{
	    		if (url.getProtocol().equals("file"))
	    		{
	    			try
	    			{
	    				File file = new File(url.toURI());
	    				if (!ignoreEntry(file.getName()))
	    				{
	    					filtered.add(url);
	    				}
	    			}
	    			catch (URISyntaxException e)
	    			{
	    				throw new ScannerException("Error running crux scanner", e);
	    			}
	    		}
	    		else
	    		{
	    			filtered.add(url);
	    		}
	    	}
	    }
	    return filtered.toArray(new URL[filtered.size()]);
    }

	private static List<ScannerRegistration> getScannersToRun()
	{
		List<ScannerRegistration> scanners = new ArrayList<ScannerRegistration>();

		for (ScannerRegistration scannerRegistration : registrations.values())
		{
			if (!scannerRegistration.isScanned())
			{
				scannerRegistration.setScanned();
				scanners.add(scannerRegistration);
			}
		}
		return scanners;
	}

	/**
	 * Callback called when matches are found by scanner
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface ScannerCallback
	{
		void onFound(List<ScannerMatch> matches);
	}
	
	public static interface ScannerRegistrations
	{
		void doRegistrations();
		boolean initializeEagerly();
	}
}
