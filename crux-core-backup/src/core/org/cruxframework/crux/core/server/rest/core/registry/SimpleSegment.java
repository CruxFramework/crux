/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.core.registry;

import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.NotFoundException;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.server.rest.util.Encode;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SimpleSegment extends RootSegment
{
   protected String segment;

   public SimpleSegment(String segment)
   {
      this.segment = segment;
   }

   public String getSegment()
   {
      return segment;
   }

   public ResourceMethod matchSimple(HttpRequest request, String path, int start)
   {
      UriInfo uriInfo = request.getUri();
      if (start + segment.length() == path.length()) // we've reached end of string
      {
    	  ResourceMethod invoker = match(request.getHttpMethod(), request);
         if (invoker == null)
            throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());

         uriInfo.pushMatchedURI(path, Encode.decode(path));
         return invoker;
      }
      else
      {
           return matchChildren(request, path, start + segment.length() + 1); // + 1 to ignore '/'
      }

   }
}