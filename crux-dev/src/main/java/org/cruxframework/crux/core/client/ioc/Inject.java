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

import org.cruxframework.crux.core.client.Legacy;

/**
 * This annotation can be used to bind fields to objects according with CruxIocContainer 
 * configurations.
 * <p>
 * For example, see the following class:
 * <pre>
 * {@code @Controller}("myController")
 * public class MyController
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
@Target(value={ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface Inject 
{
	/**
	 * @deprecated Use {@link IoCResource.Scope} instead
	 */
	@Deprecated
	@Legacy
	public static enum Scope{LOCAL, DOCUMENT, VIEW, DEFAULT}
	
	/**
	 * Defines the scope where the created object will be saved
	 */
	@Deprecated
	@Legacy
	Scope scope() default Scope.DEFAULT;
	
	/**
	 * Used to create a custom scope inside one of the main scope, defined by the scope attribute. 
	 */
	@Deprecated
	@Legacy
	String subscope() default "";
}