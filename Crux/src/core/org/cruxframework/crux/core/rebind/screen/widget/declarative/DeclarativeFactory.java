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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeclarativeFactory
{
	String id();
	String library();
	/**
	 * The widget class associated with the annotated factory. 
	 */
	Class<? extends IsWidget> targetWidget();
	
	/**
	 * if false, the annotated widgetFactory will not be attached to DOM. It will only be logically attached to the {@code Screen} object
	 */
	boolean attachToDOM() default true;
	/**
	 * HTMLContainers are widgets that can have innerHTML content AND, at same time, are Panels, (that can receive another widgets as children).
	 * The transformation made at server side, during Crux pages compilation, keeps the html content inside the {@code <span>} tag, that 
	 * marks the widget position. It allows ScreenFactory to just wrap those elements into created widget, for a very better performance.
	 */
	boolean htmlContainer() default false;
}
