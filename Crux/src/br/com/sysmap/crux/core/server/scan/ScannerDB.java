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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.scannotation.AnnotationDB;

public class ScannerDB extends AnnotationDB
{
	private static final long serialVersionUID = -4158104211395455896L;
	
    protected Map<String, Set<String>> interfacesIndex = new HashMap<String, Set<String>>();

    public Map<String, Set<String>> getInterfacesIndex()
	{
		return interfacesIndex;
	}

    @Override
    protected void scanClass(ClassFile cf)
    {
    	super.scanClass(cf);
    	populateInterfaces(cf.getInterfaces(), cf.getName());
    }
    
    protected void populateInterfaces(String[] interfaces, String className)
    {
       if (interfaces == null) return;
       Set<String> classInterfaces = classIndex.get(className);
       for (String str : interfaces)
       {
          Set<String> classes = interfacesIndex.get(str);
          if (classes == null)
          {
             classes = new HashSet<String>();
             interfacesIndex.put(str, classes);
          }
          classes.add(className);
          classInterfaces.add(str);
       }
    }
}