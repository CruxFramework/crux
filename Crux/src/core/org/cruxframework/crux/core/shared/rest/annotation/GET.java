/*
 * Copyright 2013 cruxframework.org
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
package org.cruxframework.crux.core.shared.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method responds to HTTP GET requests
 * @see HttpMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(HttpMethod.GET)
public @interface GET 
{
	/**
	 * The number of seconds, starting from current time, that the response can be maintained in client caches
	 * @return
	 */
	int cacheTime() default NEVER;

	/**
	 * This implies that the cache or proxy MUST NOT change any aspect of the entity-body that is specified by 
	 * these headers, including the value of the entity-body itself.
	 * @return
	 */
	boolean noTransform() default false;
	
	/**
	 * When the must-revalidate directive is present in a response received by a cache, that cache MUST NOT 
	 * use the entry after it becomes stale to respond to a subsequent request without first revalidating it 
	 * with the origin server. 
	 */
	boolean mustRevalidate() default false;
	
	/**
	 * The proxy-revalidate directive has the same meaning as the must-revalidate directive, except that it 
	 * does not apply to non-shared user agent caches.
	 * @return
	 */
	boolean proxyRevalidate() default false;
	
	/**
	 * The type of cache that can be used to store the response. Private means that information must be used only 
	 * for the client that received it (only one client). Public means that the information can be shared with other 
	 * users (and stored by intermediary cache systems) 
	 * @return
	 */
	CacheControl cacheControl() default CacheControl.PUBLIC;
	
	/**
	 * If cacheTime is zero or a negative number, than cache control assumes that no cache must be used, and 
	 * append the no-store cache-control directive.
	 * If cacheTime is a positive number, than cache control inform how this cache must work.
	 * PUBLIC Indicates that the response MAY be cached by any cache, even if it would normally be non-cacheable 
	 * or cacheable only within a non- shared cache.
	 * PRIVATE Indicates that all or part of the response message is intended for a single user 
	 * and MUST NOT be cached by a shared cache. This allows an origin server to state that the specified parts of the 
	 * response are intended for only one user and are not a valid response for requests by other users.
	 * NO_CACHE means that a cache MUST NOT use the response to satisfy a subsequent request without successful 
	 * revalidation with the origin server.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum CacheControl
	{ 
		/**
		 * Indicates that the response MAY be cached by any cache, even if it would normally be non-cacheable or cacheable 
		 * only within a non- shared cache.
		 */
		PUBLIC,
		/**
		 * Indicates that all or part of the response message is intended for a single user and MUST NOT be cached by a shared cache. 
		 * This allows an origin server to state that the specified parts of the 
		 * response are intended for only one user and are not a valid response for requests by other users.
		 */
		PRIVATE,
		/**
		 * Indicates that a cache MUST NOT use the response to satisfy a subsequent request without successful 
		 * revalidation with the origin server.
		 */
		NO_CACHE
	}
	
	public static final int NEVER = -1;
	public static final int ONE_MINUTE = 60;
	public static final int ONE_HOUR = ONE_MINUTE * 60;
	public static final int ONE_DAY = ONE_HOUR * 24;
	public static final int ONE_MONTH = ONE_DAY * 30;
	public static final int ONE_YEAR = ONE_DAY * 365;
}