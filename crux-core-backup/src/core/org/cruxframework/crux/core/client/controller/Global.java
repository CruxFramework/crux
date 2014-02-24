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
 * This annotation can be used to give a Global scope to a controller.
 * It only makes any effect if used on a {@link Controller} class.
 * <p>
 * A Global controller does not need to be imported on a screen to be used. All application views
 * will automatically import the controller. 
 * <p>
 * For example:
 * <pre>
 * {@code @}Global
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @}{@link Expose}
 *    public void myEventHandler()
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 *    
 *    //Only can handle click events 
 *    {@code @}{@link Expose}
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
 *        {@code <g:button} id="myButton" onClick="myController.myClickEventHandler" 
 *            onDoubleClick="myController.myEventHandler" text="My Button" {@code ></g:button>}
 *     {@code </body>}
 *  {@code </html>}
 * </pre>
 *  
 * NOTE: Only use this annotation when needed. Using this indiscriminately, can decrease 
 * the performance, spending unnecessary memory on client.
 *  
 * @see Controller  
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Global {
}
