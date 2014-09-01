/*
 * Copyright 2013 cruxframework.org
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
package org.cruxframework.crux.core.shared.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to map a REST method parameter to a path parameter on the HTTP request
 * <p>
 * See the following example:
 * <pre>
 * ..
 * {@code @}{@link GET}
 * {@code @}{@link Path}("test/{param1}")
 * public String testOperation({@code @}PathParam("param1") String value) {
 *    return null;
 * }
 * </pre>
 * </p>
 * <p>
 * The parameter value will be bound to the fragment extracted from the requested path 
 * to the server. 
 * </p>
 * @author Thiago da Rosa de Bustamante
 *
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParam {
    /**
     * Defines the name of the URI template parameter whose value will be used
     * to initialize the value of the annotated method parameter, class field or
     * property. See {@link Path#value()} for a description of the syntax of
     * template parameters.
     * 
     * <p>E.g. a class annotated with: <code>&#64;Path("widgets/{id}")</code>
     * can have methods annotated whose arguments are annotated
     * with <code>&#64;PathParam("id")</code>.
     */
    String value();
}