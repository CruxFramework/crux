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
package org.cruxframework.crux.core.rebind;

import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractWrapperProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	public AbstractWrapperProxyCreator(RebindContext context, JClassType baseIntf)
    {
	    this(context, baseIntf, true);
    }

	public AbstractWrapperProxyCreator(RebindContext context, JClassType baseIntf, boolean cacheable)
    {
	    super(context, baseIntf, cacheable);
    }

	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		generateProxyMethods(srcWriter, baseIntf);
    }
	
    protected void generateProxyMethods(SourcePrinter srcWriter, JClassType clazz) throws CruxGeneratorException
    {
    	JMethod[] methods = clazz.getOverridableMethods();
    	for (JMethod method : methods)
    	{
    		generateWrapperMethod(method, srcWriter);
    	}
    }

	protected abstract void generateWrapperMethod(JMethod method, SourcePrinter srcWriter);
}
