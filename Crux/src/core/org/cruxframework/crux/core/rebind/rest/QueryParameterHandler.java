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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.server.rest.annotation.PathParam;
import org.cruxframework.crux.core.server.rest.annotation.QueryParam;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class QueryParameterHandler extends AbstractParameterHelper
{
	public QueryParameterHandler(GeneratorContext context)
    {
		super(context);
    }
	
	public String getQueryString(JMethod method, Method implementationMethod)
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		Annotation[][] parameterAnnotations = implementationMethod.getParameterAnnotations();
		JParameter[] parameters = method.getParameters();

		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations)
			{
				if (annotation instanceof QueryParam)
				{
					if (!first)
					{
						str.append("&");
					}
					first = false;
					if (JClassUtils.isSimpleType(parameters[i].getType()))
					{
						buildQueryStringForSimpleType(str, parameters[i].getName(), ((QueryParam)annotation).value());
					}
					else
					{
						buildQueryStringForComplexType(str, parameters[i].getName(), parameters[i].getType(), ((QueryParam)annotation).value());
					}
				}
			}
		}

		return str.toString();
	}

	public void generateMethodParamToURICode(SourcePrinter srcWriter, RestMethodInfo methodInfo, String parameterStringVariable)
	{
		Annotation[][] parameterAnnotations = methodInfo.implementationMethod.getParameterAnnotations();
		JParameter[] parameters = methodInfo.method.getParameters();

		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations)
			{
				if ((annotation instanceof QueryParam) || (annotation instanceof PathParam))
				{
					JParameter parameter = parameters[i];
					JType parameterType = parameter.getType();
					String parameterName = parameter.getName();
					if (JClassUtils.isSimpleType(parameterType))
					{
						generateMethodParamToCodeForSimpleType(srcWriter, parameterStringVariable, parameterType, parameterName, 
								parameterName, (parameterType.isPrimitive() != null?"true":parameterName+"!=null"));
					}
					else
					{
						generateMethodParamToCodeForComplexType(srcWriter, parameterStringVariable, parameterType, 
								parameterName, parameterName, parameterName+"!=null"); 
					}
				}
			}
		}
	}

	private void buildQueryStringForComplexType(StringBuilder str, String name, JType parameterType, String value)
    {
		PropertyInfo[] propertiesInfo = JClassUtils.extractBeanPropertiesInfo(parameterType.isClassOrInterface());
		boolean first = true;
		for (PropertyInfo propertyInfo : propertiesInfo)
        {
			if (!first)
			{
				str.append("&");
			}
			first = false;
	        if (JClassUtils.isSimpleType(propertyInfo.getType()))
	        {
				buildQueryStringForSimpleType(str, name+"."+propertyInfo.getName(), value+"."+propertyInfo.getName());
	        }
	        else
	        {
				buildQueryStringForComplexType(str, name+"."+propertyInfo.getName(), propertyInfo.getType(), value+"."+propertyInfo.getName());
	        }
        }
    }

	private void buildQueryStringForSimpleType(StringBuilder str, String parameterExpression, String parameterName)
    {
	    str.append(parameterName+"={"+parameterExpression+"}");
    }
}
