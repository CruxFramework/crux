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
package org.cruxframework.crux.core.client.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.PathParam;

import com.google.gwt.editor.client.Editor.Path;

/**
 * <p>
 * Creates a rest proxy to invoke external services that are not necessarily defined using 
 * Crux at the server side.  
 * </p>
 * <p>
 * For example, see the following client rest proxy:
 * <pre>
 * {@code @TargetEndPoint}("http://targethost/rest")
 * {@code @}{@link Path}("test")
 * public interface MyRestServiceProxy extends ExternalRestProxy
 * {
 *    {@code @}{@link GET}
 *    {@code @}{@link Path}("hello/${userName}")
 *    void sayHello({@code @}{@link PathParam} String userName, Callback{@code <String>} callback);
 * }
 * </pre>
 * </p>
 * <p>
 * It Could be use to call a rest service located on http://targethost/rest/. Calling 
 * MyRestServiceProxy.sayHello("Thiago") would make a request to 
 * http://targethost/rest/test/hello/Thiago URI and expect to receive an String as result.
 * </p>
 * @author Thiago da Rosa de Bustamante
 */
public interface ExternalRestProxy
{
	
	/**
	 * Annotation used to associate an end point address to the current proxy. Crux will search for rest 
	 * annotations on proxy methods to realize how to build the requests to rest services.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface TargetEndPoint
	{
		/**
		 * The address of the rest service endPoint.
		 * @return rest service endPoint
		 */
		String value();
	}
}
