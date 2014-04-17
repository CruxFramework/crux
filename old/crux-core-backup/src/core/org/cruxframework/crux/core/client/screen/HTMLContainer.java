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
package org.cruxframework.crux.core.client.screen;

/**
 * HTMLContainers are widgets that can have innerHTML content AND, at same time, are Panels, (that can receive another widgets as children).
 * The transformation made at server side, during Crux pages compilation, keeps the HTML content inside the page, marking 
 * the widget position. It allows ScreenFactory to just wrap those elements into created widget, for a very better performance.
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HTMLContainer
{
	/**
	 * Called by ViewFactory 
	 */
	void onAttach();
}
