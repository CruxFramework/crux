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
package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.gadgets.client.container.ContainerView;
import org.cruxframework.crux.gadgets.client.container.Gadget;
import org.cruxframework.crux.gadgets.client.container.GadgetMetadata;

import com.google.gwt.user.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface LayoutManager
{
	ContainerView profile = ContainerView.profile;
	ContainerView canvas = ContainerView.canvas;
	
	/**
	 * Configure the container, creating all elements to include the gadgets.
	 * This method must be called before any other interaction with the manager.
	 * It is called by GadgetContainer, after all configuration data is retrieved.
	 */
	void configure();
	
	/**
	 * Change the current view, keeping the gadget responsible for the event visible.
	 * @param gadgetId gadget that fired the event
	 * @param view the current gadget view 
	 */
	void changeGadgetView(int gadgetId, ContainerView view);
	
	/**
	 * Retrieve the DIV element used as wrapper to render the requested gadget 
	 * @param gadgetId gadget identifier
	 * @return
	 */
	Element getGadgetChrome(int gadgetId);
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId gadget identifier
	 * @return
	 */
	String getGadgetChromeId(int gadgetId);
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetChrome wrapper element
	 * @return
	 */
	String getGadgetId(Element gadgetChrome);
	
	/**
	 * Open settings menu for gadget
	 * @param gadget the gadget to be configured
	 * @param referenceElement element used as reference to find the options menu dialog position.
	 */
	void openMenuOptions(Gadget gadget, final Element referenceElement);

	/**
	 * Open a new gadget on the specified view.
	 * @param gadgetUrl url for the gadget
	 * @param view target view where gadget will be opened
	 */
	void openGadget(GadgetMetadata gadgetMetadata, ContainerView view);
	
}
