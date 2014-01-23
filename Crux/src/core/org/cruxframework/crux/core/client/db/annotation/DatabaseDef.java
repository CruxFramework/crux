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
package org.cruxframework.crux.core.client.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;

/**
 * Defines the database metadada. All Database interfaces must be annotated with 
 * this annotation to inform Crux the database structure.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public @interface DatabaseDef
{
	/**
	 * @return Database name.
	 */
	String name() default "";
	/**
	 * @return Database version.
	 */
	int version() default 1;
	/**
	 * @return Object stores metadata.
	 */
	ObjectStoreDef[] objectStores();
	
	/**
	 * Set a defaultErrorHandler for database
	 */
	Class<? extends DatabaseErrorHandler> defaultErrorHandler() default NoErrorHandler.class;

//	/**
//	 * If this property is true, Crux will override any existent Object store or index when updating database.
//	 * If false, only non existent database or index will be created on database updating. 
//	 * @return
//	 */
//	boolean overrideDBElements() default false;
	
	/**
	 * Defines a new object store for current database. 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface ObjectStoreDef
	{
		String name() default "";
		boolean autoIncrement() default false;
		String[] keyPath() default {};
		Class<?> targetClass() default Empty.class;
		IndexDef[] indexes() default {};
	}
	
	/**
	 * Defines a new Index for a given object store.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface IndexDef
	{
		String name() default "";
		String[] keyPath();
		boolean unique() default false;
	}

	public static class Empty{}
	
	public static class NoErrorHandler implements DatabaseErrorHandler
	{
        public void onError(String message){}
        public void onError(String message, Throwable t){}
	}

}