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
 * Indicates that the annotated method responds to HTTP PUT requests. 
 * PUT operations are used to write data (insert or updates). PUT operations are idempotent, 
 * which means it can be performed repeatedly without side-effects.
 * 
 * @see HttpMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(HttpMethod.PUT)
public @interface PUT 
{ 
	/**
	 * If this state validation is enabled, Crux will add an If-Match HTTP header to ensure that the PUT operation will only be 
	 * executed if the client retains the current state of the resource being updated. 
	 * @return
	 */
	StateValidationModel validatePreviousState() default StateValidationModel.NO_VALIDATE;
}