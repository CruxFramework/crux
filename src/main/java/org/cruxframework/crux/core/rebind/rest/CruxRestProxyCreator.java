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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.rest.Callback;
import org.cruxframework.crux.core.client.rest.RestError;
import org.cruxframework.crux.core.client.rest.RestProxy;
import org.cruxframework.crux.core.client.rest.RestProxy.UseJsonP;
import org.cruxframework.crux.core.client.rpc.CruxRpcRequestBuilder;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.client.screen.views.ViewBindable;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.shared.rest.annotation.Path;
import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public abstract class CruxRestProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private static final String RANDOM_TOKEN = new BigInteger(130, new SecureRandom()).toString(32).substring(0, 5); 
	protected JClassType callbackType;
	protected JClassType javascriptObjectType;
	protected String serviceBasePath;
	protected Set<String> readMethods = new HashSet<String>();
	protected Set<String> updateMethods = new HashSet<String>();
	protected Set<RestMethodInfo> restMethods = new HashSet<RestMethodInfo>();
	protected boolean mustGenerateStateControlMethods;
	protected QueryParameterHandler queryParameterHandler;
	protected BodyParameterHandler bodyParameterHandler;
	protected JClassType restProxyType;
	protected boolean useJsonP;
	protected String jsonPCallbackParam;
	protected String jsonPFailureCallbackParam;
	protected JsonPRestCreatorHelper jsonPRestCreatorHelper;
	private JClassType viewBindableType;
	private JClassType viewAwareType;

	public CruxRestProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, false);
		callbackType = context.getTypeOracle().findType(Callback.class.getCanonicalName());
		restProxyType = context.getTypeOracle().findType(RestProxy.class.getCanonicalName());
		javascriptObjectType = context.getTypeOracle().findType(JavaScriptObject.class.getCanonicalName());
		viewBindableType = context.getTypeOracle().findType(ViewBindable.class.getCanonicalName());
		viewAwareType = context.getTypeOracle().findType(ViewAware.class.getCanonicalName());
		UseJsonP jsonP = baseIntf.getAnnotation(UseJsonP.class);
		useJsonP = jsonP != null;
		if (useJsonP)
		{
			jsonPRestCreatorHelper = new JsonPRestCreatorHelper(context, logger);
			jsonPCallbackParam = jsonP.callbackParam();
			jsonPFailureCallbackParam = jsonP.failureCallbackParam();
		}
		queryParameterHandler = new QueryParameterHandler(context);
		bodyParameterHandler = new BodyParameterHandler(logger, context);
		serviceBasePath = getServiceBasePath(context);
		initializeRestMethods();
	}

	protected abstract String getServiceBasePath(GeneratorContext context);
	protected abstract void generateHostPathInitialization(SourcePrinter srcWriter);
	protected abstract RestMethodInfo getRestMethodInfo(JMethod method) throws InvalidRestMethod;

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException 
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		generateHostPathInitialization(srcWriter);
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
		srcWriter.println("private String __view;");
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
		generateSetEndpointMethod(srcWriter);
		generateViewBindableMethods(srcWriter);
	}

	protected void generateViewBindableMethods(SourcePrinter sourceWriter)
    {
		sourceWriter.println("public String getBoundCruxViewId(){");
		sourceWriter.println("return this.__view;");
		sourceWriter.println("}");
		sourceWriter.println();
		sourceWriter.println("public "+View.class.getCanonicalName()+" getBoundCruxView(){");
		sourceWriter.println("return (this.__view!=null?"+View.class.getCanonicalName()+".getView(this.__view):null);");
		sourceWriter.println("}");
		sourceWriter.println();
		sourceWriter.println("public void bindCruxView(String view){");
		sourceWriter.println("this.__view = view;");
		sourceWriter.println("}");
		sourceWriter.println();
    }
	
	protected void generateSetEndpointMethod(SourcePrinter srcWriter) 
	{
		srcWriter.println("public void setEndpoint(String address){");
		srcWriter.println("this.__hostPath = address;");
		srcWriter.println("if (__hostPath.endsWith(\"/\")){");
		srcWriter.println("__hostPath = __hostPath.substring(0, __hostPath.length()-1);");
		srcWriter.println("}");
		srcWriter.println("}");
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
			try
			{
				if ((!restProxyType.equals(method.getEnclosingType())) && 
					(!viewAwareType.equals(method.getEnclosingType())) &&
					(!viewBindableType.equals(method.getEnclosingType())))
				{
					validateProxyMethod(method);
					RestMethodInfo methodInfo = getRestMethodInfo(method);
					
					if (useJsonP)
					{
						if (methodInfo.isReadMethod)
						{
							readMethods.add(methodInfo.methodURI);
						}
						else if (methodInfo.validationModel != null && !methodInfo.validationModel.equals(StateValidationModel.NO_VALIDATE))
						{
							updateMethods.add(methodInfo.methodURI);
						}
					}
					
					restMethods.add(methodInfo);
				}
			}
			catch (InvalidRestMethod e)
			{
				throw new CruxGeneratorException("Invalid Method: " + method.getEnclosingType().getName() + "." + method.getName() + "().", e);
			}
		}

		mustGenerateStateControlMethods = false;
		for (String methodURI : updateMethods)
		{
			if (!readMethods.contains(methodURI))
			{
				throw new CruxGeneratorException("Can not create the rest proxy. Can not found the " +
						"GET method for state dependent write method ["+methodURI+"].");
			}
			mustGenerateStateControlMethods = true; 
		}
	}

	protected void generateWrapperMethod(RestMethodInfo methodInfo, SourcePrinter srcWriter)
	{
		List<JParameter> parameters = generateProxyWrapperMethodDeclaration(srcWriter, methodInfo.method);
		JParameter callbackParameter = parameters.get(parameters.size()-1);
		String callbackResultTypeName = getCallbackResultTypeName(callbackParameter.getType().isClassOrInterface());
		String callbackParameterName = callbackParameter.getName();

		srcWriter.println("String baseURIPath = " + EscapeUtils.quote(methodInfo.methodURI) + ";");
		queryParameterHandler.generateMethodParamToURICode(srcWriter, methodInfo, "baseURIPath");
		srcWriter.println("final String restURI = __hostPath + baseURIPath;");

		if (useJsonP)
		{
			jsonPRestCreatorHelper.generateJSONPInvocation(methodInfo, srcWriter, callbackParameter, callbackResultTypeName, 
										callbackParameterName, "restURI", jsonPCallbackParam, jsonPFailureCallbackParam);
		}
		else
		{
			generateAJAXInvocation(methodInfo, srcWriter, callbackParameter, callbackResultTypeName, callbackParameterName, "restURI");
		}
		srcWriter.println("}");
	}


	protected void generateAJAXInvocation(RestMethodInfo methodInfo, SourcePrinter srcWriter, JParameter callbackParameter, 
			String callbackResultTypeName, String callbackParameterName, String restURIParam)
    {
	    srcWriter.println("RequestBuilder builder = new RequestBuilder(RequestBuilder."+methodInfo.httpMethod+", "+restURIParam+");");
		setLocaleInfo(srcWriter, "builder");
		
		if (ConfigurationFactory.getConfigurations().sendCruxViewNameOnClientRequests().equals("true"))
		{
			srcWriter.println("builder.setHeader("+EscapeUtils.quote(CruxRpcRequestBuilder.VIEW_INFO_HEADER)+", __view);");
		}
		
		srcWriter.println("builder.setCallback(new RequestCallback(){");

		String responseVariable = getNonConflictedVarName("response", callbackParameter.getName());
		
		srcWriter.println("public void onResponseReceived(Request request, Response "+responseVariable+"){");
		srcWriter.println("int s = ("+responseVariable+".getStatusCode()-200);");
		srcWriter.println("if (s >= 0 && s < 10){");
		generateSuccessCallHandlingCode(methodInfo, srcWriter, callbackParameter, callbackResultTypeName, callbackParameterName, restURIParam);
		srcWriter.println("}else{ ");
		generateExceptionCallHandlingCode(methodInfo, srcWriter, callbackParameterName, responseVariable);
		srcWriter.println("}");
		srcWriter.println("}");

		srcWriter.println("public void onError(Request request, Throwable exception){");
		srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceUnexpectedError(exception.getMessage())));");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("try{");
		bodyParameterHandler.generateMethodParamToBodyCode(srcWriter, methodInfo, "builder", methodInfo.httpMethod);
		generateValidateStateBlock(srcWriter, methodInfo.validationModel, "builder", restURIParam, methodInfo.methodURI, callbackParameterName);
		generateXSRFHeaderProtectionForWrites(methodInfo.httpMethod, "builder", srcWriter);
		srcWriter.println("builder.send();");
		srcWriter.println("}catch (Exception e){");
		generateLogHandlingCode(srcWriter, "Level.SEVERE", "e");
		srcWriter.println(callbackParameterName+".onError(new RestError(-1, Crux.getMessages().restServiceUnexpectedError(e.getMessage())));");
		srcWriter.println("}");
    }

	private static void generateLogHandlingCode(SourcePrinter srcWriter, String logLevel, String e) 
	{
		srcWriter.println("if (LogConfiguration.loggingIsEnabled()){");
		srcWriter.println("__log.log("+logLevel+", "+e+".getMessage(), e);");
		srcWriter.println("}");
	}

	protected void generateExceptionCallHandlingCode(RestMethodInfo methodInfo, SourcePrinter srcWriter, String callbackParameterName, String responseVariable)
	{
		try
		{
			srcWriter.println("if (LogConfiguration.loggingIsEnabled()){");
			srcWriter.println("__log.log(Level.SEVERE, \"Error received from service: \"+"+responseVariable+".getText());");
			srcWriter.println("}");
			//try to parse response object
			srcWriter.println("JSONObject jsonObject = null;");
			srcWriter.println("try {");
			srcWriter.println("jsonObject = JSONParser.parseStrict("+responseVariable+".getText()).isObject();");
			//For instance if we have 400-404 server response, the object is not a json value. This will make JSON throws an Exception
			srcWriter.println("} catch (Exception exception) {");
			srcWriter.println(callbackParameterName+".onError(new RestError("+responseVariable+".getStatusCode(), "+responseVariable+".getText()));");
			srcWriter.println("return;");
			srcWriter.println("}");

			srcWriter.println(callbackParameterName+".onError(new RestError("+responseVariable+".getStatusCode(), (jsonObject.get(\"message\") != null && jsonObject.get(\"message\").isString() != null) ? jsonObject.get(\"message\").isString().stringValue() : \"\"));");
		}
		catch (Exception e) 
		{
			throw new CruxGeneratorException("Error generatirng exception handlers for type ["+baseIntf.getParameterizedQualifiedSourceName()+"].", e);
		}
	}
	
	//TODO: put this in a DeferredBindingUtils class
	private static String getNonConflictedVarName(String originalVar, String possibleConflictedVar)
	{
		if (possibleConflictedVar.equals(originalVar))
		{
			return originalVar + "_" + RANDOM_TOKEN;
		}
		//return the same variable to improve code legibility
		return originalVar;
	}

	protected void generateSuccessCallHandlingCode(RestMethodInfo methodInfo, SourcePrinter srcWriter, 
			JParameter callbackParameter, String callbackResultTypeName, String callbackParameterName, String restURIParam)
	{
		String resultVariable = getNonConflictedVarName("result", callbackParameter.getName());
		String responseVariable = getNonConflictedVarName("response", callbackParameter.getName());
		
		if (!callbackResultTypeName.equalsIgnoreCase("void"))
		{
			JClassType callbackResultType = JClassUtils.getTypeArgForGenericType(callbackParameter.getType().isClassOrInterface());
			srcWriter.println("String jsonText = "+responseVariable+".getText();");
			srcWriter.println("if (Response.SC_NO_CONTENT != "+responseVariable+".getStatusCode() && !"+StringUtils.class.getCanonicalName()+".isEmpty(jsonText)){");
			srcWriter.println("try{");

			if (callbackResultType != null && callbackResultType.isAssignableTo(javascriptObjectType))
			{
				srcWriter.println(callbackResultTypeName+" "+resultVariable+" = "+JsonUtils.class.getCanonicalName()+".safeEval(jsonText);");
			}
			else
			{
				srcWriter.println("JSONValue jsonValue = JSONParser.parseStrict(jsonText);");
				String serializerName = new JSonSerializerProxyCreator(context, logger, callbackResultType).create();
				srcWriter.println(callbackResultTypeName+" "+resultVariable+" = new "+serializerName+"().decode(jsonValue);");
			}
			generateSaveStateBlock(srcWriter, methodInfo.isReadMethod, responseVariable, restURIParam, methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess("+resultVariable+");");
			srcWriter.println("}catch (Exception e){");
			generateLogHandlingCode(srcWriter, "Level.SEVERE", "e");
			srcWriter.println("}");
			srcWriter.println("}else {");
			generateSaveStateBlock(srcWriter, methodInfo.isReadMethod, responseVariable, restURIParam, methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess(null);");
			srcWriter.println("}");
		}
		else
		{
			generateSaveStateBlock(srcWriter, methodInfo.isReadMethod, responseVariable, restURIParam, methodInfo.methodURI);
			srcWriter.println(callbackParameterName+".onSuccess(null);");
		}
	}

	protected void setLocaleInfo(SourcePrinter srcWriter, String builderVariable)
	{
		srcWriter.println("String _locale = "+Screen.class.getCanonicalName()+".getLocale();");
		srcWriter.println("if (_locale != null && !"+StringUtils.class.getCanonicalName()+".unsafeEquals(_locale, \"default\")){");
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

	protected void generateSaveStateBlock(SourcePrinter srcWriter, boolean isReadMethod, String responseVar, String uriVar, String uri)
	{
		if (mustGenerateStateControlMethods && readMethods.contains(uri) && updateMethods.contains(uri))
		{
			if (isReadMethod)
			{
				srcWriter.println("__saveCurrentEtag("+uriVar+", "+responseVar+");");
			}
		}
	}

	protected void generateValidateStateBlock(SourcePrinter srcWriter, StateValidationModel validationModel, String builderVar, String uriVar, String uri, String callbackParameterName)
	{
		if (mustGenerateStateControlMethods &&  readMethods.contains(uri) && updateMethods.contains(uri))
		{
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

	protected String getRestURI(JMethod method, Annotation[][] parameterAnnotations, Path path)
	{
		String methodPath = paths(serviceBasePath);
		if (path != null)
		{
			methodPath = paths(methodPath, path.value());
		}
		String queryString = queryParameterHandler.getQueryString(method, parameterAnnotations);
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
				segment = Encode.encodePath(PathUtils.getSegmentParameter(segment));
				path += segment;
			}
			else
			{
				segment = Encode.encodePath(PathUtils.getSegmentParameter(segment));
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
				JSONNumber.class.getCanonicalName(),
				JSONString.class.getCanonicalName(),
				JSONParser.class.getCanonicalName(), 
				URL.class.getCanonicalName(), 
				Crux.class.getCanonicalName(),
				Callback.class.getCanonicalName(),
				StringUtils.class.getCanonicalName(),
				RestError.class.getCanonicalName(),
				JsonpRequestBuilder.class.getCanonicalName(), 
				AsyncCallback.class.getCanonicalName()
		};
	}
	
	protected void validateProxyMethod(JMethod method)
	{
		if (method.getReturnType() != JPrimitiveType.VOID) 
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method <"+method.getName()+">. Any method must be void");
		}
		JType[] parameterTypes = method.getParameterTypes();
		if (parameterTypes == null || parameterTypes.length < 1)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method <"+method.getName()+">. Any method must have a last parameter of type Callback");
		}
		JClassType lastParameterType = parameterTypes[parameterTypes.length - 1].isClassOrInterface();
		if (lastParameterType == null || !callbackType.isAssignableFrom(lastParameterType))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method <"+method.getName()+">. Any method must have a last parameter of type Callback");
		}
	}
}