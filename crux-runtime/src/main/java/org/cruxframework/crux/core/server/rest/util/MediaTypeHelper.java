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
package org.cruxframework.crux.core.server.rest.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHelper
{
   public static float getQ(MediaType type)
   {
      float rtn = getQWithParamInfo(type);
      if (rtn == 2.0F) return 1.0F;
      return rtn;
   }

   public static float getQWithParamInfo(MediaType type)
   {
      if (type.getParameters() != null)
      {
         String val = type.getParameters().get("q");
         try
         {
            if (val != null)
            {
               float rtn = Float.valueOf(val);
               if (rtn > 1.0F)
                  throw new RestFailure("MediaType q value cannot be greater than 1.0: " + type.toString(), HttpResponseCodes.SC_BAD_REQUEST);
               return rtn;
            }
         }
         catch (NumberFormatException e)
         {
            throw new RuntimeException("MediaType q parameter must be a float: " + type, e);
         }
      }
      return 2.0f;
   }

   /**
    * subtypes like application/*+xml
    *
    * @param subtype
    * @return
    */
   public static boolean isCompositeWildcardSubtype(String subtype)
   {
      return subtype.startsWith("*+");
   }

   /**
    * subtypes like application/*+xml
    *
    * @param subtype
    * @return
    */
   public static boolean isWildcardCompositeSubtype(String subtype)
   {
      return subtype.endsWith("+*");
   }

   public static boolean isComposite(String subtype)
   {
      return (isCompositeWildcardSubtype(subtype) || isWildcardCompositeSubtype(subtype));
   }

   private static class MediaTypeComparator implements Comparator<MediaType>, Serializable
   {

      private static final long serialVersionUID = -5828700121582498092L;

      public int compare(MediaType mediaType2, MediaType mediaType)
      {
         float q = getQWithParamInfo(mediaType);
         boolean wasQ = q != 2.0f;
         if (q == 2.0f) q = 1.0f;

         float q2 = getQWithParamInfo(mediaType2);
         boolean wasQ2 = q2 != 2.0f;
         if (q2 == 2.0f) q2 = 1.0f;


         if (q < q2) return -1;
         if (q > q2) return 1;

         if (mediaType.isWildcardType() && !mediaType2.isWildcardType()) return -1;
         if (!mediaType.isWildcardType() && mediaType2.isWildcardType()) return 1;
         if (mediaType.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) return -1;
         if (!mediaType.isWildcardSubtype() && mediaType2.isWildcardSubtype()) return 1;
         if (isComposite(mediaType.getSubtype()) && !isComposite(mediaType2.getSubtype()))
            return -1;
         if (!isComposite(mediaType.getSubtype()) && isComposite(mediaType2.getSubtype()))
            return 1;
         if (isCompositeWildcardSubtype(mediaType.getSubtype()) && !isCompositeWildcardSubtype(mediaType2.getSubtype()))
            return -1;
         if (!isCompositeWildcardSubtype(mediaType.getSubtype()) && isCompositeWildcardSubtype(mediaType2.getSubtype()))
            return 1;
         if (isWildcardCompositeSubtype(mediaType.getSubtype()) && !isWildcardCompositeSubtype(mediaType2.getSubtype()))
            return -1;
         if (!isWildcardCompositeSubtype(mediaType.getSubtype()) && isWildcardCompositeSubtype(mediaType2.getSubtype()))
            return 1;

         int numNonQ = 0;
         if (mediaType.getParameters() != null)
         {
            numNonQ = mediaType.getParameters().size();
            if (wasQ) numNonQ--;
         }

         int numNonQ2 = 0;
         if (mediaType2.getParameters() != null)
         {
            numNonQ2 = mediaType2.getParameters().size();
            if (wasQ2) numNonQ2--;
         }

         if (numNonQ < numNonQ2) return -1;
         if (numNonQ > numNonQ2) return 1;


         return 0;
      }
   }

   public static int compareWeight(MediaType one, MediaType two)
   {
      return new MediaTypeComparator().compare(one, two);
   }

   public static boolean sameWeight(MediaType one, MediaType two)
   {
      return new MediaTypeComparator().compare(one, two) == 0;
   }

   public static void sortByWeight(List<MediaType> types)
   {
      if (types == null || types.size() <= 1) return;
      Collections.sort(types, new MediaTypeComparator());
   }

   public static MediaType getBestMatch(List<MediaType> desired, List<MediaType> provided)
   {
      sortByWeight(desired);
      sortByWeight(provided);
      boolean emptyDesired = desired == null || desired.size() == 0;
      boolean emptyProvided = provided == null || provided.size() == 0;

      if (emptyDesired && emptyProvided) return null;
      if (emptyDesired && !emptyProvided) return provided.get(0);
      if (emptyProvided && !emptyDesired) return desired.get(0);

      for (MediaType desire : desired)
      {
         for (MediaType provide : provided)
         {
            if (provide.isCompatible(desire)) return provide;
         }
      }
      return null;
   }

   public static List<MediaType> parseHeader(String header)
   {
      ArrayList<MediaType> types = new ArrayList<MediaType>();
      String[] medias = header.split(",");
      for (int i = 0; i < medias.length; i++)
      {
         types.add(MediaType.valueOf(medias[i].trim()));
      }
      return types;
   }

   public static boolean equivalent(MediaType m1, MediaType m2)
   {
      if (m1 == m2) return true;

      if (!m1.getType().equals(m2.getType())) return false;
      if (!m1.getSubtype().equals(m2.getSubtype())) return false;

      return equivalentParams(m1, m2);
   }

   public static boolean equivalentParams(MediaType m1, MediaType m2)
   {
      Map<String, String> params1 = m1.getParameters();
      Map<String, String> params2 = m2.getParameters();

      if (params1 == params2) return true;
      if (params1 == null || params2 == null) return false;
      if (params1.size() == 0 && params2.size() == 0) return true;
      int numParams1 = params1.size();
      if (params1.containsKey("q")) numParams1--;
      int numParams2 = params2.size();
      if (params2.containsKey("q")) numParams2--;

      if (numParams1 != numParams2) return false;
      if (numParams1 == 0) return true;

      for (Map.Entry<String, String> entry : params1.entrySet())
      {
         String key = entry.getKey();
         if (key.equals("q")) continue;
         String value = entry.getValue();
         String value2 = params2.get(key);
         if (value == value2) continue; // both null
         if (value == null || value2 == null) return false;
         if (value.equals(value2) == false) return false;
      }
      return true;
   }
}