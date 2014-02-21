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
package org.cruxframework.crux.module.rebind;

import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.config.CruxClientConfigProxyCreator;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.module.CruxModuleHandler;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConfigProxyCreator extends CruxClientConfigProxyCreator
{
	public ConfigProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context);
    }

	@Override
	protected void generateEnableChildrenWindowsDebugMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public boolean enableDebugForURL(String url){");
		if (!Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableChildrenWindowsDebug()))
		{
			sourceWriter.println("return false;");
		}
		else
		{
			String[] developmentModules = Environment.isProduction()?null:CruxModuleHandler.getDevelopmentModules();
			if (developmentModules != null)
			{
				sourceWriter.println("if (url == null){");
				sourceWriter.println("return false;");
				sourceWriter.println("}");

				sourceWriter.println("String urlWithoutParameters = url;");
				sourceWriter.println("int index = url.indexOf(\"?\");");
				sourceWriter.println("if (index  > 0){");
				sourceWriter.println("urlWithoutParameters = url.substring(0,index);");
				sourceWriter.println("}");
				for (String moduleName : developmentModules)
				{
					String[] pages = CruxModuleHandler.getCruxModule(moduleName).getPages();
					if (pages != null)
					{
						for (String page : pages)
						{
							sourceWriter.println("if (urlWithoutParameters.endsWith(\""+page+"\")){");
							sourceWriter.println("return true;");
							sourceWriter.println("}");
						}
					}
				}
			}
			sourceWriter.println("return false;");
		}
		sourceWriter.println("}");
	}
}
