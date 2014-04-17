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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.ScreenBinder;

/**
 * This annotation can be used to customize the binding of a {@link ValueObject} field with 
 * a screen widget.
 * <p>
 * It only makes any effect if used on fields of a {@link ValueObject} class.
 * <p>
 * See the following example:
 * <pre>
 * {@code@}{@link ValueObject}(bindWidgetByFieldName=false)
 *public class Person
 *{
 *       {@code@}ScreenBind("person.name")
 *       private String name;
 *
 *       {@code@}@ScreenBind
 *       private String phone; //will be bound to "phone" widget
 *       
 *       private String address; // will not be bound.
 *       ...//getters and setters
 *}
 * </pre>
 * @author Thiago da Rosa de Bustamante
 * @see ValueObject
 * @deprecated See {@link ScreenBinder} feature
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
@Legacy
public @interface ScreenBind
{
	/**
	 * The name of the widget on the screen that will be bound to the annotated field. 
	 * If empty, the field name is used. 
	 */
	String value() default "";
}
