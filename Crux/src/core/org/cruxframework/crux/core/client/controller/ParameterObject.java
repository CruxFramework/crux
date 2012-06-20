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
 * This annotation can be used to populate a Data Object with values from the page URL.
 * <p>
 * It only makes any effect if used on fields of a {@link Controller} class.
 * <p>
 * See the following example:
 *<p>
 *<pre> 
 * {@code @}ParameterObject
 * public class Parameters
 * {
 *       {@code @}{@link Parameter}("personName")
 *       private String person;
 *       private int field;
 *       
 *       public String getPerson() {
 *               return person;
 *       }
 *       public void setPerson(String person) {
 *               this.person = person;
 *       }
 *       public int getField() {
 *               return field;
 *       }
 *       public void setField(int field) {
 *               this.field = field;
 *       }
 *}
 *</pre>
 *<p>
 *<pre>
 *{@code @}{@link Controller}("myController")
 *public class MyClass
 *{
 *   {@code @}{@link Create}
 *   protected Parameters parameters;
 *
 *   {@code @}{@link Expose}
 *   public void myMethod()
 *   {
 *       Window.alert(parameters.getPerson());
 *       Window.alert(Integer.toString(parameters.getField()));
 *   }
 *}
 *</pre>
 * @author Thiago da Rosa de Bustamante
 * @see Parameter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ParameterObject
{
	/**
	 * Setting this value to false makes Crux do not bind all parameters automatically. 
	 * If you set this, you must specify, for each field, the name of the parameter that 
	 * will be bound to it (through {@code @}{@link Parameter} annotation). 
	 */
	boolean bindParameterByFieldName() default true;
}
