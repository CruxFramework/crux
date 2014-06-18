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

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.util.Date;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.shared.rest.annotation.CookieParam;
import org.cruxframework.crux.core.shared.rest.annotation.FormParam;
import org.cruxframework.crux.core.shared.rest.annotation.HeaderParam;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.JClassUtils.PropertyInfo;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.Cookies;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class BodyParameterHandler extends AbstractParameterHelper
{
	private final TreeLogger logger;
	private final GeneratorContext context;

	public BodyParameterHandler(TreeLogger logger, GeneratorContext context)
    {
		super(context);
		this.logger = logger;
		this.context = context;
    }
	
	public void generateMethodParamToBodyCode(SourcePrinter srcWriter, RestMethodInfo methodInfo, String builder, String httpMethod)
	{
		JParameter[] parameters = methodInfo.method.getParameters();
		boolean formEncoded = false;
		boolean hasBodyObject = false;

		String formString = getFormString(methodInfo); 
		if (!StringUtils.isEmpty(formString))
		{
			srcWriter.println("String requestData = "+EscapeUtils.quote(formString)+";");
		}
		for (int i = 0; i< methodInfo.parameterAnnotations.length; i++)
		{
			Annotation[] annotations = methodInfo.parameterAnnotations[i];
			if (annotations == null || annotations.length == 0)
			{ // JSON on body
				if(hasBodyObject)
				{
					throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
					"Request body can not contain more than one body parameter (JSON serialized object).");
				}

				hasBodyObject = true;
				String serializerName = new JSonSerializerProxyCreator(context, logger, parameters[i].getType()).create();
				srcWriter.println(builder+".setHeader(\""+HttpHeaderNames.CONTENT_TYPE+"\", \"application/json\");");
				srcWriter.println("JSONValue serialized = new "+serializerName+"().encode("+parameters[i].getName()+");");
				srcWriter.println("String requestData = (serialized==null||serialized.isNull()!=null)?null:serialized.toString();");
			}
			else
			{
				for (Annotation annotation : annotations)
				{
					JParameter parameter = parameters[i];
					JType parameterType = parameter.getType();
					String parameterName = parameter.getName();
					formEncoded = generateMethodParamToBodyCodeForAnnotatedParameter(srcWriter, builder, parameters, formEncoded, i, annotation, parameterType, parameterName);
				}
			}
		}
		if (hasBodyObject && formEncoded)
		{
			throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
			"Request body can not contain form parameters and a JSON serialized object.");
		}
		if (hasBodyObject || formEncoded)
		{
			if (httpMethod.equals("GET"))
			{
				throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
				"Can not use request body parameters on a GET operation.");
			}
			srcWriter.println(builder+".setRequestData(requestData);");
		}
	}

	private String getFormString(RestMethodInfo methodInfo)
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		JParameter[] parameters = methodInfo.method.getParameters();

		try
		{
			for (int i = 0; i< methodInfo.parameterAnnotations.length; i++)
			{
				Annotation[] annotations = methodInfo.parameterAnnotations[i];
				for (Annotation annotation : annotations)
				{
					if (annotation instanceof FormParam)
					{
						if (!first)
						{
							str.append("&");
						}
						first = false;
						if (JClassUtils.isSimpleType(parameters[i].getType()))
						{
							buildFormStringForSimpleType(str, ((FormParam)annotation).value());
						}
						else
						{
							buildFormStringForComplexType(str, parameters[i].getType(), ((FormParam)annotation).value());
						}
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new CruxGeneratorException("Unsupported encoding for parameter name on method ["+methodInfo.method.toString()+"]");
		}
		return str.toString();
	}

	private boolean generateMethodParamToBodyCodeForAnnotatedParameter(SourcePrinter srcWriter, String builder, 
			JParameter[] parameters, boolean formEncoded, int i, Annotation annotation, JType parameterType, String parameterName)
    {
		if (annotation instanceof FormParam)
		{
			if (!formEncoded)
			{
				srcWriter.println(builder+".setHeader(\""+HttpHeaderNames.CONTENT_TYPE+"\", \"application/x-www-form-urlencoded\");");
				formEncoded = true;
			}
		}
		if (JClassUtils.isSimpleType(parameterType))
		{
			if (annotation instanceof FormParam)
			{
				generateMethodParamToCodeForSimpleType(srcWriter, "requestData", parameterType, ((FormParam) annotation).value(), 
						parameterName, (parameterType.isPrimitive() != null?"true":parameterName+"!=null"));
			}
			if (annotation instanceof HeaderParam)
			{
				generateMethodParamToHeaderCodeForSimpleType(srcWriter, builder, ((HeaderParam) annotation).value(), parameterType,  
						parameterName, (parameterType.isPrimitive() != null?"true":parameterName+"!=null"));
			}
			if (annotation instanceof CookieParam)
			{
				generateMethodParamToCookieCodeForSimpleType(srcWriter, ((CookieParam) annotation).value(), parameterType, 
						parameterName, (parameterType.isPrimitive() != null?"true":parameterName+"!=null"));
			}
		}
		else
		{
			if (annotation instanceof FormParam)
			{
				generateMethodParamToCodeForComplexType(srcWriter, "requestData", parameterType, 
						((FormParam) annotation).value(), parameterName, parameterName+"!=null"); 
			}
			if (annotation instanceof HeaderParam)
			{
				generateMethodParamToHeaderCodeForComplexType(srcWriter, builder, ((HeaderParam) annotation).value(), parameterType, 
						parameterName, parameterName+"!=null"); 
			}
			if (annotation instanceof CookieParam)
			{
				generateMethodParamToCookieCodeForComplexType(srcWriter, ((CookieParam) annotation).value(), parameterType, 
						parameterName, parameterName+"!=null"); 
			}
		}
	    return formEncoded;
    }
	
	private void generateMethodParamToCookieCodeForComplexType(SourcePrinter srcWriter, String cookieName, JType parameterType, 
			String parameterExpression, String parameterCheckExpression)
    {
		PropertyInfo[] propertiesInfo = JClassUtils.extractBeanPropertiesInfo(parameterType.isClassOrInterface());
		for (PropertyInfo propertyInfo : propertiesInfo)
        {
	        if (JClassUtils.isSimpleType(propertyInfo.getType()))
	        {
	        	generateMethodParamToCookieCodeForSimpleType(srcWriter, cookieName+"."+propertyInfo.getName(), propertyInfo.getType(), 
	        			parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			(propertyInfo.getType().isPrimitive()!=null?
	        					parameterCheckExpression:
	        					parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null"));
	        }
	        else
	        {
	        	generateMethodParamToCookieCodeForComplexType(srcWriter, cookieName+"."+propertyInfo.getName(), propertyInfo.getType(), 
	        			parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null");
	        }
        }
    }

	private void generateMethodParamToCookieCodeForSimpleType(SourcePrinter srcWriter, String cookieName, JType parameterType, 
			String parameterexpression, String parameterCheckExpression)
    {
		JClassType jClassType = parameterType.isClassOrInterface();
		if (jClassType != null)
		{
			if (jClassType.isAssignableTo(stringType))
			{
				srcWriter.println(Cookies.class.getCanonicalName()+".setCookie("+EscapeUtils.quote(cookieName) + 
						", "+"("+parameterCheckExpression+"?"+parameterexpression+":\"\"), new "+Date.class.getCanonicalName()+"(2240532000000L), null, \"/\", false);");
			}
			else if (jClassType.isAssignableTo(dateType))
			{
				srcWriter.println(Cookies.class.getCanonicalName()+".setCookie("+EscapeUtils.quote(cookieName) + 
						", "+"("+parameterCheckExpression+"?Long.toString("+parameterexpression+".getTime()):\"\"), new "+Date.class.getCanonicalName()+"(2240532000000L), null, \"/\", false);");
			}
		    else
		    {
				srcWriter.println(Cookies.class.getCanonicalName()+".setCookie("+EscapeUtils.quote(cookieName) + 
						", "+"("+parameterCheckExpression+"?(\"\"+"+parameterexpression+"):\"\"), new "+Date.class.getCanonicalName()+"(2240532000000L), null, \"/\", false);");
		    }
		}
	    else
	    {
			srcWriter.println(Cookies.class.getCanonicalName()+".setCookie("+EscapeUtils.quote(cookieName) + 
					", "+"("+parameterCheckExpression+"?(\"\"+"+parameterexpression+"):\"\"), new "+Date.class.getCanonicalName()+"(2240532000000L), null, \"/\", false);");
	    }
    }

	private void generateMethodParamToHeaderCodeForComplexType(SourcePrinter srcWriter, String builder, String headerName, JType parameterType, 
					String parameterExpression, String parameterCheckExpression)
    {
		PropertyInfo[] propertiesInfo = JClassUtils.extractBeanPropertiesInfo(parameterType.isClassOrInterface());
		for (PropertyInfo propertyInfo : propertiesInfo)
        {
	        if (JClassUtils.isSimpleType(propertyInfo.getType()))
	        {
	        	generateMethodParamToHeaderCodeForSimpleType(srcWriter, builder, headerName+"."+propertyInfo.getName(), propertyInfo.getType(), 
	        			parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			(propertyInfo.getType().isPrimitive()!=null?
	        					parameterCheckExpression:
	        					parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null"));
	        }
	        else
	        {
	        	generateMethodParamToHeaderCodeForComplexType(srcWriter, builder, headerName+"."+propertyInfo.getName(), propertyInfo.getType(), 
	        			parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()", 
	        			parameterCheckExpression + " && " + parameterExpression+"."+propertyInfo.getReadMethod().getName()+"()!=null");
	        }
        }
    }

	private void generateMethodParamToHeaderCodeForSimpleType(SourcePrinter srcWriter, String builderVarName, String headerName, 
			JType parameterType, String parameterexpression, String parameterCheckExpression)
    {
		JClassType jClassType = parameterType.isClassOrInterface();
		srcWriter.println("if ("+parameterCheckExpression+"){");
		if (jClassType != null)
		{
			if (jClassType.isAssignableTo(stringType))
			{
				srcWriter.println(builderVarName+".setHeader("+EscapeUtils.quote(headerName)+", URL.encodePathSegment("+
						parameterexpression+"));");
			}
			else if (jClassType.isAssignableTo(dateType))
			{
				srcWriter.println(builderVarName+".setHeader("+EscapeUtils.quote(headerName)+", URL.encodePathSegment("+
						"Long.toString("+parameterexpression+".getTime())));");
			}
		    else
		    {
				srcWriter.println(builderVarName+".setHeader("+EscapeUtils.quote(headerName)+", URL.encodePathSegment("+
						"\"\"+"+parameterexpression+"));");
		    }
		}
	    else
	    {
			srcWriter.println(builderVarName+".setHeader("+EscapeUtils.quote(headerName)+", URL.encodePathSegment("+
					"\"\"+"+parameterexpression+"));");
	    }
		srcWriter.println("}");
    }

	
	private void buildFormStringForComplexType(StringBuilder str, JType parameterType, String value) throws UnsupportedEncodingException
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
	        String parameterName = (StringUtils.isEmpty(value)?propertyInfo.getName():value+"."+propertyInfo.getName());
			if (JClassUtils.isSimpleType(propertyInfo.getType()))
	        {
				buildFormStringForSimpleType(str, parameterName);
	        }
	        else
	        {
				buildFormStringForComplexType(str, propertyInfo.getType(), parameterName);
	        }
        }
    }
	
	private void buildFormStringForSimpleType(StringBuilder str, String parameterName) throws UnsupportedEncodingException
    {
	    str.append(URLEncoder.encode(parameterName, "UTF-8")+"={"+parameterName+"}");
    }
}
