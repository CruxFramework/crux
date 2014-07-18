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
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.rest.RestProxy.TargetEndPoint;
import org.cruxframework.crux.core.client.rest.RestProxy.TargetRestService;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceFactoryInitializer;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.shared.rest.RestException;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.Path;
import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;
import org.cruxframework.crux.core.utils.EncryptUtils;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxRestProxyCreatorFromServerMetadata extends CruxRestProxyCreator
{
	private Class<?> restImplementationClass;

	public CruxRestProxyCreatorFromServerMetadata(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
	{
		super(logger, context, baseIntf);
	}

	@Override
	protected void generateHostPathInitialization(SourcePrinter srcWriter)
    {
		TargetEndPoint targetEndPoint = baseIntf.getAnnotation(TargetEndPoint.class);
		if (targetEndPoint != null)
		{
			String basePath = targetEndPoint.value();
			if (basePath.endsWith("/"))
			{
				basePath = basePath.substring(0, basePath.length()-1);
			}

			srcWriter.println("__hostPath = \""+basePath+"\";");
		}
		else
		{
			if(Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableRestHostPageBaseURL()))
			{
				srcWriter.println("__hostPath = com.google.gwt.core.client.GWT.getHostPageBaseURL();");
			} else 
			{
				srcWriter.println("__hostPath = com.google.gwt.core.client.GWT.getModuleBaseURL();");
			}
			srcWriter.println("__hostPath = __hostPath.substring(0, __hostPath.lastIndexOf(com.google.gwt.core.client.GWT.getModuleName()));");
		}
    }
	
	@Override
	protected String getServiceBasePath(GeneratorContext context)
    {
		restImplementationClass = getRestImplementationClass(baseIntf);
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
		if (value == null)
		{
			value = "";
		}
		else if (value.startsWith("/"))
		{
			value = value.substring(1);
		}
		return basePath+"/"+value;
    }

	@Override
	protected RestMethodInfo getRestMethodInfo(JMethod method) throws InvalidRestMethod
    {
	    Method implementationMethod = getImplementationMethod(method);
	    Annotation[][] parameterAnnotations = implementationMethod.getParameterAnnotations();
		String methodURI = getRestURI(method, parameterAnnotations, implementationMethod.getAnnotation(Path.class));
	    StateValidationModel validationModel = HttpMethodHelper.getStateValidationModel(implementationMethod);
	    String httpMethod = HttpMethodHelper.getHttpMethod(implementationMethod.getAnnotations(), false);
	    boolean isReadMethod = implementationMethod.getAnnotation(GET.class) != null;
	    RestMethodInfo methodInfo = new RestMethodInfo(method, parameterAnnotations, methodURI, httpMethod, validationModel, isReadMethod);
	    return methodInfo;
    }

	@Override
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

			Class<?>[] restExceptionTypes = getRestExceptionTypes(getImplementationMethod(methodInfo.method));
			if (restExceptionTypes != null && restExceptionTypes.length > 0)
			{
				srcWriter.println("JSONValue exId = jsonObject.get(\"exId\");");
				srcWriter.println("if (exId == null){");
				srcWriter.println("JSONValue jsonErrorMsg = jsonObject.get(\"message\");");
				srcWriter.println("String stringJsonErrorMsg = (jsonErrorMsg != null && jsonErrorMsg.isString() != null) ? jsonErrorMsg.isString().stringValue() : \"\";");
				srcWriter.println(callbackParameterName+".onError(new RestError("+responseVariable+".getStatusCode(), stringJsonErrorMsg));");
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
				srcWriter.println(callbackParameterName+".onError(new RestError("+responseVariable+".getStatusCode(), jsonObject.get(\"exData\").toString()));");
				srcWriter.println("}");

				srcWriter.println("}");
			}
			else
			{
				srcWriter.println(callbackParameterName+".onError(new RestError("+responseVariable+".getStatusCode(), (jsonObject.get(\"message\") != null && jsonObject.get(\"message\").isString() != null) ? jsonObject.get(\"message\").isString().stringValue() : \"\"));");
			}
		}
		catch (Exception e) 
		{
			throw new CruxGeneratorException("Error generatirng exception handlers for type ["+baseIntf.getParameterizedQualifiedSourceName()+"].", e);
		}
	}

	private Class<?> getRestImplementationClass(JClassType baseIntf)
	{
		TargetRestService restService = baseIntf.getAnnotation(TargetRestService.class);
		if (restService == null)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Use @RestProxy.TargetRestService annotation to inform the target of current proxy.");
		}
		String serviceName = restService.value();
		try
		{
			return RestServiceFactoryInitializer.getServiceFactory().getServiceClass(serviceName);
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Can not found the implementationClass for service name ["+serviceName+"].");
		}
	}

	private Class<?>[] getRestExceptionTypes(Method method)
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

	private Method getImplementationMethod(JMethod method)
	{
		Method implementationMethod = getImplementationMethod(method, restImplementationClass);					
		validateImplementationMethod(method, implementationMethod);
		return implementationMethod;
	}

	private Method getImplementationMethod(JMethod method, Class<?> clazz)
    {
		Method[] allMethods = clazz.getMethods();
		for (Method m: allMethods)
		{
			if (!m.isSynthetic() && m.getName().equals(method.getName()))
			{
				return m;
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null)
		{
			return getImplementationMethod(method, superClass);
		}
		
	    return null;
    }

	private void validateImplementationMethod(JMethod method, Method implementationMethod)
	{
		if (implementationMethod == null)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Can not found the implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName());
		}
		
		if (!Modifier.isPublic(implementationMethod.getModifiers()))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName()+", is not public.");
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

	private boolean isTypesCompatiblesForSerialization(Class<?> class1, JType jType)
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

	private boolean isEnumTypesCompatibles(Class<?> class1, JEnumType jType)
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

	private static List<Class<?>> getAllowedType(JType jType)
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
		else if (jType.getQualifiedSourceName().equals(java.sql.Date.class.getCanonicalName()))
		{
			result.add(java.sql.Date.class);
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
