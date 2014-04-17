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

import org.cruxframework.crux.core.client.errors.ValidationErrorHandler;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * This annotation can be used to set a validation method for an exposed controller method.
 * It only makes any effect if used on exposed methods of a {@link Controller} class.
 * <p>
 * The validation method is executed before the exposed method annotated. If the validation
 * method executes without any exception, the target exposed method is called, otherwise, 
 * Crux {@link ValidationErrorHandler} is called to handle the error reported by validation 
 * method, and the exposed method is not called.  
 * <p> 
 * See the following example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @}Validate
 *    {@code @}{@link Expose}
 *    public void myEventHandler()
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 *    
 *    //Validate Method for myEventHandler method 
 *    public void validateMyEventHandler()
 *    {
 *    	if (someTest)
 *      {
 *         throw new MyValidationException("Validation message");
 *      }
 *    }
 * }
 * </pre>
 * Any call made to the previous controller method ({@code myEventHandler}) will be preceded 
 * by a call to validation method ({@code validateMyEventHandler}). It applies to any event 
 * triggered by a {@code .crux.xml} or {@code .view.xml} page.    
 * <p>
 * Validation method must obey the following constraints:  
 * <p>
 * 1) Be annotated with {@code @}Validate annotation.
 * <p>
 * 2) Have at least package visibility.
 * <p>
 * 3) Have no parameter or have only one parameter, with type equals to the type of the event
 * handled by the method.
 * <p>
 * For example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @}Validate
 *    {@code @}{@link Expose}
 *    public void myEventHandler({@link ClickEvent} event)
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 *    
 *    //Validate Method for myEventHandler method 
 *    public void validateMyEventHandler({@link ClickEvent} event)
 *    {
 *    	if (someTest)
 *      {
 *         throw new MyValidationException("Validation message");
 *      }
 *    }
 * }
 * </pre>
 * @see Expose
 * @see Controller  
 * @author Thiago da Rosa de Bustamante
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Validate {
	/**
	 * The name of the validation method that will be associated with the annotated method.
	 * If not provided, Crux will use the following name convention: 
	 * {@code validate<MethodName>(First capitalized)}  
	 */
	String value() default "";
}
