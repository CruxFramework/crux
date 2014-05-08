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
 * This annotation can be used to expose a controller method as an factory method.
 * It only makes any effect if used on methods of a {@link Controller} class.
 * <p>
 * Methods annotated with this annotation can be refereed on {@code .crux.xml} and {@code .view.xml} pages. 
 * A method must obey the following constraints to be exposed as a factory:  
 * <p>
 * 1) Be annotated with {@code @}Factory annotation.
 * <p>
 * 2) Have public visibility.
 * <p>
 * 3) Have only one parameter (The factory input).
 * <p>
 * 4) Return a value (not void). This is the factory output.
 * <p>
 * For example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @}Factory
 *    public Label myWidgetFactory(Person person)
 *    {
 *    	return new Label(person.getName());
 *    }
 * }
 * </pre>
 * <p>
 * It can be used on a {@code .crux.xml} or {@code .view.xml} page, as illustrated by the following example: 
 * <pre>
 * {@code <html} 
 *  xmlns="http://www.w3.org/1999/xhtml"
 *  xmlns:c="http://www.cruxframework.org/crux"  
 *  xmlns:g="http://www.cruxframework.org/crux/gwt"{@code >}
 *  xmlns:faces="http://www.cruxframework.org/crux/smart-faces"
 *     {@code <body>}
 *        {@code <c:screen} useController="myController" useDataSource="personDS" {@code />}
 *          {@code <faces:widgetList dataSource="personDS" id="people" autoLoadData="true" pageSize="20" width="100%" height="100%">
 *               <faces:widgetFactoryOnController onCreateWidget="myController.myWidgetFactory"/>
 *          </faces:widgetList>}
 *     {@code </body>}
 *  {@code </html>}
 * </pre>
 *  
 * @see Controller  
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Factory {
}
