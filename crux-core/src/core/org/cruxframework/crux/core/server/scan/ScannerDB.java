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
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.scannotation.AnnotationDB;

import javassist.bytecode.ClassFile;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScannerDB extends AnnotationDB
{
	private static final long serialVersionUID = -4158104211395455896L;
	
    protected Map<String, Set<String>> interfacesIndex = new HashMap<String, Set<String>>();
    protected Map<String, String> superClasses = new HashMap<String, String>();
    protected Map<String, Set<String>> classInterfaces = new HashMap<String, Set<String>>();
    
    /**
     * 
     * @return
     */
    public Map<String, Set<String>> getInterfacesIndex()
	{
		return interfacesIndex;
	}

    /**
     * 
     */
    @Override
    public void scanArchives(URL... urls) throws IOException
    {
    	interfacesIndex.clear();
    	superClasses.clear();
    	
    	super.scanArchives(urls);
    	populateInterfacesFromSuperClass();
    }
    
    @Override
    protected void scanClass(ClassFile cf)
    {
    	super.scanClass(cf);
    	populateInterfaces(cf);
    }
    
    protected void populateInterfaces(ClassFile cf)
    {
    	String className = cf.getName();
    	String superClassName = cf.getSuperclass();

    	populateInterfaces(cf.getInterfaces(), className);
    	
		superClasses.put(className, superClassName);
    }
    
    protected void populateInterfacesFromSuperClass()
    {
    	Set<String> processedClasses = new HashSet<String>();
    	for (String className : superClasses.keySet())
		{
    		Set<String> interfacesFromSuperClass = getInterfacesFromSuperClass(className, processedClasses);
    		if (interfacesFromSuperClass != null)
    		{
    			for (String interfaceName : interfacesFromSuperClass)
				{
    				Set<String> classes = interfacesIndex.get(interfaceName);
    				if (classes == null)
    				{
    					classes = new HashSet<String>();
    					interfacesIndex.put(interfaceName, classes);
    				}
    				classes.add(className);
				}
    		}
		}
    }
    
    protected Set<String> getInterfacesFromSuperClass(String className, Set<String> processedClasses)
    {
    	if (processedClasses.contains(className))
    	{
    		return classInterfaces.get(className);
    	}
    	else
    	{
    		Set<String> result = new HashSet<String>();
    		String superClassName = superClasses.get(className);
    		if (superClassName != null)
    		{
    			Set<String> superClassesInterfaces = classInterfaces.get(superClassName);
    			if (superClassesInterfaces != null)
    			{
    				result.addAll(superClassesInterfaces);
    			}
    			Set<String> interfacesFromSuperClass = getInterfacesFromSuperClass(superClassName, processedClasses);
    			if (interfacesFromSuperClass != null && interfacesFromSuperClass.size() > 0)
    			{
    				result.addAll(interfacesFromSuperClass);
    			}
    		}
    		Set<String> interfaces = classInterfaces.get(className);
    		if (result.size() > 0)
    		{
        		if (interfaces == null)
        		{
        			interfaces = new HashSet<String>();
        			classInterfaces.put(className, interfaces);
        		}
        		
        		interfaces.addAll(result);
    		}

    		processedClasses.add(className);
    		return result;
    	}
    }
    
    protected void populateInterfaces(String[] interfaces, String className)
    {
       if (interfaces == null) return;
       Set<String> classesIndex = classIndex.get(className);
       Set<String> classesInterfaces = classInterfaces.get(className);
       if (classesInterfaces == null)
       {
    	   classesInterfaces = new HashSet<String>();
    	   classInterfaces.put(className, classesInterfaces);
       }
       for (String str : interfaces)
       {
          Set<String> classes = interfacesIndex.get(str);
          if (classes == null)
          {
             classes = new HashSet<String>();
             interfacesIndex.put(str, classes);
          }
          classes.add(className);
          classesIndex.add(str);
          classesInterfaces.add(str);
       }
    }
}