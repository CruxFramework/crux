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

import com.google.gwt.event.dom.client.ClickEvent;
/**
 * This annotation can be used to expose a controller method as an event handler method.
 * It only makes any effect if used on methods of a {@link Controller} class.
 * <p>
 * Only methods annotated with this annotation can be refereed on {@code .crux.xml} and {@code .view.xml} pages. 
 * A method must obey the following constraints to be exposed as an event handler:  
 * <p>
 * 1) Be annotated with {@code @}Expose annotation.
 * <p>
 * 2) Have public visibility.
 * <p>
 * 3) Have no parameter or have only one parameter, with type equals to the type of the event
 * handled by the method.
 * <p>
 * For example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @}Expose
 *    public void myEventHandler()
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 *    
 *    //Only can handle click events 
 *    {@code @}Expose
 *    public void myClickEventHandler({@link ClickEvent} event)
 *    {
 *    	Window.alert("event dispatched!");
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
 *     {@code <body>}
 *        {@code <c:screen} useController="myController" {@code />}
 *        {@code <g:button} id="myButton" onClick="myController.myClickEventHandler" 
 *            onDoubleClick="myController.myEventHandler" text="My Button" {@code ></g:button>}
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
public @interface Expose {
}
