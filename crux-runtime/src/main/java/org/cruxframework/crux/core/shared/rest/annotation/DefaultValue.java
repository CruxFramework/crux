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
 * Default value for a REST method parameter.
 * <p>
 * See the following example:
 * <pre>
 * ..
 * {@code @}{@link GET}
 * {@code @}{@link Path}("test")
 * public String testOperation({@code @}{@link CookieParam}("cookieName") {@code @}{@link DefaultValue}("default") String value) {
 *    return null;
 * }
 * </pre>
 * </p>
 * <p>
 * The value "default" will be passed to the value parameter when the cookie cookieName is not present on the HTTP request.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
    /**
     * The default value.
     */
    String value();
}