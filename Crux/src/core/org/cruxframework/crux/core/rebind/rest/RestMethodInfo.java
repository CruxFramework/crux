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

import org.cruxframework.crux.core.shared.rest.annotation.StateValidationModel;

import com.google.gwt.core.ext.typeinfo.JMethod;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class RestMethodInfo
{
	protected JMethod method;
	protected Annotation[][] parameterAnnotations;
	protected String methodURI;
	protected String httpMethod;
	protected StateValidationModel validationModel;
	public boolean isReadMethod;
	
	public RestMethodInfo(JMethod method, Annotation[][] parameterAnnotations, String methodURI, String httpMethod,
			StateValidationModel validationModel, boolean isReadMethod)
    {
		this.method = method;
		this.parameterAnnotations = parameterAnnotations;
		this.methodURI = methodURI;
		this.httpMethod = httpMethod;
		this.validationModel = validationModel;
		this.isReadMethod = isReadMethod;
    }
}
