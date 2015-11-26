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
package org.cruxframework.crux.core.rebind.config;

import org.cruxframework.crux.core.client.config.CruxClientConfig;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxClientConfigProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public CruxClientConfigProxyCreator(RebindContext context)
    {
	    super(context, context.getGeneratorContext().getTypeOracle().findType(CruxClientConfig.class.getCanonicalName()), true);
    }
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		Screen.class.getCanonicalName()
			};
		    return imports;    
    }

	@Override
    protected void generateProxyMethods(SourcePrinter sourceWriter) throws CruxGeneratorException
    {
		generatePreferWebSQLForNativeDB(sourceWriter);
		generateRootViewElementId(sourceWriter);
    }
	
	protected void generateRootViewElementId(SourcePrinter sourceWriter) 
	{
		sourceWriter.println("public String rootViewElementId(){");
		sourceWriter.println("return " + "\"" + ConfigurationFactory.getConfigurations().rootViewElementId() + "\"" + ";");
		sourceWriter.println("}");
	}

	protected void generatePreferWebSQLForNativeDB(SourcePrinter sourceWriter)
    {
		sourceWriter.println("public boolean preferWebSQLForNativeDB(){");
		sourceWriter.println("return " + ConfigurationFactory.getConfigurations().preferWebSQLForNativeDB() + ";");
		sourceWriter.println("}");
    }
}
