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
package org.cruxframework.crux.core.rebind.screen.widget.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TagAttribute
{
	String value();
	/**
	 * The type considering the Declarative context
	 * @return
	 */
	Class<?> type() default String.class;
	/**
	 * The type considering the target widget property
	 * @return
	 */
	Class<?> widgetType() default SameAsType.class;
	String defaultValue() default "";
	String property() default "";
	String method() default "";
	boolean required() default false;
	boolean supportsI18N() default false;
	boolean supportsResources() default false;
	boolean supportsDataBinding() default true;
	boolean dataBindingTargetsAttributes() default true;
	Class<?> processor() default AttributeProcessor.NoProcessor.class;
	boolean xsdIgnore() default false;
	Device[] supportedDevices() default {Device.all};
	/**
	 * A description to be used to compose the documentation of the generated library 
	 */
	String description() default "";
	/**
	 * Define when the attribute will be processed
	 */
	ProcessingTime processingTime() default ProcessingTime.afterInstantiation;
	
	/**
	 * A marker type to inform that an attribute of a tag on xml view files makes reference
	 * to an widget contained on the view
	 * @author Thiago da Rosa de Bustamante
	 */
	public class WidgetReference {}
	
	/**
	 * A marker type to inform that the widgetType is equal to the type
	 * to an widget contained on the view
	 * @author Thiago da Rosa de Bustamante
	 */
	public class SameAsType {}
}
