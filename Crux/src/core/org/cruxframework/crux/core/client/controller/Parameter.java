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
package org.cruxframework.crux.core.client.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to bind of a {@link Controller} field with 
 * an URL paramter.
 * <p>
 * It only makes any effect if used on fields of a {@link Controller} class.
 * <p>
 * For the following URL...
 * <pre>
        http://myhost.com/myapp/mymodule/mypage.html?person=Thiago&parameterName=123
 * </pre>
 * <p>
 *...you may have a Controller like this:
 *<pre>
 *{@code @}{@link Controller}("myController")
 *public class MyClass
 *{
 *  {@code @}Parameter
 *   protected String person;
 *
 *   {@code @}Parameter(value="parameterName", required=true)
 *   protected int field;
 *
 *   {@code @}{@link Expose}
 *   public void myMethod()
 *   {
 *       Window.alert(person);
 *       Window.alert(Integer.toString(field));
 *   }
 *}
 *</pre>
 *<p>
 * In the above example, the value of the "person" parameter on window URL will be bound to 
 * field "person" of the controller (the same is true to "field").
 * @author Thiago da Rosa de Bustamante
 * @see ParameterObject
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter
{
	/**
	 * The name of the parameter. If empty, the field name is used 
	 */
	String value() default "";
	
	/**
	 * If true, a validation is done to ensure that the parameter is present in the URL.
	 */
	boolean required() default false;
}
