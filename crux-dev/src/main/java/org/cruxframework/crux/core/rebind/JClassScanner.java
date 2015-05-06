/*
 * Copyright 2014 cruxframework.org.
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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JClassScanner
{
	private GeneratorContext context;

	public JClassScanner(GeneratorContext context)
    {
		this.context = context;
    }
	
	public JClassType[] searchClassesByInterface(JClassType interfaceClass)
	{
		return interfaceClass.getSubtypes();
	}
	
	public JClassType[] searchClassesByInterface(String interfaceClass) throws NotFoundException
	{
		JClassType classType = context.getTypeOracle().getType(interfaceClass);
		return classType.getSubtypes();
	}
	
	public JClassType[] searchClassesByAnnotation(Class<? extends Annotation> annotationClass)
	{
		List<JClassType> result = new ArrayList<JClassType>(); 
		for (JClassType type : context.getTypeOracle().getTypes()) 
		{
			Annotation annotation = type.getAnnotation(annotationClass);
			if (annotation != null)
			{
				result.add(type);
			}
		}
		return result.toArray(new JClassType[result.size()]);
	}
	
}
