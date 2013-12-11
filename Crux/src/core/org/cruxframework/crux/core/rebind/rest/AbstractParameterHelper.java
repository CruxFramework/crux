/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.rest;

import java.util.Date;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
abstract class AbstractParameterHelper
{
	protected JClassType stringType;
	protected JClassType dateType;
	
	public AbstractParameterHelper(GeneratorContext context)
    {
		stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		dateType = context.getTypeOracle().findType(Date.class.getCanonicalName());
    }
	
	protected void generateMethodParamToCodeForComplexType(SourcePrinter srcWriter, String parameterStringVariable, JType parameterType, 
			String parameterName, String parameterExpression, String parameterCheckExpression)
    {
		PropertyInfo[] propertiesInfo = JClassUtils.extractBeanPropertiesInfo(parameterType.isClassOrInterface());
		for (PropertyInfo propertyInfo : propertiesInfo)
        {
	        if (JClassUtils.isSimpleType(propertyInfo.getType()))
	        {
	        	generateMethodParamToCodeForSimpleType(srcWriter, parameterStringVariable, propertyInfo.getType(), 
	        			parameterName+"."+propertyInfo.getName(), parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			(propertyInfo.getType().isPrimitive()!=null?
	        					parameterCheckExpression:
	        					parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null"));
	        }
	        else
	        {
	        	generateMethodParamToCodeForComplexType(srcWriter, parameterStringVariable, propertyInfo.getType(), 
	        			parameterName+"."+propertyInfo.getName(), parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null");
	        }
        }
    }

	protected void generateMethodParamToCodeForSimpleType(SourcePrinter srcWriter, String parameterStringVariable, JType parameterType, 
			String parameterName, String parameterexpression, String parameterCheckExpression)
    {
		JClassType jClassType = parameterType.isClassOrInterface();
		if (jClassType != null)
		{
			if (jClassType.isAssignableTo(stringType))
			{
				srcWriter.println(parameterStringVariable+"="+parameterStringVariable+".replace(\"{"+parameterName+"}\", URL.encodePathSegment("+
						"("+parameterCheckExpression+"?"+parameterexpression+":\"\")));");
			}
			else if (jClassType.isAssignableTo(dateType))
			{
				srcWriter.println(parameterStringVariable+"="+parameterStringVariable+".replace(\"{"+parameterName+"}\", URL.encodePathSegment("+
						"("+parameterCheckExpression+"?Long.toString("+parameterexpression+".getTime()):\"\")));");
			}
		    else
		    {
				srcWriter.println(parameterStringVariable+"="+parameterStringVariable+".replace(\"{"+parameterName+"}\", URL.encodePathSegment("+
						"("+parameterCheckExpression+"?(\"\"+"+parameterexpression+"):\"\")));");
		    }
		}
	    else
	    {
			srcWriter.println(parameterStringVariable+"="+parameterStringVariable+".replace(\"{"+parameterName+"}\", URL.encodePathSegment("+
					"("+parameterCheckExpression+"?(\"\"+"+parameterexpression+"):\"\")));");
	    }
    }

}
