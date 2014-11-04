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

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

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
	public CruxClientConfigProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(CruxClientConfig.class.getCanonicalName()), true);
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
		generateEnableChildrenWindowsDebugMethod(sourceWriter);
		generateEnableCrux2OldInterfacesCompatibility(sourceWriter);
		generatePreferWebSQLForNativeDB(sourceWriter);
		generateNotifierCompilerPort(sourceWriter);
    }

	protected void generatePreferWebSQLForNativeDB(SourcePrinter sourceWriter)
    {
		sourceWriter.println("public boolean preferWebSQLForNativeDB(){");
		sourceWriter.println("return " + ConfigurationFactory.getConfigurations().preferWebSQLForNativeDB() + ";");
		sourceWriter.println("}");
    }

	protected void generateNotifierCompilerPort(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public String notifierCompilerPort(){");
		sourceWriter.println("return \"" + ConfigurationFactory.getConfigurations().notifierCompilerPort() + "\";");
		sourceWriter.println("}");
	}
	
	/**
	 * @param sourceWriter
	 */
	protected void generateEnableChildrenWindowsDebugMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public boolean enableDebugForURL(String url){");
		sourceWriter.println("return " + ConfigurationFactory.getConfigurations().enableChildrenWindowsDebug() + ";");
		sourceWriter.println("}");
	}
	
	/**
	 * @param sourceWriter
	 */
	protected void generateEnableCrux2OldInterfacesCompatibility(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public boolean enableCrux2OldInterfacesCompatibility(){");
		sourceWriter.println("return " + isCrux2OldInterfacesCompatibilityEnabled() + ";");
		sourceWriter.println("}");
	}
}
