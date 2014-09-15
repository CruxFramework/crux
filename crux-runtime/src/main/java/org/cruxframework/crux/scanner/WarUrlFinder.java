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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WarUrlFinder
{
   public static URL[] findWebInfLibClasspaths(ServletContextEvent servletContextEvent)
   {
      ServletContext servletContext = servletContextEvent.getServletContext();
      return findWebInfLibClasspaths(servletContext);
   }

   public static URL[] findWebInfLibClasspaths(ServletContext servletContext)
   {
      ArrayList<URL> list = new ArrayList<URL>();
      Set<?> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
      for (Object jar : libJars)
      {
         try
         {
            list.add(servletContext.getResource((String) jar));
         }
         catch (MalformedURLException e)
         {
            throw new RuntimeException(e);
         }
      }
      return list.toArray(new URL[list.size()]);
   }

   public static URL findWebInfClassesPath(ServletContextEvent servletContextEvent)
   {
      ServletContext servletContext = servletContextEvent.getServletContext();
      return findWebInfClassesPath(servletContext);
   }

   /**
    * Find the URL pointing to "/WEB-INF/classes"  This method may not work in conjunction with IteratorFactory
    * if your servlet container does not extract the /WEB-INF/classes into a real file-based directory
    *
    * @param servletContext
    * @return null if cannot determine /WEB-INF/classes
    */
   public static URL findWebInfClassesPath(ServletContext servletContext)
   {
      String path = servletContext.getRealPath("/WEB-INF/classes");
      if (path == null) return null;
      File fp = new File(path);
      if (fp.exists() == false) return null;
      try
      {
         return fp.toURI().toURL();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
