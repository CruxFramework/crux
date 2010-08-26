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
package br.com.sysmap.crux.core.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractWrapperProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	public AbstractWrapperProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateProxyMethods(srcWriter, baseIntf);
    }
	
    protected void generateProxyMethods(SourceWriter srcWriter, JClassType clazz) throws CruxGeneratorException
    {
    	JMethod[] methods = clazz.getMethods();
    	for (JMethod method : methods)
    	{
    		generateWrapperMethod(method, srcWriter, clazz);
    	}
    	
    	JClassType[] interfaces = clazz.getImplementedInterfaces();
    	for (JClassType intf : interfaces)
        {
    		generateProxyMethods(srcWriter, intf);
        }
    }

	protected abstract void generateWrapperMethod(JMethod method, SourceWriter srcWriter, JClassType clazz);
}
