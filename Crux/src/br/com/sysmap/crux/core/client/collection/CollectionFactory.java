/*
 * Copyright 2009 Google Inc.
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
package br.com.sysmap.crux.core.client.collection;


/**
 * Made to be switched out using super source even while Collections itself isn't.
 */
public class CollectionFactory {
  public static native <E> Array<E> createArray() /*-{
    return Array();
  }-*/;
  
  public static native <E> Array<E> createArray(int size) /*-{
    return Array(size);
  }-*/;
  
  public static native <E> Array<E> createArray(int size, E fillValue) /*-{
    var r = Array(size);
    if (fillValue != null) {
      for (i = 0; i < size; ++i) {
        r[i] = fillValue;
      }
    }
    return r;
  }-*/;

  public static native <V> Map<V> createMap() /*-{
    return Object();
  }-*/;
}
