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

import org.cruxframework.crux.core.client.ioc.Inject;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * This annotation can be used to instantiate automatically a controller field.
 * It only makes any effect if used on fields of a {@link Controller} class.
 * <p>
 * The annotation can be used to create any field, but it is specially useful to:
 * <p>
 * 1) Make the rpc mechanism painless. If an interface that extends
 * RemoteService (not annotated with {@code @}{@link RemoteServiceRelativePath}) is the type
 * for the field, the serviceEntryPoint is mapped to {@code crux.rpc} (Crux default servlet)
 * <p>
 * 2) Create DTOs that bound to screen, using the annotation {@code @}{@link ScreenBind}. 
 *<p>
 * For example:
 * <pre>
 * {@code @}{@link Controller}("myController")
 * public class MyController
 * {
 *    {@code @Create}
 *    protected MyServiceAsync service;
 *    
 *    {@code @}{@link Expose}
 *    public void myEventHandler()
 *    {
 *    	service.myMethod(new AsyncCallBackAdapter(){...});
 *    }
 * }
 * </pre>
 * @see Controller  
 * @author Thiago da Rosa de Bustamante
 * /@deprecated Use {@code @}{@link Inject} instead
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
//@Deprecated
public @interface Create 
{
}
