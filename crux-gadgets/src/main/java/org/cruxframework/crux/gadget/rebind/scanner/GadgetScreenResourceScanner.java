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
package org.cruxframework.crux.gadget.rebind.scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.DeclarativeUIScreenResourceScanner;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.scanner.Scanners;


/**
 * A Scanner to accept only valid gadgets as pages. A valid Gadget 
 * must contains one gadgetView widget to delimit the content of a 
 * gadget view.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetScreenResourceScanner extends DeclarativeUIScreenResourceScanner
{

	private static final Log logger = LogFactory.getLog(DeclarativeUIScreenResourceScanner.class);
	private static boolean initialized = false;
	private static GadgetScreenResourceScanner instance = new GadgetScreenResourceScanner();
	
	public static GadgetScreenResourceScanner getInstance()
	{
		return instance;
	}

	public static synchronized void initializeScanner()
	{
		if (!initialized)
		{
			if (logger.isInfoEnabled())
			{
				logger.info("Initializing screen resources scanner...");
			}
			Scanners.registerScanner(getInstance());
			initialized = true;
		}
	}
	
	
	/**
	 * @see org.cruxframework.crux.core.declarativeui.DeclarativeUIScreenResourceScanner#accepts(java.lang.String)
	 */
	protected boolean accepts(String urlString)
	{
		boolean isValidGadget = false;
		if (urlString != null && urlString.endsWith(".crux.xml") && !urlString.endsWith("cruxViewTester.crux.xml"))
		{
			try
			{
				Screen screen = ScreenFactory.getInstance().getScreen(urlString, null);
				isValidGadget = hasGadgetViewWidget(screen);
			}
			catch (ScreenConfigException e)
			{
				isValidGadget = false;
			}
		}
		return isValidGadget;
	}

	/**
	 * @param screen
	 * @return
	 */
	private boolean hasGadgetViewWidget(Screen screen)
	{
		return screen.getRootView().getWidgetTypesIncluded().contains("gadget_gadgetView");
	}
}
