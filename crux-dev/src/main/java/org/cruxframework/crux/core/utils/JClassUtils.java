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
package org.cruxframework.crux.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JClassUtils
{

	public static JType buildGetValueExpression(StringBuilder out, JClassType dtoType, String propertyPath, 
			String objectVariable, boolean finishCommand) 
					throws NoSuchFieldException
    {
        if (StringUtils.isEmpty(propertyPath))
        {
			throw new NoSuchFieldException(propertyPath);
        }
        String[] props;
        if (propertyPath.contains("."))
        {
        	props = propertyPath.split("\\.");
        }
        else
        {
        	props = new String[]{propertyPath};
        }
        
        if (props != null && props.length > 0)
        {
        	StringBuilder getExpression = new StringBuilder();
        	StringBuilder checkNullExpression = new StringBuilder();
        	
        	getExpression.append(objectVariable);
        	JType baseType = dtoType;
        	JClassType baseClassType = baseType.isClassOrInterface();
        	for (int i=0; i < props.length; i++)
        	{
        		if (baseClassType == null && i < props.length-1)
        		{
        			throw new NoSuchFieldException(propertyPath);
        		}
        		String prop = props[i];
        		if (i>0)
        		{
        			if (i>1)
        			{
        				checkNullExpression.append(" || ");
        			}
        			checkNullExpression.append(getExpression.toString()+"==null ");
        		}
        		
        		String getterMethod = JClassUtils.getGetterMethod(prop, baseClassType);
        		if (getterMethod == null)
        		{
        			JMethod method = getMethod(baseClassType, prop, new JType[]{});
        			if (method == null)
        			{
        				throw new NoSuchFieldException(propertyPath);
        			}
        			else
        			{
        				getterMethod = prop;
        			}
        		}
        		getExpression.append("."+getterMethod+"()");
        		baseType = JClassUtils.getReturnTypeFromMethodClass(baseClassType, getterMethod, new JType[]{});
        		baseClassType = baseType.isClassOrInterface();
        	}
        	if (finishCommand)
        	{
        		getExpression.append(";");
        	}
        	
        	if (checkNullExpression.length() > 0)
        	{
        		out.append(checkNullExpression.toString()+"?null:");
        	}
        	out.append(getExpression.toString());
        	
        	return baseType;
        }
        else
        {
			throw new NoSuchFieldException(propertyPath);
        }
    }

	public static JClassType getType(TypeOracle typeOracle, String className) throws NotFoundException
	{
		return getType(typeOracle, className, null);
	}
	
	public static JClassType getType(TypeOracle typeOracle, String className, String viewName) throws NotFoundException
	{
		try
		{
			return typeOracle.getType(className);
		} catch (Exception e)
		{
			String message = "Class ["+className+"] " + (viewName != null ? ", declared on view ["+viewName+"]," : "") + " could not be loaded. "
			   + "\n Possible causes:"
			   + "\n\t 1. Check if any type or subtype used by this class refers to another module and if this module is inherited in the .gwt.xml file."
			   + "\n\t 2. Check if your class or its members belongs to a client package."
			   + "\n\t 3. Check the versions of all your modules.";
			
			throw new NotFoundException(message, e);
		}
	}
	
	public static JType buildSetValueExpression(SourcePrinter out, JClassType dtoType, String propertyPath, String objectVariable, String value) 
					throws NoSuchFieldException
    {
        if (StringUtils.isEmpty(propertyPath))
        {
			throw new NoSuchFieldException(propertyPath);
        }
        String[] props;
        if (propertyPath.contains("."))
        {
        	props = propertyPath.split("\\.");
        }
        else
        {
        	props = new String[]{propertyPath};
        }
        
        if (props != null && props.length > 0)
        {
        	StringBuilder getExpression = new StringBuilder();
        	getExpression.append(objectVariable);
        	JType baseType = dtoType;
        	JClassType baseClassType = baseType.isClassOrInterface();
        	for (int i=0; i < props.length-1; i++)
        	{
        		if (baseClassType == null && i < props.length-1)
        		{
        			throw new NoSuchFieldException(propertyPath);
        		}
        		String prop = props[i];
        		JClassType propertyType = getTypeForProperty(prop, baseClassType).isClassOrInterface();
        		
        		String getterMethod = getGetterMethod(prop, baseClassType);
        		String setterMethod = getSetterMethod(prop, baseClassType, propertyType);

        		out.println("if ("+getExpression.toString()+"."+getterMethod+"()==null){");
        		out.println(getExpression+"."+setterMethod+"(("+propertyType.getParameterizedQualifiedSourceName()+")"+GWT.class.getCanonicalName()+".create("+propertyType.getQualifiedSourceName()+".class));");
        		out.println("}");
        		getExpression.append("."+getterMethod+"()");
        		
        		baseClassType = propertyType;
        	}
    		String prop = props[props.length-1];
    		JType propertyType = getTypeForProperty(prop, baseClassType);
			String setterMethod = getSetterMethod(prop, baseClassType, propertyType);
			out.println(getExpression+"."+setterMethod+"("+value+");");
        	
        	return propertyType;
        }
        else
        {
			throw new NoSuchFieldException(propertyPath);
        }
    }			
	
	/**
	 * 
	 * @param propertyName
	 * @param baseClass 
	 * @return
	 */
	public static String getGetterMethod(String propertyName, JClassType baseClass)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = ""+Character.toUpperCase(propertyName.charAt(0)); 
		result += propertyName.substring(1);
		if (propertyName.length() > 1)
		{
			try
            {
	            baseClass.getMethod("get"+result, new JType[]{});
                result = "get"+result;
            }
            catch (Exception e)
            {
	            try
                {
	                baseClass.getMethod("is"+result, new JType[]{});
	                result = "is"+result;
                }
                catch (Exception e1)
                {
                	if (baseClass.getSuperclass() == null)
                	{
                		result = null;
                	}
                	else
                	{
                		result = getGetterMethod(propertyName, baseClass.getSuperclass());
                	}
                }
            }
			
		}
		return result;
	}

	/**
	 * 
	 * @param propertyName
	 * @param baseClass 
	 * @return
	 */
	public static String getSetterMethod(String propertyName, JClassType baseClass, JType propertyType)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = ""+Character.toUpperCase(propertyName.charAt(0)); 
		result += propertyName.substring(1);
		if (propertyName.length() > 1)
		{
			try
            {
	            baseClass.getMethod("set"+result, new JType[]{propertyType});
                result = "set"+result;
            }
            catch (Exception e)
            {
            	if (baseClass.getSuperclass() == null)
            	{
            		result = null;
            	}
            	else
            	{
            		result = getSetterMethod(propertyName, baseClass.getSuperclass(), propertyType);
            	}
            }
		}
		return result;
	}

	/**
	 * @param methodName
	 * @return
	 */
	public static JType getReturnTypeFromMethodClass(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = getMethod(clazz, methodName, params);
		
		if (method == null)
		{
			return null;
		}
		JType returnType = method.getReturnType();
		return returnType;
    }

	/**
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 */
	public static JMethod getMethod(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = null;
	    JClassType superClass = clazz;
	    while (method == null && superClass != null)
	    {
	    	method = superClass.findMethod(methodName, params);
	    	superClass = superClass.getSuperclass();
	    }
	    return method;
    }

	/**
	 * Retrieve a field from class. Searches also into the class hierarchy 
	 * @param clazz class to search the field
	 * @param fieldName field name
	 * @return the field
	 */
	public static JField getField(JClassType clazz, String fieldName)
    {
	    JField field = null;
	    JClassType superClass = clazz;
	    while (field == null && superClass != null)
	    {
	    	field = superClass.findField(fieldName);
	    	superClass = superClass.getSuperclass();
	    }
	    return field;
    }

	/**
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static JMethod[] findMethods(JClassType clazz, String methodName)
	{
		List<JMethod> result = new ArrayList<JMethod>();
		JMethod[] methods = null;
	    JClassType superClass = clazz;
	    while (superClass.getSuperclass() != null)
	    {
	    	methods = superClass.getMethods();
	    	if (methods != null)
	    	{
	    		for (JMethod method : methods)
                {
	                if (method.getName().equals(methodName))
	                {
	                	result.add(method);
	                }
                }
	    	}
	    	superClass = superClass.getSuperclass();
	    }
	    return result.toArray(new JMethod[result.size()]);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static JMethod[] findSetterMethods(JClassType clazz, String propertyName)
	{
		String setterMethodName = "set"+Character.toUpperCase(propertyName.charAt(0)); 
		if (propertyName.length() > 1)
		{
			setterMethodName += propertyName.substring(1);
		}
		
		JMethod[] methods = findMethods(clazz, setterMethodName);
		List<JMethod> result = new ArrayList<JMethod>();
		for (JMethod method : methods)
        {
	        if (method.getParameters().length == 1)
	        {
	        	result.add(method);
	        }
        }
		return result.toArray(new JMethod[result.size()]);
	}
	

	
	
	/**
	 * 
	 * @param valueVariable
	 * @param expectedType
	 * @return
	 * @throws NotFoundException 
	 */
	public static String getParsingExpressionForSimpleType(String valueVariable, JType expectedType) throws NotFoundException
	{
		if (expectedType == JPrimitiveType.INT || Integer.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Integer.parseInt("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.SHORT || Short.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Short.parseShort("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.LONG || Long.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Long.parseLong("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BYTE || Byte.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Byte.parseByte("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.FLOAT || Float.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Float.parseFloat("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.DOUBLE || Double.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Double.parseDouble("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Boolean.parseBoolean("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.CHAR || Character.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return valueVariable+".charAt(0)";
		}
		else if (Date.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "new "+Date.class.getCanonicalName()+"(Long.parseLong("+valueVariable+"))";
		}
		else if (expectedType.isEnum() != null)
		{
			return expectedType.getQualifiedSourceName()+".valueOf("+valueVariable+")";
		}
		else
		{
			JClassType stringType = ((JClassType)expectedType).getOracle().getType(String.class.getName());
		    if (stringType.isAssignableFrom((JClassType)expectedType))
		    {
		    	return valueVariable;
		    }
		    
		    if (((JClassType)expectedType).findConstructor(new JType[]{stringType}) != null)
		    {
		    	return "new "+expectedType.getQualifiedSourceName()+"("+valueVariable+")";
		    }
		    JMethod valueOfMethod = ((JClassType)expectedType).findMethod("valueOf", new JType[]{stringType});
			if (valueOfMethod != null && valueOfMethod.isStatic())
		    {
		    	return expectedType.getQualifiedSourceName()+".valueOf("+valueVariable+")";
		    }
		    JMethod fromStringMethod = ((JClassType)expectedType).findMethod("fromString", new JType[]{stringType});
			if (fromStringMethod != null && fromStringMethod.isStatic())
		    {
		    	return expectedType.getQualifiedSourceName()+".fromString("+valueVariable+")";
		    }
		}

		return null;
	}

	/**
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static JField getDeclaredField(JClassType clazz, String name) throws NoSuchFieldException
	{
		JField field = clazz.getField(name);
		if (field == null)
		{
			if (clazz.getSuperclass() == null)
			{
				throw new NoSuchFieldException(name);
			}
			field = getDeclaredField(clazz.getSuperclass(), name);
		}

		return field;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public static JField[] getDeclaredFields(JClassType clazz)
	{
		if (clazz.getSuperclass() == null)
		{
			return new JField[0];
		}
		Set<JField> result = new HashSet<JField>();
		JField[] declaredFields = clazz.getFields();
		for (JField field : declaredFields)
		{
			result.add(field);
		}
		clazz = clazz.getSuperclass();
		while (clazz.getSuperclass() != null)
		{
			declaredFields = clazz.getFields();
			for (JField field : declaredFields)
			{
				if (!result.contains(field))
				{
					result.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		return result.toArray(new JField[result.size()]);
	}
	
	/**
	 * @param method
	 * @return
	 */
	public static String getMethodDescription(JMethod method)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(method.getEnclosingType().getQualifiedSourceName());
		str.append(".");
		str.append(method.getName());
		str.append("(");
		boolean needsComma = false;
		
		for (JParameter parameter: method.getParameters())
		{
			if (needsComma)
			{
				str.append(",");
			}
			needsComma = true;
			str.append(parameter.getType().getParameterizedQualifiedSourceName());
		}
		str.append(")");
		
		return str.toString();
	}
	
	/**
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	public static String getGenericDeclForType(JType type)
	{
		if (type.isPrimitive() != null)
		{
			JPrimitiveType jPrimitiveType = type.isPrimitive();
			return jPrimitiveType.getQualifiedBoxedSourceName();
		}
		else
		{
			return type.getParameterizedQualifiedSourceName();
		}
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static boolean isSimpleType(JType type)
	{
		if (type instanceof JPrimitiveType)
		{
			return true;
		}
		else
		{
			try
            {
	            JClassType classType = (JClassType)type;

	            JClassType charSequenceType = classType.getOracle().getType(CharSequence.class.getCanonicalName());
	            JClassType dateType = classType.getOracle().getType(Date.class.getCanonicalName());
	            JClassType numberType = classType.getOracle().getType(Number.class.getCanonicalName());
	            JClassType booleanType = classType.getOracle().getType(Boolean.class.getCanonicalName());
	            JClassType characterType = classType.getOracle().getType(Character.class.getCanonicalName());
	            JClassType bigIntegerType = classType.getOracle().getType(BigInteger.class.getCanonicalName());
	            JClassType bigDecimalType = classType.getOracle().getType(BigDecimal.class.getCanonicalName());

	            return (classType.isPrimitive() != null) ||
	            (numberType.isAssignableFrom(classType)) ||
	            (booleanType.isAssignableFrom(classType)) ||
	            (characterType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (dateType.isAssignableFrom(classType)) ||
	            (bigIntegerType.isAssignableFrom(classType)) ||
	            (bigDecimalType.isAssignableFrom(classType)) ||
	            (classType.isEnum() != null);
            }
            catch (NotFoundException e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }		
		}
	}
	
	/**
	 * Returns <code>true</code> is the given field has both a "get" and a "set" methods.
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static boolean hasGetAndSetMethods(JField field, JClassType clazz)
	{
		return hasGetMethod(field, clazz) && hasSetMethod(field, clazz);
	}
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "get" method.
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static boolean hasGetMethod(JField field, JClassType clazz)
	{
		String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
		}
		catch (Exception e)
		{
			try
			{
				getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
				return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
			}
			catch (Exception e1)
			{
				if (clazz.getSuperclass() == null)
				{
					return false;
				}
				else
				{
					return hasGetMethod(field, clazz.getSuperclass());
				}
			}
		}
	}	
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "set" method.
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static boolean hasSetMethod(JField field, JClassType clazz)
	{
		String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(setterMethodName, new JType[]{field.getType()}) != null);
		}
		catch (Exception e)
		{
			if (clazz.getSuperclass() == null)
			{
				return false;
			}
			else
			{
				return hasSetMethod(field, clazz.getSuperclass());
			}
		}
	}
	
	/**
	 * Verify if the given field is fully accessible.
	 * @param field
	 * @param clazz
	 * @return <code>true</code> if the field is public or has associated "get" and "set" methods.
	 */
	public static boolean isFullAccessibleField(JField field, JClassType clazz)
	{
		return field.isPublic() || hasGetAndSetMethods(field, clazz);
	}	
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @param allowProtected
	 * @return
	 */
	public static boolean isPropertyVisibleToRead(JClassType voClass, JField field, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			return true;
		}
		else
		{
			return hasGetMethod(field, voClass);
		}
	}
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @param allowProtected
	 * @return
	 */
	public static boolean isPropertyVisibleToWrite(JClassType voClass, JField field, boolean allowProtected)
	{
		if ((field.isPublic() || (allowProtected && field.isProtected())) && !field.isFinal())
		{
			return true;
		}
		else
		{
			return hasSetMethod(field, voClass);
		}
	}

	/**
	 * Generates a property set block. First try to set the field directly, then try to use a javabean setter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param valueVariable
	 * @param sourceWriter
	 */
	public static void generateFieldValueSet(JClassType voClass, JField field, String parentVariable,  
			                           String valueVariable, SourcePrinter sourceWriter, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			sourceWriter.println(parentVariable+"."+field.getName()+"="+valueVariable+";");
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(setterMethodName, new JType[]{field.getType()}) != null)
				{
					sourceWriter.println(parentVariable+"."+setterMethodName+"("+valueVariable+");");
				}
			}
			catch (Exception e)
			{
				throw new CruxGeneratorException("Property ["+field.getName()+"] could not be created. This is not visible neither has a getter/setter method.");
			}
		}
	}
	
	/**
	 * Retrieve the property type on the given class
	 * @param clazz base class
	 * @param propertyName property name
	 * @return property type or null, if property is not present
	 */
	public static JType getPropertyType(JClassType clazz, String propertyName)
    {
	    JType propertyType = null;
	    JMethod method = JClassUtils.getMethod(clazz, JClassUtils.getGetterMethod(propertyName, clazz), new JType[]{});
	    if (method != null)
	    {
	    	propertyType = method.getReturnType();
	    }
	    else
	    {
	    	JField field = JClassUtils.getField(clazz, propertyName);
	    	if (field != null)
	    	{
	    		propertyType = field.getType();
	    	}
	    }
	    return propertyType;
    }	
	
	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * @param clazz class where the property will be searched.
	 * @param propertyName property name
	 * @param parentVariable the name of the parent variable to use in generated expression
	 * @param allowProtected if this expression allow protected fields and methods access
	 * @return an expression in the form {@code <parentVar>.<propertyAccessor>}
	 */
	public static String getFieldValueGet(JClassType clazz, String propertyName, String parentVariable, boolean allowProtected)
	{
		JMethod jMethod = JClassUtils.getMethod(clazz, JClassUtils.getGetterMethod(propertyName, clazz), new JType[]{});
		if (jMethod != null && (jMethod.isPublic() || (allowProtected && jMethod.isProtected())))
		{
			return (parentVariable+"."+jMethod.getName()+"()");
		}
		JField field = getField(clazz, propertyName);

		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			return parentVariable+"."+field.getName();
		}

		throw new CruxGeneratorException("Property ["+propertyName+"] could not be created. It is not visible neither has a getter/setter method.");
	}
	
	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param allowProtected
	 */
	@Deprecated
	public static String getFieldValueGet(JClassType voClass, JField field, String parentVariable, boolean allowProtected)
	{
		return getFieldValueGet(voClass, field.getName(), parentVariable, allowProtected);
	}

	public static boolean isValidSetterMethod(JMethod method)
	{
        return (method.isPublic() && method.getName().startsWith("set") && method.getName().length() >3 && method.getParameters().length == 1);
	}
	
	public static boolean isValidGetterMethod(JMethod method)
	{
        return ((method.isPublic() && method.getName().startsWith("get") && method.getName().length() >3 
        		&& method.getParameters().length == 0) && !method.getName().equals("getClass") 
        		|| (method.isPublic() && method.getName().startsWith("is") && method.getName().length() >2 
                		&& method.getParameters().length == 0) 
                		&& (method.getReturnType() == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(method.getReturnType().getQualifiedSourceName()))
        		);
	}
	
	public static String getPropertyForGetterOrSetterMethod(JMethod method)
    {
		String name = method.getName();
		if (name.startsWith("get") || name.startsWith("set"))
		{
			name = name.substring(3);
		}
		else if (name.startsWith("is"))
		{
			name = name.substring(2);
		}
		name = Character.toLowerCase(name.charAt(0))+ name.substring(1);
		
		return name;
    }
	
	public static List<JMethod> getSetterMethods(JClassType objectType)
    {
		List<JMethod> result = new ArrayList<JMethod>();
	    JMethod[] methods = objectType.getOverridableMethods();
	    
	    for (JMethod jMethod : methods)
        {
	        if (isValidSetterMethod(jMethod))
	        {
	        	result.add(jMethod);
	        }
        }
	    
	    return result;
    }

	public static List<JMethod> getGetterMethods(JClassType objectType)
    {
		List<JMethod> result = new ArrayList<JMethod>();
	    JMethod[] methods = objectType.getOverridableMethods();
	    
	    for (JMethod jMethod : methods)
        {
	        if (isValidGetterMethod(jMethod))
	        {
	        	result.add(jMethod);
	        }
        }
	    
	    return result;
    }

	public static class PropertyInfo
	{
		private final String name;
		private final JType type;
		private final JMethod readMethod;
		private final JMethod writeMethod;

		public PropertyInfo(String name, JType type, JMethod readMethod, JMethod writeMethod)
        {
			this.name = name;
			this.type = type;
			this.readMethod = readMethod;
			this.writeMethod = writeMethod;
        }

		public String getName()
        {
        	return name;
        }

		public JType getType()
        {
        	return type;
        }

		public JMethod getReadMethod()
        {
        	return readMethod;
        }

		public JMethod getWriteMethod()
        {
        	return writeMethod;
        }
	}

	public static PropertyInfo[] extractBeanPropertiesInfo(JClassType type)
	{
		List<PropertyInfo> result = new ArrayList<PropertyInfo>();

		List<JMethod> getterMethods = getGetterMethods(type);
		List<JMethod> setterMethods = getSetterMethods(type);
		
		for (JMethod setterMethod : setterMethods)
        {
	        String setterProperty = getPropertyForGetterOrSetterMethod(setterMethod);
	        for (JMethod getterMethod : getterMethods)
            {
		        String getterProperty = getPropertyForGetterOrSetterMethod(getterMethod);
	            if (getterProperty.equals(setterProperty))
	            {
					result.add(new PropertyInfo(setterProperty, getterMethod.getReturnType(), getterMethod, setterMethod));
					break;
	            }
            }
        }
		return result.toArray(new PropertyInfo[result.size()]);
	}
	
	public static JType getTypeForProperty(String property, JClassType objectType)
	{
		if (objectType == null)
		{
			return null;
		}
		int index = property.indexOf('.');
		if (index < 0)
		{
			String getterMethod = JClassUtils.getGetterMethod(property, objectType);
			if (StringUtils.isEmpty(getterMethod))
			{
				return null;
			}
			return JClassUtils.getReturnTypeFromMethodClass(objectType, getterMethod, new JType[]{});
		}
		String getterMethod = JClassUtils.getGetterMethod(property.substring(0, index), objectType);
		if (StringUtils.isEmpty(getterMethod))
		{
			return null;
		}
		return getTypeForProperty(property.substring(index+1), JClassUtils.getReturnTypeFromMethodClass(objectType, getterMethod, new JType[]{}).isClassOrInterface());
	}
	
	
	public static String getEmptyValueForType(JType objectType)
    {
		JPrimitiveType primitiveType = objectType.isPrimitive();
		if (primitiveType != null)
		{
			if ((primitiveType == JPrimitiveType.INT)
					||(primitiveType == JPrimitiveType.SHORT)
					||(primitiveType == JPrimitiveType.LONG)
					||(primitiveType == JPrimitiveType.BYTE)
					||(primitiveType == JPrimitiveType.FLOAT)
					||(primitiveType == JPrimitiveType.DOUBLE))
					{
				return "0";
					}
			else if (primitiveType == JPrimitiveType.BOOLEAN)
			{
				return "false";
			}
			else if (primitiveType == JPrimitiveType.CHAR)
			{
				return "' '";
			}
		}
		
		return "null";
    }
	
	public static JClassType getTypeArgForGenericType(JClassType type)
    {
	    JParameterizedType parameterized = type.isParameterized();
	    if (parameterized == null)
	    {
	    	return type.getOracle().findType("java.lang.Object");
	    }
	    JClassType jClassType = parameterized.getTypeArgs()[0];
	    return jClassType;
    }
	
	public static JClassType[] getActualParameterTypes(JClassType baseType, JClassType desiredInterfaceType)
	{
		Set<? extends JClassType> interfaces = baseType.getFlattenedSupertypeHierarchy();
		for (JClassType intf : interfaces)
		{
			JParameterizedType parameterized = intf.isParameterized();
			if (parameterized != null)
			{
				if (parameterized.getBaseType().getQualifiedSourceName().equals(desiredInterfaceType.getQualifiedSourceName()))
				{
					return parameterized.getTypeArgs();
				}
			}
		}
		throw new RuntimeException("Desired interface ["+desiredInterfaceType.getQualifiedSourceName()+"] is nor parameterized or baseIntef does not extends that interface.");
	}
}
