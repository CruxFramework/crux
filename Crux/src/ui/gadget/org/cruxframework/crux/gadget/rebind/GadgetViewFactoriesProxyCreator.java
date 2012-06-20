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
package org.cruxframework.crux.gadget.rebind;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoriesProxyCreator;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetViewFactoriesProxyCreator extends ViewFactoriesProxyCreator
{

	public GadgetViewFactoriesProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context);
    }
	
	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		sourceWriter.println("public void createView(String screenId) throws InterfaceConfigException{ ");
		sourceWriter.indent();

		generateViewCreationForAllScreens(sourceWriter);
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		if (Environment.isProduction())
		{
			generateFragmentedViewFactoryCreation(sourceWriter);
		}
    }

}
