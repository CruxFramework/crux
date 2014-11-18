/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cruxframework.crux.core.server.rest.core.RequestPreprocessors;
import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.shared.rest.annotation.CookieParam;
import org.cruxframework.crux.core.shared.rest.annotation.DefaultValue;
import org.cruxframework.crux.core.shared.rest.annotation.FormParam;
import org.cruxframework.crux.core.shared.rest.annotation.HeaderParam;
import org.cruxframework.crux.core.shared.rest.annotation.PathParam;
import org.cruxframework.crux.core.shared.rest.annotation.QueryParam;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class MethodInvoker
{
	protected static enum RestParameterType{query, header, form, cookie, path, body}

	protected Method method;
	protected Class<?> rootClass;
	protected ValueInjector[] params;
	protected List<RequestPreprocessor> preprocessors;
	private RestErrorHandler restErrorHandler; 

	public MethodInvoker(Class<?> root, Method method, String httpMethod)
	{
		this.method = method;
		this.rootClass = root;
		this.restErrorHandler = RestErrorHandlerFactory.createErrorHandler(method);
		this.params = new ValueInjector[method.getParameterTypes().length];
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		for (int i = 0; i < genericParameterTypes.length; i++)
		{
			Annotation[] annotations = method.getParameterAnnotations()[i];
			Type paramType = ClassUtils.resolveGenericTypeOnMethod(genericParameterTypes[i], rootClass, method);
			params[i] = createParameterExtractor(root, paramType, annotations);
		}
		validateParamExtractors(httpMethod);
		initializePreprocessors();
	}

	public ValueInjector[] getParams()
	{
		return params;
	}

	public Object[] injectArguments(HttpRequest input)
	{
		try
		{
			Object[] args = null;
			if (params != null && params.length > 0)
			{
				args = new Object[params.length];
				int i = 0;
				for (ValueInjector extractor : params)
				{
					args[i++] = extractor.inject(input);
				}
			}
			return args;
		}
		catch (RestFailure f)
		{
			throw f;
		}
		catch (Exception e)
		{
			BadRequestException badRequest = new BadRequestException("Failed processing arguments of " + method.toString(), "Can not invoke requested service with given arguments", e);
			throw badRequest;
		}
	}

	public Object invoke(HttpRequest request, Object resource) throws RestFailure
	{
		preprocess(request);
		
		Object[] args = injectArguments(request);

		try
		{
			Object result = method.invoke(resource, args);
			return result;
		}
		catch (IllegalAccessException e)
		{
			throw new InternalServerErrorException("Not allowed to reflect on method: " + method.toString(), "Can not execute requested service", e);
		}
		catch (InvocationTargetException e)
		{
			return restErrorHandler.handleError(e);
		}
		catch (IllegalArgumentException e)
		{
			String msg = "Bad arguments passed to " + method.toString() + "  (";
			if (args != null)
			{
				boolean first = false;
				for (Object arg : args)
				{
					if (!first)
					{
						first = true;
					}
					else
					{
						msg += ",";
					}
					if (arg == null)
					{
						msg += " null";
						continue;
					}
					msg += " " + arg.getClass().getName();
				}
			}
			msg += " )";
			throw new InternalServerErrorException(msg, "Can not execute requested service", e);
		}
	}

	protected void initializePreprocessors() throws RequestProcessorException
    {
		RequestProcessorContext context = new RequestProcessorContext();
		context.setTargetMethod(method);
		context.setTargetClass(rootClass);
		
	    preprocessors = new ArrayList<RequestPreprocessor>();
	    Iterator<RequestPreprocessor> iterator = RequestPreprocessors.iteratePreprocessors();
	    while (iterator.hasNext())
        {
	    	RequestPreprocessor processor = iterator.next().createProcessor(context);
	    	if (processor != null)
	    	{
	    		preprocessors.add(processor);
	    	}
        }
    }

	protected void preprocess(HttpRequest request) throws RestFailure
    {
		for (RequestPreprocessor preprocessor : preprocessors)
        {
	        preprocessor.preprocess(request);
        }
    }

	protected static ValueInjector createParameterExtractor(Class<?> injectTargetClass, Type type, Annotation[] annotations)
	{
		if (ClassUtils.isSimpleType(type))
		{
			return createParameterExtractorForSimpleType(injectTargetClass, type, annotations);
			
		}
		return createParameterExtractorForComplexType(injectTargetClass, type, annotations);
	}
	
	protected static ValueInjector createParameterExtractorForSimpleType(Class<?> injectTargetClass, Type type, Annotation[] annotations)
	{
		DefaultValue defaultValue = ClassUtils.findAnnotation(annotations, DefaultValue.class);
		String defaultVal = null;
		if (defaultValue != null)
		{
			defaultVal = defaultValue.value();
		}

		QueryParam query;
		HeaderParam header;
		PathParam uriParam;
		CookieParam cookie;
		FormParam formParam;

		if ((query = ClassUtils.findAnnotation(annotations, QueryParam.class)) != null)
		{
			return createParameterExtractorForSimpleType(RestParameterType.query, injectTargetClass, type, query.value(), defaultVal);
		}
		else if ((header = ClassUtils.findAnnotation(annotations, HeaderParam.class)) != null)
		{
			return createParameterExtractorForSimpleType(RestParameterType.header, injectTargetClass, type, header.value(), defaultVal);
		}
		else if ((formParam = ClassUtils.findAnnotation(annotations, FormParam.class)) != null)
		{
			return createParameterExtractorForSimpleType(RestParameterType.form, injectTargetClass, type, formParam.value(), defaultVal);
		}
		else if ((cookie = ClassUtils.findAnnotation(annotations, CookieParam.class)) != null)
		{
			return createParameterExtractorForSimpleType(RestParameterType.cookie, injectTargetClass, type, cookie.value(), defaultVal);
		}
		else if ((uriParam = ClassUtils.findAnnotation(annotations, PathParam.class)) != null)
		{
			return createParameterExtractorForSimpleType(RestParameterType.path, injectTargetClass, type, uriParam.value(), defaultVal);
		}
		else
		{
			return createParameterExtractorForSimpleType(RestParameterType.body, injectTargetClass, type, null, null);
		}
	}
	
	protected static ValueInjector createParameterExtractorForSimpleType(RestParameterType restParameterType, Class<?> injectTargetClass, Type type, 
																  String paramName, String defaultValue)
	{
		switch (restParameterType)
        {
        	case query: 
        		return new QueryParamInjector(type, paramName, defaultValue);
        	case header:
        		return new HeaderParamInjector(type, paramName, defaultValue);
        	case form:
        		return new FormParamInjector(type, paramName, defaultValue);
        	case cookie:
        		return new CookieParamInjector(type, paramName, defaultValue);
        	case path:
        		return new PathParamInjector(type, paramName, defaultValue);
        	default:
    			return new MessageBodyParamInjector(injectTargetClass, type);
        }
	}

	/**
	 * The user can define a value object to group parameters that are passed in the same way (query, form, path, cookie, header).
	 * @param injectTargetClass
	 * @param injectTarget
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @return
	 */
	protected static ValueInjector createParameterExtractorForComplexType(Class<?> injectTargetClass, Type type, Annotation[] annotations)
	{
		QueryParam query;
		HeaderParam header;
		PathParam uriParam;
		CookieParam cookie;
		FormParam formParam;

		if ((query = ClassUtils.findAnnotation(annotations, QueryParam.class)) != null)
		{
			return new GroupValueInjector(RestParameterType.query, type, query.value());
		}
		else if ((header = ClassUtils.findAnnotation(annotations, HeaderParam.class)) != null)
		{
			return new GroupValueInjector(RestParameterType.header, type, header.value());
		}
		else if ((formParam = ClassUtils.findAnnotation(annotations, FormParam.class)) != null)
		{
			return new GroupValueInjector(RestParameterType.form, type, formParam.value());
		}
		else if ((cookie = ClassUtils.findAnnotation(annotations, CookieParam.class)) != null)
		{
			return new GroupValueInjector(RestParameterType.cookie, type, cookie.value());
		}
		else if ((uriParam = ClassUtils.findAnnotation(annotations, PathParam.class)) != null)
		{
			return new GroupValueInjector(RestParameterType.path, type, uriParam.value());
		}
		else
		{
			return new MessageBodyParamInjector(injectTargetClass, type);
		}
	}

	protected void validateParamExtractors(String httpMethod)
    {
		boolean hasFormParam = false;
		boolean hasBodyParam = false;
		
		for (ValueInjector paramInjector : params)
        {
	        if (paramInjector instanceof FormParamInjector)
	        {
	        	hasFormParam = true;
	        }
	        if (paramInjector instanceof MessageBodyParamInjector)
	        {
	    		if (hasBodyParam)
	    		{
	    			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can not receive " +
	    					"more than one parameter through body text", "Can not execute requested service");
	    		}
	        	hasBodyParam = true;
	        }
        }

		if (hasBodyParam && hasFormParam)
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can not use both " +
					"types on the same method: FormParam and BodyParam", "Can not execute requested service");
		}
		if ((hasBodyParam || hasFormParam) && httpMethod.equals("GET"))
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can not receive " +
					"parameters on body for GET methods.", "Can not execute requested service");
		}
    }
}