/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.context;

import java.lang.reflect.Field;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.DelegatingGeneratorContext;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.dev.CompilerContext;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.javac.StandardGeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class ContextUtil
{
	/**
	 * A Hack to read the module being compiled by GWT.
	 */
	public static ModuleDef getCurrentModule(GeneratorContext context)
	{
		if (context instanceof StandardGeneratorContext)
		{
			StandardGeneratorContext stdContext = (StandardGeneratorContext) context;
			try
            {
	            Field compilerContextField = StandardGeneratorContext.class.getDeclaredField("compilerContext");
	            compilerContextField.setAccessible(true);
	            CompilerContext compilerContext = (CompilerContext) compilerContextField.get(stdContext);
	            
	            return compilerContext.getModule();
            }
            catch (Exception e)
            {
            	throw new CruxGeneratorException("Can not retrieve the current module being compiled.", e);
            }
		}
		else if (context instanceof DelegatingGeneratorContext)
		{
			DelegatingGeneratorContext delContext = (DelegatingGeneratorContext) context;
			try
            {
	            Field baseContextField = StandardGeneratorContext.class.getDeclaredField("baseContext");
	            baseContextField.setAccessible(true);
	            context = (GeneratorContext) baseContextField.get(delContext);
	            
	            return getCurrentModule(context);
            }
            catch (Exception e)
            {
            	throw new CruxGeneratorException("Can not retrieve the current module being compiled.", e);
            }
		}
    	throw new CruxGeneratorException("Can not retrieve the current module being compiled. Unknow GeneratorContext type.");
	}
}
