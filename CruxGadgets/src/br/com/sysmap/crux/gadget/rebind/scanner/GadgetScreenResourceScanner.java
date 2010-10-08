/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gadget.rebind.scanner;

import br.com.sysmap.crux.core.declarativeui.DeclarativeUIScreenResourceScanner;
import br.com.sysmap.crux.core.rebind.scanner.screen.Screen;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenFactory;


/**
 * A Scanner to accept only valid gadgets as pages. A valid Gadget 
 * must contains one gadgetView widget to delimit the content of a 
 * gadget view.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetScreenResourceScanner extends DeclarativeUIScreenResourceScanner
{
	/**
	 * @see br.com.sysmap.crux.core.declarativeui.DeclarativeUIScreenResourceScanner#accepts(java.lang.String)
	 */
	protected boolean accepts(String urlString)
	{
		boolean isValidGadget = false;
		if (urlString != null && urlString.endsWith(".crux.xml"))
		{
			try
			{
				Screen screen = ScreenFactory.getInstance().getScreen(urlString);
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
		return screen.getWidgetTypesIncluded().contains("gadget_gadgetView");
	}
}
