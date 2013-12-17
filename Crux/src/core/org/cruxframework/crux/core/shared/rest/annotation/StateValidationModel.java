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
package org.cruxframework.crux.core.shared.rest.annotation;

/**
 * Indicates if Crux must validate previous state of resources being updated. 
 * @author Thiago da Rosa de Bustamante
 */
public enum StateValidationModel
{
	/**
	 * Disable the state validation for the PUT operation.
	 */
	NO_VALIDATE,
	/**
	 * If the target resource was previously loaded and the client retain some state for this resource, Crux will 
	 * ensure that the PUT operation will be executed only if this state matches the current state of the resource
	 */
	VALIDATE_IF_PRESENT,
	/**
	 * PUT operation will be executed only if the client retains the current state for the resource. If the resource 
	 * was not previously loaded by a GET operation, the method will fail.
	 */
	ENSURE_STATE_MATCHES
}
