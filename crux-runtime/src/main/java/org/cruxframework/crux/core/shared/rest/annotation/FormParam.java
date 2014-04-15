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
 * Used to map a REST method parameter to a form parameter on the HTTP request
 * <p>
 * See the following example:
 * <pre>
 * ..
 * {@code @}{@link GET}
 * {@code @}{@link Path}("test")
 * public String testOperation({@code @}FormParam("param") String value) {
 *    return null;
 * }
 * </pre>
 * </p>
 * <p>
 * The form parameter "param" will be bound to the value parameter when processing this method 
 * invocation.
 * </p>
 * @author Thiago da Rosa de Bustamante
 *
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormParam {
    /**
     * Defines the name of the form parameter whose value will be used
     * to initialize the value of the annotated method argument. The name is 
     * specified in decoded form, any percent encoded literals within the value
     * will not be decoded and will instead be treated as literal text. E.g. if
     * the parameter name is "a b" then the value of the annotation is "a b", 
     * <i>not</i> "a+b" or "a%20b".
     */
    String value();
}