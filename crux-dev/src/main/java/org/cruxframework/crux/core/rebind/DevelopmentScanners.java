/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.DeclarativeUIScreenResourceScanner;
import org.cruxframework.crux.core.declarativeui.template.TemplatesScanner;
import org.cruxframework.crux.core.declarativeui.view.ViewsScanner;
import org.cruxframework.crux.core.rebind.module.ModulesScanner;
import org.cruxframework.crux.core.rebind.offline.OfflineScreensScanner;
import org.cruxframework.crux.scanner.ClassScanner;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DevelopmentScanners
{
	private static final Log logger = LogFactory.getLog(DevelopmentScanners.class);
	private static boolean initialized = false;
	
	public static void initializeScanners()
	{
		if (!initialized)
		{
			if (logger.isInfoEnabled())
			{
				logger.info("Registering scanners for crux compilation...");
			}
			ModulesScanner.initializeScanner();
			ClassScanner.initializeScanner();
			DeclarativeUIScreenResourceScanner.initializeScanner();
			TemplatesScanner.initializeScanner();
			ViewsScanner.initializeScanner();
			OfflineScreensScanner.initializeScanner();
			initialized = true;
		}
	}
}
