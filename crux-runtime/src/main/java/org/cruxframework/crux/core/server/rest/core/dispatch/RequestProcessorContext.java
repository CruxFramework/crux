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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.reflect.Method;

/**
 * @author Gesse Dafe
 * 
 */
public class RequestProcessorContext 
{
	protected Method targetMethod;

	protected Class<?> targetClass;

	public Method getTargetMethod() 
	{
		return targetMethod;
	}

	public void setTargetMethod(Method targetMethod) 
	{
		this.targetMethod = targetMethod;
	}

	public Class<?> getTargetClass() 
	{
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) 
	{
		this.targetClass = targetClass;
	}
}
