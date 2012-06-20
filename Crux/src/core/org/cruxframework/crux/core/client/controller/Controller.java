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
 * This annotation can be used to expose a class as a Crux controller.
 * <p>
 * Controller classes can be refereed from a {@code .crux.xml} page and contain the methods
 * used to handle events triggered by the user interface (the pages). 
 * 
 *<p>
 * For example, see the following controller:
 * <pre>
 * {@code @Controller}("myController")
 * public class MyController
 * {
 *    {@code @}{@link Expose}
 *    public void myEventHandler()
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 * }
 * </pre>
 * <p>
 * It can be used on a {@code .crux.xml} page, as illustrated by the following example:
 * <pre>
 * {@code <html} 
 *  xmlns="http://www.w3.org/1999/xhtml"
 *  xmlns:c="http://www.cruxframework.org/crux"  
 *  xmlns:g="http://www.cruxframework.org/crux/gwt"{@code >}
 *     {@code <body>}
 *        {@code <c:screen} useController="myController" {@code />}
 *        {@code <g:button} id="myButton" onClick="myController.myEventHandler" text="My Button" {@code ></g:button>}
 *     {@code </body>}
 *  {@code </html>}
 * </pre>
 * 
 * On the previous example, the controller {@code myController} is imported into the screen
 * (through the {@code <c:screen>} tag) and the method {@code myEventHandler} of this controller
 * is associated with click events of the widget {@code myButton}.
 * 
 * @see Expose  
 * @see Create  
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller 
{
	/**
	 * The name associated with the controller. This name is used on {@code .crux.xml} 
	 * pages to identify the controller. 
	 */
	String value();
	/**
	 * A Statefull controller keeps its state between two controller calls. If 
	 * this property is true, the same instance of controller class will be used to handle 
	 * all events. If false a new instance will be created for any event triggered. 
	 */
	boolean stateful() default true;
	/**
	 * This property enables the auto binding support for this controller. When it is
	 * enabled, all fields, injected through {@code @}{@link Create} annotation, of a 
	 * type annotated with {@code @}{@link ValueObject} annotation is automatically bound
	 * with the values coming from the screen.
	 */
	boolean autoBind() default true;
	/**
	 * If this property is true, the controller is only instantiated by Crux engine when 
	 * it is first required to handle an event.
	 */
	boolean lazy() default true;
	/**
	 * Since Crux 4.0, it became useless.
	 */
	@Deprecated
	String fragment() default "";
}
