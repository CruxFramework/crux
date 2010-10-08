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
package br.com.sysmap.crux.gadget.rebind.gadget;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.gadget.client.Gadget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(Gadget.class.getCanonicalName()));
    }
	
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				Screen.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		// TODO Auto-generated method stub
		
	}
}
