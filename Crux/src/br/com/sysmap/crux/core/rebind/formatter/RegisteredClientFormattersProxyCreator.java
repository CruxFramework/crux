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
package br.com.sysmap.crux.core.rebind.formatter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.RegisteredClientFormatters;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.screen.Screen;
import br.com.sysmap.crux.core.rebind.scanner.screen.formatter.Formatters;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
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
	public RegisteredClientFormattersProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredClientFormatters.class.getCanonicalName()));
    }	

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
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
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
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
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<Formatter> clientFormatters = new FastMap<Formatter>();");
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
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

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }	
	
	private void generateFormatterBlock(SourceWriter sourceWriter, String formatter)
	{
		try
        {
	        String formatterParams = null;
	        String formatterName = formatter;
	        StringBuilder parameters = new StringBuilder();
	        int index = formatter.indexOf("(");
	        if (index > 0)
	        {
	        	formatterParams = formatter.substring(index+1,formatter.indexOf(")"));
	        	formatterName = formatter.substring(0,index).trim();
	        	String[] params = RegexpPatterns.REGEXP_COMMA.split(formatterParams);
	        	parameters.append("new String[]{");
	        	for (int i=0; i < params.length; i++) 
	        	{
	        		if (i>0)
	        		{
	        			parameters.append(",");
	        		}
	        		parameters.append(EscapeUtils.quote(params[i]).trim());
	        	}
	        	parameters.append("}");
	        }

	        if (!formattersAdded.containsKey(formatter) && Formatters.getFormatter(formatterName)!= null)
	        {
	        	JClassType formatterClass = baseIntf.getOracle().getType(Formatters.getFormatter(formatterName));
	        	sourceWriter.println("clientFormatters.put(\""+formatter+"\", new " + formatterClass.getParameterizedQualifiedSourceName() + "("+parameters.toString()+"));");
	        	formattersAdded.put(formatter, true);
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
}
