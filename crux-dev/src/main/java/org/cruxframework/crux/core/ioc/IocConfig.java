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
package org.cruxframework.crux.core.ioc;

import org.cruxframework.crux.core.client.ioc.IocProvider;


/**
 * A Type binding configuration for Crux IoC container.
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface IocConfig<T>
{
	/**
	 * Bound a provider class that will be used to instantiate new objects by IOC container.
	 * @param providerClass
	 * @return
	 */
	IocConfig<T> toProvider(Class<? extends IocProvider<? extends T>> providerClass);
	/**
	 * Bound a new concrete class that will be returned when given class is requested to IOC container. The given class
	 * must be assignable to <T>
	 * @param toClass
	 * @return
	 */
	IocConfig<T> toClass(Class<? extends T> toClass);
	/**
	 * By default, types assigned to container can not be requested directly by user programmatically. It can be changed
	 * by enabling this property. Then, you can request the bound type directly to container doing:
	 * {@code view.getIoCContainer().get(MyType.class, scope, subscope);}
	 * 
	 * @param accessible
	 * @return
	 */
	IocConfig<T> runtimeAccessible(boolean accessible);
}
