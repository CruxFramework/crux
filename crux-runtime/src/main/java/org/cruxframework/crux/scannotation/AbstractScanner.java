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
package org.cruxframework.crux.scannotation;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.utils.RegexpPatterns;

/**
 * @author Thiago da Rosa de Bustamante 
 *
 */
public abstract class AbstractScanner
{
	protected Set<String> ignoredPackages = new LinkedHashSet<String>();
	protected Set<String> allowedPackages = new LinkedHashSet<String>();
	protected Set<String> requiredPackages = new LinkedHashSet<String>();
	
	protected String[] DEFAULT_IGNORED_PACKAGES = {"javax", "java", "sun", "com.sun", "org.apache", 
			"net.sf.saxon", "javassist", "junit"};
	protected String[] DEFAULT_REQUIRED_PACKAGES = {"org.cruxframework.crux", "com.google.gwt.i18n"};

	/**
	 * 
	 */
	public AbstractScanner()
	{
		initializeDefaultSearchPackages();
	}
	
	/**
	 * 
	 */
	private void initializeDefaultSearchPackages()
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
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getIgnoredPackages()
	{
		return ignoredPackages.toArray(new String[ignoredPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public void setIgnoredPackages(String[] ignoredPackages)
	{
		this.ignoredPackages = new HashSet<String>();
		if (ignoredPackages != null)
		{
			for (String pkg : ignoredPackages)
			{
				this.ignoredPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public void addIgnoredPackage(String ignoredPackage)
	{
		this.ignoredPackages.add(ignoredPackage);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getAllowedPackages()
	{
		return allowedPackages.toArray(new String[allowedPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public void setAllowedPackages(String[] allowedPackages)
	{
		this.allowedPackages = new HashSet<String>();
		if (allowedPackages != null)
		{
			for (String pkg : allowedPackages)
			{
				this.allowedPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public void addAllowedPackage(String allowedPackage)
	{
		this.allowedPackages.add(allowedPackage);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getRequiredPackages()
	{
		return requiredPackages.toArray(new String[requiredPackages.size()]);
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public void setRequiredPackages(String[] requiredPackages)
	{
		this.requiredPackages = new HashSet<String>();
		if (requiredPackages != null)
		{
			for (String pkg : requiredPackages)
			{
				this.requiredPackages.add(pkg);
			}
		}
	}
	
	/**
	 * @param ignoredPackage
	 */
	public void addRequiredPackage(String requiredPackages)
	{
		this.requiredPackages.add(requiredPackages);
	}

	/**
	 * 
	 * @param intf
	 * @return
	 */
	protected boolean ignoreScan(URL baseURL, String intf)
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
}
