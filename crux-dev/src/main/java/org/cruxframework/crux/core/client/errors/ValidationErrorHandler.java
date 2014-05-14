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
package org.cruxframework.crux.core.client.errors;

import com.google.gwt.user.client.ui.Widget;

/**
 * An ValidationErrorHandler is called to report errors caused by a bad use of 
 * the application (typically validations over the screen state, based in business rules, 
 * before performing an action).
 *  
 * <p>
 * To specify your own ValidationErrorHandler, configure in your application module descriptor
 * something like:
 * <p>
 * <pre>
 * {@code <replace-with class="YourValidationErrorHandlerClass">}
 *     {@code <when-type-assignable class="org.cruxframework.crux.core.client.errors.ValidationErrorHandler" />}
 * {@code </replace-with>}
 * </pre>

 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public interface ValidationErrorHandler
{
	/**Handle a validation error 
	 * @param errorMessage The error message.
	 */
	void handleValidationError(String errorMessage);
	
	/**Handle a validation error inside a widget
	 * @param errorMessage The error message.
	 * @param widget the corresponding widget to be marked as invalid
	 * @return the widget appended to the errorElement
	 */
	Widget handleValidationError(Widget widget, String errorMessage);
}
