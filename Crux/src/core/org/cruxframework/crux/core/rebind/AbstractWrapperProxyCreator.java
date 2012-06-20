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

import com.google.gwt.core.ext.GeneratorContextExt;
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
	public AbstractWrapperProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf, true);
    }

	public AbstractWrapperProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf, boolean cacheable)
    {
	    super(logger, context, baseIntf, cacheable);
    }

	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateProxyMethods(srcWriter, baseIntf);
    }
	
    protected void generateProxyMethods(SourceWriter srcWriter, JClassType clazz) throws CruxGeneratorException
    {
    	JMethod[] methods = clazz.getOverridableMethods();
    	for (JMethod method : methods)
    	{
    		generateWrapperMethod(method, srcWriter);
    	}
    }

	protected abstract void generateWrapperMethod(JMethod method, SourceWriter srcWriter);
}
