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
package org.cruxframework.crux.core.server.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify a class as a REST service. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestService
{
	/**
	 * The name of the rest service.
	 * @return
	 */
	String value();
	
	/**
	 * Annotation used to inform that a given rest service class must support CORS requests.
	 *  
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public static @interface CorsSupport
	{
		/**
		 * Origins allowed to make cors requests to this service.
		 * @return list of origins allowed
		 */
		String[] allowOrigin() default {};
		
		/**
		 * Origins allowed to make cors requests to this service. Inform a file name containing 
		 * the origins allowed. The file must be visible into application's classpath and declare
		 * one origin per line of the raw text file specified. 
		 * @return list of origins allowed
		 */
		String allowOriginConfigFile() default "";

		/**
		 * By default, cookies are not included in CORS requests. Use this header to indicate that 
		 * cookies should be included in CORS requests.
		 * @return true if this service allow credentials sharing.
		 */
		boolean allowCredentials() default false;
		
		/**
		 * Cors requests can only access the following headers by default: Cache-Control, Content-Language
		 * Content-Type, Expires, Last-Modified and Pragma. If you want clients to be able to access other 
		 * headers,use this property to expose them
		 * @return list of non default headers to expose to cors clients.
		 */
		String[] exposeHeaders() default {};

		/**
		 * Specify a cache time for pre-flight CORS requests 
		 * @return
		 */
		int maxAge();
	}
	
	/**
	 * Annotation used to inform that a given rest service class must support JsonP requests.
	 *  
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public static @interface JsonPSupport
	{
		String callbackParam() default "callback";
	}
	
}
