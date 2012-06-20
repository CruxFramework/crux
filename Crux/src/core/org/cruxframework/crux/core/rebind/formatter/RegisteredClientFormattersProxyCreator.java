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
package org.cruxframework.crux.core.rebind.formatter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.RegisteredClientFormatters;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Screen;


import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredClientFormattersProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, Boolean> formattersAdded = new HashMap<String, Boolean>();

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredClientFormattersProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredClientFormatters.class.getCanonicalName()), false);
    }	

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		Formatter.class.getCanonicalName(), 
	    		FastMap.class.getCanonicalName()
			};
	    return imports;
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public "+getProxySimpleName()+"(){ ");
		srcWriter.indent();

		List<Screen> screens = getScreens();
		for (Screen screen : screens)
		{
			Iterator<String> iterator = screen.iterateFormatters();
			while (iterator.hasNext())
			{
				String formatter = iterator.next();
				generateFormatterBlock(srcWriter, formatter);
			}
		}
		srcWriter.outdent();
		srcWriter.println("}");
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<Formatter> clientFormatters = new FastMap<Formatter>();");
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public Formatter getClientFormatter(String id){");
		srcWriter.indent();
		srcWriter.println("return clientFormatters.get(id);");
		srcWriter.outdent();
		srcWriter.println("}");
    }
	
	private void generateFormatterBlock(SourceWriter sourceWriter, String formatter)
	{
		if (!formattersAdded.containsKey(formatter))
		{
			sourceWriter.println("clientFormatters.put(\""+formatter+"\", "+ Formatters.getFormatterInstantionCommand(formatter) +");");
			formattersAdded.put(formatter, true);
		}
	}
}
