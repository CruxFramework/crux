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
package org.cruxframework.crux.gadget.client.features;



/**
 * Provides access to the dynamic height feature.
 */
public interface DynamicHeightFeature {

  /**
   * Causes the Gadget to be resized. Will attempt to fit the gadget to its content 
   */
  void adjustHeight();

  /**
   * Causes the Gadget to be resized. 
   * @param height A preferred height in pixels. 
   */
  void adjustHeight(int height);
  
  /**
   * Detects the inner dimensions of a frame. See http://www.quirksmode.org/dom/w3c_cssom.html for more information.
   * @returnAn object with width and height properties.
   */
  ViewPortDimensions getViewportDimensions();
}