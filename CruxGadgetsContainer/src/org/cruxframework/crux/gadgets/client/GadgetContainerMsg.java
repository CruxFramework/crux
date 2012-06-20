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
package org.cruxframework.crux.gadgets.client;

import org.cruxframework.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@MessageName("_containerMsg")
public interface GadgetContainerMsg extends Messages
{
	@DefaultMessage("Dashboard")
	String profileTitle();
	
	@DefaultMessage("LayoutManager is not configured. Insert one of the crux layoutManager templates on your container page.")
	String layoutManagerNotFound();

	@DefaultMessage("LayoutManager is already configured. Do not insert more than one layoutManager on your container page.")
	String duplicatedLayoutManager();

	@DefaultMessage("gadget {0} is not contained on your page.")
	String gadgetNotFound(int gadgetId);

	@DefaultMessage("Delete this gadget")
	SafeHtml deleteGadgetLink();

	@DefaultMessage("Configure this gadget")
	SafeHtml settingsGadgetLink();

	@DefaultMessage("About this gadget")
	SafeHtml aboutGadgetLink();

	@DefaultMessage("Gadget container is not correctly configured. Configure it before call this method.")
	String containerNotConfigured();
}
