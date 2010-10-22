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
 * Shared assertions and related messages.
 */
class Assertions {
  
  /**
   * Message produced when asserting that access into a set or map  uses a value
   * not in the domain. 
   */
  static final String ACCESS_UNSUPPORTED_VALUE = "Unsupported value";
  
  /**
   * Message for asserting that access to an empty array is an illegal operation.
   */
  public static final String ACCESS_EMPTY_ARRAY_MESSAGE = 
    "Attempt to access an element in an empty array";

  static void assertIndexInRange(int index, int minInclusive, int maxExclusive) {
    assert minInclusive < maxExclusive : ACCESS_EMPTY_ARRAY_MESSAGE;
    assert (index >= minInclusive && index < maxExclusive) : "Index " + index 
        + " was not in the acceptable range [" + minInclusive + ", " 
        + maxExclusive + ")";
  }

  static void assertNotNull(Object ref) {
    assert (ref != null) : "A null reference is not allowed here";
  }
}
