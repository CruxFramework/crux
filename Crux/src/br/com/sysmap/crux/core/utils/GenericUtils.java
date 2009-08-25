/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.utils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class is adapted from org.springframework.core.GenericTypeResolver class
 * 
 * @author Juergen Hoeller
 * @author Rob Harrop 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public abstract class GenericUtils
{
	/** Cache from Class to TypeVariable Map */
	private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> typeVariableCache =
			Collections.synchronizedMap(new WeakHashMap<Class<?>, Reference<Map<TypeVariable<?>, Type>>>());

	/**
	 * Determine the target type for the generic return type of the given method.
	 * @param clazz
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Class<?> resolveReturnType(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		
		Method method;
		try
		{
			method = clazz.getMethod(methodName, parameterTypes);
			return resolveReturnType(method, clazz);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Determine the target type for the generic return type of the given method.
	 * @param method the method to introspect
	 * @param clazz the class to resolve type variables against
	 * @return the corresponding generic parameter or return type
	 */
	static Class<?> resolveReturnType(Method method, Class<?> clazz) {
		if (method == null)
		{
			throw new NullPointerException("Method must not be null");
		}		
		Type genericType = method.getGenericReturnType();
		if (clazz == null)
		{
			throw new NullPointerException("Class must not be null");
		}		
		Map<TypeVariable<?>, Type> typeVariableMap = getTypeVariableMap(clazz);
		Type rawType = getRawType(genericType, typeVariableMap);
		return (rawType instanceof Class ? (Class<?>) rawType : method.getReturnType());
	}

	/**
	 * Resolve the given type variable against the given class.
	 * @param tv the type variable to resolve
	 * @param clazz the class that defines the type variable
	 * somewhere in its inheritance hierarchy
	 * @return the resolved type that the variable can get replaced with,
	 * or <code>null</code> if none found
	 */
	public static Type resolveTypeVariable(TypeVariable<?> tv, Class<?> clazz) {
		return getTypeVariableMap(clazz).get(tv);
	}


	/**
	 * Resolve the specified generic type against the given TypeVariable map.
	 * @param genericType the generic type to resolve
	 * @param typeVariableMap the TypeVariable Map to resolved against
	 * @return the type if it resolves to a Class, or <code>Object.class</code> otherwise
	 */
	static Class<?> resolveType(Type genericType, Map<TypeVariable<?>, Type> typeVariableMap) {
		Type rawType = getRawType(genericType, typeVariableMap);
		return (rawType instanceof Class ? (Class<?>) rawType : Object.class);
	}

	/**
	 * Determine the raw type for the given generic parameter type.
	 * @param genericType the generic type to resolve
	 * @param typeVariableMap the TypeVariable Map to resolved against
	 * @return the resolved raw type
	 */
	static Type getRawType(Type genericType, Map<TypeVariable<?>, Type> typeVariableMap) {
		Type resolvedType = genericType;
		if (genericType instanceof TypeVariable) {
			TypeVariable<?> tv = (TypeVariable<?>) genericType;
			resolvedType = typeVariableMap.get(tv);
			if (resolvedType == null) {
				resolvedType = extractBoundForTypeVariable(tv);
			}
		}
		if (resolvedType instanceof ParameterizedType) {
			return ((ParameterizedType) resolvedType).getRawType();
		}
		else {
			return resolvedType;
		}
	}

	/**
	 * Build a mapping of {@link TypeVariable#getName TypeVariable names} to concrete
	 * {@link Class} for the specified {@link Class}. Searches all super types,
	 * enclosing types and interfaces.
	 */
	static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> clazz) {
		Reference<Map<TypeVariable<?>, Type>> ref = typeVariableCache.get(clazz);
		Map<TypeVariable<?>, Type> typeVariableMap = (ref != null ? ref.get() : null);

		if (typeVariableMap == null) {
			typeVariableMap = new HashMap<TypeVariable<?>, Type>();

			// interfaces
			extractTypeVariablesFromGenericInterfaces(clazz.getGenericInterfaces(), typeVariableMap);

			// super class
			Type genericType = clazz.getGenericSuperclass();
			Class<?> type = clazz.getSuperclass();
			while (type != null && !Object.class.equals(type)) {
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericType;
					populateTypeMapFromParameterizedType(pt, typeVariableMap);
				}
				extractTypeVariablesFromGenericInterfaces(type.getGenericInterfaces(), typeVariableMap);
				genericType = type.getGenericSuperclass();
				type = type.getSuperclass();
			}

			// enclosing class
			type = clazz;
			while (type.isMemberClass()) {
				genericType = type.getGenericSuperclass();
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericType;
					populateTypeMapFromParameterizedType(pt, typeVariableMap);
				}
				type = type.getEnclosingClass();
			}

			typeVariableCache.put(clazz, new WeakReference<Map<TypeVariable<?>, Type>>(typeVariableMap));
		}

		return typeVariableMap;
	}

	/**
	 * Extracts the bound <code>Type</code> for a given {@link TypeVariable}.
	 */
	static Type extractBoundForTypeVariable(TypeVariable<?> typeVariable) {
		Type[] bounds = typeVariable.getBounds();
		if (bounds.length == 0) {
			return Object.class;
		}
		Type bound = bounds[0];
		if (bound instanceof TypeVariable) {
			bound = extractBoundForTypeVariable((TypeVariable<?>) bound);
		}
		return bound;
	}

	private static void extractTypeVariablesFromGenericInterfaces(Type[] genericInterfaces, Map<TypeVariable<?>, Type> typeVariableMap) {
		for (Type genericInterface : genericInterfaces) {
			if (genericInterface instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericInterface;
				populateTypeMapFromParameterizedType(pt, typeVariableMap);
				if (pt.getRawType() instanceof Class) {
					extractTypeVariablesFromGenericInterfaces(
							((Class<?>) pt.getRawType()).getGenericInterfaces(), typeVariableMap);
				}
			}
			else if (genericInterface instanceof Class) {
				extractTypeVariablesFromGenericInterfaces(
						((Class<?>) genericInterface).getGenericInterfaces(), typeVariableMap);
			}
		}
	}

	/**
	 * Read the {@link TypeVariable TypeVariables} from the supplied {@link ParameterizedType}
	 * and add mappings corresponding to the {@link TypeVariable#getName TypeVariable name} ->
	 * concrete type to the supplied {@link Map}.
	 * <p>Consider this case:
	 * <pre class="code>
	 * public interface Foo<S, T> {
	 *  ..
	 * }
	 *
	 * public class FooImpl implements Foo<String, Integer> {
	 *  ..
	 * }</pre>
	 * For '<code>FooImpl</code>' the following mappings would be added to the {@link Map}:
	 * {S=java.lang.String, T=java.lang.Integer}.
	 */
	private static void populateTypeMapFromParameterizedType(ParameterizedType type, Map<TypeVariable<?>, Type> typeVariableMap) {
		if (type.getRawType() instanceof Class) {
			Type[] actualTypeArguments = type.getActualTypeArguments();
			TypeVariable<?>[] typeVariables = ((Class<?>) type.getRawType()).getTypeParameters();
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type actualTypeArgument = actualTypeArguments[i];
				TypeVariable<?> variable = typeVariables[i];
				if (actualTypeArgument instanceof Class) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof GenericArrayType) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof ParameterizedType) {
					typeVariableMap.put(variable, actualTypeArgument);
				}
				else if (actualTypeArgument instanceof TypeVariable) {
					// We have a type that is parameterized at instantiation time
					// the nearest match on the bridge method will be the bounded type.
					TypeVariable<?> typeVariableArgument = (TypeVariable<?>) actualTypeArgument;
					Type resolvedType = typeVariableMap.get(typeVariableArgument);
					if (resolvedType == null) {
						resolvedType = extractBoundForTypeVariable(typeVariableArgument);
					}
					typeVariableMap.put(variable, resolvedType);
				}
			}
		}
	}	
}
