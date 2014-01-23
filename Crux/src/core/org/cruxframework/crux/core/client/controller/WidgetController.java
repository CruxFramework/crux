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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * This annotation is used to link the import of a {@link Controller} class to the presence 
 * of a particular type of widget in the {@code .view.xml} page.
 * <p>
 * If a {@link Controller} class is annotated with {@code @}WidgetController annotation, it is 
 * automatically imported by view if the target widget is present in the page. 
 * <p>
 * For example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * {@code @}WidgetController({MyWidget.class, MyOtherWidget.class })
 * public class MyController
 * {
 * }
 * </pre>
 * <p>
 * It is useful for widget developers that wants to ensure that a specific controller be present
 * to handle something needed by their widgets, when they are used.
 *     
 * @see Controller  
 * @author Thiago da Rosa de Bustamante
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WidgetController
{
	/**
	 * The list of widget classes that makes the annotated controller automatically imported, 
	 * if present in screen. 
	 */
	Class<? extends IsWidget>[] value();
}
