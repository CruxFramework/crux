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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;


/**
 * Create the view, based on the {@code .crux.xml} page.  
 * Do not use this class directly. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ViewFactory 
{
	/**
	 * Create the view for the informed view Name
	 * @param viewName the view name
	 * @param callback called when the view is created
	 */
	void createView(String viewName, CreateCallback callback) throws InterfaceConfigException;

	/**
	 * Create the view for the informed view Name
	 * @param viewName the view name
	 * @param viewId the view identifier
	 * @param callback called when the view is created
	 */
	void createView(String viewName, String viewId, CreateCallback callback) throws InterfaceConfigException;

	/**
	 * Retrieve the device that runs the application
	 * @return
	 */
	Device getCurrentDevice();
	
	/**
	 * A callback called when the requested view is created
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface CreateCallback
	{
		class EmptyCallback implements CreateCallback
		{
			private EmptyCallback(){}
			
			@Override
            public void onViewCreated(View view)
            {
            }
		}
		
		EmptyCallback EMPTY_CALLBACK = new EmptyCallback();
		
		/**
		 * Handler method called when the requested view is created
		 * @param view the requested view
		 */
		void onViewCreated(View view);
	}
}
