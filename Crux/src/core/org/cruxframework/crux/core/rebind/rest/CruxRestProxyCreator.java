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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.service.RestProxy.Callback;
import org.cruxframework.crux.core.client.service.RestProxy.RestError;
import org.cruxframework.crux.core.client.service.RestProxy.TargetRestService;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.annotation.GET;
import org.cruxframework.crux.core.server.rest.annotation.Path;
import org.cruxframework.crux.core.server.rest.annotation.StateValidationModel;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceScanner;
import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.shared.rest.RestException;
import org.cruxframework.crux.core.utils.EncryptUtils;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxRestProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Class<?> restImplementationClass;
	private JClassType callbackType;
	private JClassType javascriptObjectType;
	private String serviceBasePath;
	private Map<String, Method> readMethods = new HashMap<String, Method>();
	private Map<String, Method> updateMethods = new HashMap<String, Method>();
	private Set<RestMethodInfo> restMethods = new HashSet<RestMethodInfo>();
	private boolean mustGenerateStateControlMethods;
	private QueryParameterHandler queryParameterHandler;
	private BodyParameterHandler bodyParameterHandler;

	public CruxRestProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, false);
		callbackType = context.getTypeOracle().findType(Callback.class.getCanonicalName());
		javascriptObjectType = context.getTypeOracle().findType(JavaScriptObject.class.getCanonicalName());
		restImplementationClass = getRestImplementationClass(baseIntf);
		queryParameterHandler = new QueryParameterHandler(context);
		bodyParameterHandler = new BodyParameterHandler(logger, context);
		String basePath;
		try
		{
			basePath = context.getPropertyOracle().getConfigurationProperty("crux.rest.base.path").getValues().get(0);
			if (basePath.endsWith("/"))
			{
				basePath = basePath.substring(0, basePath.length()-1);
			}
		}
		catch (Exception e)
		{
			basePath = "rest";
		}
		String value = restImplementationClass.getAnnotation(Path.class).value();
		if (value.startsWith("/"))
		{
			value = value.substring(1);
		}
		serviceBasePath = basePath+"/"+value;
		initializeRestMethods();
	}

	protected Class<?> getRestImplementationClass(JClassType baseIntf)
	{
		TargetRestService restService = baseIntf.getAnnotation(TargetRestService.class);
		if (restService == null)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Use @RestProxy.TargetRestService annotation to inform the target of current proxy.");
		}
		String serviceClassName = RestServiceScanner.getInstance().getServiceClassName(restService.value());
		Class<?> restImplementationClass;
		try
		{
			restImplementationClass = Class.forName(serviceClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Can not found the implementationClass.");
		}
		return restImplementationClass;
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException 
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.println("__hostPath = com.google.gwt.core.client.GWT.getModuleBaseURL();");
		srcWriter.println("__hostPath = __hostPath.substring(0, __hostPath.indexOf(com.google.gwt.core.client.GWT.getModuleName()));");
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		if (mustGenerateStateControlMethods)
		{
			srcWriter.println(FastMap.class.getCanonicalName()+"<String> __currentEtags = new "+FastMap.class.getCanonicalName()+"<String>();");
		}
		srcWriter.println("private String __hostPath;");
		srcWriter.println("private static Logger __log = Logger.getLogger("+getProxyQualifiedName()+".class.getName());");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		if (mustGenerateStateControlMethods)
		{
			generateStateControlMethods(srcWriter);
		}
		for (RestMethodInfo methodInfo : restMethods)
		{
			generateWrapperMethod(methodInfo, srcWriter);
		}
	}

	protected void generateStateControlMethods(SourcePrinter srcWriter)
	{
		srcWriter.println("public boolean __readCurrentEtag(String uri, RequestBuilder builder, boolean required){");
		srcWriter.println("String etag = __currentEtags.get(uri);");
		srcWriter.println("if (required && etag == null){");
		srcWriter.println("return false;");
		srcWriter.println("}");
		srcWriter.println("if (etag != null){");
		srcWriter.println("builder.setHeader("+EscapeUtils.quote(HttpHeaderNames.IF_MATCH)+", etag);");
		srcWriter.println("}");
		srcWriter.println("return true;");
		srcWriter.println("}");
		srcWriter.println("public void __saveCurrentEtag(String uri, Response response){");
		srcWriter.println("String etag = response.getHeader("+EscapeUtils.quote(HttpHeaderNames.ETAG)+");");
		srcWriter.println("__currentEtags.put(uri, etag);");
		srcWriter.println("}");
	}

	protected void initializeRestMethods()
	{
		JMethod[] methods = baseIntf.getOverridableMethods();
		for (JMethod method : methods)
		{
			Method implementationMethod = getImplementationMethod(method);
			String methodURI = getRestURI(method, implementationMethod);
			StateValidationModel validationModel = HttpMethodHelper.getStateValidationModel(implementationMethod);
			if (validationModel != null && !validationModel.equals(StateValidationModel.NO_VALIDATE))
			{
				updateMethods.put(methodURI, implementationMethod);
			}
			else if (implementationMethod.getAnnotation(GET.class) != null)
			{
				readMethods.put(methodURI, implementationMethod);
			}
			restMethods.add(new RestMethodInfo(method, implementationMethod, methodURI));
		}

		mustGenerateStateControlMethods = false;
		for (Entry<String, Method> entry : updateMethods.entrySet())
		{
			Method method = entry.getValue();
			Method readMethod = readMethods.get(entry.getKey());
			if (readMethod == null)
			{
				throw new CruxGeneratorException("Can not create the rest proxy. Can not found the " +
						"GET method for state dependent write method ["+method.toString()+"].");
			}
			mustGenerateStateControlMethods = true; 
		}
	}

	protected void generateWrapperMethod(RestMethodInfo methodInfo, SourcePrinter srcWriter)
	{
		try
		{
			List<JParameter> parameters = generateProxyWrapperMethodDeclaration(srcWriter, methodInfo.method);
			String httpMethod = HttpMethodHelper.getHttpMethod(methodInfo.implementationMethod, false);
			JParameter callbackParameter = parameters.get(parameters.size()-1);
			String callbackResultTypeName = getCallbackResultTypeName(callbackParameter.getType().isClassOrInterface());
			String callbackParameterName = callbackParameter.getName();

			srcWriter.println("String baseURIPath = " + EscapeUtils.quote(methodInfo.methodURI) + ";");
			queryParameterHandler.generateMethodParamToURICode(srcWriter, methodInfo, "baseURIPath");
			srcWriter.println("final String restURI = __hostPath + baseURIPath;");

			srcWriter.println("RequestBuilder builder = new RequestBuilder(RequestBuilder."+httpMethod+", restURI);");
			setLocaleInfo(srcWriter, "builder");
			srcWriter.println("builder.setCallback(new RequestCallback(){");

			srcWriter.println("public void onResponseReceived(Request request, Response response){");
			srcWriter.println("int s = (response.getStatusCode()-200);");
			srcWriter.println("if (s >= 0 && s < 10){");
			generateSuccessCallHandlingCode(methodInfo, srcWriter, callbackParameter, callbackResultTypeName, callbackParameterName);
			srcWriter.println("}else{ ");
			generateExceptionCallHandlingCode(methodInfo, srcWriter, callbackParameterName);
			srcWriter.println("}");
			srcWriter.println("}");

			srcWriter.println("public void onError(Request request, Throwable exception){");
			srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceUnexpectedError(exception.getMessage())));");
			srcWriter.println("}");
			srcWriter.println("});");

			srcWriter.println("try{");
			bodyParameterHandler.generateMethodParamToBodyCode(srcWriter, methodInfo, "builder", httpMethod);
			generateValidateStateBlock(srcWriter, methodInfo.implementationMethod, "builder", "restURI", methodInfo.methodURI, callbackParameterName);
			generateXSRFHeaderProtectionForWrites(httpMethod, "builder", srcWriter);
			srcWriter.println("builder.send();");
			srcWriter.println("}catch (Exception e){");
			srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceUnexpectedError(e.getMessage())));");
			srcWriter.println("}");
			srcWriter.println("}");
		}
		catch (InvalidRestMethod e)
		{
			throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "().", e);
		}
	}

	protected void generateExceptionCallHandlingCode(RestMethodInfo methodInfo, SourcePrinter srcWriter, String callbackParameterName)
	{
		try
		{
			//try to parse response object
			srcWriter.println("JSONObject jsonObject = null;");
			srcWriter.println("try {");
			srcWriter.println("jsonObject = JSONParser.parseStrict(response.getText()).isObject();");
			//For instance if we have 400-404 server response, the object is not a json value. This will make JSON throws an Exception
			srcWriter.println("} catch (Exception exception) {");
			srcWriter.println(callbackParameterName+".onError(new RestError(response.getStatusCode(), response.getText()));");
			srcWriter.println("return;");
			srcWriter.println("}");
			
			Class<?>[] restExceptionTypes = getRestExceptionTypes(methodInfo.implementationMethod);
			if (restExceptionTypes != null && restExceptionTypes.length > 0)
			{
				srcWriter.println("JSONValue exId = jsonObject.get(\"exId\");");
				srcWriter.println("if (exId == null){");
				srcWriter.println("JSONValue jsonErrorMsg = jsonObject.get(\"message\");");
				srcWriter.println("String stringJsonErrorMsg = (jsonErrorMsg != null && jsonErrorMsg.isString() != null) ? jsonErrorMsg.isString().stringValue() : \"\";");
				srcWriter.println(callbackParameterName+".onError(new RestError(response.getStatusCode(), stringJsonErrorMsg));");
				srcWriter.println("} else {");
				srcWriter.println("String hash = exId.isString().stringValue();");
				boolean first = true;
				for (Class<?> restException : restExceptionTypes)
				{
					JClassType exceptionType = context.getTypeOracle().findType(restException.getCanonicalName());
					if (exceptionType == null)
					{
						throw new CruxGeneratorException("Exception type ["+restException.getCanonicalName()+"] can not be used on client code. Add this exeption to a GWT client package.");
					}
					if (!first)
					{
						srcWriter.print("else ");
					}
					first = false;
					srcWriter.println("if (StringUtils.unsafeEquals(hash,"+EscapeUtils.quote(EncryptUtils.hash(exceptionType.getParameterizedQualifiedSourceName()))+")){");
					String serializerName = new JSonSerializerProxyCreator(context, logger, exceptionType).create();
					srcWriter.println("Exception ex = new "+serializerName+"().decode(jsonObject.get(\"exData\"));");
					srcWriter.println(callbackParameterName+".onError(ex);");
					srcWriter.println("}");
				}
				srcWriter.println("else {");
				srcWriter.println(callbackParameterName+".onError(new RestError(response.getStatusCode(), jsonObject.get(\"exData\").toString()));");
				srcWriter.println("}");
				
				srcWriter.println("}");
			}
			else
			{
				srcWriter.println(callbackParameterName+".onError(new RestError(response.getStatusCode(), (jsonObject.get(\"message\") != null && jsonObject.get(\"message\").isString() != null) ? jsonObject.get(\"message\").isString().stringValue() : \"\"));");
			}
		}
		catch (Exception e) 
		{
			throw new CruxGeneratorException("Error generatirng exception handlers for type ["+baseIntf.getParameterizedQualifiedSourceName()+"].", e);
		}
	}

	protected Class<?>[] getRestExceptionTypes(Method method)
	{
		List<Class<?>> result = new ArrayList<Class<?>>();
		Class<?>[] types = method.getExceptionTypes();
		for (Class<?> exceptionClass : types)
		{
			if (RestException.class.isAssignableFrom(exceptionClass))
			{
				result.add(exceptionClass);
			}
		}
		return result.toArray(new Class[result.size()]);
	}

	protected void generateSuccessCallHandlingCode(RestMethodInfo methodInfo, SourcePrinter srcWriter, 
			JParameter callbackParameter, String callbackResultTypeName, String callbackParameterName)
	{
		if (!callbackResultTypeName.equalsIgnoreCase("void"))
		{
			JClassType callbackResultType = JClassUtils.getTypeArgForGenericType(callbackParameter.getType().isClassOrInterface());
			srcWriter.println("String jsonText = response.getText();");
			srcWriter.println("if (Response.SC_NO_CONTENT != response.getStatusCode() && !"+StringUtils.class.getCanonicalName()+".isEmpty(jsonText)){");
			srcWriter.println("try{");

			if (callbackResultType != null && callbackResultType.isAssignableTo(javascriptObjectType))
			{
				srcWriter.println(callbackResultTypeName+" result = "+JsonUtils.class.getCanonicalName()+".safeEval(jsonText);");
			}
			else
			{
				srcWriter.println("JSONValue jsonValue = JSONParser.parseStrict(jsonText);");
				String serializerName = new JSonSerializerProxyCreator(context, logger, callbackResultType).create();
				srcWriter.println(callbackResultTypeName+" result = new "+serializerName+"().decode(jsonValue);");
			}
			generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess(result);");
			srcWriter.println("}catch (Exception e){");
			srcWriter.println("if (LogConfiguration.loggingIsEnabled()){");
			srcWriter.println("__log.log(Level.SEVERE, e.getMessage(), e);");
			//srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceUnexpectedError(e.getMessage())));");
			srcWriter.println("}");
			srcWriter.println("}");
			srcWriter.println("}else {");
			generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess(null);");
			srcWriter.println("}");
		}
		else
		{
			generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess(null);");
		}
	}

	protected void setLocaleInfo(SourcePrinter srcWriter, String builderVariable)
	{

		srcWriter.println("String _locale = "+Screen.class.getCanonicalName()+".getLocale();");
		srcWriter.println("if (_locale != null){");
		srcWriter.println(builderVariable+".setHeader(\""+HttpHeaderNames.ACCEPT_LANGUAGE+"\", _locale.replace('_', '-'));");//	pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3
		srcWriter.println("}");
	}

	protected void generateXSRFHeaderProtectionForWrites(String httpMethod, String builderVar, SourcePrinter srcWriter)
	{
		if (!httpMethod.equals("GET"))
		{
			srcWriter.println(builderVar+".setHeader("+EscapeUtils.quote(HttpHeaderNames.XSRF_PROTECTION_HEADER)+", \"1\");");
		}
	}

	protected void generateSalveStateBlock(SourcePrinter srcWriter, Method method, String responseVar, String uriVar, String uri)
	{
		if (readMethods.containsKey(uri) && updateMethods.containsKey(uri))
		{
			GET get = method.getAnnotation(GET.class);
			if (get != null)
			{
				srcWriter.println("__saveCurrentEtag("+uriVar+", "+responseVar+");");
			}
		}
	}

	protected void generateValidateStateBlock(SourcePrinter srcWriter, Method method, String builderVar, String uriVar, String uri, String callbackParameterName)
	{
		if (readMethods.containsKey(uri) && updateMethods.containsKey(uri))
		{
			StateValidationModel validationModel = HttpMethodHelper.getStateValidationModel(method);
			if (validationModel != null)
			{
				srcWriter.println("if (!__readCurrentEtag("+uriVar+", "+builderVar+","+validationModel.equals(StateValidationModel.ENSURE_STATE_MATCHES)+")){");
				srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceMissingStateEtag("+uriVar+")));");
				srcWriter.println("return;");
				srcWriter.println("}");
			}
		}
	}

	protected String getCallbackResultTypeName(JClassType callbackParameter)
	{
		JClassType jClassType = JClassUtils.getTypeArgForGenericType(callbackParameter);
		if (jClassType.isPrimitive() != null)
		{
			return jClassType.isPrimitive().getQualifiedBoxedSourceName();
		}
		return jClassType.getParameterizedQualifiedSourceName();
	}

	protected String getRestURI(JMethod method, Method implementationMethod)
	{
		String methodPath = paths(serviceBasePath);
		Path path = implementationMethod.getAnnotation(Path.class);
		if (path != null)
		{
			methodPath = paths(methodPath, path.value());
		}
		String queryString = queryParameterHandler.getQueryString(method, implementationMethod);
		if (queryString.length() > 0)
		{
			return methodPath+"?"+queryString;
		}
		return methodPath;
	}

	protected String paths(String basePath, String... segments)
	{
		String path = basePath;
		if (path == null)
		{
			path = "";
		}
		for (String segment : segments)
		{
			if ("".equals(segment))
			{
				continue;
			}
			if (path.endsWith("/"))
			{
				if (segment.startsWith("/"))
				{
					segment = segment.substring(1);
					if ("".equals(segment))
					{
						continue;
					}
				}
				segment = Encode.encodePath(segment);
				path += segment;
			}
			else
			{
				segment = Encode.encodePath(segment);
				if ("".equals(path))
				{
					path = segment;
				}
				else if (segment.startsWith("/"))
				{
					path += segment;
				}
				else
				{
					path += "/" + segment;
				}
			}

		}
		return path;
	}

	@Override
	protected String[] getImports()
	{
		return new String[] { 
				Level.class.getCanonicalName(),
				Logger.class.getCanonicalName(),
				LogConfiguration.class.getCanonicalName(),
				RequestBuilder.class.getCanonicalName(), 
				RequestCallback.class.getCanonicalName(),
				Request.class.getCanonicalName(), 
				Response.class.getCanonicalName(), 
				JsonUtils.class.getCanonicalName(), 
				JSONValue.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(),
				JSONParser.class.getCanonicalName(), 
				URL.class.getCanonicalName(), 
				Crux.class.getCanonicalName(),
				Callback.class.getCanonicalName(),
				StringUtils.class.getCanonicalName(),
				RestError.class.getCanonicalName()
		};
	}

	protected Method getImplementationMethod(JMethod method)
	{
		validateProxyMethod(method);
		Method implementationMethod = null;

		Method[] allMethods = restImplementationClass.getMethods();
		for (Method m: allMethods)
		{
			if (m.getName().equals(method.getName()))
			{
				implementationMethod = m;
				break;
			}
		}					

		validateImplementationMethod(method, implementationMethod);
		return implementationMethod;
	}

	protected void validateProxyMethod(JMethod method)
	{
		if (method.getReturnType() != JPrimitiveType.VOID) 
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must be void");
		}
		JType[] parameterTypes = method.getParameterTypes();
		if (parameterTypes == null || parameterTypes.length < 1)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must have a last parameter of type RestProxy.Callback");
		}
		JClassType lastParameterType = parameterTypes[parameterTypes.length - 1].isClassOrInterface();
		if (lastParameterType == null || !callbackType.isAssignableFrom(lastParameterType))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must have a last parameter of type RestProxy.Callback");
		}
	}

	protected void validateImplementationMethod(JMethod method, Method implementationMethod)
	{
		if (implementationMethod == null)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Can not found the implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName());
		}

		Class<?>[] implTypes = implementationMethod.getParameterTypes();
		JType[] proxyTypes = method.getParameterTypes();

		if ((proxyTypes.length -1)!= implTypes.length)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. The implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName() + " does not match the parameters list.");
		}
		for (int i=0; i<implTypes.length; i++)
		{
			if (!isTypesCompatiblesForSerialization(implTypes[i], proxyTypes[i]))
			{
				throw new CruxGeneratorException("Invalid signature for rest proxy method. Incompatible parameters on method["+method.getReadableDeclaration()+"]");
			}
		}

		JClassType lastParameterType = proxyTypes[proxyTypes.length - 1].isClassOrInterface();
		if (!isTypesCompatiblesForSerialization(implementationMethod.getReturnType(), JClassUtils.getTypeArgForGenericType(lastParameterType)))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Return type of implementation method is not compatible with Callback's type. Method["+method.getReadableDeclaration()+"]");
		}
	}

	protected boolean isTypesCompatiblesForSerialization(Class<?> class1, JType jType)
	{
		if (jType.isEnum() != null)
		{
			return isEnumTypesCompatibles(class1, jType.isEnum());
		}
		else if (JClassUtils.isSimpleType(jType))
		{
			return (getAllowedType(jType).contains(class1));
		}
		else
		{ 
			JClassType classOrInterface = jType.isClassOrInterface();
			if (classOrInterface != null)
			{
				if (javascriptObjectType.isAssignableFrom(classOrInterface))
				{
					if (classOrInterface.getQualifiedSourceName().equals(JsArray.class.getCanonicalName()))
					{
						boolean validArray = false;
						if (class1.isArray())
						{
							Class<?> componentType = class1.getComponentType();
							JClassType jClassType = jType.isClassOrInterface();
							validArray = jClassType != null && isTypesCompatiblesForSerialization(componentType, JClassUtils.getTypeArgForGenericType(jClassType));
						}
						return validArray || (List.class.isAssignableFrom(class1)) || (Set.class.isAssignableFrom(class1));
					}
					else
					{
						return true;
					}
				}
			}
		}
		//Use a jsonEncorer implicitly
		return true;
	}

	protected boolean isEnumTypesCompatibles(Class<?> class1, JEnumType jType)
	{
		if (class1.isEnum())
		{
			Object[] values1 = class1.getEnumConstants();
			JEnumConstant[] values2 = jType.getEnumConstants();
			if (values1.length != values2.length)
			{
				return false;
			}
			for (JEnumConstant jEnumConstant : values2)
			{
				String name = jEnumConstant.getName();
				boolean found = false;
				for (Object enumConstant : values1)
				{
					if (name.equals(enumConstant.toString()))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					return false;
				}
			}
			return true;
		}
		else if (String.class.isAssignableFrom(class1))
		{
			return true;
		}
		return false;
	}

	protected static List<Class<?>> getAllowedType(JType jType)
	{
		List<Class<?>> result = new ArrayList<Class<?>>();
		JPrimitiveType primitiveType = jType.isPrimitive();
		if (primitiveType == JPrimitiveType.INT || jType.getQualifiedSourceName().equals(Integer.class.getCanonicalName()))
		{
			result.add(Integer.TYPE);
			result.add(Integer.class);
		}
		else if (primitiveType == JPrimitiveType.SHORT || jType.getQualifiedSourceName().equals(Short.class.getCanonicalName()))
		{
			result.add(Short.TYPE);
			result.add(Short.class);
		}
		else if (primitiveType == JPrimitiveType.LONG || jType.getQualifiedSourceName().equals(Long.class.getCanonicalName()))
		{
			result.add(Long.TYPE);
			result.add(Long.class);
		}
		else if (primitiveType == JPrimitiveType.BYTE || jType.getQualifiedSourceName().equals(Byte.class.getCanonicalName()))
		{
			result.add(Byte.TYPE);
			result.add(Byte.class);
		}
		else if (primitiveType == JPrimitiveType.FLOAT || jType.getQualifiedSourceName().equals(Float.class.getCanonicalName()))
		{
			result.add(Float.TYPE);
			result.add(Float.class);
		}
		else if (primitiveType == JPrimitiveType.DOUBLE || jType.getQualifiedSourceName().equals(Double.class.getCanonicalName()))
		{
			result.add(Double.TYPE);
			result.add(Double.class);
		}
		else if (primitiveType == JPrimitiveType.BOOLEAN || jType.getQualifiedSourceName().equals(Boolean.class.getCanonicalName()))
		{
			result.add(Boolean.TYPE);
			result.add(Boolean.class);
		}
		else if (primitiveType == JPrimitiveType.CHAR || jType.getQualifiedSourceName().equals(Character.class.getCanonicalName()))
		{
			result.add(Character.TYPE);
			result.add(Character.class);
		}
		else if (jType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			result.add(String.class);
		}
		else if (jType.getQualifiedSourceName().equals(Date.class.getCanonicalName()))
		{
			result.add(Date.class);
		}
		else if (jType.getQualifiedSourceName().equals(BigInteger.class.getCanonicalName()))
		{
			result.add(BigInteger.class);
		}
		else if (jType.getQualifiedSourceName().equals(BigDecimal.class.getCanonicalName()))
		{
			result.add(BigDecimal.class);
		}
		return result;
	}
}
