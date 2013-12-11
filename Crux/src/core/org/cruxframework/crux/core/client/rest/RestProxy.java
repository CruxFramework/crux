/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.cruxframework.crux.core.shared.rest.annotation.PathParam;

import com.google.gwt.editor.client.Editor.Path;

/**
 * Base interface to define REST clients. It can be used to invoke services 
 * defined with {@code @}{@link org.cruxframework.crux.core.server.rest.annotation.RestService} 
 * annotation 
 * </p>
 * 
 * <p>
 * For example, see the following rest service defined on application's server side:
 * <pre>
 * {@code @RestService}("myService")
 * {@code @}{@link Path}("test")
 * public class MyRestService
 * {
 *    {@code @}{@link GET}
 *    {@code @}{@link Path}("hello/${userName}")
 *    public String sayHello({@code @}{@link PathParam} String userName)
 *    {
 *        return "Hello "+userName+"!";
 *    }
 * }
 * </pre>
 * </p>
 * <p>
 * It could be called from application's client side with the following rest proxy:    
 * <pre>
 * {@code @TargetRestService}("myService")
 * public interface MyRestServiceProxy extends RestProxy
 * {
 *    void sayHello(String userName, Callback{@code <String>} callback);
 * }
 * </pre>
 * </p>
 *   
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface RestProxy
{
	/**
	 * Set base endpoint address for rest services calls made by this proxy
	 * @param address endpoint address
	 */
	void setEndpoint(String address);
	
	/**
	 * Annotation used to associate a server side Rest service to the current proxy. Crux will extract all 
	 * metadata information (service Path, methods, parameters, etc) from the target rest service class. 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface TargetRestService
	{
		/**
		 * The name of the rest service. You must inform the same name you used on 
		 * {@code @}{@link org.cruxframework.crux.core.server.rest.annotation.RestService} annotation value, 
		 * on server rest service class.
		 * @return rest service name
		 */
		String value();
	}
}
