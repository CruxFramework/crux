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
package org.cruxframework.crux.core.client.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to bind a type to CruxIocContainer 
 * <p>
 * For example, see the following class:
 * <pre>
 * {@code @IoCResource}
 * public class MyClass
 * {
 *    {@code @}Inject
 *    private MyService service; 
 * }
 * </pre>
 * <p>
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface IoCResource 
{
	public static enum Scope{LOCAL, SINGLETON, VIEW}

	/**
	 * A provider to instantiate the objects for the annotated class.
	 * @return
	 */
	Class<? extends IocProvider<?>> provider() default NoProvider.class;

	/**
	 * The implementation class created for the annotated class. If empty, the own class will be used.
	 */
	Class<?> bindClass() default NoClass.class;

	/**
	 * If true, the annotated class would be accessible by IoCContainer at runtime.
	 */
	boolean runtimeAccessible() default false;

	/**
	 * Defines the scope where the created object will be saved
	 */
	Scope scope() default Scope.LOCAL;
		
	public static class NoProvider implements IocProvider<Object>
	{
        public Object get()
        {
	        return null;
        }
	}

	public static class NoClass 
	{
	}
}