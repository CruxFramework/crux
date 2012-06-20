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

import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenBinder;
/**
 * This annotation can be used to bind a Data Object to the {@link Screen}.
 * <p>
 * Objects tied to the screen are automatically populated with information from associated 
 * fields before any event is dispatched to your {@link Controller}.
 * <p>
 * After the event handler method finish, the screen is updated with any change made on the 
 * tied object.
 *<p>
 * See the following example:
 *
 * <pre>
 *{@code @}ValueObject
 *public class Person
 *{
 *       private String name;
 *       private String phone;
 *       
 *       public String getName() {
 *               return name;
 *       }
 *       public void setName(String name) {
 *               this.name = name;
 *       }
 *       public String getPhone() {
 *               return phone;
 *       }
 *       public void setPhone(String phone) {
 *               this.phone = phone;
 *       }
 *} 
 * </pre>
 * <p>
 * <pre>
 *{@code @}{@link Controller}("myController")
 *public class MyClass
 *{
 *   {@code @}{@link Create}
 *   protected Person person;
 *
 *  {@code @}{@link Expose}
 *   public void myMethod()
 *   {
 *       Window.alert(person.getName());
 *       person.setPhone("1234-5678");
 *   }
 *}
 *</pre> 
 *<p>
 *<pre>
 *{@code <html xmlns="http://www.w3.org/1999/xhtml" 
 *     xmlns:crux="http://www.cruxframework.org/crux" 
 *     xmlns:gwt="http://www.cruxframework.org/crux/gwt">}
 *  {@code <head>}
 *      {@code <script language="javascript" src="cruxtest/cruxtest.nocache.js"></script>}
 *  {@code </head>}
 *  {@code <body>}
 *      {@code <crux:screen useController="myController" >}
 *           {@code <gwt:textBox id="name" />}
 *           {@code <gwt:textBox id="phone" />}
 *           {@code <gwt:button id="myButton" text="Hello" onClick="myController.myMethod" />}
 *      {@code </crux:screen>}
 *  {@code </body>}
 *{@code </html>}
 *</pre>
 *<p>
 *In the above example, the value of the "name" textBox on page will be bound to field 
 *"name" of the Person object created by controller (the same is true to "phone"). 
 *<p>
 *After the handler execution, the changes made in the value object will be reflected on page. 
 * @see Controller  
 * @see Create  
 * @see ScreenBind 
 * @author Thiago da Rosa de Bustamante
 * @deprecated See {@link ScreenBinder} feature
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface ValueObject
{
	/**
     * Setting this value to false makes Crux to does not bind all value object fields to widgets 
     * automatically. If you set this, you must specify for each field, the name of the widget 
     * that it will be bound (through @ScreenBind annotation).	 
     */
	boolean bindWidgetByFieldName() default true;
}
