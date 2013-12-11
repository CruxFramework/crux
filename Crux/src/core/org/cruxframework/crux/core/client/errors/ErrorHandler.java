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
package org.cruxframework.crux.core.client.errors;

/**
 * An ErrorHandler is called to report errors in application code 
 * (bad use of the framework, or an uncaught exception).
 * <p>
 * To specify your own ErrorHandler, configure in your application module descriptor
 * something like:
 * <p>
 * <pre>
 * {@code <replace-with class="YourErrorHandlerClass">}
 *     {@code <when-type-assignable class="org.cruxframework.crux.core.client.errors.ErrorHandler" />}
 * {@code </replace-with>}
 * </pre>
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ErrorHandler
{
	/**
	 * Handle an error on application
	 * @param errorMessage The error message
	 */
	void handleError(String errorMessage);
	
	/**
	 * Handle an error on application
	 * @param t The exception 
	 */
	void handleError(Throwable t);
	
	/**
	 * Handle an error on application
	 * @param t The exception 
	 * @param uncaught True if it is an uncaught exception 
	 */
	void handleError(Throwable t, boolean uncaught);

	/**
	 * Handle an error on application
	 * @param errorMessage The error message
	 * @param t The exception 
	 */
	void handleError(String errorMessage, Throwable t);
}
