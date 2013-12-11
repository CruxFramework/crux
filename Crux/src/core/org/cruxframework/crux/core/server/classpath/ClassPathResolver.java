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
package org.cruxframework.crux.core.server.classpath;

import java.net.URL;

/**
 * ClassPathResolvers are used by Crux to find the location of some application's directory, like web dir, 
 * WEB-INF/classes and WEB-INF/lib dirs. Those directories are searched by some Crux scanners.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ClassPathResolver
{
	/**
	 * @return URL pointing to application's WEB-INF/classes directory
	 */
	URL findWebInfClassesPath();

	/**
	 * @return URL pointing to application's WEB-INF/lib directory
	 */
	URL findWebInfLibPath();
	
	/**
	 * @return An Array of URLs pointing to application's WEB-INF/lib jars
	 */
	URL[] findWebInfLibJars();

	/**
	 * Return an array of URLs pointing to application's web directories. More than one directory can exist
	 * if application has more than one crux module.
	 * 
	 * @return An Array of URLs pointing to application's web directories
	 */
	URL[] findWebBaseDirs();
	
	
	/**
	 * @param url new path to web-inf/classes folder 
	 */
	void setWebInfClassesPath(URL url);
	
	/**
	 * @param url new path to web-inf/lib folder
	 */
	void setWebInfLibPath(URL url);
	
	/**
	 * @param url list of jar files present into web-inf/lib folder
	 */
	void setWebInfLibJars(URL[] url);

	/**
	 * @param url list of applications web folders
	 */
	void setWebBaseDirs(URL[] url);

	/**
	 * Initialize the classpath resolver
	 */
	void initialize();
}
