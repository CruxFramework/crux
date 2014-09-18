/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.clientoffline;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface OfflineMessages extends Messages
{
	@DefaultMessage("Downloading resources...")
	String downloadingResources();

	@DefaultMessage("Checking for updates...")
	String checkingResources();

	@DefaultMessage("Downloading resources ({0} of {1})")
	String progressStatus(int total, int loaded);

	@DefaultMessage("Can not retrieve application cache manifest file. Probably, client does not have network connection.")
	String applicationCacheError();

	@DefaultMessage("There are updates ready to be installed. Would you like to restart now?")
	String requestUpdate();

	@DefaultMessage("Updates notification")
	String requestUpdateTitle();

	@DefaultMessage("Application is now onLine.")
	String networkOnLine();

	@DefaultMessage("Application is now offLine.")
	String networkOffLine();

	@DefaultMessage("Application cache is obsolete. The application can not work as expected.")
	String applicationCacheObsolete();

}
